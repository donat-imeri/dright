package com.dright;

public class UserProfile {
    public String getName() {
        return name;
    }

    public void setName(String fullname) {
        this.name = fullname;
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

    private String name, email;
    int userRate;
    Docent docents;

    public UserProfile(String email, String name, int userRate, Docent Docents){
        this.name=name;
        this.email=email;
        this.docents=Docents;
        this.userRate=userRate;
    }

    public UserProfile(){

    }
}
