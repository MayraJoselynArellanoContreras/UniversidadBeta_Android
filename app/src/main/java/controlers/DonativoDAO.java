package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Donativo;

@Dao
public interface DonativoDAO {

    @Insert
    void agregarDonativo(Donativo donativo);

    @Delete
    void eliminarDonativo(Donativo donativo);

    @Query("DELETE FROM Donativo WHERE idDonativo = :id")
    void eliminarDonativoPorId(int id);

    @Update
    void actualizarDonativo(Donativo donativo);

    @Query("UPDATE Donativo SET monto = :m WHERE idDonativo = :id")
    void actualizarDonativoPorId(double m, int id);

    @Query("SELECT * FROM Donativo")
    List<Donativo> mostrarTodos();

    @Query("SELECT * FROM Donativo WHERE fecha = :f")
    List<Donativo> mostrarPorFecha(String f);

    @Query("SELECT * FROM Donativo WHERE monto >= :m")
    List<Donativo> buscarPorMontoMinimo(double m);
}
