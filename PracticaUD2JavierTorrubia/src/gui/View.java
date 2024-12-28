package gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import gui.base.enums.Locations;

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
    DatePicker eventDate;
    JComboBox comboLocation;
    JTextField txtLabels;
    JTextField txtAttendees;
    JButton btnImageLoad;
    JButton btnToggleTheme;
    JButton btnEventsDelete;
    JButton btnEventsUpdate;
    JButton btnEventsAdd;
    JTable eventsTable;

    //Activities
    JPanel JPanelActivity;
    JTextField txtActivityName;
    JTextField txtActivityDescription;
    JComboBox comboActivityType;
    JComboBox comboEvent;
    DateTimePicker activityStartDate;
    DateTimePicker activityEndDate;
    JTextField txtDuration;
    JTextField txtVacants;
    JButton btnActivitiesAdd;
    JButton btnActivitiesUpdate;
    JButton btnActivitiesDelete;
    JTable activitiesTable;

    //Users
    JPanel JPanelUser;
    JTextField txtUserName;
    JTextField txtUserSurname;
    JTextField txtDNI;
    JTextField txtEmail;
    DatePicker birthDate;
    JButton btnUsersAdd;
    JButton btnUsersUpdate;
    JButton btnUserDelete;
    JTable usersTable;

    //Reserves
    JComboBox comboUserReserve;
    JComboBox comboEventReserve;
    JComboBox comboActivityReserve;
    JButton btnReserveActivity;

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

        ImageIcon icon = new ImageIcon("logo.png");
        this.setIconImage(icon.getImage());

        initFrame();
    }

    private void initFrame() {
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(new Dimension(this.getWidth()+10,this.getHeight()-30));
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

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
            comboLocation.addItem(location.getValue());
        }

        comboLocation.setSelectedIndex(-1);
    }

    private void setTableModels() {
        this.dtmEvents = new DefaultTableModel();
        this.eventsTable.setModel(dtmEvents);

        this.dtmUsers = new DefaultTableModel();
        this.usersTable.setModel(dtmUsers);

        this.dtmActivities = new DefaultTableModel();
        this.activitiesTable.setModel(dtmActivities);
    }

}
