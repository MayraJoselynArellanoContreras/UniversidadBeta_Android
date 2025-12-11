package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Usuarios")
public class Usuarios {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idUsuario")
    public int idUsuario;

    @NonNull
    @ColumnInfo(name = "usuario")
    public String usuario;

    @NonNull
    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "rol")
    public String rol;

    public Usuarios(int idUsuario, @NonNull String usuario, @NonNull String password, String rol) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    @Override
    public String toString() {
        return usuario;
    }
}

