package com.example.t2.database;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "renta_vehiculos.db";
    private static final int VERSION_BD = 6;

    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tablas Maestras
        db.execSQL("CREATE TABLE Rol (" +
                "idRol INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombreRol TEXT NOT NULL);");

        db.execSQL("CREATE TABLE Usuario (" +
                "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "correo TEXT UNIQUE NOT NULL, " +
                "clave TEXT NOT NULL DEFAULT '123456', " +
                "idRol INTEGER, " +
                "FOREIGN KEY(idRol) REFERENCES Rol(idRol));");

        db.execSQL("CREATE TABLE Sede (" +
                "idSede INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombreSede TEXT NOT NULL);");

        db.execSQL("CREATE TABLE Categoria (" +
                "idCategoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descripcion TEXT NOT NULL);");

        db.execSQL("CREATE TABLE Flota (" +
                "idFlota INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idSede INTEGER, " +
                "FOREIGN KEY(idSede) REFERENCES Sede(idSede));");

        db.execSQL("CREATE TABLE Vehiculo (" +
                "idVehiculo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "placa TEXT UNIQUE NOT NULL, " +
                "marca TEXT NOT NULL, " +
                "modelo TEXT NOT NULL, " +
                "estado TEXT NOT NULL, " +
                "idFlota INTEGER, " +
                "idCategoria INTEGER, " +
                "FOREIGN KEY(idFlota) REFERENCES Flota(idFlota), " +
                "FOREIGN KEY(idCategoria) REFERENCES Categoria(idCategoria));");

        // Transacciones principales
        db.execSQL("CREATE TABLE Reserva (" +
                "idReserva INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fechaReserva TEXT NOT NULL, " +
                "montoAdelanto REAL DEFAULT 0, " +
                "estadoReserva TEXT NOT NULL DEFAULT 'Confirmada', " +
                "idCliente INTEGER, " +
                "idVehiculo INTEGER, " +
                "FOREIGN KEY(idCliente) REFERENCES Usuario(idUsuario), " +
                "FOREIGN KEY(idVehiculo) REFERENCES Vehiculo(idVehiculo));");

        db.execSQL("CREATE TABLE ComprobanteReserva (" +
                "idComprobanteReserva INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idReserva INTEGER, " +
                "montoAdelanto REAL NOT NULL, " +
                "fechaEmision TEXT NOT NULL, " +
                "FOREIGN KEY(idReserva) REFERENCES Reserva(idReserva));");

        db.execSQL("CREATE TABLE ConstanciaReserva (" +
                "idConstancia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idReserva INTEGER, " +
                "codigoConstancia TEXT NOT NULL, " +
                "fechaEmision TEXT NOT NULL, " +
                "FOREIGN KEY(idReserva) REFERENCES Reserva(idReserva));");

        db.execSQL("CREATE TABLE ContratoAlquiler (" +
                "idContrato INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fechaInicio TEXT NOT NULL, " +
                "fechaFin TEXT NOT NULL, " +
                "montoTotal REAL NOT NULL, " +
                "idCliente INTEGER, " +
                "idRecepcionista INTEGER, " +
                "idVehiculo INTEGER, " +
                "idReserva INTEGER, " +
                "FOREIGN KEY(idCliente) REFERENCES Usuario(idUsuario), " +
                "FOREIGN KEY(idRecepcionista) REFERENCES Usuario(idUsuario), " +
                "FOREIGN KEY(idVehiculo) REFERENCES Vehiculo(idVehiculo), " +
                "FOREIGN KEY(idReserva) REFERENCES Reserva(idReserva));");

        db.execSQL("CREATE TABLE ComprobantePago (" +
                "idComprobante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idContrato INTEGER, " +
                "monto REAL NOT NULL, " +
                "fechaPago TEXT NOT NULL, " +
                "FOREIGN KEY(idContrato) REFERENCES ContratoAlquiler(idContrato));");

        db.execSQL("CREATE TABLE OrdenMantenimiento (" +
                "idOrden INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo TEXT NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "costo REAL NOT NULL, " +
                "idVehiculo INTEGER, " +
                "FOREIGN KEY(idVehiculo) REFERENCES Vehiculo(idVehiculo));");

        db.execSQL("CREATE TABLE PolizaSeguro (" +
                "idPoliza INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numeroPoliza TEXT NOT NULL, " +
                "cobertura TEXT NOT NULL, " +
                "montoCobertura REAL NOT NULL, " +
                "fechaVencimiento TEXT NOT NULL, " +
                "idVehiculo INTEGER, " +
                "FOREIGN KEY(idVehiculo) REFERENCES Vehiculo(idVehiculo));");

        db.execSQL("CREATE TABLE HojaInspeccion (" +
                "idInspeccion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha TEXT NOT NULL, " +
                "observaciones TEXT, " +
                "idInspector INTEGER, " +
                "idVehiculo INTEGER, " +
                "FOREIGN KEY(idInspector) REFERENCES Usuario(idUsuario), " +
                "FOREIGN KEY(idVehiculo) REFERENCES Vehiculo(idVehiculo));");

        db.execSQL("CREATE TABLE Penalidad (" +
                "idPenalidad INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "motivo TEXT NOT NULL, " +
                "monto REAL NOT NULL, " +
                "idContrato INTEGER, " +
                "FOREIGN KEY(idContrato) REFERENCES ContratoAlquiler(idContrato));");

        // Datos iniciales semillas
        db.execSQL("INSERT INTO Rol (nombreRol) VALUES ('Cliente'), ('Recepcionista de venta'), ('Inspector de flota'), ('Recepcionista de devolución');");
        db.execSQL("INSERT INTO Sede (nombreSede) VALUES ('Sede Central - Lima'), ('Sede Norte - Trujillo'), ('Sede Sur - Arequipa');");
        db.execSQL("INSERT INTO Categoria (descripcion) VALUES ('Económico'), ('SUV'), ('Camioneta 4x4'), ('Lujo');");
        db.execSQL("INSERT INTO Flota (idSede) VALUES (1), (2), (3);");

        // Cuentas de demostración (Administración y Cliente)
        db.execSQL("INSERT INTO Usuario (nombre, correo, clave, idRol) VALUES ('Administrador Sistema', 'admin@renta.com', '123456', 2);");
        db.execSQL("INSERT INTO Usuario (nombre, correo, clave, idRol) VALUES ('Junior Ramos Flores', 'cliente@gmail.com', '123456', 1);");

        insertarVehiculosPredeterminados(db);
        insertarDatosDemostracion(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            insertarVehiculosPredeterminados(db);
        }
        if (oldVersion < 6) {
            actualizarUsuariosDemostracion(db);
        }
        if (oldVersion < 5) {
            insertarDatosDemostracion(db);
        }
    }

    private void actualizarUsuariosDemostracion(SQLiteDatabase db) {
        // Se conservan los IDs, las claves y las relaciones con reservas y contratos.
        actualizarUsuarioDemo(db, "cliente@gmail.com", "Junior Ramos Flores", "cliente@gmail.com");
        actualizarUsuarioDemo(db, "ana.ventas@renta.com", "Christian De la Cadena Mosquera", "christian.ventas@renta.com");
        actualizarUsuarioDemo(db, "luis.ventas@renta.com", "Aldair Romero Medina", "aldair.ventas@renta.com");
        actualizarUsuarioDemo(db, "pedro.ventas@renta.com", "Antony Yerén Lezama", "antony.ventas@renta.com");
    }

    private void actualizarUsuarioDemo(SQLiteDatabase db, String correoAnterior, String nombre, String correoNuevo) {
        ContentValues datos = new ContentValues();
        datos.put("nombre", nombre);
        datos.put("correo", correoNuevo);
        // Si alguien ya registró el correo nuevo, no se sobrescribe su cuenta ni se bloquea la actualización.
        db.updateWithOnConflict("Usuario", datos, "correo = ?", new String[]{correoAnterior},
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertarVehiculosPredeterminados(SQLiteDatabase db) {
        // Las placas son únicas; al actualizar se respetan los vehículos existentes.
        db.execSQL("INSERT OR IGNORE INTO Vehiculo (placa, marca, modelo, estado, idFlota, idCategoria) VALUES " +
                "('RTA-101', 'Toyota', 'Yaris', 'Disponible', 1, 1), " +
                "('RTA-102', 'Hyundai', 'Accent', 'Disponible', 1, 1), " +
                "('RTA-103', 'Kia', 'Sportage', 'Disponible', 1, 2), " +
                "('RTA-104', 'Toyota', 'Hilux', 'Disponible', 1, 3), " +
                "('RTA-105', 'Chevrolet', 'Onix', 'Disponible', 2, 1), " +
                "('RTA-106', 'Nissan', 'Kicks', 'Disponible', 2, 2), " +
                "('RTA-107', 'Mitsubishi', 'L200', 'Disponible', 2, 3), " +
                "('RTA-108', 'Mazda', 'CX-5', 'Disponible', 2, 2), " +
                "('RTA-109', 'Suzuki', 'Swift', 'Disponible', 3, 1), " +
                "('RTA-110', 'Toyota', 'RAV4', 'Disponible', 3, 2), " +
                "('RTA-111', 'BMW', 'Serie 3', 'Disponible', 3, 4);");
    }

    private long buscarId(SQLiteDatabase db, String tabla, String columnaId, String columnaClave, String valor) {
        try (Cursor c = db.query(tabla, new String[]{columnaId}, columnaClave + " = ?",
                new String[]{valor}, null, null, null, "1")) {
            return c.moveToFirst() ? c.getLong(0) : -1;
        }
    }

    private boolean existe(SQLiteDatabase db, String tabla, String condicion, String... argumentos) {
        try (Cursor c = db.query(tabla, new String[]{"1"}, condicion, argumentos,
                null, null, null, "1")) {
            return c.moveToFirst();
        }
    }

    private void insertarUsuarioDemo(SQLiteDatabase db, String nombre, String correo, int rol) {
        ContentValues datos = new ContentValues();
        datos.put("nombre", nombre);
        datos.put("correo", correo);
        datos.put("clave", "123456");
        datos.put("idRol", rol);
        db.insertWithOnConflict("Usuario", null, datos, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertarDatosDemostracion(SQLiteDatabase db) {
        // Las claves naturales evitan duplicados al actualizar sin borrar información del usuario.
        insertarUsuarioDemo(db, "Christian De la Cadena Mosquera", "christian.ventas@renta.com", 2);
        insertarUsuarioDemo(db, "Aldair Romero Medina", "aldair.ventas@renta.com", 2);
        insertarUsuarioDemo(db, "Marta Díaz", "marta.ventas@renta.com", 2);
        insertarUsuarioDemo(db, "Antony Yerén Lezama", "antony.ventas@renta.com", 2);
        insertarUsuarioDemo(db, "Carmen Ruiz", "carmen.inspeccion@renta.com", 3);
        insertarUsuarioDemo(db, "Diego Flores", "diego.devolucion@renta.com", 4);
        insertarUsuarioDemo(db, "Lucía Gómez", "lucia.cliente@renta.com", 1);

        long junior = buscarId(db, "Usuario", "idUsuario", "correo", "cliente@gmail.com");
        long lucia = buscarId(db, "Usuario", "idUsuario", "correo", "lucia.cliente@renta.com");
        long inspector = buscarId(db, "Usuario", "idUsuario", "correo", "carmen.inspeccion@renta.com");
        long[] recepcionistas = {
                buscarId(db, "Usuario", "idUsuario", "correo", "christian.ventas@renta.com"),
                buscarId(db, "Usuario", "idUsuario", "correo", "aldair.ventas@renta.com"),
                buscarId(db, "Usuario", "idUsuario", "correo", "marta.ventas@renta.com"),
                buscarId(db, "Usuario", "idUsuario", "correo", "antony.ventas@renta.com")
        };
        String[] placas = {"RTA-101", "RTA-102", "RTA-101", "RTA-104"};
        String[] inicios = {"2026-09-03", "2026-09-18", "2026-10-05", "2026-10-12"};
        String[] fines = {"2026-09-07", "2026-09-21", "2026-10-09", "2026-10-15"};
        double[] montos = {480, 390, 560, 720};

        long vehiculoReservado = buscarId(db, "Vehiculo", "idVehiculo", "placa", "RTA-101");
        long reserva = -1;
        if (junior > 0 && vehiculoReservado > 0) {
            if (!existe(db, "Reserva", "idCliente = ? AND idVehiculo = ? AND fechaReserva = ?",
                    String.valueOf(junior), String.valueOf(vehiculoReservado), "2026-09-01")) {
                ContentValues datos = new ContentValues();
                datos.put("fechaReserva", "2026-09-01");
                datos.put("montoAdelanto", 100);
                datos.put("estadoReserva", "Atendida");
                datos.put("idCliente", junior);
                datos.put("idVehiculo", vehiculoReservado);
                reserva = db.insertOrThrow("Reserva", null, datos);
            }
        }
        if (reserva > 0) {
            if (!existe(db, "ComprobanteReserva", "idReserva = ?", String.valueOf(reserva))) {
                ContentValues datos = new ContentValues();
                datos.put("idReserva", reserva);
                datos.put("montoAdelanto", 100);
                datos.put("fechaEmision", "2026-09-01");
                db.insertOrThrow("ComprobanteReserva", null, datos);
            }
            if (!existe(db, "ConstanciaReserva", "idReserva = ?", String.valueOf(reserva))) {
                ContentValues datos = new ContentValues();
                datos.put("idReserva", reserva);
                datos.put("codigoConstancia", "CONST-DEMO-2026-001");
                datos.put("fechaEmision", "2026-09-01");
                db.insertOrThrow("ConstanciaReserva", null, datos);
            }
        }

        for (int i = 0; i < placas.length; i++) {
            long vehiculo = buscarId(db, "Vehiculo", "idVehiculo", "placa", placas[i]);
            long cliente = i == 3 ? lucia : junior;
            if (vehiculo <= 0 || cliente <= 0 || recepcionistas[i] <= 0) continue;
            if (existe(db, "ContratoAlquiler", "idVehiculo = ? AND fechaInicio = ? AND idCliente = ?",
                    String.valueOf(vehiculo), inicios[i], String.valueOf(cliente))) continue;
            ContentValues datos = new ContentValues();
            datos.put("fechaInicio", inicios[i]);
            datos.put("fechaFin", fines[i]);
            datos.put("montoTotal", montos[i]);
            datos.put("idCliente", cliente);
            datos.put("idRecepcionista", recepcionistas[i]);
            datos.put("idVehiculo", vehiculo);
            if (i == 0 && reserva > 0) datos.put("idReserva", reserva);
            long contrato = db.insertOrThrow("ContratoAlquiler", null, datos);
            ContentValues pago = new ContentValues();
            pago.put("idContrato", contrato);
            pago.put("monto", montos[i]);
            pago.put("fechaPago", inicios[i]);
            db.insertOrThrow("ComprobantePago", null, pago);
            if (i == 0) {
                ContentValues penalidad = new ContentValues();
                penalidad.put("idContrato", contrato);
                penalidad.put("motivo", "Retraso en devolución (demo)");
                penalidad.put("monto", 50);
                db.insertOrThrow("Penalidad", null, penalidad);
            }
        }

        String[] placasMantenimiento = {"RTA-105", "RTA-105", "RTA-106"};
        String[] fechasMantenimiento = {"2026-09-10", "2026-10-02", "2026-09-25"};
        String[] tipos = {"Preventivo", "Correctivo", "Preventivo"};
        double[] costos = {180, 350, 210};
        for (int i = 0; i < tipos.length; i++) {
            long vehiculo = buscarId(db, "Vehiculo", "idVehiculo", "placa", placasMantenimiento[i]);
            if (vehiculo > 0 && !existe(db, "OrdenMantenimiento", "idVehiculo = ? AND fecha = ? AND tipo = ?",
                    String.valueOf(vehiculo), fechasMantenimiento[i], tipos[i])) {
                ContentValues datos = new ContentValues();
                datos.put("idVehiculo", vehiculo);
                datos.put("fecha", fechasMantenimiento[i]);
                datos.put("tipo", tipos[i]);
                datos.put("costo", costos[i]);
                db.insertOrThrow("OrdenMantenimiento", null, datos);
            }
        }

        String[] placasPoliza = {"RTA-101", "RTA-105", "RTA-111"};
        String[] coberturas = {"Pérdida o Robo", "Accidente", "Siniestro Todo Riesgo"};
        for (int i = 0; i < placasPoliza.length; i++) {
            String numero = "POL-DEMO-2026-00" + (i + 1);
            long vehiculo = buscarId(db, "Vehiculo", "idVehiculo", "placa", placasPoliza[i]);
            if (vehiculo > 0 && !existe(db, "PolizaSeguro", "numeroPoliza = ?", numero)) {
                ContentValues datos = new ContentValues();
                datos.put("numeroPoliza", numero);
                datos.put("cobertura", coberturas[i]);
                datos.put("montoCobertura", 30000 + i * 15000);
                datos.put("fechaVencimiento", "2027-12-31");
                datos.put("idVehiculo", vehiculo);
                db.insertOrThrow("PolizaSeguro", null, datos);
            }
        }

        if (inspector > 0 && vehiculoReservado > 0 && !existe(db, "HojaInspeccion",
                "idVehiculo = ? AND fecha = ? AND idInspector = ?", String.valueOf(vehiculoReservado),
                "2026-09-07", String.valueOf(inspector))) {
            ContentValues datos = new ContentValues();
            datos.put("fecha", "2026-09-07");
            datos.put("observaciones", "Devolución con retraso (ejemplo)");
            datos.put("idInspector", inspector);
            datos.put("idVehiculo", vehiculoReservado);
            db.insertOrThrow("HojaInspeccion", null, datos);
        }
    }
}
