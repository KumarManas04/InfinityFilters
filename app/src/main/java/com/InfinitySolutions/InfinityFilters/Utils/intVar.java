package com.InfinitySolutions.InfinityFilters.Utils;

public class intVar {
    private int intVariable = 0;
    private changeListener listener;

    public void setVal(int newValue){
        intVariable = newValue;
        if(newValue == 1) {
            listener.onChange();
        }
    }

    public int getValue(){
        return intVariable;
    }

    public void setListener(changeListener listener){
        this.listener = listener;
    }

    public interface changeListener{
        void onChange();
    }
}
