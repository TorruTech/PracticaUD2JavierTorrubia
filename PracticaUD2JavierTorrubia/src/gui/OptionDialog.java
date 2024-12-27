package gui;

import javax.swing.*;
import java.awt.*;

public class OptionDialog extends JDialog{
    private JPanel panel1;
    JTextField txtIP;
    JTextField txtUsuario;
    JButton btnOpcionesGuardar;
    JPasswordField pfPass;
    JPasswordField pfAdmin;
    private Frame owner;

    public OptionDialog(Frame owner) {
        //modal true, desactiva el resto de ventanas
        super(owner,"Opciones",true);
        this.owner=owner;
        initDialog();
    }
    private void initDialog() {
        this.setContentPane(panel1);
        //creamos espacios
        this.panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        //si funciona el cerrar pero no cierra la aplicacion
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        //doy dimension
        this.setSize(new Dimension(this.getWidth()+200,this.getHeight()));
        //lo colocamos en owner
        this.setLocationRelativeTo(owner);
    }
}
