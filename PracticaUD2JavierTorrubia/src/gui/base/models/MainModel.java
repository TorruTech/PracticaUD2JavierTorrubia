package gui.base.models;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

public class MainModel {

    private String ip;
    private String user;
    private String password;
    private String adminPassword;
    private Connection connection;
    private final EventModel eventModel;
    private final ActivityModel activityModel;
    private final UserModel userModel;
    private final ReserveModel reserveModel;

    public MainModel() {
        getPropValues();
        connect();
        eventModel = new EventModel(connection);
        activityModel = new ActivityModel(connection);
        userModel = new UserModel(connection);
        reserveModel = new ReserveModel(connection);
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
    public EventModel getEventModel() {
        return eventModel;
    }
    public ActivityModel getActivityModel() {
        return activityModel;
    }
    public UserModel getUserModel() {
        return userModel;
    }
    public ReserveModel getReserveModel() {
        return reserveModel;
    }

    public void connect() {

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://"+ip+":3306/eventdb",user, password);
        } catch (SQLException sqle) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://"+ip+":3306/",user, password);

                PreparedStatement statement = null;

                String code = readFile();
                String[] query = code.split("--");
                for (String aQuery : query) {
                    statement = connection.prepareStatement(aQuery);
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

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("main/resources/basedatos_java.sql");
        if (inputStream == null) {
            throw new FileNotFoundException("Archivo 'basedatos_java.sql' no encontrado en el classpath.");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append(" ");
        }
        reader.close();
        return stringBuilder.toString();
    }

    public void disconnect() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void insertCategory(String name, String description) {
        String sqlSentence = "INSERT INTO categories (name, description) " +
                "VALUES (?, ?)";

        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sqlSentence);
            ps.setString(1, name);
            ps.setString(2, description);

            ps.executeUpdate();

            System.out.println("Categoría insertada correctamente.");
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

    public ResultSet searchCategories() throws SQLException {
        String sqlSentence = "SELECT id_category as 'ID', " +
                "name as 'Nombre', " +
                "description as 'Descripción' " +
                "FROM categories";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = connection.prepareStatement(sqlSentence);
        rs = ps.executeQuery();
        return rs;
    }

    private void getPropValues() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "main/resources/config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
                ip = prop.getProperty("ip");
                user = prop.getProperty("user");
                password = prop.getProperty("pass");
                adminPassword = prop.getProperty("admin");
            } else {
                throw new FileNotFoundException("Archivo de configuración '" + propFileName + "' no encontrado en el classpath.");
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPropValues(String ip, String user, String pass, String adminPass) {
        try {
            Properties prop = new Properties();
            prop.setProperty("ip", ip);
            prop.setProperty("user", user);
            prop.setProperty("pass", pass);
            prop.setProperty("admin", adminPass);
            OutputStream out = new FileOutputStream("main/resources/config.properties");
            prop.store(out, null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.ip = ip;
        this.user = user;
        this.password = pass;
        this.adminPassword = adminPass;
    }

    public boolean categoryNameExists(String name) {
        String query = "SELECT existsCategoryByName(?)";
        PreparedStatement ps;
        boolean categoryExists = false;
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();

            categoryExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryExists;
    }

}
