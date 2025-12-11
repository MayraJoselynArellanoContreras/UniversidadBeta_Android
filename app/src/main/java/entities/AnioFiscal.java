package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AnioFiscal")
public class AnioFiscal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idAnioFiscal")
    public int idAnioFiscal;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @NonNull
    @ColumnInfo(name = "fechaInicio")
    public String fechaInicio;

    @NonNull
    @ColumnInfo(name = "fechaFin")
    public String fechaFin;

    @ColumnInfo(name = "activo")
    public Boolean activo;

    @ColumnInfo(name = "fechaCreacion")
    public String fechaCreacion;

    public AnioFiscal(int idAnioFiscal, @NonNull String nombre, @NonNull String fechaInicio,
                      @NonNull String fechaFin, Boolean activo, String fechaCreacion) {
        this.idAnioFiscal = idAnioFiscal;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

