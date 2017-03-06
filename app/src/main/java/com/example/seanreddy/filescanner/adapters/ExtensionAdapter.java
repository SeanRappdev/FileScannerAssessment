package com.example.seanreddy.filescanner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.seanreddy.filescanner.R;
import com.example.seanreddy.filescanner.fragments.ExtensionFragment;

import java.util.ArrayList;
import java.util.List;


/*
* Recycler view Adapter for Extension Fragment
* */
public class ExtensionAdapter extends RecyclerView.Adapter<ExtensionAdapter.ExtensionHolder> {

    Context context;
    int layout;
    List<ExtensionFragment.ExtensionFileItems> extensionFileItemsesList;

    /*
    * constructor
    * Context context, int details_layoutr, List<ExtensionFragment.ExtensionFileItems> extensionFileItemsesList
    * */
    public ExtensionAdapter(Context context, int details_layoutr, List<ExtensionFragment.ExtensionFileItems> extensionFileItemsesList) {
        this.context = context;
        this.layout = details_layoutr;
        if(extensionFileItemsesList != null){
            this.extensionFileItemsesList = extensionFileItemsesList;
        }
        else {
            this.extensionFileItemsesList = new ArrayList<>();
        }

    }

    @Override
    public ExtensionAdapter.ExtensionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        return new ExtensionAdapter.ExtensionHolder(view);
    }

    @Override
    public void onBindViewHolder(ExtensionAdapter.ExtensionHolder holder, int position) {
        TextView name = holder.name;
        TextView size = holder.size;
        ExtensionFragment.ExtensionFileItems dataHeld = extensionFileItemsesList.get(position);
        if(dataHeld.extName != null){
            if (dataHeld.extName.length() > 15) {
                name.setText(String.format("File Extension: %s", dataHeld.extName.substring(0, 15)));
            } else {
                name.setText(String.format("File Extension: %s",dataHeld.extName));
            }

        }
        size.setText(String.format("Count: %s", dataHeld.count));
    }

    @Override
    public int getItemCount() {
        return extensionFileItemsesList.size();
    }


    public class ExtensionHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView size;
        public ExtensionHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name_discription);
            size = (TextView)itemView.findViewById(R.id.detail_description);
        }
    }
}
