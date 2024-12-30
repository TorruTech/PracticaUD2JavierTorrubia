package gui.base.controllers;

import gui.View;
import gui.base.Model;
import util.Util;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Vector;

public class MainController implements ActionListener, ItemListener, ListSelectionListener, WindowListener {

    private Model model;
    private View view;
    private EventController eventController;
    private ActivityController activityController;
    private UserController userController;
    boolean refresh;

    public MainController(Model model, View view) {
        this.model = model;
        this.view = view;
        this.eventController = new EventController(view, model, this);
        this.activityController = new ActivityController(view, model, this);
        this.userController = new UserController(view, model, this);
        model.connect();
        setOptions();
        addActionListeners(this);
        addItemListeners(this);
        addWindowListeners(this);
        refreshAll();
        start();
    }

    void setDataVector(ResultSet rs, int columnCount, Vector<Vector<Object>> data) throws SQLException {
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
    }
    
    private void setOptions() {
        view.optionDialog.txtIP.setText(model.getIp());
        view.optionDialog.txtUser.setText(model.getUser());
        view.optionDialog.pfPass.setText(model.getPassword());
        view.optionDialog.pfAdmin.setText(model.getAdminPassword());
    }

    private void refreshAll() {
        userController.refreshUsers();
        activityController.refreshActivities();
        eventController.refreshEvents();
    }

    private void addActionListeners(ActionListener listener) {
        view.btnImageLoad.addActionListener(listener);
        view.btnToggleTheme.addActionListener(listener);
        view.btnEventsAdd.addActionListener(listener);
        view.btnEventsAdd.setActionCommand("addEvent");
        view.btnEventsUpdate.addActionListener(listener);
        view.btnEventsUpdate.setActionCommand("updateEvent");
        view.btnEventsDelete.addActionListener(listener);
        view.btnEventsDelete.setActionCommand("deleteEvent");
        view.btnActivitiesAdd.addActionListener(listener);
        view.btnActivitiesAdd.setActionCommand("addActivity");
        view.btnActivitiesUpdate.addActionListener(listener);
        view.btnActivitiesUpdate.setActionCommand("updateActivity");
        view.btnActivitiesDelete.addActionListener(listener);
        view.btnActivitiesDelete.setActionCommand("deleteActivity");
        view.btnUsersAdd.addActionListener(listener);
        view.btnUsersAdd.setActionCommand("addUser");
        view.btnUsersUpdate.addActionListener(listener);
        view.btnUsersUpdate.setActionCommand("updateUser");
        view.btnUsersDelete.addActionListener(listener);
        view.btnUsersDelete.setActionCommand("deleteUser");
        view.itemDisconnect.addActionListener(listener);
        view.itemOptions.addActionListener(listener);
        view.itemExit.addActionListener(listener);
        view.btnValidate.addActionListener(listener);
        view.optionDialog.btnSaveOptions.addActionListener(listener);
        view.optionDialog.btnSaveOptions.setActionCommand("saveOptions");
    }

    private void addWindowListeners(WindowListener listener) {
        view.addWindowListener(listener);
    }

    void start() {
        view.eventsTable.setCellSelectionEnabled(true);
        ListSelectionModel cellSelectionModel =  view.eventsTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !((ListSelectionModel) e.getSource()).isSelectionEmpty()) {

                    if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                        int row = view.eventsTable.getSelectedRow();
                        view.txtEventTitle.setText(String.valueOf(view.eventsTable.getValueAt(row, 1)));
                        view.txtEventDescription.setText(String.valueOf(view.eventsTable.getValueAt(row, 2)));
                        view.eventDate.setDate(LocalDate.parse(String.valueOf(view.eventsTable.getValueAt(row, 3))));
                        view.comboCategory.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 4)));
                        view.txtLabels.setText(String.valueOf(view.eventsTable.getValueAt(row, 5)));
                        view.txtAttendees.setText(String.valueOf(view.eventsTable.getValueAt(row, 6)));
                        view.comboLocation.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 7)));
                        view.imagePathLbl.setText(String.valueOf(view.eventsTable.getValueAt(row, 8)));
                    } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty()
                    && !refresh) {
                        if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            eventController.deleteEventFields();
                        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            activityController.deleteActivityFields();
                        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                            userController.deleteUserFields();
                        }

                    }
                }
            }
        });

        view.activitiesTable.setCellSelectionEnabled(true);
        ListSelectionModel cellSelectionModel2 =  view.activitiesTable.getSelectionModel();
        cellSelectionModel2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cellSelectionModel2.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !((ListSelectionModel) e.getSource()).isSelectionEmpty()) {

                    if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                        int row = view.activitiesTable.getSelectedRow();
                        view.txtActivityName.setText(String.valueOf(view.activitiesTable.getValueAt(row, 1)));
                        view.txtActivityDescription.setText(String.valueOf(view.activitiesTable.getValueAt(row, 2)));
                        view.comboActivityType.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 3)));
                        view.txtDuration.setText(String.valueOf(view.activitiesTable.getValueAt(row, 4)));

                        String startDateString = String.valueOf(view.activitiesTable.getValueAt(row, 5));
                        String endDateString = String.valueOf(view.activitiesTable.getValueAt(row, 6));

                        try {
                            if (startDateString != null && !startDateString.isEmpty()) {
                                Timestamp startTimestamp = Timestamp.valueOf(startDateString);
                                view.activityStartDate.setDateTimePermissive(startTimestamp.toLocalDateTime());
                            }

                            if (endDateString != null && !endDateString.isEmpty()) {
                                Timestamp endTimestamp = Timestamp.valueOf(endDateString);
                                view.activityEndDate.setDateTimePermissive(endTimestamp.toLocalDateTime());
                            }
                        } catch (IllegalArgumentException ex) {
                            System.err.println("Error parseando la fecha: " + ex.getMessage());
                            view.activityStartDate.setDateTimePermissive(null);
                            view.activityEndDate.setDateTimePermissive(null);
                        }

                        view.txtVacants.setText(String.valueOf(view.activitiesTable.getValueAt(row, 7)));
                        view.comboEvent.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 8)));
                    } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty() && !refresh) {
                        if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                            eventController.deleteEventFields();
                        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            activityController.deleteActivityFields();
                        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                            userController.deleteUserFields();
                        }
                    }
                }
            }
        });

        view.usersTable.setCellSelectionEnabled(true);
        ListSelectionModel cellSelectionModel3 =  view.usersTable.getSelectionModel();
        cellSelectionModel3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cellSelectionModel3.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !((ListSelectionModel) e.getSource()).isSelectionEmpty()) {

                    if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                        userController.fillUserFields();
                    } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty()
                    && !refresh) {
                        if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                            eventController.deleteEventFields();
                        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            activityController.deleteActivityFields();
                        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                            userController.deleteUserFields();
                        }

                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Opciones":
                view.adminPasswordDialog.setVisible(true);
                break;
            case "Desconectar":
                model.disconnect();
                break;
            case "Salir":
                int resp = Util.showConfirmDialog("¿Desea salir de la aplicación?", "Salir");
                if (resp == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
                break;
            case "abrirOpciones":
                openOptions();
                break;
            case "saveOptions":
                saveOptions();
                break;
            case "addEvent":
                eventController.addEvent();
                break;
            case "addActivity":
                activityController.addActivity();
                break;
            case "addUser":
                userController.addUser();
                break;
            case "updateEvent":
                eventController.updateEvent();
                break;
            case "updateActivity":
                activityController.updateActivity();
                break;
            case "updateUser":
                userController.updateUser();
                break;
            case "deleteEvent":
                eventController.deleteEvent();
                break;
            case "deleteActivity":
                activityController.deleteActivity();
                break;
            case "deleteUser":
                userController.deleteUser();
                break;
            case "Cargar Imagen":
                uploadImage();
                break;
            case "Modo Oscuro":
            case "Modo Claro":
                view.toggleTheme();
                break;

        }
    }

    private void saveOptions() {
        model.setPropValues(view.optionDialog.txtIP.getText(), view.optionDialog.txtUser.getText(),
                String.valueOf(view.optionDialog.pfPass.getPassword()), String.valueOf(view.optionDialog.pfAdmin.getPassword()));
        view.optionDialog.dispose();
        view.dispose();
        new MainController(new Model(), new View());
    }

    private void openOptions() {
        if(String.valueOf(view.adminPassword.getPassword()).equals(model.getAdminPassword())) {
            view.adminPassword.setText("");
            view.adminPasswordDialog.dispose();
            view.optionDialog.setVisible(true);
        } else {
            Util.showErrorAlert("La contraseña introducida no es correcta.");
        }
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imagen");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String targetDir = "assets/images/";
            String targetPath = targetDir + selectedFile.getName();

            new File(targetDir).mkdirs();

            try {
                Files.copy(selectedFile.toPath(), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Util.showErrorAlert("Error al cargar la imagen: " + e.getMessage());
            }

            view.imagePathLbl.setText(targetPath);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }

    private void addItemListeners(MainController controller) {
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        int resp = Util.showConfirmDialog("¿Desea salir de la aplicación?", "Salir");
        if (resp == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !((ListSelectionModel) e.getSource()).isSelectionEmpty()) {
            if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                eventController.fillEventFields();
            }
        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                activityController.fillActivityFields();
        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                userController.fillUserFields();
        } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty() && !refresh) {
            if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                eventController.deleteEventFields();
            } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                activityController.deleteActivityFields();
            } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                userController.deleteUserFields();
            }
        }
    }

}

