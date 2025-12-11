package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "RepresentanteClase")
public class RepresentanteClase {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idRepresentante")
    public int idRepresentante;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "telefono")
    public String telefono;

    public RepresentanteClase(int idRepresentante, @NonNull String nombre,
                              String email, String telefono) {
        this.idRepresentante = idRepresentante;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
