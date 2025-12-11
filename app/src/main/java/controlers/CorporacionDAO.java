package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Corporacion;

@Dao
public interface CorporacionDAO {

    @Insert
    void agregarCorporacion(Corporacion corporacion);

    @Delete
    void eliminarCorporacion(Corporacion corporacion);

    @Query("DELETE FROM Corporacion WHERE idCorporacion = :id")
    void eliminarCorporacionPorId(int id);

    @Update
    void actualizarCorporacion(Corporacion corporacion);

    @Query("UPDATE Corporacion SET nombre = :n WHERE idCorporacion = :id")
    void actualizarCorporacionPorId(String n, int id);

    @Query("SELECT * FROM Corporacion")
    List<Corporacion> mostrarTodos();

    @Query("SELECT * FROM Corporacion WHERE nombre = :n")
    List<Corporacion> mostrarPorNombre(String n);

    @Query("SELECT * FROM Corporacion WHERE nombre LIKE :pattern")
    List<Corporacion> buscarPorNombreSimilar(String pattern);

    @Query("SELECT * FROM Corporacion WHERE nombre LIKE '%' || :filtro || '%'")
    List<Corporacion> buscarPorCoincidencia(String filtro);
}
