package com.couchbase.todo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.todo.util.ListFragmentPagerAdapter;

public class ListDetailActivity extends AppCompatActivity {

    public static final String INTENT_LIST_ID = "list_id";

    private Database mDatabase;
    private Document mTaskList;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        Application application = (Application)getApplication();
        mDatabase = application.getDatabase();
        mUsername = application.getUsername();
        mTaskList = mDatabase.getDocument(getIntent().getStringExtra(INTENT_LIST_ID));


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ListFragmentPagerAdapter(getSupportFragmentManager(),1));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}