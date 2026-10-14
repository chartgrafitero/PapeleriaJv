package DAO;

import models.Producto;

public enum TipoVenta {
    UNIDAD {
        @Override public int unidadesADescontar(Producto p, int cantidad) {
            return cantidad;
        }
    },
    CAJA {
        @Override public int unidadesADescontar(Producto p, int cantidad) {
            if (p.getUnidadesPorCaja() == null)
                throw new IllegalStateException("Este producto no se vende por caja");
            return cantidad * p.getUnidadesPorCaja();
        }
    };

    public abstract int unidadesADescontar(Producto p, int cantidad);
}