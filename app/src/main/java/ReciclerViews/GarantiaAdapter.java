package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import entities.Garantia;
import com.example.bd_sqlite_2025.R;

public class GarantiaAdapter extends RecyclerView.Adapter<GarantiaAdapter.ViewHolder> {

    private List<Garantia> garantias;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Garantia garantia);
        void onItemLongClick(Garantia garantia);
    }

    public GarantiaAdapter(List<Garantia> garantias, OnItemClickListener listener) {
        this.garantias = garantias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_garantia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Garantia garantia = garantias.get(position);

        holder.tvNombre.setText(garantia.nombre);
        holder.tvId.setText("ID: " + garantia.idGarantia);

        // Extraer información resumida de la descripción
        String descripcionResumida = obtenerDescripcionResumida(garantia.descripcion);
        holder.tvDescripcion.setText(descripcionResumida);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(garantia);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(garantia);
            }
            return true;
        });
    }

    private String obtenerDescripcionResumida(String descripcionCompleta) {
        if (descripcionCompleta == null || descripcionCompleta.isEmpty()) {
            return "Sin descripción";
        }

        // Tomar las primeras 2 líneas o primeros 100 caracteres
        String[] lineas = descripcionCompleta.split("\n");
        if (lineas.length >= 2) {
            return lineas[0] + " | " + lineas[1];
        } else {
            return descripcionCompleta.length() > 100 ?
                    descripcionCompleta.substring(0, 100) + "..." :
                    descripcionCompleta;
        }
    }

    @Override
    public int getItemCount() {
        return garantias.size();
    }

    public void setGarantias(List<Garantia> garantias) {
        this.garantias = garantias;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvId, tvDescripcion;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvId = itemView.findViewById(R.id.tvId);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }
}