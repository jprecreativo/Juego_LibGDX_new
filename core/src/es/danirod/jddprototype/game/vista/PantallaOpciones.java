package es.danirod.jddprototype.game.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by david on 03/04/2017.
 */

public class PantallaOpciones extends es.danirod.jddprototype.game.vista.BaseScreen {

    // el escenario donde se añadiran los actores
    private Stage stage;

    // skin para dar estilo a los botones
    private Skin skin;

    /** etiquetas con la informacion */
    private Label volumen, numVolumen, dificultad, personaje;

    // boton para volver al menu
    private TextButton atras;

    // chechbox para elegir dificultad
    private CheckBox facil, medio, dificil, cbminijoe, cbcubo, cbcr;
    private ButtonGroup bg, bgpersonajes;

    // imagenes de los personajes
    private Image minijoe, cubo, cr;

    // barra para seleccionar el volumen deseado
    private Slider slider;

    // sonidos saltos
    private Music jumpSoundCR, jumpSound;

    public PantallaOpciones(final es.danirod.jddprototype.game.controlador.MainGame game) {
        super(game);

        // creamos nuevo escenario
        stage = new Stage(new FitViewport(640, 360));

        // cargamos las skins
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // creamos boton Atras
        atras = new TextButton("Atras", skin);

        // etiquetas
        volumen = new Label("Volumen: ", skin);
        numVolumen = new Label("0 %", skin);
        dificultad = new Label("Dificultad: ", skin);
        personaje = new Label("Personaje: ", skin);

        // creamos imagenes
        cubo = new Image(game.getManager().get("player.png", Texture.class));
        minijoe = new Image(game.getManager().get("player2.png", Texture.class));
        cr = new Image(game.getManager().get("player3.png", Texture.class));

        // creamos el slider
        slider = new Slider(0, 100, 5, false, skin);

        // creamos los checkbox
        facil = new CheckBox("Facil", skin);
        medio = new CheckBox("Medio", skin);
        dificil = new CheckBox("Dificil", skin);
        cbcubo = new CheckBox("", skin);
        cbminijoe = new CheckBox("", skin);
        cbcr = new CheckBox("", skin);
        bg = new ButtonGroup();
        bgpersonajes = new ButtonGroup();

        // capturamos el evento al pulsar el boton
        atras.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen = slider.getValue() / 100;

                if(facil.isChecked())
                    es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 0;
                else if(medio.isChecked())
                    es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 1;
                else
                    es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 2;

                if(cbcubo.isChecked())
                    es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 0;
                else if(cbminijoe.isChecked())
                    es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 1;
                else
                    es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 2;
                // guardo las preferencias del usuario
                Preferences prefes = Gdx.app.getPreferences("jumpdontdie_opciones");
                // compruebo si no esta creado anteriormente el archivo de preferencias
                if(!prefes.getString("key").equals("creado"))
                    prefes.putString("key", "creado"); // con esta preferencia ya sabre que esta creado
                prefes.putFloat("volumen", es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
                prefes.putInteger("dificultad", es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad);
                prefes.putInteger("personaje", es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje);
                prefes.flush(); // persiste los datos
                game.setScreen(game.menuScreen);
            }
        });

        // capturamos cuando cambia el valor del slider
        slider.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                numVolumen.setText((int)slider.getValue() + " %");
            }
        });

        // posicionamos los actores
        volumen.setPosition(40, 300);
        numVolumen.setPosition(300, 300);
        atras.setSize(200, 80);
        atras.setPosition(40, 40);
        slider.setPosition(150, 303);
        dificultad.setPosition(40, 250);
        facil.setPosition(150, 250);
        medio.setPosition(210, 250);
        dificil.setPosition(290, 250);
        personaje.setPosition(40, 150);
        cbcubo.setPosition(150, 150);
        cubo.setSize(55.6f, 66);
        cubo.setPosition(175, 150);
        cbminijoe.setPosition(270, 150);
        minijoe.setSize(58.4f, 64);
        minijoe.setPosition(295, 150);
        cbcr.setPosition(390, 150);
        cr.setPosition(405,143);

        // añadimos los actores al escenario
        stage.addActor(atras);
        stage.addActor(volumen);
        stage.addActor(numVolumen);
        stage.addActor(slider);
        stage.addActor(dificultad);
        stage.addActor(facil);
        stage.addActor(medio);
        stage.addActor(dificil);
        stage.addActor(personaje);
        stage.addActor(cubo);
        stage.addActor(minijoe);
        stage.addActor(cr);
        stage.addActor(cbcr);
        stage.addActor(cbcubo);
        stage.addActor(cbminijoe);

        jumpSoundCR = game.getManager().get("audio/suuu.ogg");
        jumpSound = game.getManager().get("audio/jump.ogg");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // cargo valores
        numVolumen.setText(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen * 100 + " %");
        slider.setValue(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen * 100);

        if(es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad == 0)
            facil.setChecked(true);
        else if(es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad == 1)
            medio.setChecked(true);
        else
            dificil.setChecked(true);

        if(es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje == 0)
            cbcubo.setChecked(true);
        else if(es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje == 1)
            cbminijoe.setChecked(true);
        else
            cbcr.setChecked(true);

        // capturo la tecla Atras
        Gdx.input.setCatchBackKey(true);
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen = slider.getValue() / 100;

            if(facil.isChecked())
                es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 0;
            else if(medio.isChecked())
                es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 1;
            else
                es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 2;

            if(cbcubo.isChecked())
                es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 0;
            else if(cbminijoe.isChecked())
                es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 1;
            else
                es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 2;

            // guardo las preferencias del usuario
            Preferences prefes = Gdx.app.getPreferences("jumpdontdie_opciones");
            // compruebo si no esta creado anteriormente el archivo de preferencias
            if(!prefes.getString("key").equals("creado"))
                prefes.putString("key", "creado"); // con esta preferencia ya sabre que esta creado
            prefes.putFloat("volumen", es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            prefes.putInteger("dificultad", es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad);
            prefes.putInteger("personaje", es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje);
            prefes.flush(); // persiste los datos
            game.setScreen(game.menuScreen);
        }

        // añadimos los checkbox al buttongroup para que solo este activado uno a la vez
        bg.add(facil);
        bg.add(medio);
        bg.add(dificil);

        // añadimos los checkbox de los personajes al buttongroup
        bgpersonajes.add(cbcr);
        bgpersonajes.add(cbcubo);
        bgpersonajes.add(cbminijoe);

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        if(cbcr.isPressed()) {
            jumpSoundCR.setVolume(slider.getValue() / 100);
            jumpSoundCR.play();
        }
        if(cbminijoe.isPressed() || cbcubo.isPressed()) {
            jumpSound.setVolume(slider.getValue() / 100);
            jumpSound.play();
        }
        stage.draw();
    }
}
