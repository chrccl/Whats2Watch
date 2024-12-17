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

    opens com.whats2watch.w2w.controllers to javafx.fxml;
    opens com.whats2watch.w2w.model to com.google.gson;
    exports com.whats2watch.w2w;
}