package com.dright;

import android.provider.ContactsContract;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class ContentFilter extends Filter{

    RecyclerViewAdapter adapter;
    public ArrayList<ProfileModel> filterList;

    public ContentFilter(ArrayList<ProfileModel> filterList, RecyclerViewAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        if(constraint != null && constraint.length() > 0)
        {
            constraint=constraint.toString();
            ArrayList<ProfileModel> filteredResults=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {

                if(filterList.get(i).name.toString().contains(constraint))
                {
                    filteredResults.add(filterList.get(i));
                }
            }

            results.count=filteredResults.size();
            results.values=filteredResults;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.mUser= (ArrayList<ProfileModel>) results.values;

        adapter.notifyDataSetChanged();
    }
}
