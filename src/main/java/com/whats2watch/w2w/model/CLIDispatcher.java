package com.whats2watch.w2w.model;

import com.whats2watch.w2w.view.cli_view.HomePageView;
import com.whats2watch.w2w.view.cli_view.RegisterView;
import com.whats2watch.w2w.view.cli_view.RoomView;
import com.whats2watch.w2w.view.cli_view.SwipeView;

public class CLIDispatcher implements Dispatcher{

    @Override
    public void showLoginPage() {
        RegisterView.showMenu(this);
    }

    @Override
    public void showRegisterPage() {
        RegisterView.showMenu(this);
    }

    @Override
    public void showHomePage(User user) {
        HomePageView.setMainApp(this, user);
    }

    @Override
    public void showRoomPage(User user, Genre genre) {
        RoomView.showMenu(this, user, genre);
    }

    @Override
    public void showProfilePage(User user) {
        System.out.println("Profile Page");
        System.out.println(user);
    }

    @Override
    public void showSwipePage(User user, Room room) {
        SwipeView.initPage(this, user, room);
    }

    @Override
    public void showMatchesPage(User user, Room room) {
        //Resolved in the Swipe Page
        System.out.println("Matches Page");
    }
}
