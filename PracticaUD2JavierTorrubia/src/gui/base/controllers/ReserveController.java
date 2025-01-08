package gui.base.controllers;

import gui.View;
import gui.base.models.MainModel;

public class ReserveController {
    private View view;
    private MainModel mainModel;
    private MainController mainController;

    public ReserveController(View view, MainModel mainModel, MainController mainController) {
        this.view = view;
        this.mainModel = mainModel;
        this.mainController = mainController;
    }

    public void reserveActivity() {
    }
}
