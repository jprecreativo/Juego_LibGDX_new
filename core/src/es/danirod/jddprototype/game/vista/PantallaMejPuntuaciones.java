package es.danirod.jddprototype.game.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by david on 17/04/2017.
 */

public class PantallaMejPuntuaciones extends es.danirod.jddprototype.game.vista.BaseScreen {

    /** The stage where all the buttons are added. */
    private Stage stage;

    /** The skin that we use to set the style of the buttons. */
    private Skin skin;

    /** The buttons for retrying or for going back to menu. */
    private TextButton menu;

    // etiquetas
    private Label texto;

    public PantallaMejPuntuaciones(final es.danirod.jddprototype.game.controlador.MainGame game) {
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

        // crea etiquetas
        texto = new Label("", skin);

        menu.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // And here I go to the menu screen.
                game.setScreen(game.menuScreen);
            }
        });

        // Now I position things on screen. Sorry for making this the hardest part of this screen.
        // I position things on the screen so that they look centered. This is why I make the
        // buttons the same size.
        texto.setPosition(200, 210);
        menu.setSize(200, 80);
        menu.setPosition(200, 10);

        // Do not forget to add actors to the stage or we wouldn't see anything.
        stage.addActor(texto);
        stage.addActor(menu);
    }

    @Override
    public void show() {
        // Now this is important. If you want to be able to click the button, you have to make
        // the Input system handle input using this Stage. Stages are also InputProcessors. By
        // making the Stage the default input processor for this game, it is now possible to
        // click on buttons and even to type on input fields.
        Gdx.input.setInputProcessor(stage);

        String cadena = "TOP PUNTUACIONES\n\n";
        for (int i = 0; i < es.danirod.jddprototype.game.modelo.VariablesGlobales.punt.length; i++) {
            cadena += i+1 + ".- " + es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[i] + " --> " + es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[i] + "\n";
        }

        texto.setText(cadena);
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

}
