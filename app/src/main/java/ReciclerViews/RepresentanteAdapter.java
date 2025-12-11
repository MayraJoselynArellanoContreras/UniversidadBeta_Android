package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import entities.RepresentanteClase;
import com.example.bd_sqlite_2025.R;

public class RepresentanteAdapter extends RecyclerView.Adapter<RepresentanteAdapter.ViewHolder> {

    private List<RepresentanteClase> representantes;
    private OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(RepresentanteClase representante);
        void onItemLongClick(RepresentanteClase representante);
    }

    public RepresentanteAdapter(List<RepresentanteClase> representantes, OnItemClickListener listener) {
        this.representantes = representantes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_representante, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepresentanteClase representante = representantes.get(position);

        // Configurar datos en las vistas
        holder.txtIdRepresentanteItem.setText("ID: " + representante.idRepresentante);
        holder.txtNombreItem.setText(representante.nombre);

        // Nota: Tu entidad RepresentanteClase no tiene campo "generacion"
        // Si necesitas mostrar generación, debes agregarla a la entidad
        // Por ahora muestro un texto genérico
        holder.txtGeneracionItem.setText("Representante");

        // Teléfono
        if (representante.telefono != null && !representante.telefono.isEmpty()) {
            holder.txtTelefonoItem.setText("Tel: " + representante.telefono);
            holder.txtTelefonoItem.setVisibility(View.VISIBLE);
        } else {
            holder.txtTelefonoItem.setVisibility(View.GONE);
        }

        // Email
        if (representante.email != null && !representante.email.isEmpty()) {
            holder.txtEmailItem.setText(representante.email);
            holder.txtEmailItem.setVisibility(View.VISIBLE);
        } else {
            holder.txtEmailItem.setVisibility(View.GONE);
        }

        // Cambiar color de fondo si está seleccionado
        holder.itemView.setBackgroundColor(
                position == selectedPosition ?
                        holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light) :
                        holder.itemView.getContext().getResources().getColor(android.R.color.transparent)
        );

        // Configurar listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(representante);
                setSelectedPosition(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(representante);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return representantes.size();
    }

    public void setRepresentantes(List<RepresentanteClase> representantes) {
        this.representantes = representantes;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    private void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        if (oldPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPosition);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIdRepresentanteItem, txtNombreItem, txtGeneracionItem,
                txtTelefonoItem, txtEmailItem;

        ViewHolder(View itemView) {
            super(itemView);
            txtIdRepresentanteItem = itemView.findViewById(R.id.txtIdRepresentanteItem);
            txtNombreItem = itemView.findViewById(R.id.txtNombreItem);
            txtGeneracionItem = itemView.findViewById(R.id.txtGeneracionItem);
            txtTelefonoItem = itemView.findViewById(R.id.txtTelefonoItem);
            txtEmailItem = itemView.findViewById(R.id.txtEmailItem);
        }
    }
}