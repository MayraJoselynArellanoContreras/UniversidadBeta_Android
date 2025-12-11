package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Donativo",
        foreignKeys = {
                @ForeignKey(
                        entity = Donador.class,
                        parentColumns = "idDonador",
                        childColumns = "idDonador"
                ),
                @ForeignKey(
                        entity = Garantia.class,
                        parentColumns = "idGarantia",
                        childColumns = "idGarantia"
                )
        },
        indices = {@Index("idDonador"), @Index("idGarantia")}
)
public class Donativo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idDonativo")
    public int idDonativo;

    @ColumnInfo(name = "monto")
    public double monto;

    @NonNull
    @ColumnInfo(name = "fecha")
    public String fecha;

    @ColumnInfo(name = "idDonador")
    public int idDonador;

    @ColumnInfo(name = "idGarantia")
    public Integer idGarantia;

    public Donativo(int idDonativo, double monto, @NonNull String fecha,
                    int idDonador, Integer idGarantia) {
        this.idDonativo = idDonativo;
        this.monto = monto;
        this.fecha = fecha;
        this.idDonador = idDonador;
        this.idGarantia = idGarantia;
    }

    @Override
    public String toString() {
        return "Donativo: $" + monto;
    }
}

