package com.community.weare.Models;

public class Page {
    int index;
    int size;

    public Page() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void increment(){
        this.index+=1;
    }

}
