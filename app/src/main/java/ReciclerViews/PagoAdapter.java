package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import entities.Pago;
import entities.Garantia;
import com.example.bd_sqlite_2025.R;

public class PagoAdapter extends RecyclerView.Adapter<PagoAdapter.ViewHolder> {

    private List<Pago> pagos;
    private OnItemClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnItemClickListener {
        void onItemClick(Pago pago);
        void onItemLongClick(Pago pago);
    }

    public PagoAdapter(List<Pago> pagos, OnItemClickListener listener) {
        this.pagos = pagos;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pago, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pago pago = pagos.get(position);

        holder.tvMonto.setText(currencyFormat.format(pago.monto));
        holder.tvFecha.setText(pago.fecha);
        holder.tvId.setText("ID: " + pago.idPago);
        holder.tvDonativoId.setText("Donativo ID: " + pago.idDonativo);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pago);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(pago);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return pagos.size();
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonto, tvFecha, tvId, tvDonativoId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvId = itemView.findViewById(R.id.tvId);
            tvDonativoId = itemView.findViewById(R.id.tvDonativoId);
        }
    }
}
