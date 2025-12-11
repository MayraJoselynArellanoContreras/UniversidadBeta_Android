package ReciclerViews;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import entities.Donador;
import com.example.bd_sqlite_2025.R;

public class DonadorAdapter extends RecyclerView.Adapter<DonadorAdapter.ViewHolder> {

    private List<Donador> donadores;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Donador donador);
        void onItemLongClick(Donador donador);
    }

    public DonadorAdapter(List<Donador> donadores, OnItemClickListener listener) {
        this.donadores = donadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donador donador = donadores.get(position);

        // Configurar datos en las vistas
        holder.txtIdDonadorItem.setText("ID-" + donador.idDonador);
        holder.txtNombreDonador.setText(donador.nombre);

        // Teléfono
        if (donador.telefono != null && !donador.telefono.isEmpty()) {
            holder.txtTelefonoItem.setText("Tel: " + donador.telefono);
        } else {
            holder.txtTelefonoItem.setText("Sin teléfono");
        }

        // Email
        if (donador.email != null && !donador.email.isEmpty()) {
            holder.txtEmailItem.setText(donador.email);
        } else {
            holder.txtEmailItem.setText("Sin email");
        }

        // Categoría (necesitas obtener el nombre de la categoría desde la base de datos)
        // Por ahora mostrar "Categoría ID"
        if (donador.idCategoria != null) {
            holder.txtCategoriaItem.setText("Cat-ID: " + donador.idCategoria);

        } else {
            holder.txtCategoriaItem.setText("Sin cat.");
            holder.txtCategoriaItem.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.darker_gray));
        }

        // Corporación (necesitas obtener el nombre de la corporación desde la base de datos)
        if (donador.idCorporacion != null) {
            holder.txtCorporacionItem.setText("Corp-ID: " + donador.idCorporacion);

        } else {
            holder.txtCorporacionItem.setText("Sin corp.");
            holder.txtCorporacionItem.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.darker_gray));
        }

        // Configurar listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(donador);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(donador);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return donadores.size();
    }

    public void setDonadores(List<Donador> donadores) {
        this.donadores = donadores;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIdDonadorItem, txtNombreDonador, txtTelefonoItem,
                txtEmailItem, txtCategoriaItem, txtCorporacionItem;

        ViewHolder(View itemView) {
            super(itemView);
            txtIdDonadorItem = itemView.findViewById(R.id.txtIdDonadorItem);
            txtNombreDonador = itemView.findViewById(R.id.txtNombreDonador);
            txtTelefonoItem = itemView.findViewById(R.id.txtTelefonoItem);
            txtEmailItem = itemView.findViewById(R.id.txtEmailItem);
            txtCategoriaItem = itemView.findViewById(R.id.txtCategoriaItem);
            txtCorporacionItem = itemView.findViewById(R.id.txtCorporacionItem);
        }
    }
}