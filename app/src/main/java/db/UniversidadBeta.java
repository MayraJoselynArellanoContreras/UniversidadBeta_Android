package db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import controlers.*;
import entities.*;

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
        version = 1
)
public abstract class UniversidadBeta extends RoomDatabase {


    private static UniversidadBeta INSTANCE;

    // === DAOS ===
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

    // === Singleton ===
    public static UniversidadBeta getAppDatabase(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    UniversidadBeta.class,
                    "UniversidadBeta"      // <- ✔ Aquí se cambia el nombre REAL de la BD
            ).build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
