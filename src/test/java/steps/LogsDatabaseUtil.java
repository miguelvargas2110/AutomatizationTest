package steps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class LogsDatabaseUtil {
    private static final String URL = "jdbc:postgresql://dbLogs:5433/logsDB";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ROOT";

    // Verifica si hay registros en la base de datos
    public static boolean hayRegistros() {
        String query = "SELECT COUNT(*) FROM logs";
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

    private static final String URL2 = "jdbc:postgresql://dbUsers:5432/UserDB";
    private static final String USER2 = "postgres";
    private static final String PASSWORD2 = "ROOT";

    public static boolean verificarUsuarioExiste(String username) {
        String query = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL2, USER2, PASSWORD2);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
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
