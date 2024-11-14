package steps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class MonitoringDatabaseUtil {
    private static final String URL = "jdbc:postgresql://dbMonitoring:5434/monitoring_service_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ROOT";

    // Verifica si hay servicios en la base de datos
    public static boolean hayServicios() {
        String query = "SELECT COUNT(*) FROM monitored_service";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Devuelve true si hay registros en la tabla
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de excepciones adecuado
        }

        return false;  // Si hay algún error o la tabla está vacía
    }

    public boolean verificarNombreServicioExiste(String name) {
        String query = "SELECT COUNT(*) FROM monitored_service WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Devuelve true si el usuario existe
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de excepciones adecuado
        }

        return false;  // Si hay algún error o no encuentra el usuario
    }

    public boolean verificarEndpointServicioExiste(String endpoint) {
        String query = "SELECT COUNT(*) FROM monitored_service WHERE endpoint = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, endpoint);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Devuelve true si el usuario existe
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de excepciones adecuado
        }

        return false;  // Si hay algún error o no encuentra el usuario
    }
}
