package com.couchbase.arqui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.couchbase.arqui.util.LiveQueryAdapter;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.util.Log;
import com.couchbase.arqui.libreria.CouchbaseList;

import java.util.Map;

public class ListsActivity extends AppCompatActivity {

    private Database mDatabase;
    private String mUsername;

    private LiveQuery listsLiveQuery = null;
    private ListAdapter mAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listsLiveQuery != null) {
            listsLiveQuery.stop();
            listsLiveQuery = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCreateDialog();
            }
        });


        Application application = (Application) getApplication();
        mDatabase = application.getDatabase();
        mUsername = application.getUsername();

        setupViewAndQuery();
        mAdapter = new ListAdapter(this, listsLiveQuery);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Document list = (Document) mAdapter.getItem(i);
                showTasks(list);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
                PopupMenu popup = new PopupMenu(ListsActivity.this, view);
                popup.inflate(R.menu.list_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Document list = (Document) mAdapter.getItem(pos);
                        Application application = (Application) getApplication();
                        handleListPopupAction(item, list);

                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
    }


    private void setupViewAndQuery() {
        if (mDatabase == null) {
            return;
        }
        com.couchbase.lite.View listsView = CouchbaseList.getVista(mDatabase, "list/listsByName");
        if (listsView.getMap() == null) {
            listsView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    String type = (String) document.get("type");
                    if ("list".equals(type)) {
                        emitter.emit(document.get("name"), null);
                    }
                }
            }, "1.0");
        }

        listsLiveQuery = listsView.createQuery().toLiveQuery();

    }

    private void handleListPopupAction(MenuItem item, Document list) {
        switch (item.getItemId()) {
            case R.id.update:
                return;
            case R.id.delete:
                CouchbaseList.deleteList(list);
                return;
        }
    }

    private void showTasks(Document list) {
        Intent intent = new Intent(this, ListDetailActivity.class);
        intent.putExtra(ListDetailActivity.INTENT_LIST_ID, list.getId());
        startActivity(intent);
    }


    private class ListAdapter extends LiveQueryAdapter {
        public ListAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_list, null);
            }

            final Document list = (Document) getItem(position);
            String key = (String) getKey(position);
            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(key);


            return convertView;
        }

    }

    private void displayCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.view_dialog_input, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String title = input.getText().toString();
                    if (title.length() == 0)
                        return;
                    CouchbaseList.createList(mDatabase, title, mUsername);
                } catch (CouchbaseLiteException e) {
                    Log.e(Application.TAG, "Cannot create a new list", e);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

}
