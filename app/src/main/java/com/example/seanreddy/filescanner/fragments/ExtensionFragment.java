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
import android.widget.ListView;

import com.example.seanreddy.filescanner.MainActivity;
import com.example.seanreddy.filescanner.R;
import com.example.seanreddy.filescanner.adapters.ExtensionAdapter;
import com.example.seanreddy.filescanner.util.FileInfo;

import java.util.ArrayList;
import java.util.List;


public class ExtensionFragment extends Fragment {

    private List<ExtensionFileItems> mExtensionItems;
    public ExtensionFragment(){
        mExtensionItems = new ArrayList<ExtensionFileItems>();
    }
    private ExtensionAdapter extinsionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_view,container,false);
        setRetainInstance(true);
        RecyclerView extFilesList = (RecyclerView) view.findViewById(R.id.dataListView);
        extFilesList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        extinsionAdapter= new ExtensionAdapter(getContext(),R.layout.details_layoutr,mExtensionItems);
        extFilesList.setAdapter(extinsionAdapter);

        return view;
    }

    /*
    * updates file info
    * */
    public void updateExtensionList(FileInfo fileInfo){
        mExtensionItems.clear();
        int length = fileInfo.mostFrequentFiveExtensionsCount.length<5
                ?fileInfo.mostFrequentFiveExtensionsCount.length:5;
        for(int i =0;i<length;i++){
            ExtensionFragment.ExtensionFileItems fileDataItem = new ExtensionFragment.ExtensionFileItems();
            fileDataItem.extName = fileInfo.mostFrequentFiveExtensions[i];
            fileDataItem.count = Long.toString(fileInfo.mostFrequentFiveExtensionsCount[i]);
            Log.d("FragmentData",fileDataItem.extName+" ");
            mExtensionItems.add(fileDataItem);
        }
        if(extinsionAdapter != null)
            extinsionAdapter.notifyDataSetChanged();
    }

    /*
    * Extension entity class
    * */
    public class ExtensionFileItems{
        public String extName;
        public String count;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FileInfo fileInfo = ((MainActivity)getActivity()).getFileResult();
        if(fileInfo != null)
            updateExtensionList(fileInfo);
    }
}
