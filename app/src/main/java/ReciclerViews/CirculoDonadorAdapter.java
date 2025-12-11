package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import entities.CirculoDonador;
import com.example.bd_sqlite_2025.EventosActivity;
import com.example.bd_sqlite_2025.R;

public class CirculoDonadorAdapter extends RecyclerView.Adapter<CirculoDonadorAdapter.ViewHolder> {

    private List<CirculoDonador> circulos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CirculoDonador circulo);
        void onItemLongClick(CirculoDonador circulo);
    }

    public CirculoDonadorAdapter(List<CirculoDonador> circulos, OnItemClickListener listener) {
        this.circulos = circulos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circulo_donador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CirculoDonador circulo = circulos.get(position);

        holder.tvNombre.setText(circulo.nombre);
        holder.tvId.setText("ID: " + circulo.idCirculo);
        holder.tvDescripcion.setText(circulo.descripcion != null ? circulo.descripcion : "Sin descripción");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(circulo);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(circulo);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return circulos.size();
    }

    public void setCirculos(List<CirculoDonador> circulos) {
        this.circulos = circulos;
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