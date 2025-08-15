package utez.edu.mx.prestamos_utez.sesion;

import utez.edu.mx.prestamos_utez.model.Usuario; // usa tu modelo de usuario

public class AdministradorSesion {
    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario u) { usuarioActual = u; }
    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static boolean isLoggedIn() { return usuarioActual != null; }
    public static void clear() { usuarioActual = null; }
}
