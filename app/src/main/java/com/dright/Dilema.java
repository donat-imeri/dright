package com.dright;

import java.util.List;

public class Dilema {
    private String dilemaDescription;
    private List<String> dilemaOptions;
    private List<String> dilemaCategories;
    private List<Integer> optionsResults;
    private String dilemaAsker;
    private int dilemaPriority;
    private long dilemaTimeOut;
    private boolean stayAnonymous;

    public Dilema(){

    }

    public Dilema(String dilemaDescription, List<String> dilemaOptions, List<String> dilemaCategories,
                  String dilemaAsker, int dilemaPriority, long dilemaTimeOut, boolean stayAnonymous, List<Integer> optionResults) {
        this.dilemaDescription = dilemaDescription;
        this.dilemaOptions = dilemaOptions;
        this.dilemaCategories = dilemaCategories;
        this.dilemaAsker = dilemaAsker;
        this.dilemaPriority = dilemaPriority;
        this.dilemaTimeOut = dilemaTimeOut;
        this.stayAnonymous = stayAnonymous;
        this.optionsResults=optionResults;
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

    public String getDilemaAsker() {
        return dilemaAsker;
    }

    public void setDilemaAsker(String dilemaAsker) {
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

    public List<Integer> getOptionsResults() {
        return optionsResults;
    }

    public void setOptionsResults(List<Integer> optionsResults) {
        this.optionsResults = optionsResults;
    }

}
