package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.RepresentanteClase;

@Dao
public interface RepresentanteClaseDAO {

    @Insert
    void agregarRepresentante(RepresentanteClase rep);

    @Delete
    void eliminarRepresentante(RepresentanteClase rep);

    @Query("DELETE FROM RepresentanteClase WHERE idRepresentante = :id")
    void eliminarRepresentantePorId(int id);

    @Update
    void actualizarRepresentante(RepresentanteClase rep);

    @Query("UPDATE RepresentanteClase SET nombre = :n WHERE idRepresentante = :id")
    void actualizarRepresentantePorId(String n, int id);

    @Query("SELECT * FROM RepresentanteClase")
    List<RepresentanteClase> mostrarTodos();

    @Query("SELECT * FROM RepresentanteClase WHERE nombre = :n")
    List<RepresentanteClase> mostrarPorNombre(String n);

    @Query("SELECT * FROM RepresentanteClase WHERE nombre LIKE :pattern")
    List<RepresentanteClase> buscarPorNombreSimilar(String pattern);

    @Query("SELECT * FROM RepresentanteClase WHERE nombre LIKE '%' || :filtro || '%'")
    List<RepresentanteClase> buscarPorCoincidencia(String filtro);
}
