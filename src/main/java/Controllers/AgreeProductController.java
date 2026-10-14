package Controllers;

import DAO.ProductoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import models.Producto;

public class AgreeProductController extends BaseController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecioUnidad;
    @FXML private CheckBox chkPorCaja;
    @FXML private TextField txtUnidades;
    @FXML private TextField txtPrecioCaja;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;

    private final ProductoDAO dao = new ProductoDAO();
    private Producto productoEditando;   // null = modo "nuevo"
    private boolean guardado = false;

    @FXML
    public void initialize() {
        txtStock.setTextFormatter(soloEnteros());
        txtUnidades.setTextFormatter(soloEnteros());
        txtPrecioUnidad.setTextFormatter(soloDecimales());
        txtPrecioCaja.setTextFormatter(soloDecimales());

        // Los campos de caja solo se usan si el checkbox está marcado
        txtUnidades.disableProperty().bind(chkPorCaja.selectedProperty().not());
        txtPrecioCaja.disableProperty().bind(chkPorCaja.selectedProperty().not());
        chkPorCaja.selectedProperty().addListener((obs, antes, ahora) -> {
            if (!ahora) {
                txtUnidades.clear();
                txtPrecioCaja.clear();
            } else {
                txtUnidades.requestFocus();
            }
        });

        for (TextField campo : new TextField[]{txtNombre, txtStock, txtPrecioUnidad, txtUnidades, txtPrecioCaja}) {
            campo.textProperty().addListener((o, a, b) -> ocultarError());
        }
    }

    /** Llamar justo después de cargar el FXML. null = producto nuevo. */
    public void setProducto(Producto p) {
        this.productoEditando = p;
        if (p == null) return;

        lblTitulo.setText("Editar producto");
        btnGuardar.setText("Guardar cambios");
        txtNombre.setText(p.getNombre());
        txtStock.setText(String.valueOf(p.getStock()));
        if (p.getPrecioUnidad() != null) {
            txtPrecioUnidad.setText(p.getPrecioUnidad().toPlainString());
        }
        if (p.seVendePorCaja()) {
            chkPorCaja.setSelected(true);
            txtUnidades.setText(String.valueOf(p.getUnidadesPorCaja()));
            txtPrecioCaja.setText(p.getPrecioCaja().toPlainString());
        }
    }

    public boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarErrorCampo("Escribe el nombre del producto.", txtNombre);
            return;
        }
        if (txtStock.getText().isEmpty()) {
            mostrarErrorCampo("Indica el stock (puede ser 0).", txtStock);
            return;
        }
        int stock = Integer.parseInt(txtStock.getText());

        BigDecimal precioUnidad = leerPrecio(txtPrecioUnidad.getText());
        if (precioUnidad == null || precioUnidad.signum() <= 0) {
            mostrarErrorCampo("Indica el precio por unidad (mayor a 0).", txtPrecioUnidad);
            return;
        }

        Integer unidades = null;
        BigDecimal precioCaja = null;
        if (chkPorCaja.isSelected()) {
            if (txtUnidades.getText().isEmpty() || Integer.parseInt(txtUnidades.getText()) <= 0) {
                mostrarErrorCampo("Indica cuántas unidades trae cada caja (mayor a 0).", txtUnidades);
                return;
            }
            unidades = Integer.parseInt(txtUnidades.getText());

            precioCaja = leerPrecio(txtPrecioCaja.getText());
            if (precioCaja == null || precioCaja.signum() <= 0) {
                mostrarErrorCampo("Indica el precio por caja (mayor a 0).", txtPrecioCaja);
                return;
            }
        }

        try {
            if (productoEditando == null) {
                dao.insertar(new Producto(nombre, stock, unidades, precioUnidad, precioCaja));
            } else {
                productoEditando.setNombre(nombre);
                productoEditando.setStock(stock);
                productoEditando.setUnidadesPorCaja(unidades);
                productoEditando.setPrecioUnidad(precioUnidad);
                productoEditando.setPrecioCaja(precioCaja);
                dao.actualizar(productoEditando);
            }
            guardado = true;
            cerrar();
        } catch (SQLException e) {
            mostrarError("No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private BigDecimal leerPrecio(String texto) {
        try {
            return new BigDecimal(texto.trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarErrorCampo(String mensaje, TextField campo) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
        campo.requestFocus();
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private TextFormatter<String> soloEnteros() {
        return new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,7}") ? c : null);
    }

    private TextFormatter<String> soloDecimales() {
        return new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,6}(\\.\\d{0,2})?") ? c : null);
    }
}