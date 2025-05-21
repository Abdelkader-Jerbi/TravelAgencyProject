package entities;

public class Session {
    private static Utilisateur loggedInUser;

    // Private constructor to prevent instantiation
    private Session() {}

    // Get the logged-in user
    public static Utilisateur getLoggedInUser() {
        return loggedInUser;
    }

    // Set the logged-in user
    public static void setLoggedInUser(Utilisateur user) {
        loggedInUser = user;
    }
}
