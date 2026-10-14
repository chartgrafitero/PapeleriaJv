/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Producto;

/**
 *
 * @author chart
 */
public class InventarioController {
    

    private TableView<Producto> tablaProductos;

    private TableColumn<Producto, Integer> colId;

    private TableColumn<Producto, String> colNombre;

    private TableColumn<Producto, Integer> colStock;

    private TableColumn<Producto, Integer> colUnidades;

    public void initialize() {

        colId.setCellValueFactory(
            new PropertyValueFactory<>("idProducto")
        );

        colNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );

        colStock.setCellValueFactory(
            new PropertyValueFactory<>("stock")
        );

        colUnidades.setCellValueFactory(
            new PropertyValueFactory<>("unidadesPorCaja")
        );

        tablaProductos.getItems().add(
            new Producto(1, "Lápiz HB", 120, 12)
        );

        tablaProductos.getItems().add(
            new Producto(2, "Cuaderno", 45, 10)
        );

        tablaProductos.getItems().add(
            new Producto(3, "Borrador", 80, 20)
        );
    }
    
    private void handleNuevoProducto(){
        System.out.println("¡Botón 'Nuevo producto' presionado!");
    }
}
