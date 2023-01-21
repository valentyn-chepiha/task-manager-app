package ua.edu.sumdu.j2se.chepiha.tasks.controller;

import ua.edu.sumdu.j2se.chepiha.tasks.view.Observer;
import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListState;

public interface Observable {
    void registerObserver(Observer o);

    void notifyObservers(ListState state);
}
