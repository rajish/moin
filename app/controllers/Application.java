package controllers;

import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        System.out.println("Application.index()");
        if(session.contains("username")) {
            render();
        } else {
            redirect("/login");
        }
    }

    public static void userProfile() {
        System.out.println("Application.userProfile()");
        render();
    }
}