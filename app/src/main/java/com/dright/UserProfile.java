package com.dright;

public class UserProfile {
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserRate() {
        return userRate;
    }

    public void setUserRate(int userRate) {
        this.userRate = userRate;
    }

    public Docent getDocents() {
        return docents;
    }

    public void setDocents(Docent docents) {
        this.docents = docents;
    }

    private String fullname, email;
    int userRate;
    Docent docents;

    public UserProfile(String email, String fullname, int userRate, Docent Docents){
        this.fullname=fullname;
        this.email=email;
        this.docents=Docents;
        this.userRate=userRate;
    }

    public UserProfile(){

    }
}
