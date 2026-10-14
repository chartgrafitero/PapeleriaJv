/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */package DAO;

import database.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import models.Producto;

public class ProductoDAO {

    private static final String COLUMNAS =
            "Id_Producto, Nombre, Stock, unidades_PorCaja, Precio_Unidad, Precio_Caja";

    // INSERTAR
    public boolean insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO Producto "
                + "(Nombre, Stock, unidades_PorCaja, Precio_Unidad, Precio_Caja) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            asignarCampos(ps, producto);
            return ps.executeUpdate() == 1;
        }
    }

    // LISTAR
    public List<Producto> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM Producto ORDER BY Nombre";
        List<Producto> productos = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }

    // BUSCAR POR ID (conexión propia)
    public Producto buscarPorId(int id) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            return buscarPorId(conexion, id);
        }
    }

    // BUSCAR POR ID dentro de una transacción ya abierta
    public Producto buscarPorId(Connection conexion, int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM Producto WHERE Id_Producto = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        return null;
    }

    // ACTUALIZAR
    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE Producto SET Nombre = ?, Stock = ?, unidades_PorCaja = ?, "
                + "Precio_Unidad = ?, Precio_Caja = ? WHERE Id_Producto = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            asignarCampos(ps, producto);
            ps.setInt(6, producto.getIdProducto());
            return ps.executeUpdate() == 1;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Producto WHERE Id_Producto = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Descuenta stock de forma segura: la condición "Stock >= ?" hace que la BD
     * rechace el descuento si no alcanza (nunca queda stock negativo, aunque dos
     * cajeros vendan lo mismo a la vez). Devuelve false si no había suficiente.
     */
    public boolean descontarStock(Connection conexion, int idProducto, int unidades) throws SQLException {
        String sql = "UPDATE Producto SET Stock = Stock - ? "
                + "WHERE Id_Producto = ? AND Stock >= ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, unidades);
            ps.setInt(2, idProducto);
            ps.setInt(3, unidades);
            return ps.executeUpdate() == 1;
        }
    }

    // Parámetros 1..5 compartidos por INSERT y UPDATE
    private void asignarCampos(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setInt(2, p.getStock());

        if (p.getUnidadesPorCaja() == null) {
            ps.setNull(3, Types.INTEGER);
        } else {
            ps.setInt(3, p.getUnidadesPorCaja());
        }

        ps.setBigDecimal(4, p.getPrecioUnidad());

        if (p.getPrecioCaja() == null) {
            ps.setNull(5, Types.DECIMAL);
        } else {
            ps.setBigDecimal(5, p.getPrecioCaja());
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Object unidades = rs.getObject("unidades_PorCaja");
        Integer unidadesPorCaja = unidades == null ? null : ((Number) unidades).intValue();

        return new Producto(
                rs.getInt("Id_Producto"),
                rs.getString("Nombre"),
                rs.getInt("Stock"),
                unidadesPorCaja,
                rs.getBigDecimal("Precio_Unidad"),
                rs.getBigDecimal("Precio_Caja")   // null si no se vende por caja
        );
    }
}