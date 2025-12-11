package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CirculoDonador")
public class CirculoDonador {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idCirculo")
    public int idCirculo;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    public CirculoDonador(int idCirculo, @NonNull String nombre, String descripcion) {
        this.idCirculo = idCirculo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

