package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.CategoriaDonador;

@Dao
public interface CategoriaDonadorDAO {

    @Insert
    void agregarCategoria(CategoriaDonador categoria);

    @Delete
    void eliminarCategoria(CategoriaDonador categoria);

    @Query("DELETE FROM CategoriaDonador WHERE idCategoria = :id")
    void eliminarCategoriaPorId(int id);

    @Update
    void actualizarCategoria(CategoriaDonador categoria);

    @Query("UPDATE CategoriaDonador SET nombre = :n WHERE idCategoria = :id")
    void actualizarCategoriaPorId(String n, int id);

    @Query("SELECT * FROM CategoriaDonador")
    List<CategoriaDonador> mostrarTodos();

    @Query("SELECT * FROM CategoriaDonador WHERE nombre = :n")
    List<CategoriaDonador> mostrarPorNombre(String n);

    @Query("SELECT * FROM CategoriaDonador WHERE nombre LIKE :pattern")
    List<CategoriaDonador> buscarPorNombreSimilar(String pattern);

    @Query("SELECT * FROM CategoriaDonador WHERE nombre LIKE '%' || :filtro || '%'")
    List<CategoriaDonador> buscarPorCoincidencia(String filtro);
}