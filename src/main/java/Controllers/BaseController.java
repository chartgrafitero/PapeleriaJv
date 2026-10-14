package Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Clase base de todos los controladores: diálogos, formato de dinero y navegación.
 */
public abstract class BaseController {

    private static final NumberFormat MONEDA =
            NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    // ---------- Diálogos ----------
    protected void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    protected void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    protected boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje,
                ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        Optional<ButtonType> r = alerta.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    // ---------- Dinero ----------
    protected static String dinero(BigDecimal valor) {
        return valor == null ? "—" : MONEDA.format(valor);
    }

    /** Celda de tabla que muestra BigDecimal como $1,234.50 alineado a la derecha. */
    protected static <S> Callback<TableColumn<S, BigDecimal>, TableCell<S, BigDecimal>> formatoDinero() {
        return columna -> new TableCell<S, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio ? null : dinero(valor));
                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }

    // ---------- Navegación ----------
    /** Cambia el contenido de la ventana actual por otro FXML (misma ventana, otra pantalla). */
    protected void navegarA(String archivoFxml, Node origen) {
        try {
            Parent raiz = FXMLLoader.load(
                    getClass().getResource("/com/papeleria/papeleriajv/" + archivoFxml));
            origen.getScene().setRoot(raiz);
        } catch (IOException e) {
            mostrarError("No se pudo abrir la pantalla", e.getMessage());
        }
    }
}