package controlers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Usuarios;

@Dao
public interface UsuariosDAO {

    @Insert
    void agregarUsuario(Usuarios usuario);

    @Delete
    void eliminarUsuario(Usuarios usuario);

    @Query("DELETE FROM Usuarios WHERE idUsuario = :id")
    void eliminarUsuarioPorId(int id);

    @Update
    void actualizarUsuario(Usuarios usuario);

    @Query("UPDATE Usuarios SET usuario = :u WHERE idUsuario = :id")
    void actualizarUsuarioPorId(String u, int id);

    @Query("SELECT * FROM Usuarios")
    List<Usuarios> mostrarTodos();

    @Query("SELECT * FROM Usuarios WHERE usuario = :u")
    List<Usuarios> mostrarPorUsuario(String u);

    @Query("SELECT * FROM Usuarios WHERE usuario LIKE '%' || :filtro || '%'")
    List<Usuarios> buscarPorCoincidencia(String filtro);

    @Query("SELECT * FROM Usuarios WHERE usuario = :u AND password = :p")
    Usuarios login(String u, String p);
}
