package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.AsistenteEvento;

@Dao
public interface AsistenteEventoDAO {

    @Insert
    void agregarAsistente(AsistenteEvento asistente);

    @Delete
    void eliminarAsistente(AsistenteEvento asistente);

    @Query("DELETE FROM AsistenteEvento WHERE idEvento = :idEvento AND idDonador = :idDonador")
    void eliminarAsistentePorLlave(int idEvento, int idDonador);

    @Update
    void actualizarAsistente(AsistenteEvento asistente);

    @Query("UPDATE AsistenteEvento SET idAsistente = :idA WHERE idEvento = :idEvento AND idDonador = :idDonador")
    void actualizarAsistentePorLlave(int idA, int idEvento, int idDonador);

    @Query("SELECT * FROM AsistenteEvento")
    List<AsistenteEvento> mostrarTodos();

    @Query("SELECT * FROM AsistenteEvento WHERE idEvento = :id")
    List<AsistenteEvento> mostrarPorEvento(int id);

    @Query("SELECT * FROM AsistenteEvento WHERE idDonador = :id")
    List<AsistenteEvento> mostrarPorDonador(int id);
}
