module com.whats2watch.w2w {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.whats2watch.w2w to javafx.fxml;
    exports com.whats2watch.w2w;
}