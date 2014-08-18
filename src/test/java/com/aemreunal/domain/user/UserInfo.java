package com.aemreunal.domain.user;

/*
 ***************************
 * Copyright (c) 2014      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

public class UserInfo {
    public String username;
    public String password;
    public Long   userId;

    public UserInfo(String username, String password, Long userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    @Override
    public String toString() {
        String info = "User Info:\n";
        info += "\tUsername: \'" + username + "\'\n";
        info += "\tPassword: \'" + password + "\'\n";
        info += "\tUser ID: \'" + userId + "\'\n";
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserInfo)) {
            return false;
        } else {
            UserInfo otherUserInfo = (UserInfo) obj;
            boolean usernameMatches = this.username.equals(otherUserInfo.username);
            boolean userIdMatches = this.userId == otherUserInfo.userId;
            return usernameMatches && userIdMatches;
        }
    }
}
