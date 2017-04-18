package es.danirod.jddprototype.game.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by david on 14/04/2017.
 */

public class PantallaPuntuacion extends es.danirod.jddprototype.game.vista.BaseScreen {

    /** The stage where all the buttons are added. */
    private Stage stage;

    /** The skin that we use to set the style of the buttons. */
    private Skin skin;

    /** The buttons for retrying or for going back to menu. */
    private TextButton menu, puntuaciones;

    // etiquetas
    private Label texto, lnombre, record;

    // cajas para introducir texto
    private TextField nombre;

    // puntuacion final jugador
    private int puntos;

    // puesto de la puntuacion obtenido por el jugador
    private int puesto;

    public PantallaPuntuacion(final es.danirod.jddprototype.game.controlador.MainGame game) {
        super(game);

        // Create a new stage, as usual.
        stage = new Stage(new FitViewport(640, 360));

        // Load the skin file. The skin file contains information about the skins. It can be
        // passed to any widget in Scene2D UI to set the style. It just works, amazing.
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // For instance, here you see that I create a new button by telling the label of the
        // button as well as the skin file. The background image for the button is in the skin
        // file.
        menu = new TextButton("Menu", skin);
        puntuaciones = new TextButton("Mejores puntuaciones", skin);

        // crea las cajas de texto
        nombre = new TextField("", skin);
        nombre.setMaxLength(30);

        // crea etiquetas
        texto = new Label("", skin);
        lnombre = new Label("Nombre: ", skin);
        record = new Label("", skin);

        menu.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // se reinician variables
                es.danirod.jddprototype.game.modelo.VariablesGlobales.muertes = 0;
                es.danirod.jddprototype.game.modelo.VariablesGlobales.saltos = 0;
                es.danirod.jddprototype.game.modelo.VariablesGlobales.checkpoint = 0;
                // se guarda las puntuaciones si ha habido cambios
                if(puesto != 0)
                    persistirPuntuaciones();
                // And here I go to the menu screen.
                game.setScreen(game.menuScreen);
            }
        });

        puntuaciones.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // se reinician variables
                es.danirod.jddprototype.game.modelo.VariablesGlobales.muertes = 0;
                es.danirod.jddprototype.game.modelo.VariablesGlobales.saltos = 0;
                es.danirod.jddprototype.game.modelo.VariablesGlobales.checkpoint = 0;
                // se guarda las puntuaciones si ha habido cambios
                if(puesto != 0)
                    persistirPuntuaciones();
                // And here I go to the menu screen.
                game.setScreen(game.pantallaMejPuntuaciones);
            }
        });

        // Now I position things on screen. Sorry for making this the hardest part of this screen.
        // I position things on the screen so that they look centered. This is why I make the
        // buttons the same size.
        texto.setPosition(200, 280);
        menu.setSize(200, 80);
        menu.setPosition(420, 10);
        puntuaciones.setSize(200, 80);
        puntuaciones.setPosition(20, 10);
        record.setPosition(130, 180);
        lnombre.setPosition(150, 120);
        nombre.setSize(220, 30);
        nombre.setPosition(230, 120);

        // Do not forget to add actors to the stage or we wouldn't see anything.
        stage.addActor(texto);
        stage.addActor(menu);
        stage.addActor(nombre);
        stage.addActor(puntuaciones);
        stage.addActor(record);
        stage.addActor(lnombre);
    }

    @Override
    public void show() {
        // Now this is important. If you want to be able to click the button, you have to make
        // the Input system handle input using this Stage. Stages are also InputProcessors. By
        // making the Stage the default input processor for this game, it is now possible to
        // click on buttons and even to type on input fields.
        Gdx.input.setInputProcessor(stage);

        nombre.setText("");

        // calculo de puntuacion
        puntos = es.danirod.jddprototype.game.modelo.Constants.PUNTOS_BASE - (es.danirod.jddprototype.game.modelo.Constants.PUNTOS_MUERTE * es.danirod.jddprototype.game.modelo.VariablesGlobales.muertes)
                - (es.danirod.jddprototype.game.modelo.Constants.PUNTOS_SALTO * es.danirod.jddprototype.game.modelo.VariablesGlobales.saltos);

        String multiplicador = "Multiplicador dificultad: ";
        if(es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad == 1) { // medio
            puntos = (int) (puntos * 1.5);
            multiplicador += "x1.5";
        }
        else if(es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad == 2) { // dificil
            puntos = puntos * 2;
            multiplicador += "x2";
        }
        else {
            multiplicador += "x1";
        }

        if(puntos < 0)
            puntos = 0;

        texto.setText("ENHORABUENA, LO LOGRASTE!!!\n\nPuntuacion base:  " + es.danirod.jddprototype.game.modelo.Constants.PUNTOS_BASE + "\n" +
                "Muertes:  " + es.danirod.jddprototype.game.modelo.VariablesGlobales.muertes + "  (-" + es.danirod.jddprototype.game.modelo.Constants.PUNTOS_MUERTE + " cada muerte)\nSaltos:  "
                + es.danirod.jddprototype.game.modelo.VariablesGlobales.saltos + "  (-" + es.danirod.jddprototype.game.modelo.Constants.PUNTOS_SALTO + " cada salto)" +
                "\n" + multiplicador + "\nPuntuacion final:  " + puntos);

        // se comprueba si la puntuacion obtenida esta entre las 10 mejores
        puesto = 0;
        int i = 0;
        while(i < es.danirod.jddprototype.game.modelo.VariablesGlobales.punt.length && puesto == 0) {
            if(puntos > es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[i])
                puesto = i+1;
            i++;
        }

        if(puesto == 0) {
            record.setText("Lo siento, tu puntuacion no esta entre las 10 mejores");
            lnombre.setVisible(false);
            nombre.setVisible(false);
        }
        else {
            record.setText("Enhorabuena, tu puntuacion esta en la posicion " + puesto);
            lnombre.setVisible(true);
            nombre.setVisible(true);
            for (int j = es.danirod.jddprototype.game.modelo.VariablesGlobales.punt.length -1; j > puesto -1; j--) {
                es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[j] = es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[j-1];
                es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[j] = es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[j-1];
            }
            es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[puesto-1] = puntos;
        }

    }

    @Override
    public void hide() {
        // When the screen is no more visible, you have to remember to unset the input processor.
        // Otherwise, input might act weird, because even if you aren't using this screen, you are
        // still using the stage for handling input.
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        // Dispose assets.
        skin.dispose();
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        // Just render things.
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    private void persistirPuntuaciones() {
        es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[puesto-1] = nombre.getText();
        Preferences prefes = Gdx.app.getPreferences("jumpdontdie_puntuaciones");
        if(!prefes.getString("key").equals("creado"))
            prefes.putString("key", "creado"); // con esta preferencia ya sabre que esta creado
        for (int i = 0; i < es.danirod.jddprototype.game.modelo.VariablesGlobales.punt.length; i++) {
            prefes.putInteger("punt" + (i+1), es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[i]);
            prefes.putString("nomb" + (i+1), es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[i]);
        }
        prefes.flush(); // persiste los datos
    }

}
