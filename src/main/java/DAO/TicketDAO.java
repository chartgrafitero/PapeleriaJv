package DAO;

import database.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import models.DetalleTicket;
import models.Producto;
import models.Ticket;

public class TicketDAO {

    private final ProductoDAO productoDAO = new ProductoDAO();

    /**
     * Registra una venta completa en UNA transacción:
     * ticket + detalles + descuento de stock. Si algo falla, no se guarda nada.
     *
     * El precio se vuelve a leer de la BD (no se confía en el que mandó la pantalla)
     * y el stock se valida con el UPDATE atómico de ProductoDAO.descontarStock.
     *
     * @return el id del ticket creado
     */
    public long registrarVenta(Ticket ticket, List<DetalleTicket> detalles) throws SQLException {
        if (detalles == null || detalles.isEmpty()) {
            throw new SQLException("La venta no tiene productos.");
        }

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                long idTicket = insertarTicket(con, ticket);
                ticket.setIdTicket(idTicket);

                for (DetalleTicket d : detalles) {
                    Producto p = productoDAO.buscarPorId(con, d.getIdProducto());
                    if (p == null) {
                        throw new SQLException("El producto con ID " + d.getIdProducto()
                                + " ya no existe.");
                    }

                    int unidades = d.getTipoVenta().unidadesADescontar(p, d.getCantidad());
                    if (!productoDAO.descontarStock(con, p.getIdProducto(), unidades)) {
                        throw new SQLException("Stock insuficiente para «" + p.getNombre() + "».");
                    }

                    d.setIdTicket(idTicket);
                    d.setPrecioUnitarioVenta(d.getTipoVenta().precioDe(p));
                    d.setIdDetalle(insertarDetalle(con, d));
                }

                con.commit();
                return idTicket;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } catch (RuntimeException e) {          // p. ej. CAJA en un producto sin caja
                con.rollback();
                throw new SQLException(e.getMessage(), e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private long insertarTicket(Connection con, Ticket t) throws SQLException {
        String sql = "INSERT INTO ticket (fecha, personal_quien_atendio) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(t.getFecha()));
            ps.setString(2, t.getPersonalQuienAtendio());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID del ticket.");
    }

    private long insertarDetalle(Connection con, DetalleTicket d) throws SQLException {
        String sql = "INSERT INTO detalle_ticket "
                + "(id_ticket, id_producto, tipo_venta, cantidad, precio_unitario_venta) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, d.getIdTicket());
            ps.setInt(2, d.getIdProducto());
            ps.setString(3, d.getTipoVenta().name());      // "CAJA" / "UNIDAD"
            ps.setInt(4, d.getCantidad());
            ps.setBigDecimal(5, d.getPrecioUnitarioVenta());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0;
            }
        }
    }
}