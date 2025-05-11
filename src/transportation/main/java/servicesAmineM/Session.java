package servicesAmineM;

import entitiesAmineM.User;

public class Session {
    private static Session instance;
    private User currentUser;

    private Session() {}

    public static Session getSession(User user) {
        if (instance == null) {
            instance = new Session();
        }
        instance.currentUser = user;
        return instance;
    }

    public static User getCurrentUser() {
        return instance != null ? instance.currentUser : null;
    }

    public static void setCurrentUser(Session session) {
        if (instance == null) {
            instance = session;
        } else {
            instance.currentUser = session.currentUser;
        }
        System.out.println("Session setCurrentUser: " + (instance.currentUser != null ? instance.currentUser.getName() : "null"));
    }

    public static void clearSession() {
        System.out.println("Session cleared");
        if (instance != null) {
            instance.currentUser = null;
            instance = null;
        }
    }
}