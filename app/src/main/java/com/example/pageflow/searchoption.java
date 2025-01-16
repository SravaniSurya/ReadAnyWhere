package com.example.pageflow;

import android.widget.Filter;
import java.util.ArrayList;

public class searchoption extends Filter {

    private ArrayList<book> originalList;
    private bookadaptermodel adapter;

    public searchoption(ArrayList<book> originalList, bookadaptermodel adapter) {
        this.originalList = originalList;
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0) {
            ArrayList<book> filteredList = new ArrayList<>();


            for (int i = 0; i < originalList.size(); i++) {
                book item = originalList.get(i);
                if (item.getCategory() != null && item.getCategory().contains(constraint)) {
                    filteredList.add(item);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
        } else {
            results.values = originalList;
            results.count = originalList.size();
        }

        return results;
    }


    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.categoryArrayList = (ArrayList<book>) results.values;
        adapter.notifyDataSetChanged();
    }
}
