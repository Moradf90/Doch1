package t.a.m.com.doch1.management;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;

import t.a.m.com.doch1.R;
import t.a.m.com.doch1.management.autocomplete.AutoCompleteUsersAdapter;

public class GroupManagementActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private AutoCompleteTextView mSearchTextView;
    private AutoCompleteUsersAdapter mAutoCompleteUsersAdapter;
    private RecyclerView mMembersRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);

        mSearchTextView = (AutoCompleteTextView) findViewById(R.id.search_text_view);
        mSearchTextView.setThreshold(1);
        mAutoCompleteUsersAdapter = new AutoCompleteUsersAdapter(this);
        mSearchTextView.setAdapter(mAutoCompleteUsersAdapter);
        mSearchTextView.setOnItemClickListener(this);
        mSearchTextView.setDropDownWidth(500);

        mMembersRecycler = (RecyclerView) findViewById(R.id.members);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);
        RecyclerViewSwipeManager mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        ///mRecyclerViewSwipeManager.createWrappedAdapter(ArrayAdapter<String>)
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAutoCompleteUsersAdapter.destroy();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
