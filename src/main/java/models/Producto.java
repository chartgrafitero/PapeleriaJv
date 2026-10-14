
package models;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private String nombre;
    private int stock;
    private Integer unidadesPorCaja;     // null = no se vende por caja
    private BigDecimal precioUnidad;
    private BigDecimal precioCaja;       // null = no se vende por caja

    public Producto() {
    }

    public Producto(String nombre, int stock, Integer unidadesPorCaja,
                    BigDecimal precioUnidad, BigDecimal precioCaja) {
        this.nombre = nombre;
        this.stock = stock;
        this.unidadesPorCaja = unidadesPorCaja;
        this.precioUnidad = precioUnidad;
        this.precioCaja = precioCaja;
    }

    public Producto(int idProducto, String nombre, int stock, Integer unidadesPorCaja,
                    BigDecimal precioUnidad, BigDecimal precioCaja) {
        this(nombre, stock, unidadesPorCaja, precioUnidad, precioCaja);
        this.idProducto = idProducto;
    }

    public boolean seVendePorCaja() {
        return unidadesPorCaja != null && precioCaja != null;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Integer getUnidadesPorCaja() { return unidadesPorCaja; }
    public void setUnidadesPorCaja(Integer unidadesPorCaja) { this.unidadesPorCaja = unidadesPorCaja; }

    public BigDecimal getPrecioUnidad() { return precioUnidad; }
    public void setPrecioUnidad(BigDecimal precioUnidad) { this.precioUnidad = precioUnidad; }

    public BigDecimal getPrecioCaja() { return precioCaja; }
    public void setPrecioCaja(BigDecimal precioCaja) { this.precioCaja = precioCaja; }
}