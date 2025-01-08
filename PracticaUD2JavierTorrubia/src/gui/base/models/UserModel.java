package gui.base.models;

import java.sql.*;
import java.time.LocalDate;

public class UserModel {

    private final Connection conexion;

    public UserModel(Connection conexion) {
        this.conexion = conexion;
    }

    public void insertUser(String name, String surname, String dni, String email, LocalDate birthdate) {
        String sqlSentence = "INSERT INTO users (name, surname, dni, email, birthdate) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = null;

        try {
            ps = conexion.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, dni);
            ps.setString(4, email);
            ps.setDate(5, Date.valueOf(birthdate));

            ps.executeUpdate();

            System.out.println("Usuario insertado correctamente.");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    public void updateUser(String name, String surname, String dni, String email, LocalDate birthdate, int idUser) {
        String sqlSentence = "UPDATE users SET name = ?, surname = ?, dni = ?, email = ?, birthdate = ? WHERE id_user = ?";
        PreparedStatement ps = null;

        try {
            ps = conexion.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, dni);
            ps.setString(4, email);
            ps.setDate(5, Date.valueOf(birthdate));
            ps.setInt(6, idUser);

            ps.executeUpdate();
            System.out.println("Usuario modificado correctamente.");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    public void deleteUser(int idUser) {
        String sqlSentence = "DELETE FROM users WHERE id_user = ?";
        PreparedStatement ps = null;

        try {
            ps = conexion.prepareStatement(sqlSentence);
            ps.setInt(1, idUser);
            ps.executeUpdate();
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    public ResultSet searchUsers() throws SQLException {
        String sqlSentence = "SELECT id_user as 'ID', " +
                "name as 'Nombre', " +
                "surname as 'Apellidos', " +
                "dni as 'DNI', " +
                "email as 'Email', " +
                "birthdate as 'Fecha de nacimiento' " +
                "FROM users";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = conexion.prepareStatement(sqlSentence);
        rs = ps.executeQuery();
        return rs;
    }

    public boolean userDniExists(String dni) {
        String query = "SELECT existsUserByDni(?)";
        PreparedStatement ps;
        boolean userExists = false;
        try {
            ps = conexion.prepareStatement(query);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            rs.next();

            userExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    public boolean userEmailExists(String email) {
        String query = "SELECT existsUserByEmail(?)";
        PreparedStatement ps;
        boolean userExists = false;
        try {
            ps = conexion.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();

            userExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }
}
