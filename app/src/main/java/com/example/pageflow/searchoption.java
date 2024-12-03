package com.example.pageflow;

import android.widget.Filter;

import java.util.ArrayList;

public class searchoption extends Filter {

    private ArrayList<book> originalList;
    private bookadaptermodel adapterCategory;


    public searchoption(ArrayList<book> originalList, bookadaptermodel adapterCategory) {
        this.originalList = originalList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();


        if (constraint != null && constraint.length() > 0) {
            ArrayList<book> filteredList = new ArrayList<>();


            for (book bookItem : originalList) {
                if (bookItem.getTitle().contains(constraint)) {
                    filteredList.add(bookItem);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();

            results.values = originalList;
            results.count = originalList.size();
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapterCategory.categoryArrayList = (ArrayList<book>) results.values;

        adapterCategory.notifyDataSetChanged();
    }
}
