package db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import controlers.*;        // ← ESTO ES LO QUE FALTABA
import entities.*;         // ← ESTO TAMBIÉN

@Database(
        entities = {
                AnioFiscal.class,
                CategoriaDonador.class,
                CirculoDonador.class,
                Corporacion.class,
                Donador.class,
                RepresentanteClase.class,
                DonadoresRepresentante.class,
                Garantia.class,
                Donativo.class,
                Pago.class,
                Evento.class,
                AsistenteEvento.class,
                Usuarios.class,
                Voluntario.class
        },
        version = 2,
        exportSchema = false
)
public abstract class UniversidadBeta extends RoomDatabase {

    private static UniversidadBeta INSTANCE;

    // === TODOS TUS DAOs (uno por uno) ===
    public abstract AnioFiscalDAO anioFiscalDAO();
    public abstract CategoriaDonadorDAO categoriaDonadorDAO();
    public abstract CirculoDonadorDAO circuloDonadorDAO();
    public abstract CorporacionDAO corporacionDAO();
    public abstract DonadorDAO donadorDAO();
    public abstract RepresentanteClaseDAO representanteClaseDAO();
    public abstract DonadoresRepresentanteDAO donadoresRepresentanteDAO();
    public abstract GarantiaDAO garantiaDAO();
    public abstract DonativoDAO donativoDAO();
    public abstract PagoDAO pagoDAO();
    public abstract EventoDAO eventoDAO();
    public abstract AsistenteEventoDAO asistenteEventoDAO();
    public abstract UsuariosDAO usuariosDAO();
    public abstract VoluntarioDAO voluntarioDAO();

    public static UniversidadBeta getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            UniversidadBeta.class,
                            "UniversidadBeta"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}