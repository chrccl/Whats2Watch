package com.whats2watch.w2w.model;

public interface Dispatcher {

    void showLoginPage();

    void showRegisterPage();

    void showHomePage(User user);

    void showRoomPage(User user, Genre genre);

    void showProfilePage(User user);

    void showSwipePage(User user, Room room);

    void showMatchesPage(User user, Room room);

}
