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
import entities.Donativo;
import com.example.bd_sqlite_2025.R;

public class DonativoAdapter extends RecyclerView.Adapter<DonativoAdapter.ViewHolder> {

    private List<Donativo> donativos;
    private OnItemClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnItemClickListener {
        void onItemClick(Donativo donativo);
        void onItemLongClick(Donativo donativo);
    }

    public DonativoAdapter(List<Donativo> donativos, OnItemClickListener listener) {
        this.donativos = donativos;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donativo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donativo donativo = donativos.get(position);

        holder.tvId.setText("#" + donativo.idDonativo);
        holder.tvMonto.setText(currencyFormat.format(donativo.monto));
        holder.tvFecha.setText("Fecha: " + donativo.fecha);
        holder.tvDonadorId.setText("Donador ID: " + donativo.idDonador);

        // Estado (si tiene garantía o no)
        if (donativo.idGarantia != null) {
            holder.tvEstado.setText("Con Garantía");
            holder.tvEstado.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvEstado.setText("Sin Garantía");
            holder.tvEstado.setTextColor(holder.itemView.getResources().getColor(android.R.color.darker_gray));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(donativo);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(donativo);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return donativos.size();
    }

    public void setDonativos(List<Donativo> donativos) {
        this.donativos = donativos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvMonto, tvFecha, tvDonadorId, tvEstado;

        ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvDonadorId = itemView.findViewById(R.id.tvDonadorId);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
