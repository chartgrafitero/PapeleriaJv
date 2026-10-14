
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {

    private static final String URL =
            "jdbc:mariadb://localhost:3306/PapeleriaJv";

    private static final String USUARIO =
            System.getenv("PAPELERIA_DB_USER");

    private static final String CLAVE =
            System.getenv("PAPELERIA_DB_PASSWORD");

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        if (USUARIO == null || CLAVE == null) {
            throw new SQLException(
                    "Configura PAPELERIA_DB_USER y PAPELERIA_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
