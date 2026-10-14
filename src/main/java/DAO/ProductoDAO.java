/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import database.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Producto;

/**
 *
 * @author chart
 */
public class ProductoDAO {
    
    // INSERTAR PRODUCTO
    public boolean insertar(Producto producto) throws SQLException {

        String sql = "INSERT INTO Producto (Nombre, Stock, unidades_PorCaja) VALUES (?, ?, ?)";


        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getStock());

            if (producto.getUnidadesPorCaja() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, producto.getUnidadesPorCaja());
            }

            return ps.executeUpdate() == 1;
        }
    }

    // CONSULTAR TODOS LOS PRODUCTOS
    public List<Producto> listar() throws SQLException {

        String sql = " SELECT Id_Producto, Nombre, Stock, unidades_PorCaja FROM Producto ORDER BY Id_Producto";

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

    // BUSCAR PRODUCTO POR ID
    public Producto buscarPorId(int id) throws SQLException {

        String sql = "SELECT Id_Producto, Nombre, Stock, unidades_PorCaja FROM Producto WHERE Id_Producto = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }

        return null;
    }

    // ACTUALIZAR PRODUCTO
    public boolean actualizar(Producto producto) throws SQLException {

        String sql = " UPDATE Producto SET Nombre = ?, Stock = ?, unidades_PorCaja = ? WHERE Id_Producto = ? ";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getStock());

            if (producto.getUnidadesPorCaja() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, producto.getUnidadesPorCaja());
            }

            ps.setInt(4, producto.getIdProducto());

            return ps.executeUpdate() == 1;
        }
    }

    // ELIMINAR PRODUCTO
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM Producto WHERE Id_Producto = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }

    // CONVERTIR FILA SQL EN OBJETO JAVA
    private Producto mapearProducto(ResultSet rs) throws SQLException {

        Object unidades = rs.getObject("unidades_PorCaja");

        Integer unidadesPorCaja = unidades == null
                ? null
                : ((Number) unidades).intValue();

        return new Producto(
                rs.getInt("Id_Producto"),
                rs.getString("Nombre"),
                rs.getInt("Stock"),
                unidadesPorCaja
        );
    }
}
