package DAO;

import java.sql.SQLException;
import java.util.List;

public interface CrudDao<T> {
        boolean insertar(T t) throws SQLException;
        List<T> listar() throws SQLException;
        T buscarPorId(int id) throws SQLException;
        boolean actualizar(T t) throws SQLException;
        boolean eliminar(int id) throws SQLException;

    }
// ProductoDAO implements CrudDAO<Producto>  (solo agrega @Override a sus métodos)


