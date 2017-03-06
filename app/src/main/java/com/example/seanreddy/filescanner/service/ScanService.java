package com.example.seanreddy.filescanner.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.seanreddy.filescanner.MainActivity;
import com.example.seanreddy.filescanner.util.FileInfo;
import com.example.seanreddy.filescanner.util.NotificationHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ScanService extends Service {
    public static  final String EXTRA_TAG= "update";
    public static  final String EXTRA_COUNT= "count";
    public static final String SCANTAG = "com.example.shashannkreddy.fileScannerInfo.Scanner";
    private static final File EXTERNALFILE= Environment.getExternalStorageDirectory();
    private static final int NOT_ID = 99;
    public static final String TOTAL_COUNT ="total" ;
    private NotificationHandler notificationHandler;
    private volatile boolean isScanning = false;
    private volatile boolean isDone     = false;
    private volatile boolean isPaused   = false;

    private volatile int  scannedFiles    = 0;
    private volatile long scannedBytesSoFar    = 0;
    private int scanFrequency = 24;
    private volatile int freqCheck   = 0;

    private FileInfo fileInfo = new FileInfo();
    private BroadcastReceiver mReciver;
    private Map<String,Integer> extentionsMap;
    private Set<Map.Entry<String,Integer>> entrySet;
    private List<Map.Entry<String,Integer>> sorted;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       notificationHandler = NotificationHandler.getInstance();
        mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case MainActivity.TAG+".Start":
                        startScan();
                        break;
                    case MainActivity.TAG+".Pause":
                        pauseScan();
                        break;
                    case MainActivity.TAG+".Stop":
                        stopScan();
                        break;
                    case MainActivity.TAG+".Update":
                        //updateScan();
                        break;
                    case MainActivity.TAG+".Resume":
                        resumeScan();
                        break;
                }
            }


        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.TAG+".Start");
        intentFilter.addAction(MainActivity.TAG+".Pause");
        intentFilter.addAction(MainActivity.TAG+".Stop");
        intentFilter.addAction(MainActivity.TAG+".Update");
        intentFilter.addAction(MainActivity.TAG+".Resume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReciver,intentFilter);

        return Service.START_NOT_STICKY;
    }

    //Resumes scan on Pause
    private void resumeScan() {
        notificationHandler.updateMessage(NOT_ID,"scan Resumed!",this);
        isPaused = false;
    }


    //stops the scan
    private void stopScan() {
        isScanning = false;
        isDone     = true;
        isPaused   = true;
        notificationHandler.updateMessage(NOT_ID, "Scanning Stopped!", this);
        notificationHandler.setProgress();
        stopSelf();
    }

    //pause the scan
    private void pauseScan() {
        notificationHandler.updateMessage(NOT_ID,"scan paused!",this);
        isPaused = true;
    }

    //Start scan
    private void startScan() {
        isPaused = false;
        if((!isScanning && !isDone) || (!isScanning && isDone)){
            notificationHandler.initialize(NOT_ID,"Scanning: "+fileInfo.totalMB + "MBs",this);
            reStart();
        }
    }

    //create a thread and handles scan
    private void reStart() {
        isScanning = true;
        isDone = false;
        fileInfo = new FileInfo();
        scannedBytesSoFar = 0;
        scannedFiles = 0;
        extentionsMap = new HashMap<>();
        Thread syncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int total = (int) EXTERNALFILE.length();
                Intent intent = new Intent(SCANTAG +".PROGRESSTOTAL");
                intent.putExtra(TOTAL_COUNT,total);
                LocalBroadcastManager.getInstance(ScanService.this).sendBroadcast(intent);
                scanFiles(EXTERNALFILE);
                isDone = true;
                isScanning = false;
                fileInfo.isDone = 1;
                sendUpdate();
                updateNotification("Scanning Done!");
                notificationHandler.setProgress();
            }
        });
        syncThread.start();

    }

    //scan files
    private void scanFiles(File directory){
        Intent intent = new Intent(SCANTAG +".PROGRESS");
        intent.putExtra(EXTRA_COUNT,scannedFiles);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        if(isScanning){
            File[] listFile = directory.listFiles();
            if (listFile != null) {
                for (int i = 0; i < listFile.length; i++) {

                    if (listFile[i].isDirectory()) {
                        scanFiles(listFile[i]);
                    } else {
                        while (isPaused){
                            try {Thread.sleep(200);
                            } catch (InterruptedException e) {e.printStackTrace();}
                        }
                        freqCheck++;
                        scannedFiles++;
                        if(freqCheck==scanFrequency){
                            updateNotification("Scanned: "+fileInfo.totalMB + "MBs");
                            freqCheck=0;
                        }
                        process(listFile[i].getName(), getFileExtension(listFile[i].getName()), listFile[i].length());
                    }
                }
            }
        }
    }

    /*
    * call for biggest file
    * call for file extension
    * counts average and total
    *string name,String fileExtension Long length of the file
    * */
    private void process(String name, String fileExtension, long length) {
        filterFileData(name,length);
        filterExtData(fileExtension,length);
        double avgD = ((double)scannedBytesSoFar / (double)scannedFiles)/1000000;
        fileInfo.averageFileSize = avgD;
        fileInfo.totalMB = (int)(((double)scannedBytesSoFar/(double)1000000));
        scannedBytesSoFar+=length;
    }

    /*
    * params
    * string name, Long length of the file
    * gets size after comparing
    *calls retract
    * */
    private void filterFileData(String name, long length) {
        int sizeIndex = fitLong(fileInfo.biggestTenFileSizes, length);
        retract(fileInfo.biggestTenFileNames,name,sizeIndex);
    }



    private void retract(String[] strings, String name, int index) {
        if(index == -1){return;}
        String next = strings[index];
        strings[index] = name;
        for(int i = index+1;i<strings.length;i++){
            strings[i] = next;
            if(i+1<strings.length){next=strings[i+1];}
        }
    }


    /*
    * method used to get index to swap
    * long[] biggestTenFileSizes, long length
    * */
    private int fitLong(long[] biggestTenFileSizes, long length) {
        boolean isBigger = false;
        int retract = biggestTenFileSizes.length-1;
        if(biggestTenFileSizes[retract]<length){
            for(int i = biggestTenFileSizes.length-1;i>=0;i--){
                if(biggestTenFileSizes[i]<length){
                    retract=i;
                    isBigger = true;
                }
            }
        }
        if(isBigger) {
            long next = biggestTenFileSizes[retract];
            biggestTenFileSizes[retract] = length;
            for (int i = retract + 1; i < biggestTenFileSizes.length; i++) {
                biggestTenFileSizes[i] = next;
                if (i + 1 < biggestTenFileSizes.length) {
                    next = biggestTenFileSizes[i + 1];
                }

            }
        }
        return isBigger ? retract : -1;
    }

    /*
    * params
    * string fileextension, Long length of the file
    * gets size after comparing
    *compares based on extension count
    * */
    private void filterExtData(String fileExtension, long length) {
        if(extentionsMap.containsKey(fileExtension)){
            extentionsMap.put(fileExtension,extentionsMap.get(fileExtension)+1);
        }else {
            extentionsMap.put(fileExtension,1);
        }
        entrySet = extentionsMap.entrySet();
        sorted = new ArrayList<>(entrySet);

        Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a,
                               Map.Entry<String, Integer> b) {
                return b.getValue() - a.getValue();
            }
        });

        int len = sorted.size()>5 ? 5:sorted.size();
        for(int i = 0; i <len;i++){
            fileInfo.mostFrequentFiveExtensions[i]=sorted.get(i).getKey();
            fileInfo.mostFrequentFiveExtensionsCount[i] = sorted.get(i).getValue();
        }
    }

    //method to get extension off file
    private String getFileExtension(String fileext) {
        int dot = 0;
        for(int i = 0;i<fileext.length()-1;i++){
            if(fileext.charAt(fileext.length()-(1+i)) == '.'){
                dot = i;
                break;
            }
        }
        return fileext.substring(fileext.length()-(dot+1),fileext.length());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPaused = false;
        isScanning = false;
    }

    /*
    * send broadcast
    * and notification
    * */
    private void sendUpdate(){

        Intent intent = new Intent(SCANTAG +".UPDATE");
        intent.putExtra(EXTRA_TAG, fileInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        updateNotification("Scanned: "+fileInfo.totalMB + "MBs");
    }

    /*
    * send notification
    * takes message
    * */
    private void updateNotification(String msg){
        notificationHandler.updateMessage(NOT_ID,msg,this);
    }
}
