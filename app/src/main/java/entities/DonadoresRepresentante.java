package entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "DonadoresRepresentante",
        primaryKeys = {"idDonador", "idRepresentante"},
        foreignKeys = {
                @ForeignKey(
                        entity = Donador.class,
                        parentColumns = "idDonador",
                        childColumns = "idDonador"
                ),
                @ForeignKey(
                        entity = RepresentanteClase.class,
                        parentColumns = "idRepresentante",
                        childColumns = "idRepresentante"
                )
        },
        indices = {@Index("idRepresentante")}
)
public class DonadoresRepresentante {

    @ColumnInfo(name = "idDonador")
    public int idDonador;

    @ColumnInfo(name = "idRepresentante")
    public int idRepresentante;

    @ColumnInfo(name = "estatus")
    public Boolean estatus;

    public DonadoresRepresentante(int idDonador, int idRepresentante, Boolean estatus) {
        this.idDonador = idDonador;
        this.idRepresentante = idRepresentante;
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return "Representante " + idRepresentante + " de Donador " + idDonador;
    }
}
