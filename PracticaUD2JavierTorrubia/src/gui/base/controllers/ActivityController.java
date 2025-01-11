package gui.base.controllers;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import gui.View;
import gui.base.models.ActivityModel;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Vector;

public class ActivityController {

    private View view;
    private ActivityModel activityModel;
    private MainController controller;

    public ActivityController(View view, ActivityModel activityModel, MainController mainController) {
        this.view = view;
        this.activityModel = activityModel;
        this.controller = mainController;
    }

    void deleteActivity() {

        try {
            int resp3 = Util.showConfirmDialog("¿Estás seguro de eliminar la actividad?", "Eliminar");
            if (resp3 == JOptionPane.OK_OPTION) {
                activityModel.deleteActivity((Integer) view.activitiesTable.getValueAt(view.activitiesTable.getSelectedRow(), 0));
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
                activityModel.updateActivity(
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
            } else if (activityModel.activityNameExists(view.txtActivityName.getText())) {
                Util.showErrorAlert("Ya existe una actividad con ese nombre");
                return;
            } else if (view.activityStartDate.getDateTimePermissive().isAfter(view.activityEndDate.getDateTimePermissive())) {
                Util.showWarningAlert("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            } else if (view.activityStartDate.getDateTimePermissive().getDayOfYear() !=
                view.activityEndDate.getDateTimePermissive().getDayOfYear()) {
                Util.showWarningAlert("Las actividades se realizan en el mismo día");
                return;
            } else {
                activityModel.insertActivity(
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
            view.activitiesTable.setModel(buildTableModelActivities(activityModel.searchActivities()));
            view.comboActivityReserve.removeAllItems();
            for (int i = 0; i < view.dtmActivities.getRowCount(); i++) {
                view.comboActivityReserve.addItem(view.dtmActivities.getValueAt(i, 0) + " - " +
                        view.dtmActivities.getValueAt(i, 1));
            }
            view.comboActivityReserve.setSelectedIndex(-1);
            view.activitiesTable.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int row = view.activitiesTable.rowAtPoint(e.getPoint());
                    int column = view.activitiesTable.columnAtPoint(e.getPoint());
                    if (row > -1 && column > -1) {
                        Object value = view.activitiesTable.getValueAt(row, column);
                        if (value != null) {
                            view.activitiesTable.setToolTipText(value.toString());
                        }
                    }
                }
            });
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

    void filterActivities() {

        JPanel filterPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 20, 5, 20);

        JLabel startDateLabel = new JLabel("Fecha de inicio:");
        DateTimePicker starDateDTP = new DateTimePicker();
        JLabel endDateLabel = new JLabel("Fecha de fin:");
        DateTimePicker endDateDTP = new DateTimePicker();

        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(startDateLabel, gbc);

        gbc.gridx = 1;
        fieldsPanel.add(starDateDTP, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(endDateLabel, gbc);

        gbc.gridx = 1;
        fieldsPanel.add(endDateDTP, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton filterButton = new JButton("Filtrar");
        buttonPanel.add(filterButton);

        filterPanel.add(fieldsPanel, BorderLayout.CENTER);
        filterPanel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame)null, "Filtrar actividades", true);
        dialog.getContentPane().add(filterPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(starDateDTP, endDateDTP, dialog);
            }
        });

        dialog.setVisible(true);
    }

    private void applyFilter(DateTimePicker starDateDTP, DateTimePicker endDateDTP, JDialog dialog) {
        try {
            LocalDateTime startDate = starDateDTP.getDateTimePermissive();
            LocalDateTime endDate = endDateDTP.getDateTimePermissive();

            if (startDate == null || endDate == null) {
                Util.showErrorAlert("Debes seleccionar una fecha de inicio y una fecha de fin");
                return;
            } else if (startDate.isAfter(endDate)) {
                Util.showErrorAlert("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            } if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
                Util.showErrorAlert("La fecha de inicio y la fecha de fin deben estar en el mismo día");
                return;
            }
            view.activitiesTable.setModel(buildTableModelActivities(activityModel.filterActivities(startDate, endDate)));
            dialog.dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
