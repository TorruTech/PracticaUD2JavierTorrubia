package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import gui.base.enums.Types;
import gui.base.enums.Locations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class View extends JFrame{
    private JTabbedPane JPanel;
    private JPanel panel1;
    private final static String TITLE = "Gestión de Eventos";
    boolean isDarkMode = true;

    //Events
    public JPanel JPanelEvent;
    public JTextField txtEventName;
    public JTextField txtEventDescription;
    public JComboBox comboCategory;
    public DatePicker eventDate;
    public JComboBox comboLocation;
    public JTextField txtLabels;
    public JTextField txtAttendees;
    public JButton btnImageLoad;
    public JLabel imagePathLbl;
    public JButton btnToggleTheme;
    public JButton btnEventsDelete;
    public JButton btnEventsUpdate;
    public JButton btnEventsAdd;
    public JButton btnEventsClearFields;
    public JButton btnEventsOrder;
    public JTable eventsTable;

    //Activities
    public JPanel JPanelActivity;
    public JTextField txtActivityName;
    public JTextField txtActivityDescription;
    public JComboBox comboActivityType;
    public JComboBox comboEvent;
    public DateTimePicker activityStartDate;
    public DateTimePicker activityEndDate;
    public JTextField txtDuration;
    public JTextField txtVacants;
    public JButton btnActivitiesAdd;
    public JButton btnActivitiesUpdate;
    public JButton btnActivitiesDelete;
    public JButton limpiarCamposButton;
    public JButton btnActivityFilter;
    public JTable activitiesTable;

    //Users
    public JPanel JPanelUser;
    public JTextField txtUserName;
    public JTextField txtUserSurname;
    public JTextField txtDNI;
    public JTextField txtEmail;
    public DatePicker birthDate;
    public JButton btnUsersAdd;
    public JButton btnUsersUpdate;
    public JButton btnUsersDelete;
    public JTable usersTable;

    //Reserves
    public JPanel JPanelReserve;
    public JComboBox comboUserReserve;
    public JComboBox comboEventReserve;
    public JComboBox comboActivityReserve;
    public JButton btnReserveActivity;
    public JTable reservationsTable;
    public JButton btnDeleteReserve;
    public JTable reservesSearchTable;
    public JButton btnSearchReserves;

    //Categories
    public JPanel JPanelCategory;
    public JTextField txtCategoryName;
    public JTextField txtCategoryDescription;
    public JButton btnAddCategory;
    public JTable categoriesTable;

    //default table model
    public DefaultTableModel dtmEvents;
    public DefaultTableModel dtmActivities;
    public DefaultTableModel dtmUsers;
    public DefaultTableModel dtmCategories;
    public DefaultTableModel dtmReservations;
    public DefaultTableModel dtmReservesSearch;

    //menubar
    public JMenuItem itemOptions;
    public JMenuItem itemDisconnect;
    public JMenuItem itemExit;

    //cuadro dialogo
    public OptionDialog optionDialog;
    public JDialog adminPasswordDialog;
    public JButton btnValidate;
    public JPasswordField adminPassword;

    public View() {
        super(TITLE);

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("main/resources/logo.png"));
        this.setIconImage(icon.getImage());

        initFrame();
    }

    private void initFrame() {
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

        for (Types category: Types.values()) {
            comboActivityType.addItem(category.getValue());
        }

        comboActivityType.setSelectedIndex(-1);
    }

    private void setTableModels() {
        this.dtmEvents = new DefaultTableModel();
        this.eventsTable.setModel(dtmEvents);

        this.dtmUsers = new DefaultTableModel();
        this.usersTable.setModel(dtmUsers);

        this.dtmActivities = new DefaultTableModel();
        this.activitiesTable.setModel(dtmActivities);

        this.dtmCategories = new DefaultTableModel();
        this.categoriesTable.setModel(dtmCategories);

        this.dtmReservations = new DefaultTableModel();
        this.reservationsTable.setModel(dtmReservations);

        this.dtmReservesSearch = new DefaultTableModel();
        this.reservesSearchTable.setModel(dtmReservesSearch);

    }

    public void toggleTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                btnToggleTheme.setText("Modo Claro");
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                btnToggleTheme.setText("Modo Oscuro");
            }
            SwingUtilities.updateComponentTreeUI(this);
            isDarkMode = !isDarkMode;
        } catch (Exception ex) {
            System.err.println("Error aplicando los estilos");
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
