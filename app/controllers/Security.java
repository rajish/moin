package controllers;

import models.User;


public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        System.out.println("Security.authenticate()");
        return User.connect(username, password) != null;
        //return true;
    }

    static void onDisconnected() {
        System.out.println("Security.onDisconnected()");
        Invoices.index();
    }

    static void onAuthenticated() {
        System.out.println("Security.onAuthenticated()");
        Invoices.index();
    }
    
    static boolean check(String profile) {
        System.out.println("Security.check("+profile+")");
        try {
            User user = User.find("byLogin", connected()).<User>first();
            System.out.println("Security.check() user: " + connected() + " isAdmin: " + user.isAdmin);
            if("admin".equals(profile)) {
                return user.isAdmin;
            }
            if("user".equals(profile)) {
                return !user.isAdmin;
            }
        }catch (Exception e) {
            System.out.println("Security.check() user: " + connected() + "  Exception: " + e);
            
        }
        return false;
    }
}
