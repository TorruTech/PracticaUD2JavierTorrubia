package gui.base;

import com.google.gson.Gson;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

public class Model {

    private String ip;
    private String user;
    private String password;
    private String adminPassword;

    public Model() {
        getPropValues();
    }

    public String getIp() {
        return ip;
    }
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }
    public String getAdminPassword() {
        return adminPassword;
    }

    private Connection conexion;

    void connect() {

        try {
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://"+ip+":3306/eventdb",user, password);
        } catch (SQLException sqle) {
            try {
                conexion = DriverManager.getConnection(
                        "jdbc:mysql://"+ip+":3306/",user, password);

                PreparedStatement statement = null;

                String code = readFile();
                String[] query = code.split("--");
                for (String aQuery : query) {
                    statement = conexion.prepareStatement(aQuery);
                    statement.executeUpdate();
                }
                assert statement != null;
                statement.close();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("basedatos_java.sql")) ;
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    void disconnect() {
        try {
            conexion.close();
            conexion = null;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    void insertEvent(String name, String description, LocalDate date, String idCategory,
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

    void insertActivity(String name, String description, String type, String duration, LocalDateTime startDate, LocalDateTime endDate, String vacants, String event) {
        String sqlSentence = "INSERT INTO activities (name, description, type, duration, start_date, end_date, vacants, id_event) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = null;

        int idEvent = Integer.parseInt(event.split(" ")[0]);

        try {
            ps = conexion.prepareStatement(sqlSentence);
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

    void insertUser(String name, String surname, String dni, String email, LocalDate birthdate) {
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

    void updateEvent(String name, String description, LocalDate date, String idCategory,
                         String attendees, String labels, String location, String image, int idEvent) {
        String sqlSentence = "UPDATE events SET name = ?, description = ?, date = ?, id_category = ?, " +
                "attendees = ?, labels = ?, location = ?, image = ? WHERE id_event = ?";
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

    void updateActivity(String name, String description, String type, float duration,
                            LocalDate startDate, LocalDate endDate, String event, int idActivity) {
        String sqlSentence = "UPDATE activities SET name = ?, description = ?, type = ?, duration = ?, " +
                "start_date = ?, end_date = ?, id_event = ? WHERE id_activity = ?";
        PreparedStatement ps = null;

        int idEvent = Integer.parseInt(event.split(" ")[0]);

        try {
            ps = conexion.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setFloat(4, duration);
            ps.setDate(5, Date.valueOf(startDate));
            ps.setDate(6, Date.valueOf(endDate));
            ps.setInt(7, idEvent);
            ps.setInt(8, idActivity);

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

    void updateUser(String name, String surname, String dni, String email, LocalDate birthdate, int idUser) {
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

    void deleteEvent(int idEvent) {
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

    void deleteActivity(int idActivity) {
        String sqlSentence = "DELETE FROM activities WHERE id_activity = ?";
        PreparedStatement ps = null;

        try {
            ps = conexion.prepareStatement(sqlSentence);
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

    void deleteUser(int idUser) {
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

    ResultSet searchEvents() throws SQLException {
        String sqlSentence = "SELECT id_event as 'ID', " +
                "name as 'Nombre', " +
                "description as 'Descripción', " +
                "date as 'Fecha', " +
                "id_category as 'Categoría', " +
                "attendees as 'Asistentes', " +
                "labels as 'Etiquetas', " +
                "location as 'Ubicación', " +
                "image as 'Imagen' " +
                "FROM events";
        PreparedStatement sentence = null;
        ResultSet rs = null;
        sentence = conexion.prepareStatement(sqlSentence);
        rs = sentence.executeQuery();
        return rs;
    }

    ResultSet searchActivities() throws SQLException {
        String sqlSentence = "SELECT id_activity as 'ID', " +
                "name as 'Nombre', " +
                "description as 'Descripción', " +
                "type as 'Tipo', " +
                "duration as 'Duración', " +
                "start_date as 'Fecha de inicio', " +
                "end_date as 'Fecha de fin', " +
                "vacants as 'Vacantes', " +
                "id_event as 'ID del evento' " +
                "FROM activities";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = conexion.prepareStatement(sqlSentence);
        rs = ps.executeQuery();
        return rs;
    }

    ResultSet searchUsers() throws SQLException {
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

    private void getPropValues() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = new FileInputStream(propFileName);

            prop.load(inputStream);
            ip = prop.getProperty("ip");
            user = prop.getProperty("user");
            password = prop.getProperty("pass");
            adminPassword = prop.getProperty("admin");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setPropValues(String ip, String user, String pass, String adminPass) {
        try {
            Properties prop = new Properties();
            prop.setProperty("ip", ip);
            prop.setProperty("user", user);
            prop.setProperty("pass", pass);
            prop.setProperty("admin", adminPass);
            OutputStream out = new FileOutputStream("config.properties");
            prop.store(out, null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.ip = ip;
        this.user = user;
        this.password = pass;
        this.adminPassword = adminPass;
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

    public boolean activityNameExists(String name) {
        String query = "SELECT existsActivityByName(?)";
        PreparedStatement function;
        boolean activityExists = false;
        try {
            function = conexion.prepareStatement(query);
            function.setString(1, name);
            ResultSet rs = function.executeQuery();
            rs.next();

            activityExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activityExists;
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
