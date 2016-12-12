package t.a.m.com.doch1.management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.management.autocomplete.AutoCompleteGroupsAdapter;
import t.a.m.com.doch1.management.autocomplete.AutoCompleteUsersAdapter;
import t.a.m.com.doch1.views.CircleImageView;

public class GroupManagementActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private AutoCompleteTextView mSearchUserTextView;
    private AutoCompleteTextView mSearchGroupTextView;
    private AutoCompleteUsersAdapter mAutoCompleteUsersAdapter;
    private RecyclerView mMembersRecycler;
    private UsersAdapter mMembersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);

        mSearchUserTextView = (AutoCompleteTextView) findViewById(R.id.search_text_view);
        mSearchUserTextView.setThreshold(2);
        mAutoCompleteUsersAdapter = new AutoCompleteUsersAdapter(this);
        mSearchUserTextView.setAdapter(mAutoCompleteUsersAdapter);
        mSearchUserTextView.setOnItemClickListener(this);
        mSearchUserTextView.setDropDownWidth(500);

        mSearchGroupTextView = (AutoCompleteTextView) findViewById(R.id.search_group);
        mSearchGroupTextView.setThreshold(2);
        mSearchGroupTextView.setAdapter(new AutoCompleteGroupsAdapter(this));
        //mSearchGroupTextView.setOnItemClickListener(this);
        mSearchGroupTextView.setDropDownWidth(500);

        mMembersRecycler = (RecyclerView) findViewById(R.id.members);
        mMembersAdapter = new UsersAdapter();
        mMembersRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMembersRecycler.setAdapter(mMembersAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final User mig = mMembersAdapter.getItem(position);
                    AlertDialog dialog = new AlertDialog.Builder(GroupManagementActivity.this)
                            .setTitle("Removing " + mig.getName())
                            .setMessage(String.format("Are you sure you want to remove %s", mig.getName()))
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mMembersAdapter.remove(position);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mMembersAdapter.notifyDataSetChanged();
                                }
                            })
                            .create();

                    dialog.show();
                }

        });

        touchHelper.attachToRecyclerView(mMembersRecycler);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAutoCompleteUsersAdapter.destroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        User user = mAutoCompleteUsersAdapter.getItem(i);
        mMembersAdapter.add(user);
        mSearchUserTextView.setText("");
        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchUserTextView.getWindowToken(), 0);
    }

    public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder>
    {
        List<User> mUsers = new ArrayList<>();

        @Override
        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GroupManagementActivity.this).inflate(R.layout.user_in_list_layout, null);
            return new UserHolder(view);
        }

        @Override
        public void onBindViewHolder(UserHolder holder, int position) {
            holder.mName.setText(mUsers.get(position).getName());

            if(mUsers.get(position).getImage() != null) {

                Picasso.with(GroupManagementActivity.this)
                        .load(mUsers.get(position).getImage())
                        .placeholder(R.drawable.profile_pic)
                        .into(holder.mImage);
            }
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public void add(User user) {
            mUsers.add(0, user);
            notifyItemInserted(0);
            mMembersRecycler.scrollToPosition(0);
        }

        public User getItem(int position) {
            return mUsers.get(position);
        }

        public void remove(int position) {
            mUsers.remove(position);
            notifyItemRangeRemoved(position, mUsers.size());
        }

        public class UserHolder extends ViewHolder{

            public FrameLayout mContainer;
            public Button mButton;
            public TextView mName;
            public CircleImageView mImage;

            public UserHolder(View itemView) {
                super(itemView);
                mContainer = (FrameLayout) itemView.findViewById(R.id.container);
                mButton = (Button) itemView.findViewById(android.R.id.button1);
                mName = (TextView) itemView.findViewById(R.id.name);
                mImage = (CircleImageView) itemView.findViewById(R.id.image);
            }

        }
    }
}
