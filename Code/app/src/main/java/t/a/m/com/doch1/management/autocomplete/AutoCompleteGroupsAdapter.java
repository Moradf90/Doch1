package t.a.m.com.doch1.management.autocomplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.views.CircleImageView;

/**
 * Created by Morad on 12/10/2016.
 */
public class AutoCompleteGroupsAdapter extends BaseAdapter implements Filterable {

    private List<Group> mFilteredUsers;
    private LayoutInflater mInflater;
    private Context mContext;
    private GroupsAdapterFilter mFilter;

    public AutoCompleteGroupsAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFilteredUsers = new ArrayList<>();
        mFilter = new GroupsAdapterFilter() {
            @Override
            protected void publishResults(CharSequence search, FilterResults result) {
                mFilteredUsers = (List<Group>) result.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return mFilteredUsers.size();
    }

    @Override
    public Group getItem(int i) {
        return mFilteredUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {

            view = mInflater.inflate(R.layout.user_in_list_layout, parent, false);
        }

        Group value = getItem(position);

        TextView text = (TextView) view.findViewById(R.id.name);
        text.setText(value.getName());

        if(value.getImage() != null) {
            CircleImageView image = (CircleImageView) view.findViewById(R.id.image);
            Picasso.with(mContext).load(value.getImage())
                    .placeholder(R.drawable.profile_pic).into(image);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void destroy(){
        mFilter.destroy();
        mContext = null;
    }

}