package controllers;

import models.User;


public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        //return User.connect(username, password) != null;
        return true;
    }

    static void onDisconnected() {
        Application.index();
    }

    static void onAuthenticated() {
        Admin.index();
    }
    
    static boolean check(String profile) {
        if("admin".equals(profile)) {
            User user = User.find("byLogin", connected()).<User>first();
            // System.out.println("Security.check() user: " + user.login + " isAdmin: " + user.isAdmin);
            return user.isAdmin;
        }
        return false;
    }
}
