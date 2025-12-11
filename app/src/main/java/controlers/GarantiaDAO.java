package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Garantia;

@Dao
public interface GarantiaDAO {

    @Insert
    void agregarGarantia(Garantia garantia);

    @Delete
    void eliminarGarantia(Garantia garantia);

    @Query("DELETE FROM Garantia WHERE idGarantia = :id")
    void eliminarGarantiaPorId(int id);

    @Update
    void actualizarGarantia(Garantia garantia);

    @Query("UPDATE Garantia SET nombre = :n WHERE idGarantia = :id")
    void actualizarGarantiaPorId(String n, int id);

    @Query("SELECT * FROM Garantia")
    List<Garantia> mostrarTodos();

    @Query("SELECT * FROM Garantia WHERE nombre = :n")
    List<Garantia> mostrarPorNombre(String n);

    @Query("SELECT * FROM Garantia WHERE nombre LIKE :pattern")
    List<Garantia> buscarPorNombreSimilar(String pattern);

    @Query("SELECT * FROM Garantia WHERE nombre LIKE '%' || :filtro || '%'")
    List<Garantia> buscarPorCoincidencia(String filtro);
}
