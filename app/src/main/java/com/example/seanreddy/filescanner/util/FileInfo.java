package com.example.seanreddy.filescanner.util;


import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo implements Parcelable{

    public int totalMB = 0;
    public double averageFileSize  = 0;

    public String[] biggestTenFileNames = new String[10];
    public long[]   biggestTenFileSizes = new long[10];
    public long[]  mostFrequentFiveExtensionsCount = new long[5];
    public String[] mostFrequentFiveExtensions = new String[5];

    public byte isDone = 0;
    public FileInfo(Parcel in) {
        totalMB = in.readInt();
        averageFileSize = in.readDouble();
        in.readStringArray(biggestTenFileNames);
        in.readLongArray(biggestTenFileSizes);
        in.readStringArray(mostFrequentFiveExtensions);
        in.readLongArray(mostFrequentFiveExtensionsCount);
        isDone = in.readByte();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public FileInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalMB);
        dest.writeDouble(averageFileSize);

        dest.writeStringArray(biggestTenFileNames);
        dest.writeLongArray(biggestTenFileSizes);

        dest.writeLongArray(mostFrequentFiveExtensionsCount);
        dest.writeStringArray(mostFrequentFiveExtensions);
        dest.writeByte(isDone);
    }

}
