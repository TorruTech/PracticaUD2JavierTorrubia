package gui.base.controllers;

import gui.View;
import gui.base.Model;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

public class UserController {

    private View view;
    private Model model;
    private MainController mainController;

    public UserController(View view, Model model, MainController mainController) {
        this.view = view;
        this.model = model;
        this.mainController = mainController;
    }

    void deleteUser() {

        try {
            int resp2 = Util.showConfirmDialog("¿Estás seguro de eliminar el usuario?", "Eliminar");
            if (resp2 == JOptionPane.OK_OPTION) {
                model.deleteUser((Integer) view.usersTable.getValueAt(view.usersTable.getSelectedRow(), 0));
                deleteUserFields();
                refreshUsers();
                Util.showSuccessDialog("Usuario eliminado correctamente");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Util.showErrorAlert("Tienes que seleccionar un usuario");
        }
    }

    void updateUser() {
        try {
            if (!checkUserFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else {
                model.updateUser(
                        view.txtUserName.getText(),
                        view.txtUserSurname.getText(),
                        view.txtDNI.getText(),
                        view.txtEmail.getText(),
                        view.birthDate.getDate(),
                        (Integer) view.usersTable.getValueAt(view.usersTable.getSelectedRow(), 0));
            }
        } catch (NumberFormatException nfe) {
            Util.showErrorAlert("Introduce números en los campos que lo requieren");
            return;
        }
        Util.showSuccessDialog("Usuario actualizado correctamente");
        deleteUserFields();
        refreshUsers();
    }

    void addUser() {
        try {
            if (!checkUserFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else if (model.userDniExists(view.txtDNI.getText())) {
                Util.showErrorAlert("Ya existe un usuario con ese DNI");
                return;
            } else if (model.userEmailExists(view.txtEmail.getText())) {
                Util.showErrorAlert("Ya existe un usuario con ese email");
                return;
            } else {
                model.insertUser(
                        view.txtUserName.getText(),
                        view.txtUserSurname.getText(),
                        view.txtDNI.getText(),
                        view.txtEmail.getText(),
                        view.birthDate.getDate()
                );
            }
        } catch (NumberFormatException nfe) {
            Util.showErrorAlert("Introduce números en los campos que lo requieren");
            return;
        }
        Util.showSuccessDialog("Usuario insertado correctamente");
        deleteUserFields();
        refreshUsers();
    }

    void refreshUsers() {
        try {
            view.usersTable.setModel(buildTableModelUsers(model.searchUsers()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModelUsers(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Vector<String> columnNames = new Vector<>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }
            Vector<Vector<Object>> data = new Vector<>();
            mainController.setDataVector(resultSet, columnCount, data);
            view.dtmUsers.setDataVector(data, columnNames);
            return view.dtmUsers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean checkUserFields() {
        return !view.txtUserName.getText().isEmpty() && !view.txtUserSurname.getText().isEmpty()
                && !view.txtDNI.getText().isEmpty() && !view.txtEmail.getText().isEmpty()
                && view.birthDate.getDate() != null;
    }

    void deleteUserFields() {
        view.txtUserName.setText("");
        view.txtUserSurname.setText("");
        view.txtDNI.setText("");
        view.txtEmail.setText("");
        view.birthDate.setDate(null);
    }
    void fillUserFields() {
        int row = view.usersTable.getSelectedRow();
        view.txtUserName.setText(String.valueOf(view.usersTable.getValueAt(row, 1)));
        view.txtUserSurname.setText(String.valueOf(view.usersTable.getValueAt(row, 2)));
        view.txtDNI.setText(String.valueOf(view.usersTable.getValueAt(row, 3)));
        view.txtEmail.setText(String.valueOf(view.usersTable.getValueAt(row, 4)));
        view.birthDate.setDate(LocalDate.parse(String.valueOf(view.usersTable.getValueAt(row, 5))));
    }
}
