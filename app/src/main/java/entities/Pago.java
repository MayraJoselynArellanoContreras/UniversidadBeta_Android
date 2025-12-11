package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Pago",
        foreignKeys = {
                @ForeignKey(
                        entity = Donativo.class,
                        parentColumns = "idDonativo",
                        childColumns = "idDonativo"
                )
        },
        indices = {@Index("idDonativo")}
)
public class Pago {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idPago")
    public int idPago;

    @ColumnInfo(name = "monto")
    public double monto;

    @NonNull
    @ColumnInfo(name = "fecha")
    public String fecha;

    @ColumnInfo(name = "idDonativo")
    public int idDonativo;

    public Pago(int idPago, double monto, @NonNull String fecha, int idDonativo) {
        this.idPago = idPago;
        this.monto = monto;
        this.fecha = fecha;
        this.idDonativo = idDonativo;
    }

    @Override
    public String toString() {
        return "Pago: $" + monto;
    }
}
