package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CategoriaDonador")
public class CategoriaDonador {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idCategoria")
    public int idCategoria;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    public CategoriaDonador(int idCategoria, @NonNull String nombre, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
