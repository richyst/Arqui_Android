package com.couchbase.arqui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.arqui.libreria.CouchbaseList;
import com.couchbase.arqui.util.LiveQueryAdapter;

import com.couchbase.arqui.libreria.CouchbaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;


public class TasksFragment extends Fragment {

    private ListView mListView;

    private Database mDatabase;
    private String mUsername;
    public Document mTaskList;

    LiveQuery listsLiveQuery = null;

    private android.view.LayoutInflater mInflater;
    private View mainView;


    public TasksFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listsLiveQuery != null) {
            listsLiveQuery.stop();
            listsLiveQuery = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mInflater = inflater;
        mainView = inflater.inflate(R.layout.fragment_tasks, null);

        mListView = (ListView) mainView.findViewById(R.id.list);

        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                displayCreateDialog();
            }
        });

        Application application = (Application) getActivity().getApplication();
        mDatabase = application.getDatabase();
        mUsername = application.getUsername();
        Intent intent = getActivity().getIntent();
        mTaskList = CouchbaseList.getList(intent.getStringExtra(ListDetailActivity.INTENT_LIST_ID), mDatabase);

        setupViewAndQuery();

        return mainView;
    }

    private void setupViewAndQuery() {
        com.couchbase.lite.View view = mDatabase.getView("task/tasksByCreatedAt");
        if (view.getMap() == null) {
            view.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    String type = (String) document.get("type");
                    if ("user".equals(type)) {
                        Map<String, Object> taskList = (Map<String, Object>) document.get("list");
                        String listId = (String) taskList.get("id");
                        String task = (String) document.get("name");
                        ArrayList<String> key = new ArrayList<String>();
                        key.add(listId);
                        key.add(task);
                        String value = (String) document.get("_rev");
                        emitter.emit(key, value);
                    }
                }
            }, "5.0");
        }

        Query query = view.createQuery();

        ArrayList<String> key = new ArrayList<String>();
        key.add(mTaskList.getId());
        query.setStartKey(key);
        query.setEndKey(key);
        query.setPrefixMatchLevel(1);
        query.setDescending(false);
        listsLiveQuery = query.toLiveQuery();

        final TasksFragment.UserAdapter mAdapter = new TasksFragment.UserAdapter(getActivity(), listsLiveQuery);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Document user = (Document) mAdapter.getItem(i);
                displayInfo(user);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.inflate(R.menu.list_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Document task = (Document) mAdapter.getItem(pos);
                        handleTaskPopupAction(item, task);
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });

    }

    private void displayInfo(Document user){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String name = (String) user.getProperty("name");
        String password = (String) user.getProperty("password");
        String createdAt = dateFormat.format(user.getProperty("createdAt"));
        new AlertDialog.Builder(getContext())
                .setTitle("Usuario " + name)
                .setMessage("Nombre: "+name+"\nPassword: " + password + "\nCreado: "+createdAt)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .show();
    }
    private void handleTaskPopupAction(MenuItem item, Document user) {
        switch (item.getItemId()) {
            case R.id.update:
                updateUser(user);
                return;
            case R.id.delete:
                CouchbaseUser.deleteUser(user);
                return;
        }
    }

    private void updateUser(final Document user) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Update Usuario");

        final EditText input = new EditText(getContext());
        input.setMaxLines(1);
        input.setSingleLine(true);
        String text = (String) user.getProperty("name");
        input.setText(text);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CouchbaseUser.updateUser(user,"name",input.getText().toString());
            }
        });
        alert.show();
    }

    private class UserAdapter extends LiveQueryAdapter {
        public UserAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_task, null);
            }

            final Document user = (Document) getItem(position);
            if (user == null || user.getCurrentRevision() == null) {
                return convertView;
            }


            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText((String) user.getProperty("name"));

            return convertView;
        }

    }

    private void displayCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Create new User");

        final android.view.View view = mInflater.inflate(R.layout.view_dialog_input1, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        final EditText input1 = (EditText) view.findViewById(R.id.text1);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                    String title = input.getText().toString();
                    String password = input1.getText().toString();
                    if (title.length() == 0 || password.length()==0)
                        return;
                    CouchbaseUser.createUser(title, password, mTaskList, mDatabase);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

}
