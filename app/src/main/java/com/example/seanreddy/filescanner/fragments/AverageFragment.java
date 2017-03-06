package com.example.seanreddy.filescanner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.seanreddy.filescanner.MainActivity;
import com.example.seanreddy.filescanner.R;
import com.example.seanreddy.filescanner.util.FileInfo;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AverageFragment extends Fragment {

    @BindView(R.id.average_count)
    TextView textView;
    String averageText = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_average,container,false);
        setRetainInstance(true);
        ButterKnife.bind(this,view);

        //get fileInfo if present
        FileInfo fileInfo = ((MainActivity)getActivity()).getFileResult();
        if(fileInfo != null)
            update(fileInfo);
        return view;

    }

    // updates on file scan complete
    public void update(FileInfo fileInfo){
        double num = Math.round(fileInfo.averageFileSize*100);
        averageText = "Average File Size is :"+(num/100);
        if(textView!=null)
            textView.setText(averageText);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AverageText",averageText);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            averageText = savedInstanceState.getString("AverageText");
            if(textView!=null)
                textView.setText(averageText);
        }
    }
}
