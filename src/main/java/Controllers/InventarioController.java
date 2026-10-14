package Controllers;

import DAO.ProductoDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Producto;

public class InventarioController extends BaseController {

    private static final int STOCK_BAJO = 10;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, BigDecimal> colPrecioUnidad;
    @FXML private TableColumn<Producto, BigDecimal> colPrecioCaja;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Integer> colUnidades;
    @FXML private TextField txtBuscar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Label lblResumen;

    private final ProductoDAO dao = new ProductoDAO();
    private final ObservableList<Producto> datos = FXCollections.observableArrayList();
    private FilteredList<Producto> filtrados;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecioUnidad.setCellValueFactory(new PropertyValueFactory<>("precioUnidad"));
        colPrecioCaja.setCellValueFactory(new PropertyValueFactory<>("precioCaja"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnidades.setCellValueFactory(new PropertyValueFactory<>("unidadesPorCaja"));

        // Precios: $12.50 alineado a la derecha ("—" si no se vende por caja)
        colPrecioUnidad.setCellFactory(formatoDinero());
        colPrecioCaja.setCellFactory(formatoDinero());

        // Stock bajo en rojo
        colStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer valor, boolean vacio) {
                super.updateItem(valor, vacio);
                getStyleClass().remove("stock-bajo");
                if (vacio || valor == null) {
                    setText(null);
                } else {
                    setText(valor + (valor <= STOCK_BAJO ? "  ⚠ bajo" : ""));
                    if (valor <= STOCK_BAJO) getStyleClass().add("stock-bajo");
                }
            }
        });

        colUnidades.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio ? null : (valor == null ? "—" : String.valueOf(valor)));
            }
        });

        // Búsqueda en vivo + ordenamiento por columna
        filtrados = new FilteredList<>(datos, p -> true);
        txtBuscar.textProperty().addListener((obs, anterior, texto) -> {
            String f = texto.trim().toLowerCase();
            filtrados.setPredicate(p -> f.isEmpty() || p.getNombre().toLowerCase().contains(f));
            actualizarResumen();
        });
        SortedList<Producto> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(ordenados);

        btnEditar.disableProperty().bind(tablaProductos.getSelectionModel().selectedItemProperty().isNull());
        btnEliminar.disableProperty().bind(tablaProductos.getSelectionModel().selectedItemProperty().isNull());

        tablaProductos.setRowFactory(tv -> {
            TableRow<Producto> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) abrirFormulario(fila.getItem());
            });
            return fila;
        });

        cargarProductos();
    }

    private void cargarProductos() {
        try {
            datos.setAll(dao.listar());
            actualizarResumen();
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los productos", e.getMessage());
        }
    }

    private void actualizarResumen() {
        lblResumen.setText(filtrados.size() + " de " + datos.size() + " productos");
    }

    @FXML
    private void handleNuevoProducto() {
        abrirFormulario(null);
    }

    @FXML
    private void handleEditar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) abrirFormulario(seleccionado);
    }

    @FXML
    private void handleEliminar() {
        Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p == null) return;

        if (!confirmar("Eliminar producto",
                "¿Eliminar «" + p.getNombre() + "»? Esta acción no se puede deshacer.")) {
            return;
        }
        try {
            dao.eliminar(p.getIdProducto());
            cargarProductos();
        } catch (SQLException e) {
            mostrarError("No se pudo eliminar",
                    "Es posible que el producto ya tenga ventas registradas.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleIrAVentas() {
        navegarA("ventas.fxml", tablaProductos);
    }

    /** producto == null → modo "nuevo"; producto != null → modo "editar". */
    private void abrirFormulario(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/papeleria/papeleriajv/agreeProduct.fxml"));
            Parent raiz = loader.load();
            AgreeProductController ctrl = loader.getController();
            ctrl.setProducto(producto);

            Stage dialogo = new Stage();
            dialogo.initOwner(tablaProductos.getScene().getWindow());
            dialogo.initModality(Modality.APPLICATION_MODAL);
            dialogo.setTitle(producto == null ? "Nuevo producto" : "Editar producto");
            dialogo.setScene(new Scene(raiz));
            dialogo.setResizable(false);
            dialogo.showAndWait();

            if (ctrl.isGuardado()) cargarProductos();
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario", e.getMessage());
        }
    }
}