package Controllers;

import DAO.ProductoDAO;
import DAO.TicketDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import models.DetalleTicket;
import models.DetalleTicket.TipoVenta;
import models.LineaVenta;
import models.Producto;
import models.Ticket;

public class VentasController extends BaseController {

    // ----- Catálogo (izquierda) -----
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tablaCatalogo;
    @FXML private TableColumn<Producto, String> colCatNombre;
    @FXML private TableColumn<Producto, BigDecimal> colCatPrecioUnidad;
    @FXML private TableColumn<Producto, BigDecimal> colCatPrecioCaja;
    @FXML private TableColumn<Producto, Integer> colCatStock;
    @FXML private ComboBox<TipoVenta> cmbTipo;
    @FXML private TextField txtCantidad;
    @FXML private Button btnAgregar;
    @FXML private Label lblMensaje;

    // ----- Ticket (derecha) -----
    @FXML private TableView<LineaVenta> tablaTicket;
    @FXML private TableColumn<LineaVenta, String> colTkNombre;
    @FXML private TableColumn<LineaVenta, TipoVenta> colTkTipo;
    @FXML private TableColumn<LineaVenta, Integer> colTkCantidad;
    @FXML private TableColumn<LineaVenta, BigDecimal> colTkPrecio;
    @FXML private TableColumn<LineaVenta, BigDecimal> colTkSubtotal;
    @FXML private Label lblTotal;
    @FXML private TextField txtAtiende;
    @FXML private Button btnQuitar;
    @FXML private Button btnVaciar;
    @FXML private Button btnCobrar;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    private final ObservableList<Producto> catalogo = FXCollections.observableArrayList();
    private final ObservableList<LineaVenta> carrito = FXCollections.observableArrayList();
    private FilteredList<Producto> filtrados;

    @FXML
    public void initialize() {
        configurarCatalogo();
        configurarTicket();
        cargarCatalogo();
        actualizarTotal();
    }

    // =====================================================
    //  Configuración
    // =====================================================
    private void configurarCatalogo() {
        colCatNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCatPrecioUnidad.setCellValueFactory(new PropertyValueFactory<>("precioUnidad"));
        colCatPrecioCaja.setCellValueFactory(new PropertyValueFactory<>("precioCaja"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCatPrecioUnidad.setCellFactory(formatoDinero());
        colCatPrecioCaja.setCellFactory(formatoDinero());

        filtrados = new FilteredList<>(catalogo, p -> true);
        txtBuscar.textProperty().addListener((obs, a, texto) -> {
            String f = texto.trim().toLowerCase();
            filtrados.setPredicate(p -> f.isEmpty() || p.getNombre().toLowerCase().contains(f));
        });
        SortedList<Producto> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tablaCatalogo.comparatorProperty());
        tablaCatalogo.setItems(ordenados);

        // Al elegir un producto, el combo solo ofrece "Caja" si ese producto se vende por caja
        tablaCatalogo.getSelectionModel().selectedItemProperty()
                .addListener((obs, a, p) -> actualizarTiposDisponibles(p));
        btnAgregar.disableProperty().bind(tablaCatalogo.getSelectionModel().selectedItemProperty().isNull());

        // Doble clic = agregar rápido
        tablaCatalogo.setRowFactory(tv -> {
            TableRow<Producto> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) handleAgregar();
            });
            return fila;
        });

        txtCantidad.setTextFormatter(
                new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,3}") ? c : null));
    }

    private void configurarTicket() {
        colTkNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTkTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTkCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTkPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colTkSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colTkPrecio.setCellFactory(formatoDinero());
        colTkSubtotal.setCellFactory(formatoDinero());

        tablaTicket.setItems(carrito);
        tablaTicket.setPlaceholder(new Label("El ticket está vacío"));

        btnQuitar.disableProperty().bind(tablaTicket.getSelectionModel().selectedItemProperty().isNull());
        btnVaciar.disableProperty().bind(Bindings.isEmpty(carrito));
        btnCobrar.disableProperty().bind(Bindings.isEmpty(carrito));
    }

    private void cargarCatalogo() {
        try {
            catalogo.setAll(productoDAO.listar());
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los productos", e.getMessage());
        }
    }

    private void actualizarTiposDisponibles(Producto p) {
        cmbTipo.getItems().clear();
        if (p == null) return;

        cmbTipo.getItems().add(TipoVenta.UNIDAD);
        if (p.seVendePorCaja()) {
            cmbTipo.getItems().add(TipoVenta.CAJA);
        }
        cmbTipo.getSelectionModel().selectFirst();
    }

    // =====================================================
    //  Acciones
    // =====================================================
    @FXML
    private void handleAgregar() {
        Producto p = tablaCatalogo.getSelectionModel().getSelectedItem();
        TipoVenta tipo = cmbTipo.getValue();
        if (p == null || tipo == null) return;

        int cantidad = leerCantidad();
        if (cantidad <= 0) {
            mostrarMensaje("Indica una cantidad mayor a 0.");
            txtCantidad.requestFocus();
            return;
        }

        // Validación rápida en pantalla (la BD vuelve a validar al cobrar)
        int yaEnTicket = unidadesEnTicket(p);
        int necesarias = yaEnTicket + tipo.unidadesADescontar(p, cantidad);
        if (necesarias > p.getStock()) {
            mostrarMensaje("Stock insuficiente de «" + p.getNombre() + "»: hay "
                    + p.getStock() + " piezas y ya llevas " + yaEnTicket + " en el ticket.");
            return;
        }

        // Si ya existe la misma línea (mismo producto y tipo), se suma en vez de duplicar
        Optional<LineaVenta> existente = carrito.stream()
                .filter(l -> l.getProducto().getIdProducto() == p.getIdProducto() && l.getTipo() == tipo)
                .findFirst();
        if (existente.isPresent()) {
            existente.get().sumar(cantidad);
        } else {
            carrito.add(new LineaVenta(p, tipo, cantidad));
        }

        tablaTicket.refresh();
        actualizarTotal();
        ocultarMensaje();
        txtCantidad.setText("1");
    }

    @FXML
    private void handleQuitar() {
        LineaVenta l = tablaTicket.getSelectionModel().getSelectedItem();
        if (l != null) {
            carrito.remove(l);
            actualizarTotal();
        }
    }

    @FXML
    private void handleVaciar() {
        if (confirmar("Vaciar ticket", "¿Quitar todos los productos del ticket?")) {
            carrito.clear();
            actualizarTotal();
        }
    }

    @FXML
    private void handleCobrar() {
        String atiende = txtAtiende.getText().trim();
        if (atiende.isEmpty()) {
            mostrarMensaje("Escribe quién atiende la venta.");
            txtAtiende.requestFocus();
            return;
        }

        BigDecimal total = calcularTotal();
        List<DetalleTicket> detalles = carrito.stream()
                .map(LineaVenta::toDetalle)
                .collect(Collectors.toList());

        try {
            long idTicket = ticketDAO.registrarVenta(new Ticket(atiende), detalles);
            carrito.clear();
            actualizarTotal();
            ocultarMensaje();
            cargarCatalogo();   // refresca el stock
            mostrarInfo("Venta registrada", "Ticket #" + idTicket + "\nTotal cobrado: " + dinero(total));
        } catch (SQLException e) {
            mostrarError("No se pudo registrar la venta", e.getMessage());
            cargarCatalogo();   // por si el stock cambió
        }
    }

    @FXML
    private void handleVolver() {
        if (!carrito.isEmpty()
                && !confirmar("Salir de ventas", "Hay productos en el ticket. Si sales se perderán.")) {
            return;
        }
        navegarA("Inventario.fxml", tablaCatalogo);
    }

    // =====================================================
    //  Auxiliares
    // =====================================================
    private int leerCantidad() {
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Piezas sueltas de ese producto que ya están comprometidas en el ticket (cajas incluidas). */
    private int unidadesEnTicket(Producto p) {
        return carrito.stream()
                .filter(l -> l.getProducto().getIdProducto() == p.getIdProducto())
                .mapToInt(LineaVenta::getUnidadesADescontar)
                .sum();
    }

    private BigDecimal calcularTotal() {
        return carrito.stream()
                .map(LineaVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void actualizarTotal() {
        lblTotal.setText(dinero(calcularTotal()));
    }

    private void mostrarMensaje(String texto) {
        lblMensaje.setText(texto);
        lblMensaje.setVisible(true);
        lblMensaje.setManaged(true);
    }

    private void ocultarMensaje() {
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);
    }
}
