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

    opens com.whats2watch.w2w.view.gui_graphic_controllers to javafx.fxml;
    opens com.whats2watch.w2w.model to com.google.gson;
    exports com.whats2watch.w2w;
    exports com.whats2watch.w2w.exceptions;
    exports com.whats2watch.w2w.model.dao.dao_factories;
    exports com.whats2watch.w2w.model.dao.entities;
    exports com.whats2watch.w2w.model;
    exports com.whats2watch.w2w.controllers;
}