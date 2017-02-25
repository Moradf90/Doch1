package t.a.m.com.doch1.management;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.views.CircleImageView;

/**
 * Created by Morad on 12/13/2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder>
{
    private List<User> mUsers;
    private Context mContext;

    public UsersAdapter(Context context){
        mContext = context;
        mUsers = new ArrayList<>();
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_in_list_layout, null);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.mName.setText(mUsers.get(position).getName());

        holder.mImage.setImageDrawable(mContext.getDrawable(R.drawable.profile_pic));

        if(mUsers.get(position).getImage() != null) {

            Glide.with(mContext)
                    .load(mUsers.get(position).getImage())
                    .error(R.drawable.profile_pic)
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
    }

    public User getItem(int position) {
        return mUsers.get(position);
    }

    public void remove(int position) {
        mUsers.remove(position);
        notifyItemRangeRemoved(position, mUsers.size());
    }

    public class UserHolder extends RecyclerView.ViewHolder {

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
