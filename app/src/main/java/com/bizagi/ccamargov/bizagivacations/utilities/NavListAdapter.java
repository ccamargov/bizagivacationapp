package com.bizagi.ccamargov.bizagivacations.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizagi.ccamargov.bizagivacations.model.NavItem;
import com.bizagi.ccamargov.bizagivacations.R;

import java.util.ArrayList;

public class NavListAdapter extends BaseAdapter {

    private Context oContext;
    private ArrayList<NavItem> aNavItems;

    public NavListAdapter(Context context, ArrayList<NavItem> navItems) {
        this.oContext = context;
        this.aNavItems = navItems;
    }

    @Override
    public int getCount() {
        return aNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return aNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View oView;
        if (convertView == null) {
            LayoutInflater oInflater = (LayoutInflater) oContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            oView = oInflater.inflate(R.layout.list_menu_item, null);
        } else {
            oView = convertView;
        }
        TextView titleView = oView.findViewById(R.id.title_nav_item);
        TextView subtitleView = oView.findViewById(R.id.sub_title_nav_item);
        ImageView iconView = oView.findViewById(R.id.image_nav_item);
        titleView.setText(aNavItems.get(position).getTitle());
        subtitleView.setText(aNavItems.get(position).getSubtitle());
        iconView.setImageResource(aNavItems.get(position).getIcon());
        return oView;
    }

}
