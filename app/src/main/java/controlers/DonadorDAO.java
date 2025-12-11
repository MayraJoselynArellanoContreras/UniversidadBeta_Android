package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Donador;

@Dao
public interface DonadorDAO {

    // INSERTAR 1 DONADOR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(Donador donador);

    // INSERTAR VARIOS DONADORES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarLista(List<Donador> lista);

    // ACTUALIZAR DONADOR
    @Update
    void actualizar(Donador donador);

    // ELIMINAR POR OBJETO
    @Delete
    void eliminar(Donador donador);

    // ELIMINAR POR ID
    @Query("DELETE FROM Donador WHERE idDonador = :id")
    void eliminarPorId(int id);

    // MOSTRAR TODOS ORDENADOS POR NOMBRE
    @Query("SELECT * FROM Donador ORDER BY nombre COLLATE NOCASE")
    List<Donador> mostrarTodos();

    // BUSCAR POR ID
    @Query("SELECT * FROM Donador WHERE idDonador = :id LIMIT 1")
    Donador obtenerPorId(int id);

    // BUSCAR POR NOMBRE EXACTO
    @Query("SELECT * FROM Donador WHERE nombre = :nombre")
    List<Donador> buscarPorNombre(String nombre);

    // BUSCAR POR NOMBRE SIMILAR
    @Query("SELECT * FROM Donador WHERE nombre LIKE '%' || :filtro || '%' ORDER BY nombre")
    List<Donador> buscarPorCoincidencia(String filtro);

    // BUSCAR POR CATEGORIA (ID FORÁNEO)
    @Query("SELECT * FROM Donador WHERE idCategoria = :idCat ORDER BY nombre")
    List<Donador> buscarPorCategoria(int idCat);

    // BUSCAR POR CORPORACION (ID FORÁNEO)
    @Query("SELECT * FROM Donador WHERE idCorporacion = :idCorp ORDER BY nombre")
    List<Donador> buscarPorCorporacion(int idCorp);

    // BUSCAR POR AÑO DE GRADUACIÓN
    @Query("SELECT * FROM Donador WHERE anioGraduacion = :anio ORDER BY nombre")
    List<Donador> buscarPorAnioGraduacion(int anio);

    // BUSCAR POR CONYUGE
    @Query("SELECT * FROM Donador WHERE conyuge LIKE '%' || :filtro || '%' ORDER BY nombre")
    List<Donador> buscarPorConyuge(String filtro);

    // BUSCAR POR idDonadorTemp
    @Query("SELECT * FROM Donador WHERE idDonadorTemp = :temp LIMIT 1")
    Donador buscarPorIdTemp(String temp);

    // CONTAR TOTAL DE DONADORES
    @Query("SELECT COUNT(*) FROM Donador")
    int contarDonadores();
}
