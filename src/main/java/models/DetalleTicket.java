package models;

import java.math.BigDecimal;

public class DetalleTicket {

    private long idDetalle;
    private long idTicket;
    private int idProducto;
    private TipoVenta tipoVenta;
    private int cantidad;
    private BigDecimal precioUnitarioVenta;

    /**
     * POLIMORFISMO: cada tipo de venta sabe calcular su propio precio y cuántas
     * piezas descuenta del stock. Nadie más necesita un "if (tipo == CAJA)".
     * En la BD se guarda name() ("CAJA"/"UNIDAD"); toString() es solo para la pantalla.
     */
    public enum TipoVenta {
        UNIDAD("Unidad") {
            @Override
            public int unidadesADescontar(Producto p, int cantidad) {
                return cantidad;
            }

            @Override
            public BigDecimal precioDe(Producto p) {
                return p.getPrecioUnidad();
            }
        },
        CAJA("Caja") {
            @Override
            public int unidadesADescontar(Producto p, int cantidad) {
                exigirCaja(p);
                return cantidad * p.getUnidadesPorCaja();
            }

            @Override
            public BigDecimal precioDe(Producto p) {
                exigirCaja(p);
                return p.getPrecioCaja();
            }

            private void exigirCaja(Producto p) {
                if (!p.seVendePorCaja()) {
                    throw new IllegalStateException(
                            "«" + p.getNombre() + "» no se vende por caja.");
                }
            }
        };

        private final String etiqueta;

        TipoVenta(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public abstract int unidadesADescontar(Producto p, int cantidad);

        public abstract BigDecimal precioDe(Producto p);

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    public DetalleTicket() {
    }

    public DetalleTicket(long idTicket, int idProducto, TipoVenta tipoVenta,
                         int cantidad, BigDecimal precioUnitarioVenta) {
        this.idTicket = idTicket;
        this.idProducto = idProducto;
        this.tipoVenta = tipoVenta;
        this.cantidad = cantidad;
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(long idDetalle) { this.idDetalle = idDetalle; }

    public long getIdTicket() { return idTicket; }
    public void setIdTicket(long idTicket) { this.idTicket = idTicket; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public TipoVenta getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(TipoVenta tipoVenta) { this.tipoVenta = tipoVenta; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitarioVenta() { return precioUnitarioVenta; }
    public void setPrecioUnitarioVenta(BigDecimal precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public BigDecimal getSubtotal() {
        return precioUnitarioVenta.multiply(BigDecimal.valueOf(cantidad));
    }
}
