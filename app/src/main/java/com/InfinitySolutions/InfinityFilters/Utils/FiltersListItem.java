package com.InfinitySolutions.InfinityFilters.Utils;

public class FiltersListItem {

    private String mName;
    private int mFilterPreviewImageId;

    public FiltersListItem(String name,int filterPreviewImageId){
        mName = name;
        mFilterPreviewImageId = filterPreviewImageId;
    }

    public String getName(){
        return mName;
    }

    public int getFilterPreviewImageId(){
        return mFilterPreviewImageId;
    }
}
