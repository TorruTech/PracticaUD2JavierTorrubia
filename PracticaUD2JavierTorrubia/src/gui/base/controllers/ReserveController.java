package gui.base.controllers;

import gui.View;
import gui.base.models.ReserveModel;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class ReserveController {
    private View view;
    private ReserveModel reserveModel;
    private MainController mainController;

    public ReserveController(View view, ReserveModel reserveModel, MainController mainController) {
        this.view = view;
        this.reserveModel = reserveModel;
        this.mainController = mainController;
    }

    public void reserveActivity() {
        if (view.comboUserReserve.getSelectedItem() == null || view.comboActivityReserve.getSelectedItem() == null) {
            Util.showErrorAlert("Rellena todos los campos");
            return;
        } else if (reserveModel.reserveExists(view.comboUserReserve.getSelectedItem().toString(), view.comboActivityReserve.getSelectedItem().toString())) {
            Util.showErrorAlert("Ya has reservado esta actividad");
            return;
        } else {
            reserveModel.addReserve(view.comboUserReserve.getSelectedItem().toString(), view.comboActivityReserve.getSelectedItem().toString());
            Util.showSuccessDialog("Actividad reservada correctamente");
        }
        deleteReserveFields();
        refreshReserves();
    }

    void refreshReserves() {
        try {
            view.reservationsTable.setModel(buildTableModelReserves(reserveModel.searchReserves()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModelReserves(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Vector<String> columnNames = new Vector<>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }
            Vector<Vector<Object>> data = new Vector<>();
            mainController.setDataVector(resultSet, columnCount, data);
            view.dtmReservations.setDataVector(data, columnNames);
            return view.dtmReservations;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    void deleteReserveFields() {
        view.comboUserReserve.setSelectedIndex(-1);
        view.comboEventReserve.setSelectedIndex(-1);
        view.comboActivityReserve.setSelectedIndex(-1);
    }


    public void fillReserveFields() {
        int row = view.reservationsTable.getSelectedRow();
        view.comboUserReserve.setSelectedItem(view.reservationsTable.getValueAt(row, 1));
        view.comboEventReserve.setSelectedItem(view.reservationsTable.getValueAt(row, 2));
        view.comboActivityReserve.setSelectedItem(view.reservationsTable.getValueAt(row, 3));
    }

    public void deleteReserve() {
        try {
            int resp2 = Util.showConfirmDialog("¿Estás seguro de eliminar la reserva?", "Eliminar");
            if (resp2 == JOptionPane.OK_OPTION) {
                reserveModel.deleteReserve((Integer) view.reservationsTable.getValueAt(view.reservationsTable.getSelectedRow(), 0));
                deleteReserveFields();
                refreshReserves();
                Util.showSuccessDialog("Reserva eliminada correctamente");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Util.showErrorAlert("Tienes que seleccionar una reserva");
        }
    }
}
