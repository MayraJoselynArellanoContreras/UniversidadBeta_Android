package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Voluntario")
public class Voluntario {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idVoluntario")
    public int idVoluntario = 0; // Valor por defecto 0

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "telefono")
    public String telefono;

    @ColumnInfo(name = "direccion")
    public String direccion;

    @ColumnInfo(name = "fechaRegistro")
    public String fechaRegistro;

    @ColumnInfo(name = "estudiante")
    public boolean estudiante = false;

    @ColumnInfo(name = "activo")
    public boolean activo = true;

    @ColumnInfo(name = "observaciones")
    public String observaciones;

    // Constructor único que Room puede usar
    public Voluntario(int idVoluntario, @NonNull String nombre, String email,
                      String telefono, String direccion, String fechaRegistro,
                      boolean estudiante, boolean activo, String observaciones) {
        this.idVoluntario = idVoluntario;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.estudiante = estudiante;
        this.activo = activo;
        this.observaciones = observaciones;
    }

    // Getters
    public int getIdVoluntario() { return idVoluntario; }
    @NonNull public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getFechaRegistro() { return fechaRegistro; }
    public boolean isEstudiante() { return estudiante; }
    public boolean isActivo() { return activo; }
    public String getObservaciones() { return observaciones; }

    @Override
    public String toString() {
        return nombre + " - " + telefono + " - " + (activo ? "Activo" : "Inactivo");
    }
}