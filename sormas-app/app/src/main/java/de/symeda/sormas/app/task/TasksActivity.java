package de.symeda.sormas.app.task;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasRootActivity;
import de.symeda.sormas.app.util.SlidingTabLayout;

public class TasksActivity extends SormasRootActivity {

    private ViewPager pager;
    private TasksListFilterAdapter adapter;
    private SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.tasks_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_tasks));

        refreshLocalDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        createTabViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_reload:
                refreshLocalDB();
                return true;

            case R.id.action_new_case:
                showCaseNewView();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createTabViews() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new TasksListFilterAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    private void refreshLocalDB() {

        new SyncTasksTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
                    for (Fragment fragement : getSupportFragmentManager().getFragments()) {
                        if (fragement instanceof TasksListFragment) {
                            fragement.onResume();
                        }
                    }
                }

                Toast toast = Toast.makeText(TasksActivity.this, "refreshed local db", Toast.LENGTH_SHORT);
                toast.show();
            }
        }.execute();
    }
}