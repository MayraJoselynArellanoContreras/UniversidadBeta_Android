package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import entities.Voluntario;
//import com.example.bd_sqlite_2025.Vo;
import com.example.bd_sqlite_2025.R;

public class VoluntarioAdapter extends RecyclerView.Adapter<VoluntarioAdapter.VoluntarioViewHolder> {

    private List<Voluntario> listaVoluntarios;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Voluntario voluntario);
        void onItemLongClick(Voluntario voluntario);
    }

    public VoluntarioAdapter(List<Voluntario> listaVoluntarios, OnItemClickListener listener) {
        this.listaVoluntarios = listaVoluntarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoluntarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voluntarios, parent, false);
        return new VoluntarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoluntarioViewHolder holder, int position) {
        Voluntario voluntario = listaVoluntarios.get(position);
        holder.bind(voluntario, listener);
    }

    @Override
    public int getItemCount() {
        return listaVoluntarios.size();
    }

    static class VoluntarioViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombre, txtTelefono, txtEstado, txtEstudiante;

        public VoluntarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtEstudiante = itemView.findViewById(R.id.txtEstudiante);
        }

        public void bind(final Voluntario voluntario, final OnItemClickListener listener) {
            txtNombre.setText(voluntario.getNombre());
            txtTelefono.setText(voluntario.getTelefono());
            txtEstado.setText(voluntario.isActivo() ? "Activo" : "Inactivo");
            txtEstudiante.setText(voluntario.isEstudiante() ? "Estudiante" : "Voluntario");

            itemView.setOnClickListener(v -> listener.onItemClick(voluntario));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(voluntario);
                return true;
            });
        }
    }
}
