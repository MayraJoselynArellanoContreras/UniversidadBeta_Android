package entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "AsistenteEvento",
        primaryKeys = {"idEvento", "idDonador"},
        foreignKeys = {
                @ForeignKey(
                        entity = Evento.class,
                        parentColumns = "idEvento",
                        childColumns = "idEvento"
                ),
                @ForeignKey(
                        entity = Donador.class,
                        parentColumns = "idDonador",
                        childColumns = "idDonador"
                )
        },
        indices = {@Index("idDonador")}
)
public class AsistenteEvento {

    @ColumnInfo(name = "idEvento")
    public int idEvento;

    @ColumnInfo(name = "idDonador")
    public int idDonador;

    @ColumnInfo(name = "idAsistente")
    public int idAsistente;

    public AsistenteEvento(int idEvento, int idDonador, int idAsistente) {
        this.idEvento = idEvento;
        this.idDonador = idDonador;
        this.idAsistente = idAsistente;
    }

    @Override
    public String toString() {
        return "Asistente " + idAsistente;
    }
}

