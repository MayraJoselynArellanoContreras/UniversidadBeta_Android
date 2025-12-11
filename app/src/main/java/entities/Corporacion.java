package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Corporacion")
public class Corporacion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idCorporacion")
    public int idCorporacion;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "direccion")
    public String direccion;

    @ColumnInfo(name = "telefono")
    public String telefono;

    public Corporacion(int idCorporacion, @NonNull String nombre,
                       String direccion, String telefono) {
        this.idCorporacion = idCorporacion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

