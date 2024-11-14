package steps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilesDatabaseUtil {
    private static final String URL = "jdbc:postgresql://dbProfiles:5435/profiles_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ROOT";

    public boolean verificarPerfilExistente(int id_usuario) {
        String query = "SELECT COUNT(*) FROM profiles_perfil WHERE id_usuario = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id_usuario);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Devuelve true si el perfil existe
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de excepciones adecuado
        }
        return false;  // Si hay algún error o no encuentra el perfil
    }

    public void borrarTodosLosPerfiles() {
        String query = "DELETE FROM profiles_perfil";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
