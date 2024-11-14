package steps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class LogsDatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5433/logsDB";
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
}
