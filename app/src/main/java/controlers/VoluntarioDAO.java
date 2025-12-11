package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Voluntario;

@Dao
public interface VoluntarioDAO {

    @Insert
    void agregarVoluntario(Voluntario voluntario);

    @Delete
    void eliminarVoluntario(Voluntario voluntario);

    @Query("DELETE FROM Voluntario WHERE idVoluntario = :id")
    void eliminarVoluntarioPorId(int id);

    @Update
    void actualizarVoluntario(Voluntario voluntario);

    @Query("SELECT * FROM Voluntario ORDER BY fechaRegistro DESC, nombre")
    List<Voluntario> mostrarTodos();

    @Query("SELECT * FROM Voluntario WHERE nombre = :nombre")
    List<Voluntario> mostrarPorNombre(String nombre);

    @Query("SELECT * FROM Voluntario WHERE nombre LIKE '%' || :filtro || '%' OR telefono LIKE '%' || :filtro || '%'")
    List<Voluntario> buscarPorCoincidencia(String filtro);

    @Query("SELECT * FROM Voluntario WHERE idVoluntario = :id")
    Voluntario buscarPorId(int id);

    @Query("SELECT * FROM Voluntario WHERE estudiante = 1")
    List<Voluntario> mostrarEstudiantes();

    @Query("SELECT * FROM Voluntario WHERE activo = 1")
    List<Voluntario> mostrarActivos();

    @Query("SELECT * FROM Voluntario WHERE fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    List<Voluntario> mostrarPorRangoFecha(String fechaInicio, String fechaFin);

    @Query("SELECT COUNT(*) FROM Voluntario WHERE activo = 1")
    int contarVoluntariosActivos();

    @Query("SELECT COUNT(*) FROM Voluntario WHERE estudiante = 1")
    int contarVoluntariosEstudiantes();
}