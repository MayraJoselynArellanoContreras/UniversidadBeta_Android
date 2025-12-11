package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import entities.AnioFiscal;
import com.example.bd_sqlite_2025.EventosActivity;
import com.example.bd_sqlite_2025.R;

public class AnioFiscalAdapter extends RecyclerView.Adapter<AnioFiscalAdapter.ViewHolder> {

    private List<AnioFiscal> aniosFiscales;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AnioFiscal anioFiscal);
        void onItemLongClick(AnioFiscal anioFiscal);
    }

    public AnioFiscalAdapter(List<AnioFiscal> aniosFiscales, OnItemClickListener listener) {
        this.aniosFiscales = aniosFiscales;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anio_fiscal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnioFiscal anioFiscal = aniosFiscales.get(position);

        holder.tvNombre.setText(anioFiscal.nombre);
        holder.tvId.setText("ID: " + anioFiscal.idAnioFiscal);
        holder.tvRango.setText(anioFiscal.fechaInicio + " a " + anioFiscal.fechaFin);

        // Estado activo/inactivo
        if (anioFiscal.activo != null && anioFiscal.activo) {
            holder.tvEstado.setText("ACTIVO");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.tvEstado.setText("INACTIVO");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(anioFiscal);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(anioFiscal);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return aniosFiscales.size();
    }

    public void setAniosFiscales(List<AnioFiscal> aniosFiscales) {
        this.aniosFiscales = aniosFiscales;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvId, tvRango, tvEstado;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvId = itemView.findViewById(R.id.tvId);
            tvRango = itemView.findViewById(R.id.tvRango);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}