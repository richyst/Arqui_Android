package com.couchbase.todo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.SavedRevision;
import com.couchbase.todo.util.LiveQueryAdapter;

import com.couchbase.todo.libreria.CouchbaseDatabase;
import com.couchbase.todo.libreria.CouchbaseManager;
import com.couchbase.todo.libreria.CouchbaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.couchbase.todo.ListDetailActivity.INTENT_LIST_ID;


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
        mTaskList = mDatabase.getDocument(intent.getStringExtra(INTENT_LIST_ID));

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

        final TasksFragment.TaskAdapter mAdapter = new TasksFragment.TaskAdapter(getActivity(), listsLiveQuery);

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
    private void handleTaskPopupAction(MenuItem item, Document task) {
        switch (item.getItemId()) {
            case R.id.update:
                updateTask(task);
                return;
            case R.id.delete:
                deleteTask(task);
                return;
        }
    }

    private void updateTask(final Document task) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Update Usuario");

        final EditText input = new EditText(getContext());
        input.setMaxLines(1);
        input.setSingleLine(true);
        String text = (String) task.getProperty("name");
        input.setText(text);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> updatedProperties = new HashMap<String, Object>();
                updatedProperties.putAll(task.getProperties());
                updatedProperties.put("name", input.getText().toString());

                try {
                    task.putProperties(updatedProperties);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }

    private void deleteTask(final Document task) {
        try {
            task.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private class TaskAdapter extends LiveQueryAdapter {
        public TaskAdapter(Context context, LiveQuery query) {
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

    private SavedRevision createUser(String title, String password) {
        Map<String, Object> ListInfo = new HashMap<String, Object>();
        ListInfo.put("id", mTaskList.getId());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "user");
        properties.put("list", ListInfo);
        properties.put("createdAt", new Date());
        properties.put("name", title);
        properties.put("password", password);

        Document document = mDatabase.createDocument();
        try {
            return document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }
    }


}
