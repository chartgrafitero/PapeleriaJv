/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

public class Producto {

    private int idProducto;
    private String nombre;
    private int stock;
    private Integer unidadesPorCaja;

    public Producto() {
    }

    public Producto(String nombre, int stock, Integer unidadesPorCaja) {
        this.nombre = nombre;
        this.stock = stock;
        this.unidadesPorCaja = unidadesPorCaja;
    }

    public Producto(int idProducto, String nombre,
                    int stock, Integer unidadesPorCaja) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.stock = stock;
        this.unidadesPorCaja = unidadesPorCaja;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Integer getUnidadesPorCaja() {
        return unidadesPorCaja;
    }

    public void setUnidadesPorCaja(Integer unidadesPorCaja) {
        this.unidadesPorCaja = unidadesPorCaja;
    }
}

