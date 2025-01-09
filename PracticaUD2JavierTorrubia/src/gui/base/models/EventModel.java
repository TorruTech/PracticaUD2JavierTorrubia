package gui.base.models;

import com.google.gson.Gson;

import java.sql.*;
import java.time.LocalDate;

public class EventModel {

    private Connection conexion;

    public EventModel(Connection conexion) {
        this.conexion = conexion;
    }

    public void insertEvent(String name, String description, LocalDate date, String idCategory,
                            String attendees, String labels, String location, String image) {
        String sqlSentence = "INSERT INTO events (name, description, date, id_category, attendees, labels, location, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;

        Gson gson = new Gson();
        String labelsJson = gson.toJson(labels);

        try {
            ps = conexion.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, Date.valueOf(date));
            ps.setInt(4, Integer.parseInt(idCategory));
            ps.setInt(5, Integer.parseInt(attendees));
            ps.setString(6, labelsJson);
            ps.setString(7, location);
            ps.setString(8, image);

            ps.executeUpdate();

            System.out.println("Evento insertado correctamente.");
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

    public void updateEvent(String name, String description, LocalDate date, String category,
                            String attendees, String labels, String location, String image, int idEvent) {
        String sqlSentence = "UPDATE events SET name = ?, description = ?, date = ?, id_category = ?, " +
                "attendees = ?, labels = ?, location = ?, image = ? WHERE id_event = ?";
        PreparedStatement ps = null;

        int idCategory = Integer.parseInt(category.split(" ")[0]);

        Gson gson = new Gson();
        String labelsJson = gson.toJson(labels);

        try {
            ps = conexion.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, Date.valueOf(date));
            ps.setInt(4, idCategory);
            ps.setInt(5, Integer.parseInt(attendees));
            ps.setString(6, labelsJson);
            ps.setString(7, location);
            ps.setString(8, image);
            ps.setInt(9, idEvent);

            ps.executeUpdate();
            System.out.println("Evento modificado correctamente.");
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

    public void deleteEvent(int idEvent) {
        String sqlSentence = "DELETE FROM events WHERE id_event = ?";
        PreparedStatement sentence = null;

        try {
            sentence = conexion.prepareStatement(sqlSentence);
            sentence.setInt(1, idEvent);
            sentence.executeUpdate();
            System.out.println("Evento eliminado correctamente.");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentence != null) {
                try {
                    sentence.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    public ResultSet searchEvents() throws SQLException {
        String sqlSentence = "SELECT id_event as 'ID', " +
                "e.name as 'Nombre', " +
                "e.description as 'Descripción', " +
                "e.date as 'Fecha', " +
                "concat(c.id_category, ' - ', c.name) as 'Categoría', " +
                "e.attendees as 'Asistentes', " +
                "e.labels as 'Etiquetas', " +
                "e.location as 'Ubicación', " +
                "e.image as 'Imagen' " +
                "FROM events e INNER JOIN categories c ON e.id_category = c.id_category";
        PreparedStatement sentence;
        ResultSet rs;
        sentence = conexion.prepareStatement(sqlSentence);
        rs = sentence.executeQuery();
        return rs;
    }

    public boolean eventNameExists(String name) {
        String query = "SELECT existsEventByName(?)";
        PreparedStatement ps;
        boolean eventExists = false;

        try {
            ps = conexion.prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();

            eventExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventExists;
    }

    public ResultSet orderEventsByDate(String option) throws SQLException {

        ResultSet rs = null;

        if (option.equalsIgnoreCase("ASC")) {
            String query = "CALL orderEventsAsc()";
            try {
                PreparedStatement ps = conexion.prepareStatement(query);
                rs = ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        } else if (option.equalsIgnoreCase("DESC")) {
            String query = "CALL orderEventsDesc()";
            try {
                PreparedStatement ps = conexion.prepareStatement(query);
                rs = ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Opción no válida. Debe ser 'ASC' o 'DESC'.");
        }

        return rs;
    }
}
