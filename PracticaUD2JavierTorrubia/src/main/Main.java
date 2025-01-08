package main;

import com.formdev.flatlaf.FlatLightLaf;
import gui.View;
import gui.base.controllers.MainController;
import gui.base.models.MainModel;

public class Main {

    public static void main(String[] args) {

        FlatLightLaf.setup();

        View view = new View();
        MainModel mainModel = new MainModel();
        MainController mainController = new MainController(mainModel, view);
    }
}