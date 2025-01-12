package gui.base.controllers;

import gui.base.models.ActivityModel;
import gui.base.models.EventModel;
import util.Util;
import gui.View;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

public class EventController {

    private View view;
    private EventModel eventModel;
    private ActivityController activityController;
    private MainController controller;

    public EventController(View view, EventModel eventModel, ActivityController activityController, MainController controller) {
        this.view = view;
        this.eventModel = eventModel;
        this.activityController = activityController;
        this.controller = controller;
    }

    void deleteEvent() {

        try {
            int resp2 = Util.showConfirmDialog("¿Estás seguro de eliminar el evento?", "Eliminar");
            if (resp2 == JOptionPane.OK_OPTION) {
                eventModel.deleteEvent((Integer) view.eventsTable.getValueAt(view.eventsTable.getSelectedRow(), 0));
                deleteEventFields();
                refreshEvents();
                activityController.deleteActivityFields();
                activityController.refreshActivities();
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
                eventModel.updateEvent(
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
            } else if (eventModel.eventNameExists(view.txtEventName.getText())) {
                Util.showErrorAlert("Ya existe un evento con ese nombre");
                return;
            } else {
                eventModel.insertEvent(
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
            view.eventsTable.setModel(buildTableModelEvents(eventModel.searchEvents()));
            view.eventsTable.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int row = view.eventsTable.rowAtPoint(e.getPoint());
                    int column = view.eventsTable.columnAtPoint(e.getPoint());
                    if (row > -1 && column > -1) {
                        Object value = view.eventsTable.getValueAt(row, column);
                        if (value != null) {
                            view.eventsTable.setToolTipText(value.toString());
                        }
                    }
                }
            });

            view.comboEventReserve.addActionListener(e -> updateActivitiesForSelectedEvent());

            view.comboEvent.removeAllItems();
            for(int i = 0; i < view.dtmEvents.getRowCount(); i++) {
                view.comboEvent.addItem(view.dtmEvents.getValueAt(i, 0)+" - "+
                        view.dtmEvents.getValueAt(i, 1));
            }
            view.comboEvent.setSelectedIndex(-1);

            view.comboEventReserve.removeAllItems();
            for(int i = 0; i < view.dtmEvents.getRowCount(); i++) {
                view.comboEventReserve.addItem(view.dtmEvents.getValueAt(i, 0)+" - "+
                        view.dtmEvents.getValueAt(i, 1));
            }
            view.comboEventReserve.setSelectedIndex(-1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateActivitiesForSelectedEvent() {

        String selectedEvent = (String) view.comboEventReserve.getSelectedItem();
        if (selectedEvent == null || selectedEvent.isEmpty()) {
            return;
        }

        int eventId = Integer.parseInt(selectedEvent.split(" - ")[0]);

        try {

            ResultSet activitiesResultSet = activityController.getActivitiesByEventId(eventId);

            view.comboActivityReserve.removeAllItems();

            while (activitiesResultSet.next()) {
                String activityName = activitiesResultSet.getString("id_activity") + " - " + activitiesResultSet.getString("name");
                view.comboActivityReserve.addItem(activityName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Util.showErrorAlert("Error al cargar las actividades");
        }
    }

    private DefaultTableModel buildTableModelEvents(ResultSet rs) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();

        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int column = 1; column <= columnCount; column++) {
                row.add(rs.getObject(column));
            }
            data.add(row);
        }

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

    public void orderEventsAsc() {
        try {
            view.eventsTable.setModel(buildTableModelEvents(eventModel.orderEventsByDate("ASC")));
        } catch (SQLException e) {
            e.printStackTrace();
            Util.showErrorAlert("Error al ordenar los eventos");
        }
    }

    public void orderEventsDesc() {
        try {
            view.eventsTable.setModel(buildTableModelEvents(eventModel.orderEventsByDate("DESC")));
        } catch (SQLException e) {
            e.printStackTrace();
            Util.showErrorAlert("Error al ordenar los eventos");
        }
    }

}
