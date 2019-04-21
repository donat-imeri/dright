package com.dright;

import java.util.List;

public class Dilema {
    private String dilemaDescription;
    private List<String> dilemaOptions;
    private List<String> dilemaCategories;
    private UserProfile dilemaAsker;
    private int dilemaPriority;
    private long dilemaTimeOut;
    private boolean stayAnonymous;
    private boolean dilemaText;

    public Dilema(String dilemaDescription, List<String> dilemaOptions, List<String> dilemaCategories, UserProfile dilemaAsker, int dilemaPriority, long dilemaTimeOut, boolean stayAnonymous, boolean dilemaText) {
        this.dilemaDescription = dilemaDescription;
        this.dilemaOptions = dilemaOptions;
        this.dilemaCategories = dilemaCategories;
        this.dilemaAsker = dilemaAsker;
        this.dilemaPriority = dilemaPriority;
        this.dilemaTimeOut = dilemaTimeOut;
        this.stayAnonymous = stayAnonymous;
        this.dilemaText = dilemaText;
    }

    public boolean isDilemaText() {
        return dilemaText;
    }

    public void setDilemaText(boolean dilemaText) {
        this.dilemaText = dilemaText;
    }

    public String getDilemaDescription() {
        return dilemaDescription;
    }

    public void setDilemaDescription(String dilemaDescription) {
        this.dilemaDescription = dilemaDescription;
    }

    public List<String> getDilemaOptions() {
        return dilemaOptions;
    }

    public void setDilemaOptions(List<String> dilemaOptions) {
        this.dilemaOptions = dilemaOptions;
    }

    public List<String> getDilemaCategories() {
        return dilemaCategories;
    }

    public void setDilemaCategories(List<String> dilemaCategories) {
        this.dilemaCategories = dilemaCategories;
    }

    public UserProfile getDilemaAsker() {
        return dilemaAsker;
    }

    public void setDilemaAsker(UserProfile dilemaAsker) {
        this.dilemaAsker = dilemaAsker;
    }

    public int getDilemaPriority() {
        return dilemaPriority;
    }

    public void setDilemaPriority(int dilemaPriority) {
        this.dilemaPriority = dilemaPriority;
    }

    public long getDilemaTimeOut() {
        return dilemaTimeOut;
    }

    public void setDilemaTimeOut(long dilemaTimeOut) {
        this.dilemaTimeOut = dilemaTimeOut;
    }

    public boolean isStayAnonymous() {
        return stayAnonymous;
    }

    public void setStayAnonymous(boolean stayAnonymous) {
        this.stayAnonymous = stayAnonymous;
    }
}
