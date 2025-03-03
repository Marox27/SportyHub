package com.example.sportyhub.Api;

import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.EquipoMiembro;
import com.example.sportyhub.Modelos.Etiqueta;
import com.example.sportyhub.Modelos.Municipio;
import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Modelos.Pago;
import com.example.sportyhub.Modelos.Participante;
import com.example.sportyhub.Modelos.Provincia;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;

import java.util.List;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint seguro que requiere JWT
    @GET("/api/secured-data")
    Call<ResponseBody> getSecuredData();
    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/api/validar-token")
    Call<Boolean> validarToken(@Query("token") String token);

    @GET("/api/usuarios/user")
    Call<Usuario>getUser(@Query("id") int idUser);

    @GET("/api/usuarios/list")
    Call<List<Usuario>>getAllUsers();

    @GET("/api/usuarios/check-user")
    Call<Boolean>checkUser(@Query("mail") String mail);

    @POST("/api/usuarios/create")
    Call<Usuario>createUser(@Body Usuario usuario);

    @POST("/api/usuarios/edit")
    Call<Boolean>updateUsuarioInfo(@Body Usuario usuario);

    @POST("/api/usuarios/ban-user")
    Call<Boolean>banearUsuario(@Query("idUsuario") int idUsuario);

    @POST("/api/usuarios/desban-user")
    Call<Boolean>desbanearUsuario(@Query("idUsuario") int idUsuario);

    @POST("/api/usuarios/delete")
    Call<Boolean>eliminarUsuario(@Query("idUsuario") int idUsuario);

    /*============================================================================================
    * PETICIONES SOBRE ACTIVIDADES
    *=============================================================================================*/
    @GET("/api/actividades")
    Call<List<Actividad>> getActividades();

    @GET("/api/actividades/cercanas")
    Call<List<Actividad>> getActividadesCercanas(@Query("latitud") double latitud,
                                                 @Query("longitud") double longitud,
                                                 @Query("distancia") double distancia);

    @GET("/api/actividades/usuario/{idUsuario}")
    Call<List<Actividad>> getActividadesUsuario(@Path("idUsuario") int idUsuario);

    @GET("/api/actividades/creadas/{idUsuario}")
    Call<List<Actividad>> getActividadesUsuarioCreadas(@Path("idUsuario") int idUsuario);

    @POST("/api/actividades/crear")
    Call<Void> crearActividad(@Body Actividad actividad);

    @POST("api/actividades/cancelar/{idActividad}")
    Call<Boolean> cancelarActividad(@Path("idActividad") int idActividad);

    /*============================================================================================
     * PETICIONES SOBRE EQUIPOS
     *=============================================================================================*/
    @GET("/api/equipos")
    Call<List<Equipo>> getEquipos();

    @GET("/api/equipos/usuario")
    Call<List<Equipo>> getEquiposUsuario(@Query("idUsuario") int idUsuario);

    @GET("/api/equipos/creador/{idUsuario}")
    Call<List<Equipo>> getEquiposCreadosPorUsuario(@Path("idUsuario") int idUsuario);

    @GET("/api/equipo-miembro/miembro/{idUsuario}")
    Call<List<Equipo>> getEquiposPorUsuarioMiembro(@Path("idUsuario") int idUsuario);

    @POST("/api/equipos")
    Call<Equipo> crearEquipo(@Body Equipo equipoRequest);

    @POST("/api/equipos/editar")
    Call<Boolean> editarEquipo(@Body Equipo equipo);

    @DELETE("/api/equipos/eliminar")
    Call<Void> eliminarEquipo(@Query("idEquipo") int idEquipo);

    /*============================================================================================
     * PETICIONES SOBRE DEPORTES
     *=============================================================================================*/
    @GET("/api/deportes")
    Call<List<Deporte>> obtenerDeportes();

    /*============================================================================================
     * PETICIONES SOBRE PARTICIPANTES
     *=============================================================================================*/
    @GET("/api/participantes/actividad/{id}")
    Call<List<Participante>> getParticipantes(@Path("id") int idActividad);

    @POST("/api/participantes/unirse")
    Call<Boolean> unirseActividad(
            @Query("idActividad") int idActividad,
            @Query("usuarioId") int usuarioId
    );

    @POST("/api/participantes/cancelar")
    Call<Boolean> cancelarParticipacion(
            @Query("idActividad") int idActividad,
            @Query("usuarioId") int usuarioId
    );

    @POST("/api/participantes/cancelar-con-reembolso")
    Call<Boolean> cancelarParticipacionConReembolso(
            @Query("idActividad") int idActividad,
            @Query("usuarioId") int usuarioId
    );

    /*============================================================================================
     * PETICIONES SOBRE MIEMBROS DE EQUIPOS
     *=============================================================================================*/
    // Crear un nuevo miembro en un equipo
    @POST("/api/equipo-miembro")
    Call<EquipoMiembro> crearMiembro(@Body EquipoMiembro equipoMiembro);

    // Obtener miembros de un equipo
    @GET("/api/equipo-miembro/equipo/{equipoId}")
    Call<List<EquipoMiembro>> obtenerMiembrosPorEquipo(@Path("equipoId") int equipoId);

    // Obtener equipos de un usuario
    @GET("/api/equipo-miembro/usuario/{usuarioId}")
    Call<List<EquipoMiembro>> obtenerMiembrosPorUsuario(@Path("usuarioId") int usuarioId);

    // Eliminar un miembro del equipo
    @DELETE("/api/equipo-miembro/{id}")
    Call<Void> eliminarMiembro(@Path("id") int id);

    /*============================================================================================
     * PETICIONES REGISTRO
     *=============================================================================================*/
    // En el registro una vez dado el email, se envía un codigo de verificación a ese email.
    @POST("/api/verification/send-code")
    Call<String> sendVerificationCode(@Query("userEmail") String userEmail);

    // Comprueba que el código introducido coincida con el enviado al email.
    @POST("api/verification/verify")
    Call<Boolean> verifyCode(@Query("email") String email, @Query("code") String code);

    /*============================================================================================
     * PETICIONES PAGOS
     *=============================================================================================*/
    // Endpoint para retener el pago
    @POST("/api/pagos")
    Call<Pago> realizarPago(@Body Pago pago);

    // Endpoint para verificar si el pago ha sido liberado
    @GET("api/pagos/estado")
    Call<Pago> verificarEstadoPago(@Query("id") String actividadId);

    @GET("/api/pagos")
    Call<List<Pago>> obtenerPagosDeUsuario(@Query("idUsuario") int idUsuario);

    @POST("/api/pagos/liberar/{idActividad}")
    Call<Boolean> liberarPagos(@Path("idActividad") int idActividad);

    /*============================================================================================
     * PETICIONES REPORTES
     *=============================================================================================*/
    @GET("/api/reportes")
    Call<List<Reporte>> obtenerTodosLosReportes();

    @GET("/api/reportes/usuario")
    Call<List<Reporte>> obtenerReportesUsuario(@Query("idUsuario") int usuario);

    @GET("/api/reportes/usuario-reportante")
    Call<List<Reporte>> obtenerReportesUsuarioReportante(@Query("idUsuario") int idUsuario);

    @GET("/api/reportes/usuario-reportado")
    Call<List<Reporte>> obtenerReportesUsuarioReportado(@Query("idUsuario") int idUsuario);

    @GET("/api/reportes/pendientes")
    Call<List<Reporte>> obtenerReportesPendientes();

    @GET("/api/reportes/comprobar-existente")
    Call<Boolean> comprobarReporteExistente(@Query("idUsuario") int idUsuario,
                                            @Query("idEntidad") int idEntidad,
                                            @Query("Entidad") String entidad);

    @PUT("/api/reportes/revisar")
    Call<Boolean>revisarReporte(@Query("idReporte") int idReporte);

    @POST("/api/reportes/crear")
    Call<Reporte> crearReporte(@Body Reporte reporte);

    /*============================================================================================
     * PETICIONES NOTIFICACIONES
     *=============================================================================================*/
    @GET("/api/notificaciones/todas")
    Call<List<Notificacion>>obtenerNotificaciones(@Query("idUsuario") int idUsuario);

    @GET("/api/notificaciones/no-leidas")
    Call<List<Notificacion>>obtenerNotificacionesNoLeidas(@Query("idUsuario")int idUsuario);

    @POST("/api/notificaciones/crear")
    Call<Notificacion>crearNotificacion(@Body Notificacion notificacion);

    @PUT("/api/notificaciones/marcar-leidas")
    Call<Boolean> marcarNotificacionesLeidas(@Body List<Integer> ids);





    /*============================================================================================
     * PETICIONES UTILS
     *=============================================================================================*/
    @Multipart
    @POST("/api/equipos/subirImagen")
    Call<String> subirImagen(@Part MultipartBody.Part image);

    @POST("/api/tags/check-tags")
    Call<Set<Etiqueta>> comprobarEtiquetas(@Body List<String> etiquetas);

    @GET("/api/provincias")
    Call<List<Provincia>> getProvincias();

    @GET("/api/municipios")
    Call<List<Municipio>> getMunicipios(@Query("provinciaId") int idProvincia);



}



