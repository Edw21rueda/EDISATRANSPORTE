package com.example.edisatransporte.RecyclerD;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String username;

    private String desc1;
    private String desc2;
    public UserModel(String username, String desc1, String desc2) {
        this.username = username;
        this.desc1 = desc1;
        this.desc2 = desc2;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
