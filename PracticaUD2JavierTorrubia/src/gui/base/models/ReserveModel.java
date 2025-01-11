package gui.base.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReserveModel {

    private final Connection connection;

    public ReserveModel(Connection connection) {
        this.connection = connection;
    }

    public void addReserve(String user, String activity) {

        int idUser = Integer.parseInt(user.split(" ")[0]);
        int idActivity = Integer.parseInt(activity.split(" ")[0]);

        String sqlSentence = "INSERT INTO reservations (id_user, id_activity) " +
                "VALUES (?, ?)";

        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sqlSentence);
            ps.setInt(1, idUser);
            ps.setInt(2, idActivity);

            ps.executeUpdate();

            System.out.println("Reserva insertada correctamente.");
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

    public ResultSet searchReserves() throws SQLException {
        String sqlSentence = "SELECT id_reservation as 'ID', " +
                "concat(u.id_user, ' - ', u.name, ' ', u.surname) as 'Usuario', " +
                "concat(a.id_activity, ' - ', a.name) as 'Actividad', " +
                "r.reservation_date as 'Fecha de reserva' " +
                "FROM reservations r " +
                "INNER JOIN users u ON r.id_user = u.id_user " +
                "INNER JOIN activities a ON r.id_activity = a.id_activity";
        PreparedStatement sentence;
        ResultSet rs;
        sentence = connection.prepareStatement(sqlSentence);
        rs = sentence.executeQuery();
        return rs;
    }


    public void deleteReserve(int idReserve) {
        String sqlSentence = "DELETE FROM reservations WHERE id_reservation = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sqlSentence);
            ps.setInt(1, idReserve);
            ps.executeUpdate();
            System.out.println("Reserva eliminada correctamente.");
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

    public boolean reserveExists(String user, String activity) {
        String idUser = user.split(" ")[0];
        String idActivity = activity.split(" ")[0];

        String query = "SELECT existsReserve(?, ?)";
        PreparedStatement ps;
        boolean reserveExists = false;
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, idUser);
            ps.setString(2, idActivity);
            ResultSet rs = ps.executeQuery();
            rs.next();

            reserveExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reserveExists;
    }
}
