package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.DonadoresRepresentante;

@Dao
public interface DonadoresRepresentanteDAO {

    @Insert
    void agregar(DonadoresRepresentante dr);

    @Delete
    void eliminar(DonadoresRepresentante dr);

    @Query("DELETE FROM DonadoresRepresentante WHERE idDonador = :idDonador AND idRepresentante = :idRep")
    void eliminarPorLlave(int idDonador, int idRep);

    @Update
    void actualizar(DonadoresRepresentante dr);

    @Query("UPDATE DonadoresRepresentante SET estatus = :e WHERE idDonador = :idDonador AND idRepresentante = :idRep")
    void actualizarPorLlave(Boolean e, int idDonador, int idRep);

    @Query("SELECT * FROM DonadoresRepresentante")
    List<DonadoresRepresentante> mostrarTodos();

    @Query("SELECT * FROM DonadoresRepresentante WHERE idDonador = :id")
    List<DonadoresRepresentante> mostrarPorDonador(int id);

    @Query("SELECT * FROM DonadoresRepresentante WHERE idRepresentante = :id")
    List<DonadoresRepresentante> mostrarPorRepresentante(int id);
}
