
package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Evento")
public class Evento {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idEvento")
    public int idEvento;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    @NonNull
    @ColumnInfo(name = "fecha")
    public String fecha;

    @NonNull
    @ColumnInfo(name = "lugar")
    public String lugar;

    @NonNull
    @ColumnInfo(name = "tipo")
    public String tipo;

    @NonNull
    @ColumnInfo(name = "metaRecaudacion")
    public double metaRecaudacion;

    // Constructor completo
    public Evento(@NonNull String nombre, String descripcion, @NonNull String fecha,
                  @NonNull String lugar, @NonNull String tipo, double metaRecaudacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
        this.metaRecaudacion = metaRecaudacion;
    }

    // Getters
    public int getIdEvento() { return idEvento; }

    @NonNull
    public String getNombre() { return nombre; }

    public String getDescripcion() { return descripcion; }

    @NonNull
    public String getFecha() { return fecha; }

    @NonNull
    public String getLugar() { return lugar; }

    @NonNull
    public String getTipo() { return tipo; }

    public double getMetaRecaudacion() { return metaRecaudacion; }

    @Override
    public String toString() {
        return nombre + " (" + fecha + ")";
    }
}