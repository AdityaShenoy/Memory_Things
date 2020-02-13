package com.brainy_things.memory_things;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

public class Card implements Serializable, Comparable<Card> {
    // These are the members which store the card information
    private String category, ques, ans, desc;

    // These store the information about the priority of the card
    private int priority;

    // This is the number of priorities that the application support
    private static final int NO_OF_PRIORITIES = 10;

    // Constructor to initialize the contents of the card
    Card(String category, String ques, String ans, String desc, int priority) {
        this.category = category;
        this.ques = ques;
        this.ans = ans;
        this.desc = desc;
        this.priority = priority;
    }

    // Higher priority number has higher priority
    @Override
    public int compareTo(@NonNull Card c) {
        return c.priority - this.priority;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US, "%s\n%s\n%s\n%s\n%d", category, ques, ans, desc, priority);
    }

    // Following methods are the getter and setter methods for the above private members
    String getCategory() {
        return category;
    }

    String getQues() {
        return ques;
    }

    String getAns() {
        return ans;
    }

    String getDesc() {
        return desc;
    }

    int getPriority() {
        return priority;
    }

    void setCategory(String category) {
        this.category = category;
    }

    void setQues(String ques) {
        this.ques = ques;
    }

    void setAns(String ans) {
        this.ans = ans;
    }

    void setDesc(String desc) {
        this.desc = desc;
    }

    void setPriority(int priority) {
        this.priority = Math.min(Math.max(0, priority), Card.NO_OF_PRIORITIES);
    }

    void setHighestPriority() {
        this.priority = NO_OF_PRIORITIES + 1;
    }

}
