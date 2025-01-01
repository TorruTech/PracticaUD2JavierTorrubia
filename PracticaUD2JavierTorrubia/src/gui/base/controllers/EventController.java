package gui.base.controllers;

import gui.base.Model;
import util.Util;
import gui.View;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

public class EventController {

    private View view;
    private Model model;
    private MainController controller;

    public EventController(View view, Model model, MainController controller) {
        this.view = view;
        this.model = model;
        this.controller = controller;
    }

    void deleteEvent() {

        try {
            int resp2 = Util.showConfirmDialog("¿Estás seguro de eliminar el evento?", "Eliminar");
            if (resp2 == JOptionPane.OK_OPTION) {
                model.deleteEvent((Integer) view.eventsTable.getValueAt(view.eventsTable.getSelectedRow(), 0));
                deleteEventFields();
                refreshEvents();
                Util.showSuccessDialog("Evento eliminado correctamente");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Util.showErrorAlert("Tienes que seleccionar un evento");
        }
    }

    void updateEvent() {
        try {
            if (!checkEventFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else {
                model.updateEvent(
                        view.txtEventName.getText(),
                        view.txtEventDescription.getText(),
                        view.eventDate.getDate(),
                        view.comboCategory.getSelectedItem().toString(),
                        view.txtAttendees.getText(),
                        view.txtLabels.getText(),
                        view.comboLocation.getSelectedItem().toString(),
                        view.imagePathLbl.getText(),
                        (Integer) view.eventsTable.getValueAt(view.eventsTable.getSelectedRow(), 0));
            }
        } catch (NumberFormatException nfe) {
            Util.showErrorAlert("Introduce números en los campos que lo requieren");
            return;
        }
        Util.showSuccessDialog("Evento actualizado correctamente");
        deleteEventFields();
        refreshEvents();
    }

    void addEvent() {
        try {
            if (!checkEventFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else if (model.eventNameExists(view.txtEventName.getText())) {
                Util.showErrorAlert("Ya existe un evento con ese nombre");
                return;
            } else {
                model.insertEvent(
                        view.txtEventName.getText(),
                        view.txtEventDescription.getText(),
                        view.eventDate.getDate(),
                        String.valueOf(1),
                        view.txtAttendees.getText(),
                        view.txtLabels.getText(),
                        view.comboLocation.getSelectedItem().toString(),
                        view.imagePathLbl.getText()
                );
            }
        } catch (NumberFormatException nfe) {
            Util.showErrorAlert("Introduce números en los campos que lo requieren");
            return;
        }
        Util.showSuccessDialog("Evento insertado correctamente");
        deleteEventFields();
        refreshEvents();
    }

    void refreshEvents() {
        try {
            view.eventsTable.setModel(buildTableModelEvents(model.searchEvents()));
            view.comboEvent.removeAllItems();
            for(int i = 0; i < view.dtmEvents.getRowCount(); i++) {
                view.comboEvent.addItem(view.dtmEvents.getValueAt(i, 0)+" - "+
                        view.dtmEvents.getValueAt(i, 1));
            }
            view.comboEvent.setSelectedIndex(-1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModelEvents(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        controller.setDataVector(rs, columnCount, data);

        view.dtmEvents.setDataVector(data, columnNames);

        return view.dtmEvents;

    }

    boolean checkEventFields() {
        return !view.txtEventName.getText().isEmpty() && !view.txtEventDescription.getText().isEmpty()
                && view.comboCategory.getSelectedIndex() != -1 && view.eventDate.getDate() != null
                && view.comboLocation.getSelectedIndex() != -1 && !view.txtLabels.getText().isEmpty()
                && !view.txtAttendees.getText().isEmpty() && !view.imagePathLbl.getText().isEmpty();
    }

    public void deleteEventFields() {
        view.txtEventName.setText("");
        view.txtEventDescription.setText("");
        view.comboCategory.setSelectedIndex(-1);
        view.eventDate.setDate(null);
        view.comboLocation.setSelectedIndex(-1);
        view.txtLabels.setText("");
        view.txtAttendees.setText("");
        view.imagePathLbl.setText("");
    }

    void fillEventFields() {
        int row = view.eventsTable.getSelectedRow();
        view.txtEventName.setText(String.valueOf(view.eventsTable.getValueAt(row, 1)));
        view.txtEventDescription.setText(String.valueOf(view.eventsTable.getValueAt(row, 2)));
        view.eventDate.setDate(LocalDate.parse(String.valueOf(view.eventsTable.getValueAt(row, 3))));
        view.comboCategory.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 4)));
        view.txtAttendees.setText(String.valueOf(view.eventsTable.getValueAt(row, 5)));
        view.txtLabels.setText(String.valueOf(view.eventsTable.getValueAt(row, 6)));
        view.comboLocation.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 7)));
        view.imagePathLbl.setText(String.valueOf(view.eventsTable.getValueAt(row, 8)));
    }

}
