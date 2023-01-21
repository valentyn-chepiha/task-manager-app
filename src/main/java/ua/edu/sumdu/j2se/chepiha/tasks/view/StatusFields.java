package ua.edu.sumdu.j2se.chepiha.tasks.view;

import javafx.scene.Node;

public class StatusFields {

    protected void visibleFields(Boolean flag, Node... fields ){
        for (Node field : fields) {
            field.setVisible(flag);
        }
    }

    protected void disableFields(Boolean flag, Node... fields ){
        for (Node field : fields) {
            field.setDisable(flag);
        }
    }

}
