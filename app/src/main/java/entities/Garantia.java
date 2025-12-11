package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Garantia")
public class Garantia {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idGarantia")
    public int idGarantia;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    public Garantia(int idGarantia, @NonNull String nombre, String descripcion) {
        this.idGarantia = idGarantia;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

