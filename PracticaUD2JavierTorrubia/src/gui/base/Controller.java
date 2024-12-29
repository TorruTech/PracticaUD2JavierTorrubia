package gui.base;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import gui.View;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Controller implements ActionListener, ItemListener, ListSelectionListener, WindowListener {

    private Model model;
    private View view;
    boolean refresh;
    private boolean darkMode = true;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        model.connect();
        setOptions();
        addActionListeners(this);
        addItemListeners(this);
        addWindowListeners(this);
        refreshAll();
        start();
    }
    
    private void setOptions() {
        view.optionDialog.txtIP.setText(model.getIp());
        view.optionDialog.txtUser.setText(model.getUser());
        view.optionDialog.pfPass.setText(model.getPassword());
        view.optionDialog.pfAdmin.setText(model.getAdminPassword());
    }

    private void refreshAll() {
        refreshUsers();
        refreshActivities();
        refreshEvents();
    }

    private void refreshUsers() {

    }

    private void refreshActivities() {
    }

    private void refreshEvents() {
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
                        view.comboCategory.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 3)));
                        view.eventDate.setDate(LocalDate.parse(String.valueOf(view.eventsTable.getValueAt(row, 4))));
                        view.comboLocation.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 5)));
                        view.txtLabels.setText(String.valueOf(view.eventsTable.getValueAt(row, 6)));
                        view.txtAttendees.setText(String.valueOf(view.eventsTable.getValueAt(row, 7)));
                    } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty()
                    && !refresh) {
                        if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            deleteEventFields();
                        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            deleteActivityFields();
                        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                            deleteUserFields();
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
                        view.comboEvent.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 4)));
                        view.activityStartDate.setDateTimePermissive(LocalDateTime.parse(String.valueOf(view.activitiesTable.getValueAt(row, 5))));
                        view.activityEndDate.setDateTimePermissive(LocalDateTime.parse(String.valueOf(view.activitiesTable.getValueAt(row, 6))));
                        view.txtDuration.setText(String.valueOf(view.activitiesTable.getValueAt(row, 7)));
                        view.txtVacants.setText(String.valueOf(view.activitiesTable.getValueAt(row, 8)));
                    } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty()
                    && !refresh) {
                        if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                            deleteEventFields();
                        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            deleteActivityFields();
                        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                            deleteUserFields();
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
                        int row = view.usersTable.getSelectedRow();
                        view.txtUserName.setText(String.valueOf(view.usersTable.getValueAt(row, 1)));
                        view.txtUserSurname.setText(String.valueOf(view.usersTable.getValueAt(row, 2)));
                        view.txtDNI.setText(String.valueOf(view.usersTable.getValueAt(row, 3)));
                        view.txtEmail.setText(String.valueOf(view.usersTable.getValueAt(row, 4)));
                        view.birthDate.setDate(LocalDate.parse(String.valueOf(view.usersTable.getValueAt(row, 5))));
                    } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty()
                    && !refresh) {
                        if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                            deleteEventFields();
                        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                            deleteActivityFields();
                        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                            deleteUserFields();
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
                if(String.valueOf(view.adminPassword.getPassword()).equals(model.getAdminPassword())) {
                    view.adminPassword.setText("");
                    view.adminPasswordDialog.dispose();
                    view.optionDialog.setVisible(true);
                } else {
                    Util.showErrorAlert("La contraseña introducida no es correcta.");
                }
                break;
            case "saveOptions":
                model.setPropValues(view.optionDialog.txtIP.getText(), view.optionDialog.txtUser.getText(),
                        String.valueOf(view.optionDialog.pfPass.getPassword()), String.valueOf(view.optionDialog.pfAdmin.getPassword()));
                view.optionDialog.dispose();
                view.dispose();
                new Controller(new Model(), new View());
                break;
            case "addEvent":
                try {
                    if (checkEventFields()) {
                        Util.showErrorAlert("Rellena todos los campos");
                        view.eventsTable.clearSelection();
                    } else if (model.eventNameExists(view.txtEventTitle.getText())) {
                        Util.showErrorAlert("Ya existe un evento con ese nombre");
                        view.eventsTable.clearSelection();
                    } else {

                        String imagePath = view.imagePathLbl.getText();

                        model.insertEvent(
                                view.txtEventTitle.getText(),
                                view.txtEventDescription.getText(),
                                view.eventDate.getDate(),
                                view.comboCategory.getSelectedItem().toString(),
                                view.comboLocation.getSelectedItem().toString(),
                                view.txtLabels.getText(),
                                view.txtAttendees.getText(),
                                imagePath
                                );
                    }
                } catch (NumberFormatException nfe) {
                    Util.showErrorAlert("Introduce números en los campos que lo requieren");
                    view.eventsTable.clearSelection();
                }
                deleteEventFields();
                refreshEvents();
                break;
            case "Cargar Imagen":
                uploadImage();
                break;
            case "Modo Oscuro":
            case "Modo Claro":
                this.toggleTheme();
                break;

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

    private void addItemListeners(Controller controller) {
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
                int row = view.eventsTable.getSelectedRow();
                view.txtEventTitle.setText(String.valueOf(view.eventsTable.getValueAt(row, 1)));
                view.txtEventDescription.setText(String.valueOf(view.eventsTable.getValueAt(row, 2)));
                view.comboCategory.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 3)));
                view.eventDate.setDate(LocalDate.parse(String.valueOf(view.eventsTable.getValueAt(row, 4))));
                view.comboLocation.setSelectedItem(String.valueOf(view.eventsTable.getValueAt(row, 5)));
                view.txtLabels.setText(String.valueOf(view.eventsTable.getValueAt(row, 6)));
                view.txtAttendees.setText(String.valueOf(view.eventsTable.getValueAt(row, 7)));
            }
        } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                int row = view.activitiesTable.getSelectedRow();
                view.txtActivityName.setText(String.valueOf(view.activitiesTable.getValueAt(row, 1)));
                view.txtActivityDescription.setText(String.valueOf(view.activitiesTable.getValueAt(row, 2)));
                view.comboActivityType.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 3)));
                view.comboEvent.setSelectedItem(String.valueOf(view.activitiesTable.getValueAt(row, 4)));
                view.activityStartDate.setDateTimePermissive(LocalDateTime.parse(String.valueOf(view.activitiesTable.getValueAt(row, 5))));
                view.activityEndDate.setDateTimePermissive(LocalDateTime.parse(String.valueOf(view.activitiesTable.getValueAt(row, 6))));
                view.txtDuration.setText(String.valueOf(view.activitiesTable.getValueAt(row, 7)));
                view.txtVacants.setText(String.valueOf(view.activitiesTable.getValueAt(row, 8)));
        } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                int row = view.usersTable.getSelectedRow();
                view.txtUserName.setText(String.valueOf(view.usersTable.getValueAt(row, 1)));
                view.txtUserSurname.setText(String.valueOf(view.usersTable.getValueAt(row, 2)));
                view.txtDNI.setText(String.valueOf(view.usersTable.getValueAt(row, 3)));
                view.txtEmail.setText(String.valueOf(view.usersTable.getValueAt(row, 4)));
                view.birthDate.setDate(LocalDate.parse(String.valueOf(view.usersTable.getValueAt(row, 5))));
        } else if (e.getValueIsAdjusting() && ((ListSelectionModel) e.getSource()).isSelectionEmpty() && !refresh) {
            if (e.getSource().equals(view.eventsTable.getSelectionModel())) {
                deleteEventFields();
            } else if (e.getSource().equals(view.activitiesTable.getSelectionModel())) {
                deleteActivityFields();
            } else if (e.getSource().equals(view.usersTable.getSelectionModel())) {
                deleteUserFields();
            }
        }
    }

    private boolean checkEventFields() {
        return !view.txtEventTitle.getText().isEmpty() && !view.txtEventDescription.getText().isEmpty()
                && view.comboCategory.getSelectedIndex() != -1 && view.eventDate.getDate() != null
                && view.comboLocation.getSelectedIndex() != -1;
    }

    private boolean checkActivityFields() {
        return !view.txtActivityName.getText().isEmpty() && !view.txtActivityDescription.getText().isEmpty()
                && view.comboActivityType.getSelectedIndex() != -1 && view.comboEvent.getSelectedIndex() != -1
                && view.activityStartDate.getDateTimePermissive() != null
                && view.activityEndDate.getDateTimePermissive() != null;
    }

    private boolean checkUserFields() {
        return !view.txtUserName.getText().isEmpty() && !view.txtUserSurname.getText().isEmpty()
                && !view.txtDNI.getText().isEmpty() && !view.txtEmail.getText().isEmpty()
                && view.birthDate.getDate() != null;
    }

    private void deleteEventFields() {
        view.txtEventTitle.setText("");
        view.txtEventDescription.setText("");
        view.comboCategory.setSelectedIndex(-1);
        view.eventDate.setDate(null);
        view.comboLocation.setSelectedItem(-1);
        view.txtLabels.setText("");
        view.txtAttendees.setText("");
    }

    private void deleteActivityFields() {
        view.txtActivityName.setText("");
        view.txtActivityDescription.setText("");
        view.comboActivityType.setSelectedIndex(-1);
        view.comboEvent.setSelectedIndex(-1);
        view.activityStartDate.setDateTimePermissive(null);
        view.activityEndDate.setDateTimePermissive(null);
        view.txtDuration.setText("");
        view.txtVacants.setText("");
    }

    private void deleteUserFields() {
        view.txtUserName.setText("");
        view.txtUserSurname.setText("");
        view.txtDNI.setText("");
        view.txtEmail.setText("");
        view.birthDate.setDate(null);
    }

    public void toggleTheme() {
        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                view.btnToggleTheme.setText("Modo Claro");
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                view.btnToggleTheme.setText("Modo Oscuro");
            }
            SwingUtilities.updateComponentTreeUI(view);
            darkMode = !darkMode;
        } catch (Exception ex) {
            System.err.println("Error aplicando los estilos");
        }
    }

}

