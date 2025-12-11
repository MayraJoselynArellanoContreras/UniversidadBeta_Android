package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Pago;

@Dao
public interface PagoDAO {

    @Insert
    void agregarPago(Pago pago);

    @Delete
    void eliminarPago(Pago pago);

    @Query("DELETE FROM Pago WHERE idPago = :id")
    void eliminarPagoPorId(int id);

    @Update
    void actualizarPago(Pago pago);

    @Query("UPDATE Pago SET monto = :m WHERE idPago = :id")
    void actualizarPagoPorId(double m, int id);

    @Query("SELECT * FROM Pago")
    List<Pago> mostrarTodos();

    @Query("SELECT * FROM Pago WHERE fecha = :f")
    List<Pago> mostrarPorFecha(String f);

    @Query("SELECT * FROM Pago WHERE monto >= :m")
    List<Pago> buscarPorMontoMinimo(double m);
}
