
package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import entities.Evento;

@Dao
public interface EventoDAO {

    @Insert
    long agregarEvento(Evento evento);

    @Delete
    void eliminarEvento(Evento evento);

    @Query("DELETE FROM Evento WHERE idEvento = :id")
    void eliminarEventoPorId(int id);

    @Update
    void actualizarEvento(Evento evento);

    @Query("SELECT * FROM Evento ORDER BY fecha DESC")
    List<Evento> mostrarTodos();

    @Query("SELECT * FROM Evento WHERE nombre = :nombre")
    List<Evento> mostrarPorNombre(String nombre);

    @Query("SELECT * FROM Evento WHERE nombre LIKE '%' || :filtro || '%'")
    List<Evento> buscarPorCoincidencia(String filtro);

    @Query("SELECT * FROM Evento WHERE tipo = :tipo")
    List<Evento> mostrarPorTipo(String tipo);

    @Query("SELECT * FROM Evento WHERE idEvento = :id")
    Evento obtenerPorId(int id);

    @Query("SELECT DISTINCT tipo FROM Evento ORDER BY tipo")
    List<String> obtenerTiposEvento();
}