package com.flatcode.littlemovieadmin.Filter;

import android.widget.Filter;

import com.flatcode.littlemovieadmin.Adapter.CastAdapter;
import com.flatcode.littlemovieadmin.Model.Cast;

import java.util.ArrayList;

public class CastFilter extends Filter {

    ArrayList<Cast> list;
    CastAdapter adapter;

    public CastFilter(ArrayList<Cast> list, CastAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Cast> filter = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toUpperCase().contains(constraint)) {
                    filter.add(list.get(i));
                }
            }
            results.count = filter.size();
            results.values = filter;
        } else {
            results.count = list.size();
            results.values = list;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.list = (ArrayList<Cast>) results.values;
        adapter.notifyDataSetChanged();
    }
}