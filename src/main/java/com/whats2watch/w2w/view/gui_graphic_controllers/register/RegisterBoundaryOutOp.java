package com.whats2watch.w2w.view.gui_graphic_controllers.register;

import javafx.scene.control.Alert;

public interface RegisterBoundaryOutOp {

    void showAlert(Alert.AlertType alertType, String title, String message);

    void clearForm();

}
