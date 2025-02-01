module com.whats2watch.w2w {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.json;
    requires org.slf4j;
    requires com.google.gson;
    requires java.sql;
    requires java.desktop;

    exports com.whats2watch.w2w;
    exports com.whats2watch.w2w.exceptions;
    exports com.whats2watch.w2w.model.dao.dao_factories;
    exports com.whats2watch.w2w.model.dao.entities;
    exports com.whats2watch.w2w.model.dto.beans;
    exports com.whats2watch.w2w.model;
    exports com.whats2watch.w2w.controllers;

    opens com.whats2watch.w2w.model to com.google.gson;
    opens com.whats2watch.w2w.view.gui_graphic_controllers.register to javafx.fxml;
    opens com.whats2watch.w2w.view.gui_graphic_controllers.homepage to javafx.fxml;
    opens com.whats2watch.w2w.view.gui_graphic_controllers.matches to javafx.fxml;
    opens com.whats2watch.w2w.view.gui_graphic_controllers.room to javafx.fxml;
    opens com.whats2watch.w2w.view.gui_graphic_controllers.swipe to javafx.fxml;
    opens com.whats2watch.w2w.view.gui_graphic_controllers.profile to javafx.fxml;
}