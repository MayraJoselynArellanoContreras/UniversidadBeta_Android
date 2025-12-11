package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(
        tableName = "Donador",
        foreignKeys = {
                @ForeignKey(
                        entity = CategoriaDonador.class,
                        parentColumns = "idCategoria",
                        childColumns = "idCategoria"
                ),
                @ForeignKey(
                        entity = Corporacion.class,
                        parentColumns = "idCorporacion",
                        childColumns = "idCorporacion"
                )
        },
        indices = {@Index("idCategoria"), @Index("idCorporacion")}
)
public class Donador {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idDonador")
    public int idDonador;

    @NonNull
    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "direccion")
    public String direccion;

    @ColumnInfo(name = "telefono")
    public String telefono;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "idCategoria")
    public Integer idCategoria;

    @ColumnInfo(name = "anioGraduacion")
    public Integer anioGraduacion;

    @ColumnInfo(name = "idCorporacion")
    public Integer idCorporacion;

    @ColumnInfo(name = "conyuge")
    public String conyuge;

    @ColumnInfo(name = "idDonadorTemp")
    public String idDonadorTemp;

    public Donador(int idDonador, @NonNull String nombre, String direccion, String telefono,
                   String email, Integer idCategoria, Integer anioGraduacion,
                   Integer idCorporacion, String conyuge, String idDonadorTemp) {
        this.idDonador = idDonador;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.idCategoria = idCategoria;
        this.anioGraduacion = anioGraduacion;
        this.idCorporacion = idCorporacion;
        this.conyuge = conyuge;
        this.idDonadorTemp = idDonadorTemp;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

