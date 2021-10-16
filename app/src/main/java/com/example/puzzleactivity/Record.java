package com.example.puzzleactivity;

public class Record {
    private int count;
    private int time;

    public Record(int count, int time) {
        this.count = count;
        this.time = time;
    }

    public Record() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
