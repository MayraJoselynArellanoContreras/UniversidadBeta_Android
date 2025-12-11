package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.AnioFiscal;

@Dao
public interface AnioFiscalDAO {

    // ALTAS
    @Insert
    void agregarAnioFiscal(AnioFiscal anioFiscal);

    // BAJAS
    @Delete
    void eliminarAnioFiscal(AnioFiscal anioFiscal);

    @Query("DELETE FROM AnioFiscal WHERE idAnioFiscal = :id")
    void eliminarAnioFiscalPorId(int id);

    // CAMBIOS
    @Update
    void actualizarAnioFiscal(AnioFiscal anioFiscal);

    @Query("UPDATE AnioFiscal SET nombre = :n WHERE idAnioFiscal = :id")
    void actualizarAnioFiscalPorId(String n, int id);

    // CONSULTAS
    @Query("SELECT * FROM AnioFiscal")
    List<AnioFiscal> mostrarTodos();

    @Query("SELECT * FROM AnioFiscal WHERE nombre = :n")
    List<AnioFiscal> mostrarPorNombre(String n);

    @Query("SELECT * FROM AnioFiscal WHERE nombre LIKE :pattern")
    List<AnioFiscal> buscarPorNombreSimilar(String pattern);

    @Query("SELECT * FROM AnioFiscal WHERE nombre LIKE '%' || :filtro || '%'")
    List<AnioFiscal> buscarPorCoincidencia(String filtro);
}
