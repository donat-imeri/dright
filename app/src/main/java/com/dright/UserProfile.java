package com.dright;

import android.media.Image;

public class UserProfile {
    String fullname;
    int id, userRate, dimes;
    Image userImage;

    public void UserProfile(String fullname,int id, int userRate, int dimes, Image userImage){
        this.fullname=fullname;
        this.userImage=userImage;
        this.dimes=dimes;
        this.id=id;
        this.userRate=userRate;
    }
}
