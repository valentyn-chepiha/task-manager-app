package ua.edu.sumdu.j2se.chepiha.tasks.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import jfxtras.scene.control.LocalDateTimeTextField;

public class TaskFormSet extends StatusFields {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setDisable(Node... fields){
        disableFields(true, fields);
    }

    public void setEnable(Node... fields){
        disableFields(false, fields);
    }

    public void setVisible(Node... fields) {
        visibleFields(true, fields);
    }

    public void setHide(Node... fields) {
        visibleFields(false, fields);
    }

    public void setSelected(CheckBox... fields){
        for (CheckBox field : fields) {
            field.setSelected(true);
        }
    }

    public void setUnselected(CheckBox... fields){
        for (CheckBox field : fields) {
            field.setSelected(false);
        }
    }

    public void setDateFormat(LocalDateTimeTextField... fields){
        for (LocalDateTimeTextField field : fields) {
            field.setDateTimeFormatter(DATE_FORMAT);
        }
    }

    public void setLocalDateTimeValue(LocalDateTime value, LocalDateTimeTextField... fields){
        for (LocalDateTimeTextField field : fields) {
            field.setLocalDateTime(value);
        }
    }

    public void setTextValue(String value, Label... fields){
        for (Label field : fields) {
            field.setText(value);
        }
    }

}
