/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;

/**
 *
 * @author chart
 */
public class DetalleTicket {
    
    private long idDetalle;
    private long idTicket;
    private int idProducto;
    private TipoVenta tipoVenta;
    private int cantidad;
    private BigDecimal precioUnitarioVenta;

    public enum TipoVenta {
        CAJA,
        UNIDAD
    }

    public DetalleTicket() {
    }

    public DetalleTicket(long idTicket, int idProducto,
                         TipoVenta tipoVenta, int cantidad,
                         BigDecimal precioUnitarioVenta) {
        this.idTicket = idTicket;
        this.idProducto = idProducto;
        this.tipoVenta = tipoVenta;
        this.cantidad = cantidad;
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(long idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(TipoVenta tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioVenta() {
        return precioUnitarioVenta;
    }

    public void setPrecioUnitarioVenta(BigDecimal precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public BigDecimal getSubtotal() {
        return precioUnitarioVenta.multiply(
            BigDecimal.valueOf(cantidad)
        );
    }
}
