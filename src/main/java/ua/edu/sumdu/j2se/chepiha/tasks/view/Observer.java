package ua.edu.sumdu.j2se.chepiha.tasks.view;

import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListState;

public interface Observer {
    void notification(ListState state);
}
