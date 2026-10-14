package models;

import java.math.BigDecimal;
import models.DetalleTicket.TipoVenta;

/** Una línea del ticket mientras se arma la venta (todavía no está en la BD). */
public class LineaVenta {

    private final Producto producto;
    private final TipoVenta tipo;
    private int cantidad;

    public LineaVenta(Producto producto, TipoVenta tipo, int cantidad) {
        this.producto = producto;
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public TipoVenta getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public String getNombre() { return producto.getNombre(); }

    public void sumar(int mas) {
        this.cantidad += mas;
    }

    public BigDecimal getPrecio() {
        return tipo.precioDe(producto);
    }

    public BigDecimal getSubtotal() {
        return getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public int getUnidadesADescontar() {
        return tipo.unidadesADescontar(producto, cantidad);
    }

    public DetalleTicket toDetalle() {
        return new DetalleTicket(0, producto.getIdProducto(), tipo, cantidad, getPrecio());
    }
}
