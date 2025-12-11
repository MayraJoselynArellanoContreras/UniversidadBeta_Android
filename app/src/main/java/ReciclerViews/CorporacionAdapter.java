package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import entities.Corporacion;
import com.example.bd_sqlite_2025.R;

public class CorporacionAdapter extends RecyclerView.Adapter<CorporacionAdapter.ViewHolder> {

    private List<Corporacion> corporaciones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Corporacion corporacion);
        void onItemLongClick(Corporacion corporacion);
    }

    public CorporacionAdapter(List<Corporacion> corporaciones, OnItemClickListener listener) {
        this.corporaciones = corporaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_corporacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Corporacion corporacion = corporaciones.get(position);

        // Configurar datos en las vistas
        holder.txtIdCorporacion.setText("ID: " + corporacion.idCorporacion);
        holder.txtNombre.setText(corporacion.nombre);

        // Dirección (acortada si es muy larga)
        if (corporacion.direccion != null && corporacion.direccion.length() > 50) {
            holder.txtDireccion.setText(corporacion.direccion.substring(0, 50) + "...");
        } else {
            holder.txtDireccion.setText(corporacion.direccion != null ?
                    corporacion.direccion : "Sin dirección");
        }

        // Teléfono (si existe)
        if (corporacion.telefono != null && !corporacion.telefono.isEmpty()) {
            holder.txtTelefono.setText("Tel: " + corporacion.telefono);
            holder.txtTelefono.setVisibility(View.VISIBLE);
        } else {
            holder.txtTelefono.setVisibility(View.GONE);
        }

        // Configurar listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(corporacion);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(corporacion);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return corporaciones.size();
    }

    public void setCorporaciones(List<Corporacion> corporaciones) {
        this.corporaciones = corporaciones;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIdCorporacion, txtNombre, txtDireccion, txtTelefono;

        ViewHolder(View itemView) {
            super(itemView);
            txtIdCorporacion = itemView.findViewById(R.id.txtIdCorporacion);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
        }
    }
}