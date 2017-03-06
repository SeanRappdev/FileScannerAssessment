package com.example.seanreddy.filescanner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.seanreddy.filescanner.R;
import com.example.seanreddy.filescanner.fragments.BiggerFilesFragment;

import java.util.ArrayList;
import java.util.List;

/*
* Recycler view Adapter for BiggerFiles Fragment
* */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    Context context;
    int layout;
    List<BiggerFilesFragment.BigFileItems> bigFileItemsesList;
    /*
    * constructor
    * Context context, int details_layoutr, List<ExtensionFragment.ExtensionFileItems> extensionFileItemsesList
    * */

    public ListAdapter(Context context, int details_layoutr, List<BiggerFilesFragment.BigFileItems> bigFileItemsList) {
        this.context = context;
        this.layout = details_layoutr;
        if(bigFileItemsList != null){
            this.bigFileItemsesList = bigFileItemsList;
        }
        else {
            this.bigFileItemsesList = new ArrayList<>();
        }

    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
        TextView name = holder.name;
        TextView size = holder.size;
        BiggerFilesFragment.BigFileItems dataHeld = bigFileItemsesList.get(position);
        if(dataHeld.name != null){
            if (dataHeld.name.length() > 15) {
                name.setText(String.format("File Name is %s", dataHeld.name.substring(0, 15)));
            } else {
                name.setText(dataHeld.name);
            }

        }
        double sizeof = Math.round(dataHeld.size/(1024*1024));
        size.setText(String.format("Size: %s MB", sizeof));
    }

    @Override
    public int getItemCount() {
        return bigFileItemsesList.size();
    }

    public class ListHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView size;
        public ListHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name_discription);
            size = (TextView)itemView.findViewById(R.id.detail_description);
        }
    }
}
