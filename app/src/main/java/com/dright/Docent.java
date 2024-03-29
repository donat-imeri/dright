package com.dright;

import java.io.Serializable;

public class Docent implements Serializable {
    private int amount;

    public Docent(){

    }

    public Docent(int amount) {
        this.amount = amount;
    }

    public boolean addDocent(int amount){
        if(amount >0) {
            this.amount += amount;
            return true;
        }
        else{
            return false;
        }

    }

    public boolean removeDocent(int amount){
        if (checkRemove(amount)){
            this.amount -= amount;
            return true;
        }
        else return false;
    }

    public boolean checkRemove(int amount){
        if( this.amount >=0 && this.amount >=amount)
        {
            return true;
        }
        else{
            return false;
        }
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String toString(){
        return amount+"";
    }
}
