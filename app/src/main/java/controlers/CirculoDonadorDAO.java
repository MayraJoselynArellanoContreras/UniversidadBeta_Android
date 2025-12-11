package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.CirculoDonador;

@Dao
public interface CirculoDonadorDAO {

    @Insert
    void agregarCirculo(CirculoDonador circulo);

    @Delete
    void eliminarCirculo(CirculoDonador circulo);

    @Query("DELETE FROM CirculoDonador WHERE idCirculo = :id")
    void eliminarCirculoPorId(int id);

    @Update
    void actualizarCirculo(CirculoDonador circulo);

    @Query("UPDATE CirculoDonador SET nombre = :n WHERE idCirculo = :id")
    void actualizarCirculoPorId(String n, int id);

    @Query("SELECT * FROM CirculoDonador")
    List<CirculoDonador> mostrarTodos();

    @Query("SELECT * FROM CirculoDonador WHERE nombre = :n")
    List<CirculoDonador> mostrarPorNombre(String n);

    @Query("SELECT * FROM CirculoDonador WHERE nombre LIKE :pattern")
    List<CirculoDonador> buscarPorNombreSimilar(String pattern);

    @Query("SELECT * FROM CirculoDonador WHERE nombre LIKE '%' || :filtro || '%'")
    List<CirculoDonador> buscarPorCoincidencia(String filtro);
}
