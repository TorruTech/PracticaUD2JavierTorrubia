package main;

import com.formdev.flatlaf.FlatLightLaf;
import gui.View;
import gui.base.controllers.MainController;
import gui.base.Model;

public class Main {

    public static void main(String[] args) {

        FlatLightLaf.setup();

        View view = new View();
        Model model = new Model();
        MainController mainController = new MainController(model, view);
    }
}