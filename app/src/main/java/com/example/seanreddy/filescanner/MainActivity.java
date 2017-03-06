package com.example.seanreddy.filescanner;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.seanreddy.filescanner.adapters.MyPagerAdapter;
import com.example.seanreddy.filescanner.fragments.AverageFragment;
import com.example.seanreddy.filescanner.fragments.BiggerFilesFragment;
import com.example.seanreddy.filescanner.fragments.ExtensionFragment;
import com.example.seanreddy.filescanner.service.ScanService;
import com.example.seanreddy.filescanner.util.FileInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.shareBtn)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.button_stop)
    Button mStopStartButton;
    @BindView(R.id.button_pause)
    Button mPauseButton;

    BroadcastReceiver mUpdateReceiver;
    FileInfo fileResult;
    MyPagerAdapter adapter;
    BiggerFilesFragment biggestFragment;
    ExtensionFragment extensionFragment;
    int fileCount;
    private boolean dataScanned = false;

    public final static String TAG = "com.example.seanreddy.fileScannerInfo.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(!(isScannerActive())){
            startService(new Intent(this,ScanService.class));
        }
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mProgressBar.setVisibility(View.GONE);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mFloatingActionButton.setVisibility(View.INVISIBLE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScanService.SCANTAG+".UPDATE");
        intentFilter.addAction(ScanService.SCANTAG+".PROGRESS");
        intentFilter.addAction(ScanService.SCANTAG+".PROGRESSTOTAL");
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(intent.getAction()){
                    case ScanService.SCANTAG+".UPDATE":
                        fileResult = intent.getParcelableExtra(ScanService.EXTRA_TAG);
                        mProgressBar.setProgress(fileCount);
                        mProgressBar.setVisibility(View.GONE);
                        refreshData();
                        mStopStartButton.setText(getResources().getString(R.string.start_button));
                        break;
                    case ScanService.SCANTAG+".PROGRESS":
                        int count = intent.getIntExtra(ScanService.EXTRA_COUNT,0);
                        mProgressBar.setProgress(count);
                        break;
                    case  ScanService.SCANTAG+".PROGRESSTOTAL":
                        fileCount = intent.getIntExtra(ScanService.TOTAL_COUNT,0);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setMax(fileCount);
                        mProgressBar.setProgress(0);
                }

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver,intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fileResult != null){
            if(fileResult.isDone == 1){
                mFloatingActionButton.setVisibility(View.VISIBLE);
            }else {
                mFloatingActionButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    /*
    * */
    private void refreshData() {
        biggestFragment = (BiggerFilesFragment) adapter.getRegisteredFragment(0);
        extensionFragment = (ExtensionFragment) adapter.getRegisteredFragment(2);
        if(biggestFragment != null)
            biggestFragment.updateList(fileResult);
        if(extensionFragment!=null)
            extensionFragment.updateExtensionList(fileResult);
        AverageFragment avg = (AverageFragment) adapter.getRegisteredFragment(1);
        if(avg!=null)
            avg.update(fileResult);

        if(fileResult.isDone == 1){
            mFloatingActionButton.setVisibility(View.VISIBLE);
        }else {
            mFloatingActionButton.setVisibility(View.INVISIBLE);
        }

    }

    //check scanner is active
    private boolean isScannerActive(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(ScanService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return  false;
    }


    @OnClick(R.id.button_pause)
    public void pauseButton(){
        if(mPauseButton.getText().toString().equals(getResources().getString(R.string.pause_button))){
            Intent intent = new Intent(TAG+".Pause");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            mPauseButton.setText(getResources().getString(R.string.resume_button));
        }else{
            Intent intent = new Intent(TAG+".Resume");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            mPauseButton.setText(getResources().getString(R.string.pause_button));
        }

    }

    //start and stop button clicks
    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick (R.id.button_stop)
    public void startStopButton(){
        if(mStopStartButton.getText().toString().equals(getResources().getString(R.string.stop_button))){
            stopScanService();
            mStopStartButton.setText(getResources().getString(R.string.start_button));
            mPauseButton.setText(getResources().getString(R.string.pause_button));
        }else {
            if(mStopStartButton.getText().toString().equals(getResources().getString(R.string.start_button))){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    startScanService();
                    mStopStartButton.setText(getResources().getString(R.string.stop_button));
                }
                else {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},999);
                }
            }
        }

    }

    //Start service here
    private void startScanService() {
        Intent intent = new Intent(TAG+".Start");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //Stop service here
    private void stopScanService() {
        Intent intent = new Intent(TAG+".Stop");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //share button clicks
    @OnClick(R.id.shareBtn)
    public  void shareButtonClicked(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getResources().getString(R.string.add_receipent)});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.store_scanned));
        i.putExtra(Intent.EXTRA_TEXT   , fileResult.totalMB + "MBs of data has been scanned.");
        i.putExtra(Intent.EXTRA_TEXT , fileResult.biggestTenFileNames);
        startActivity(Intent.createChooser(i, "Send mail..."));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startScanService();
                mStopStartButton.setText("Stop");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("FileData",fileResult);
        outState.putInt("FileCount",fileCount);
        outState.putBoolean("ShareButton",dataScanned);
        Log.d("BundleData",outState.toString()+"hello");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("SavedBundleData",savedInstanceState.toString()+"hello");
        if(savedInstanceState.getParcelable("FileData")!=null){
            fileResult = savedInstanceState.getParcelable("FileData");
        }
        fileCount = savedInstanceState.getInt("FileCount");
        dataScanned = savedInstanceState.getBoolean("ShareButton",false);
        mProgressBar.setMax(fileCount);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mUpdateReceiver !=null)
        {
            try{
                unregisterReceiver(mUpdateReceiver);
            }
            catch (Exception e){

            }
        }
    }


    //returns the file result
    public FileInfo getFileResult(){
        return fileResult;
    }


}
