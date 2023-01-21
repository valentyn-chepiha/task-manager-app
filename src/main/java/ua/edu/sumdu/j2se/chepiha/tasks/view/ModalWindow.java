package ua.edu.sumdu.j2se.chepiha.tasks.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ModalWindow {

    public static void showAlertError(String msgError) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Task fields");
        alert.setHeaderText(null);
        alert.setContentText(msgError);
        alert.showAndWait();
    }

    public static void showAlertInformation(String msgError) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tasks list");
        alert.setHeaderText(null);
        alert.setContentText(msgError);
        alert.showAndWait();
    }

    public static boolean showConfirmDelete(String msgError) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msgError, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Task delete");
        alert.setHeaderText("Do you want to delete:");
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

}
