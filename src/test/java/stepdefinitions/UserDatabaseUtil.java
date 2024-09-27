package stepdefinitions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class    UserDatabaseUtil {

    private static final String URL = "jdbc:postgresql://localhost:5432/tallerApiRest";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public boolean verificarUsuarioExiste(String username) {
        String query = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
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

    public String obtenerContraseñaUsuario(String username) {
        String query = "SELECT password FROM usuario WHERE username = ?";
        String password = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString("password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return password;
    }
}
