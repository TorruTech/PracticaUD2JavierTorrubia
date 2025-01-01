package gui.base.controllers;

import gui.View;
import gui.base.Model;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Vector;

public class ActivityController {

    private View view;
    private Model model;
    private MainController controller;

    public ActivityController(View view, Model model, MainController mainController) {
        this.view = view;
        this.model = model;
        this.controller = mainController;
    }

    void deleteActivity() {

        try {
            int resp3 = Util.showConfirmDialog("¿Estás seguro de eliminar la actividad?", "Eliminar");
            if (resp3 == JOptionPane.OK_OPTION) {
                model.deleteActivity((Integer) view.activitiesTable.getValueAt(view.activitiesTable.getSelectedRow(), 0));
                deleteActivityFields();
                refreshActivities();
                Util.showSuccessDialog("Actividad eliminada correctamente");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Util.showErrorAlert("Tienes que seleccionar una actividad");
        }
    }

    void updateActivity() {
        try {
            if (!checkActivityFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else {
                model.updateActivity(
                        view.txtActivityName.getText(),
                        view.txtActivityDescription.getText(),
                        view.comboActivityType.getSelectedItem().toString(),
                        view.txtDuration.getText(),
                        view.activityStartDate.getDateTimePermissive(),
                        view.activityEndDate.getDateTimePermissive(),
                        view.txtVacants.getText(),
                        view.comboEvent.getSelectedItem().toString(),
                        (Integer) view.activitiesTable.getValueAt(view.activitiesTable.getSelectedRow(), 0));
            }
        } catch (NumberFormatException nfe) {
            Util.showErrorAlert("Introduce números en los campos que lo requieren");
            return;
        }
        Util.showSuccessDialog("Actividad actualizada correctamente");
        deleteActivityFields();
        refreshActivities();
    }

    void addActivity() {
        try {
            if (!checkActivityFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else if (model.activityNameExists(view.txtActivityName.getText())) {
                Util.showErrorAlert("Ya existe una actividad con ese nombre");
                return;
            } else {
                model.insertActivity(
                        view.txtActivityName.getText(),
                        view.txtActivityDescription.getText(),
                        view.comboActivityType.getSelectedItem().toString(),
                        view.txtDuration.getText(),
                        view.activityStartDate.getDateTimePermissive(),
                        view.activityEndDate.getDateTimePermissive(),
                        view.txtVacants.getText(),
                        view.comboEvent.getSelectedItem().toString()
                );
            }
        } catch (NumberFormatException nfe) {
            Util.showErrorAlert("Introduce números en los campos que lo requieren");
            return;
        }
        Util.showSuccessDialog("Actividad insertada correctamente");
        deleteActivityFields();
        refreshActivities();
    }

    void refreshActivities() {
        try {
            view.activitiesTable.setModel(buildTableModelActivities(model.searchActivities()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModelActivities(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Vector<String> columnNames = new Vector<>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }
            Vector<Vector<Object>> data = new Vector<>();
            controller.setDataVector(resultSet, columnCount, data);
            view.dtmActivities.setDataVector(data, columnNames);
            return view.dtmActivities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean checkActivityFields() {
        return !view.txtActivityName.getText().isEmpty() && !view.txtActivityDescription.getText().isEmpty()
                && view.comboActivityType.getSelectedIndex() != -1 && view.comboEvent.getSelectedIndex() != -1
                && view.activityStartDate.getDateTimePermissive() != null
                && view.activityEndDate.getDateTimePermissive() != null;
    }

    void deleteActivityFields() {
        view.txtActivityName.setText("");
        view.txtActivityDescription.setText("");
        view.comboActivityType.setSelectedIndex(-1);
        view.comboEvent.setSelectedIndex(-1);
        view.activityStartDate.setDateTimePermissive(null);
        view.activityEndDate.setDateTimePermissive(null);
        view.txtDuration.setText("");
        view.txtVacants.setText("");
    }

    void fillActivityFields() {
        int row = view.activitiesTable.getSelectedRow();
        view.txtActivityName.setText(String.valueOf(view.activitiesTable.getValueAt(row, 1)));
        view.txtActivityDescription.setText(String.valueOf(view.activitiesTable.getValueAt(row, 2)));
        view.comboActivityType.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 3)));
        view.comboEvent.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 4)));
        view.activityStartDate.setDateTimePermissive(LocalDateTime.parse(String.valueOf(view.activitiesTable.getValueAt(row, 5))));
        view.activityEndDate.setDateTimePermissive(LocalDateTime.parse(String.valueOf(view.activitiesTable.getValueAt(row, 6))));
        view.txtDuration.setText(String.valueOf(view.activitiesTable.getValueAt(row, 7)));
        view.txtVacants.setText(String.valueOf(view.activitiesTable.getValueAt(row, 8)));
    }

}
