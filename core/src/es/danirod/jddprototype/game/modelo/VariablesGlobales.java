package es.danirod.jddprototype.game.modelo;

/**
 * Created by david on 04/04/2017.
 */

public class VariablesGlobales {

    // volumen de la música de fondo
    public static float volumen;

    // muertes acumuladas en la misma partida
    public static int muertes = 0;

    // saltos acumulados en la misma partida
    public static int saltos = 0;

    // indica el checkpoint donde aparecera el jugador (0 es la posicion inicial)
    public static int checkpoint = 0;

    // indica la posicion del jugador al morir (en metros)
    public static float posicion_muerte;

    // 10 mejores puntuaciones
    public static int[] punt = new int[10]; // puntuacion
    public static String[] nomb = new String[10]; // nombre

    // indica la dificultad activada: 0 = facil, 1 = medio, 2 = dificil
    public static int dificultad;

    // indica el personaje seleccionado: 0 = cubo, 1 = minijoe, 2 = cr
    public static int personaje;

}
