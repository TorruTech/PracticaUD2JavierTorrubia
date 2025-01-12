package gui.base.models;

import java.sql.*;
import java.time.LocalDateTime;

public class ActivityModel {

    private Connection connection;

    public ActivityModel(Connection conexion) {
        this.connection = conexion;
    }

    public void insertActivity(String name, String description, String type, String duration, LocalDateTime startDate, LocalDateTime endDate, String vacants, String event) {
        String sqlSentence = "INSERT INTO activities (name, description, type, duration, start_date, end_date, vacants, id_event) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = null;

        int idEvent = Integer.parseInt(event.split(" ")[0]);

        try {
            ps = connection.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setFloat(4, Float.parseFloat(duration));
            ps.setTimestamp(5, Timestamp.valueOf(startDate));
            ps.setTimestamp(6, Timestamp.valueOf(endDate));
            ps.setInt(7, Integer.parseInt(vacants));
            ps.setInt(8, idEvent);

            ps.executeUpdate();

            System.out.println("Actividad insertada correctamente.");
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

    public void updateActivity(String name, String description, String type, String duration,
                               LocalDateTime startDate, LocalDateTime endDate, String vacants, String event, int idActivity) {
        String sqlSentence = "UPDATE activities SET name = ?, description = ?, type = ?, duration = ?, " +
                "start_date = ?, end_date = ?, vacants = ?, id_event = ? WHERE id_activity = ?";
        PreparedStatement ps = null;

        int idEvent = Integer.parseInt(event.split(" ")[0]);

        try {
            ps = connection.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setFloat(4, Float.parseFloat(duration));
            ps.setTimestamp(5, Timestamp.valueOf(startDate));
            ps.setTimestamp(6, Timestamp.valueOf(endDate));
            ps.setInt(7, Integer.parseInt(vacants));
            ps.setInt(8, idEvent);
            ps.setInt(9, idActivity);

            ps.executeUpdate();
            System.out.println("Actividad modificada correctamente.");
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

    public void deleteActivity(int idActivity) {
        String sqlSentence = "DELETE FROM activities WHERE id_activity = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sqlSentence);
            ps.setInt(1, idActivity);
            ps.executeUpdate();
            System.out.println("Actividad eliminada correctamente.");
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

    public ResultSet searchActivities() throws SQLException {
        String sqlSentence = "SELECT id_activity as 'ID', " +
                "a.name as 'Nombre', " +
                "a.description as 'Descripción', " +
                "a.type as 'Tipo', " +
                "a.duration as 'Duración', " +
                "a.start_date as 'Fecha de inicio', " +
                "a.end_date as 'Fecha de fin', " +
                "a.vacants as 'Vacantes', " +
                "concat(e.id_event, ' - ', e.name) as 'Evento' " +
                "FROM activities a INNER JOIN events e ON a.id_event = e.id_event";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = connection.prepareStatement(sqlSentence);
        rs = ps.executeQuery();
        return rs;
    }

    public boolean activityNameExists(String name) {
        String query = "SELECT existsActivityByName(?)";
        PreparedStatement function;
        boolean activityExists = false;
        try {
            function = connection.prepareStatement(query);
            function.setString(1, name);
            ResultSet rs = function.executeQuery();
            rs.next();

            activityExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activityExists;
    }

    public ResultSet filterActivities(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {

        ResultSet rs = null;

        String query = "CALL filterActivities(?, ?)";
            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setTimestamp(1, Timestamp.valueOf(startDate));
                ps.setTimestamp(2, Timestamp.valueOf(endDate));
                rs = ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return rs;
    }

    public ResultSet getActivitiesByEventId(int eventId) throws SQLException {
        String query = "SELECT * FROM activities WHERE id_event = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, eventId);
        return stmt.executeQuery();
    }

}
