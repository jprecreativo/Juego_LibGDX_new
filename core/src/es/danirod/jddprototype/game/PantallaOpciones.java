package es.danirod.jddprototype.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by david on 03/04/2017.
 */

public class PantallaOpciones extends BaseScreen {

    // el escenario donde se añadiran los actores
    private Stage stage;

    // skin para dar estilo a los botones
    private Skin skin;

    /** etiquetas con la informacion */
    private Label volumen, numVolumen;

    // boton para volver al menu
    private TextButton atras;

    // barra para seleccionar el volumen deseado
    private Slider slider;

    public PantallaOpciones(final MainGame game) {
        super(game);

        // creamos nuevo escenario
        stage = new Stage(new FitViewport(640, 360));

        // cargamos las skins
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // creamos boton Atras
        atras = new TextButton("Atras", skin);

        // etiquetas
        volumen = new Label("Volumen: ", skin);
        numVolumen = new Label("75 %", skin);

        // creamos el slider
        slider = new Slider(0, 100, 5, false, skin);
        slider.setValue(75);

        // capturamos el evento al pulsar el boton
        atras.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                VariablesGlobales.volumen = slider.getValue() / 100;
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

        // añadimos los actores al escenario
        stage.addActor(atras);
        stage.addActor(volumen);
        stage.addActor(numVolumen);
        stage.addActor(slider);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
        stage.draw();
    }
}
