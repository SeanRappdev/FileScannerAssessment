package com.example.seanreddy.filescanner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.seanreddy.filescanner.MainActivity;
import com.example.seanreddy.filescanner.R;
import com.example.seanreddy.filescanner.adapters.ListAdapter;
import com.example.seanreddy.filescanner.util.FileInfo;

import java.util.ArrayList;
import java.util.List;


public class BiggerFilesFragment extends Fragment {

    private ListAdapter listAdapter;
    List<BigFileItems> bigFileItemsList;

    public BiggerFilesFragment() {
        // Required empty public constructor
        bigFileItemsList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_view,container,false);
        RecyclerView bigFilesList = (RecyclerView) view.findViewById(R.id.dataListView);
        setRetainInstance(true);
        listAdapter= new ListAdapter(getContext(),R.layout.details_layoutr,bigFileItemsList);
        bigFilesList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        bigFilesList.setAdapter(listAdapter);
        FileInfo fileInfo = ((MainActivity)getActivity()).getFileResult();
        if(fileInfo != null)
            updateList(fileInfo);
        return view;
    }


    //updates file info on scan completion
    public void updateList(FileInfo fileInfo){
        Log.w("DataCheck"," in updateList method");
        if(listAdapter != null){
            bigFileItemsList.clear();
            for(int i =0;i<fileInfo.biggestTenFileNames.length;i++){
                BigFileItems fileDataItem = new BigFileItems();
                fileDataItem.name = fileInfo.biggestTenFileNames[i];
                fileDataItem.size = fileInfo.biggestTenFileSizes[i];
                Log.d("FragmentData",fileDataItem.name+" ");
                bigFileItemsList.add(fileDataItem);
            }
            listAdapter.notifyDataSetChanged();
        }

    }

    /*
    * BiggerFiles entity class
    * */
    public class BigFileItems{
        public String name;
        public long size;
    }


}
