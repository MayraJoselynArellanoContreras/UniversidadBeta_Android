// EventosAdapter.java
package ReciclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import entities.Evento;
import java.util.List;
import java.text.DecimalFormat;
import com.example.bd_sqlite_2025.EventosActivity;
import com.example.bd_sqlite_2025.R;

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.EventoViewHolder> {

    private List<Evento> eventos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Evento evento);
    }

    public EventosAdapter(List<Evento> eventos, OnItemClickListener listener) {
        this.eventos = eventos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.bind(evento, listener);
    }

    @Override
    public int getItemCount() {
        return eventos != null ? eventos.size() : 0;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
        notifyDataSetChanged();
    }

    static class EventoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombre, txtFecha, txtTipo, txtLugar, txtMeta;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtLugar = itemView.findViewById(R.id.txtLugar);
            txtMeta = itemView.findViewById(R.id.txtMeta);
        }

        public void bind(final Evento evento, final OnItemClickListener listener) {
            txtNombre.setText(evento.getNombre());
            txtFecha.setText(evento.getFecha());
            txtTipo.setText(evento.getTipo());
            txtLugar.setText(evento.getLugar());


            DecimalFormat df = new DecimalFormat("$#,###.##");
            txtMeta.setText(df.format(evento.getMetaRecaudacion()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(evento);
                }
            });
        }
    }
}
