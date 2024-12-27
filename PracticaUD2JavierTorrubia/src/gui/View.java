package gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import gui.enums.Locations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class View extends JFrame{
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private final static String TITLE = "Gestión de Eventos";

    //Events
    JPanel JPanelEvent;
    JTextField txtEventTitle;
    JTextField txtEventDescription;
    JComboBox comboCategory;
    JTextField txtLocation;
    DatePicker eventDate;
    JTextField txtImage;
    JButton btnEventsDelete;
    JButton btnEventsModify;
    JButton btnEventsAdd;
    JTable eventsTable;

    //Activities
    JPanel JPanelActivity;
    JTextField txtActivityName;
    JTextField txtActivityDescription;
    DateTimePicker activityStartDate;
    DateTimePicker activityEndDate;
    JTextField txtDuration;
    JButton btnActivitiesAdd;
    JButton btnActivitiesModify;
    JButton btnActivitiesDelete;
    JTable activitiesTable;

    //Users
    JPanel JPanelUser;
    JTextField txtUserName;
    JTextField txtUserSurname;
    JTextField txtDNI;
    JTextField birthDate;
    JTextField txtEmail;
    JButton btnUsersAdd;
    JButton btnUsersModify;
    JButton btnUserDelete;
    JTable usersTable;

    //busqueda
    JLabel etiquetaEstado;

    //default table model
    DefaultTableModel dtmEvents;
    DefaultTableModel dtmActivities;
    DefaultTableModel dtmUsers;

    //menubar
    JMenuItem itemOptions;
    JMenuItem itemDisconnect;
    JMenuItem itemExit;

    //cuadro dialogo
    OptionDialog optionDialog;
    JDialog adminPasswordDialog;
    JButton btnValidate;
    JPasswordField adminPassword;

    public View() {
        super(TITLE);

        this.setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setSize(new Dimension(this.getWidth()+10,this.getHeight()));
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        optionDialog = new OptionDialog(this);

        setMenu();
        setAdminDialog();
        setEnumComboBox();
        setTableModels();
    }

    private void setMenu() {
        JMenuBar mbBar = new JMenuBar();
        JMenu menu = new JMenu("Archivo");
        itemOptions = new JMenuItem("Opciones");
        itemOptions.setActionCommand("Opciones");
        itemDisconnect = new JMenuItem("Desconectar");
        itemDisconnect.setActionCommand("Desconectar");
        itemExit =new JMenuItem("Salir");
        itemExit.setActionCommand("Salir");
        menu.add(itemOptions);
        menu.add(itemDisconnect);
        menu.add(itemExit);
        mbBar.add(menu);
        mbBar.add(Box.createHorizontalGlue());
        this.setJMenuBar(mbBar);
    }

    private void setAdminDialog() {
        btnValidate = new JButton("Validar");
        btnValidate.setActionCommand("abrirOpciones");
        adminPassword = new JPasswordField();
        adminPassword.setPreferredSize(new Dimension(100,26));
        Object[] options = new Object[] {adminPassword,btnValidate};
        JOptionPane jop = new JOptionPane("Introduce la contraseña",JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_OPTION,null,options);
        adminPasswordDialog = new JDialog(this,"Opciones",true);
        adminPasswordDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        adminPasswordDialog.setContentPane(jop);
        adminPasswordDialog.pack();
        adminPasswordDialog.setLocationRelativeTo(this);
    }

    private void setEnumComboBox() {

        for (Locations location: Locations.values()) {
            comboCategory.addItem(location);
        }

        comboCategory.setSelectedIndex(-1);
    }

    private void setTableModels() {
        this.dtmEvents =new DefaultTableModel();
        this.eventsTable.setModel(dtmEvents);

        this.dtmUsers = new DefaultTableModel();
        this.usersTable.setModel(dtmUsers);

        this.dtmActivities =new DefaultTableModel();
        this.activitiesTable.setModel(dtmActivities);
    }

}
