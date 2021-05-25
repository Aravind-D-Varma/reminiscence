package com.example.nostalgia;

import android.content.Context;

public class UserDetails {
    private static UserDetails sUserDetails;
    private final String mUserName;

    public static UserDetails getUserDetails(String username){
        if(sUserDetails==null)
            sUserDetails = new UserDetails(username);
        return sUserDetails;
    }
    private UserDetails(String username){
        mUserName = username;
    }

    public String getUserName() {
        return mUserName;
    }

}
