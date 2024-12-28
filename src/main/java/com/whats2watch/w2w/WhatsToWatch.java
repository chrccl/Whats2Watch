package com.whats2watch.w2w;

import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceType;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Scanner;
import java.util.logging.Logger;

public class WhatsToWatch extends Application {

    private static final Logger logger = Logger.getLogger(WhatsToWatch.class.getName());

    private static PersistenceType persistenceType; //To dynamically choose the persistence type

    private static Dispatcher dispatcher;   //To dynamically choose the UI

    @Override
    public void start(Stage stage) {
        dispatcher = new GUIDispatcher(stage);
        dispatcher.showLoginPage();
    }

    private static PersistenceType choosePersistenceType() {
        Scanner scanner = new Scanner(System.in);
        String pt;
        do{
            logger.info("Select the persistence type (DB - FS - DEMO):");
            pt = scanner.nextLine().toUpperCase();
        }while(!pt.equals("DB") && !pt.equals("FS") && !pt.equals("DEMO"));
        if(pt.equals("DB")){
            return PersistenceType.DATABASE;
        }else if(pt.equals("FS")){
            return PersistenceType.FILESYSTEM;
        }else {
            return PersistenceType.DEMO;
        }
    }

    private static boolean chooseUI(){
        Scanner scanner = new Scanner(System.in);
        String ui;
        do{
            logger.info("Select UI (CLI - GUI):");
            ui = scanner.nextLine().toUpperCase();
        }while(!ui.equals("CLI") && !ui.equals("GUI"));

        return ui.equals("GUI");
    }

    private static void launchCLI(){
        dispatcher = new CLIDispatcher();
        dispatcher.showLoginPage();
    }

    public static PersistenceType getPersistenceType(){
        return persistenceType;
    }

    public static void main(String[] args) {
        logger.info("WELCOME IN WHATS2WATCH!!!");
        persistenceType = choosePersistenceType();
        if (chooseUI())
            launch();
        else
            launchCLI();
    }
}