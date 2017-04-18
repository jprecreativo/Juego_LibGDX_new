/*
 * This file is part of Jump Don't Die.
 * Copyright (C) 2015 Dani Rodríguez <danirod@outlook.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.danirod.jddprototype.game.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import es.danirod.jddprototype.game.modelo.entidad.EntidadMuelle;
import es.danirod.jddprototype.game.modelo.entidad.EntidadPelotaBlanca;
import es.danirod.jddprototype.game.modelo.entidad.EntidadPelotaVerde;
import es.danirod.jddprototype.game.modelo.entidad.EntidadPinchoRotado;
import es.danirod.jddprototype.game.controlador.EntityFactory;
import es.danirod.jddprototype.game.modelo.entidad.FloorEntity;
import es.danirod.jddprototype.game.modelo.entidad.PlayerEntity;
import es.danirod.jddprototype.game.modelo.entidad.SpikeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main screen for the game. All the fun happen here.
 */
public class GameScreen extends es.danirod.jddprototype.game.vista.BaseScreen {

    // objeto para guardar y leer las preferencias
    private Preferences prefes_opciones, prefes_puntuaciones;

    /** Stage instance for Scene2D rendering. */
    private Stage stage;

    /** The skin that we use to set the style of the buttons. */
    private Skin skin;

    /** World instance for Box2D engine. */
    private World world;

    /** Player entity. */
    private PlayerEntity player;

    /** List of floors attached to this level. */
    private List<FloorEntity> floorList = new ArrayList<FloorEntity>();

    /** List of spikes attached to this level. */
    private List<SpikeEntity> spikeList = new ArrayList<SpikeEntity>();

    // lista de pinchos rotados
    private List<EntidadPinchoRotado> listaPinchosRotados = new ArrayList<EntidadPinchoRotado>();

    // lista de muelles en este nivel
    private List<EntidadMuelle> listaMuelles = new ArrayList<EntidadMuelle>();

    // Lista de pelotas blancas en esta pantalla.
    private List<EntidadPelotaBlanca> listaPelotasBlancas = new ArrayList<EntidadPelotaBlanca>();

    //Lista de pelotas verdes en esta pantalla.
    private List<EntidadPelotaVerde> listaPelotasVerdes = new ArrayList<EntidadPelotaVerde>();

    /** Jump sound that has to play when the player jumps. */
    private Music jumpSound;

    /** Die sound that has to play when the player collides with a spike. */
    private Music dieSound;

    /** Background music that has to play on the background all the time. */
    private Music backgroundMusic;

    // sonido de victoria
    private Music sonidoVictoria, jumpSoundCR;

    /** Initial position of the camera. Required for reseting the viewport. */
    private Vector3 position;

    // imagenes
    private Image flecha, meta, noviste;

    // botones con imagenes
    private ImageButton pausar, reanudar;
    private Texture tpausar, treanudar;
    private TextureRegion trpausar, trreanudar;
    private TextureRegionDrawable trdpausar, trdreanudar;

    // true cuando el juego este en pausa
    private boolean pausa = false;

    /**
     * Create the screen. Since this constructor cannot be invoked before libGDX is fully started,
     * it is safe to do critical code here such as loading assets and setting up the stage.
     * @param game
     */
    public GameScreen(es.danirod.jddprototype.game.controlador.MainGame game) {
        super(game);

        // instacia el atributo Preferences
        prefes_opciones = Gdx.app.getPreferences("jumpdontdie_opciones");
        prefes_puntuaciones = Gdx.app.getPreferences("jumpdontdie_puntuaciones");

        // carga las opciones
        if(prefes_opciones.getString("key").equals("creado")){ // compruebo si ya esta creado
            es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen = prefes_opciones.getFloat("volumen");
            es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = prefes_opciones.getInteger("dificultad");
            es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = prefes_opciones.getInteger("personaje");
        }
        else { // si no esta cargado, pongo valores por defecto
            es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen = 0.75f;
            es.danirod.jddprototype.game.modelo.VariablesGlobales.dificultad = 0; // facil
            es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje = 0; // cubo
        }

        // carga las puntuaciones
        if(prefes_puntuaciones.getString("key").equals("creado")) {
            for (int i = 0; i < es.danirod.jddprototype.game.modelo.VariablesGlobales.punt.length; i++) {
                es.danirod.jddprototype.game.modelo.VariablesGlobales.punt[i] = prefes_puntuaciones.getInteger("punt" + (i+1));
                es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[i] = prefes_puntuaciones.getString("nomb" + (i+1));
            }
        }
        else { // si no esta creado pongo el valor por defecto de los String (los int se inicializan a 0)
            for (int i = 0; i < es.danirod.jddprototype.game.modelo.VariablesGlobales.punt.length; i++) {
                es.danirod.jddprototype.game.modelo.VariablesGlobales.nomb[i] = "";
            }
        }

        // Create a new Scene2D stage for displaying things.
        stage = new Stage(new FitViewport(1280, 720));

        // Load the skin file. The skin file contains information about the skins. It can be
        // passed to any widget in Scene2D UI to set the style. It just works, amazing.
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // se crean los imagebutton
        tpausar = new Texture(Gdx.files.internal("pausa.png"));
        trpausar = new TextureRegion(tpausar);
        trdpausar = new TextureRegionDrawable(trpausar);
        pausar = new ImageButton(trdpausar);

        treanudar = new Texture(Gdx.files.internal("reanudar.png"));
        trreanudar = new TextureRegion(treanudar);
        trdreanudar = new TextureRegionDrawable(trreanudar);
        reanudar = new ImageButton(trdreanudar);
        // inicialmente ni se ve ni se puede interactuar con el boton para reanudar
        reanudar.setTouchable(Touchable.disabled);
        reanudar.setVisible(false);

        // definimos el corportamiento al pulsar el boton pausar
        pausar.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pausa = true;
                pausar.setVisible(false);
                pausar.setTouchable(Touchable.disabled);
                reanudar.setVisible(true);
                reanudar.setTouchable(Touchable.enabled);
                backgroundMusic.stop();
            }
        });

        // definimos el corportamiento al pulsar el boton reanudar
        reanudar.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pausa = false;
                pausar.setVisible(true);
                pausar.setTouchable(Touchable.enabled);
                reanudar.setVisible(false);
                reanudar.setTouchable(Touchable.disabled);
                backgroundMusic.play();
            }
        });

        // Create a new Box2D world for managing things.
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new GameContactListener());

        // Get the sound effect references that will play during the game.
        jumpSound = game.getManager().get("audio/jump.ogg");
        dieSound = game.getManager().get("audio/die.ogg");
        backgroundMusic = game.getManager().get("audio/song.ogg");
        sonidoVictoria = game.getManager().get("audio/victoria.ogg");
        jumpSoundCR = game.getManager().get("audio/suuu.ogg");

        // crea la imagenes
        flecha = new Image(game.getManager().get("flecha.png", Texture.class));
        meta = new Image(game.getManager().get("meta.png", Texture.class));
        noviste = new Image(game.getManager().get("noviste.png", Texture.class));
    }

    /**
     * This method will be executed when this screen is about to be rendered.
     * Here, I use this method to set up the initial position for the stage.
     */
    @Override
    public void show() {
        // Now this is important. If you want to be able to click the button, you have to make
        // the Input system handle input using this Stage. Stages are also InputProcessors. By
        // making the Stage the default input processor for this game, it is now possible to
        // click on buttons and even to type on input fields.
        Gdx.input.setInputProcessor(stage);

        EntityFactory factory = new EntityFactory(game.getManager());

        // la posicion del escenario, el personaje y los botones dependera del checkpoint actual
        switch (es.danirod.jddprototype.game.modelo.VariablesGlobales.checkpoint) {
            case 0:
                // posicion inicial. stage.getCamera().position = {640.0, 360.0, 0.0}
                //position = new Vector3(stage.getCamera().position);
                position = new Vector3(es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[0] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER + 640,360,0);
                player = factory.createPlayer(world, new Vector2(es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[0], es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[1]));
                pausar.setPosition(1100 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[0] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 600);
                reanudar.setPosition(600 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[0] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 320);
                break;
            case 1:
                // posicion despues del primer muelle
                position = new Vector3(es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[1] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER + 640,360,0);
                player = factory.createPlayer(world, new Vector2(es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[2], es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[3]));
                pausar.setPosition(1100 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[1] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 600);
                reanudar.setPosition(600 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[1] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 320);
                break;
            case 2:
                // posicion antes del salto sobre el primer hueco
                position = new Vector3(es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[2] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER + 640,360,0);
                player = factory.createPlayer(world, new Vector2(es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[4], es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[5]));
                pausar.setPosition(1100 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[2] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 600);
                reanudar.setPosition(600 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[2] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 320);
                break;
            case 3:
                // posicion antes de la zona donde flota
                position = new Vector3(es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[3] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER + 640,360,0);
                player = factory.createPlayer(world, new Vector2(es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[6], es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[7]));
                pausar.setPosition(1100 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[3] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 600);
                reanudar.setPosition(600 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[3] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 320);
                break;
            case 4:
                // posicion justo después de la zona donde flota
                position = new Vector3(es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[4] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER + 640,360,0);
                player = factory.createPlayer(world, new Vector2(es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[8], es.danirod.jddprototype.game.modelo.Constants.POSICION_JUGADOR[9]));
                pausar.setPosition(1100 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[4] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 600);
                reanudar.setPosition(600 + es.danirod.jddprototype.game.modelo.Constants.POSICION_ESCENARIO[4] * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 320);
                break;
            default:
                break;
        }

        // se resetea el valor de la gravedad porque es cambiado al final del nivel
        world.setGravity(new Vector2(0, -10));

        // Array para las dimesiones de los suelos
        // la tercera dimension = 1 indica que es suelo de la pantalla, el suelo más inferior
        int[] coordSuelos = {0,250,1, 30,10,2, 48,10,2, 95,1,2, 95,1,3, 120,8,2, 129,3,4, 145,20,2, 155,4,3, 168,3,3, 173,2,6,
                178,8,2, 186,7,3, 195,2,5, 200,22,2, 257,23,1, 269,10,4, 284,80,1, 294,2,2, 300,2,3, 306,2,4, 311,2,5, 320,2,2,
                326,2,3, 332,2,4, 337,2,5, 341,2,4, 346,2,3, 375,36,1, 410,95,9, 420,1,1, 420,1,2, 420,1,3, 425,1,8, 425,1,7,
                425,1,6, 430,1,1, 430,1,2, 430,1,3, 440,1,1, 440,1,2, 440,1,3, 440,1,8, 440,1,7, 448,1,1, 448,1,8, 448,1,7,
                448,1,6, 448,1,5, 456,1,1, 456,1,2, 456,1,3, 456,1,4, 456,1,8, 470,1,1, 470,1,8, 470,1,7, 470,1,6, 470,1,5,
                473,1,1, 473,1,8, 473,1,7, 473,1,6, 473,1,5, 476,1,1, 476,1,8, 476,1,7, 476,1,6, 476,1,5, 484,1,1, 484,1,2,
                484,1,3, 484,1,4, 484,1,8, 487,1,1, 487,1,2, 487,1,3, 487,1,4, 487,1,8, 490,1,1, 490,1,2, 490,1,3, 490,1,4,
                490,1,8, 500,50,1, 500,5,2, 500,5,3, 500,5,7, 500,5,8, 518,1,2, 523,1,2, 523,1,3, 529,1,2, 529,1,3, 529,1,4,
                536,1,2, 536,1,3, 541,1,2, 554,8,1, 561,1,2, 561,1,3, 561,1,4, 561,1,5};

        // Array para las coordenadas de los pinchos
        int[] coordPinchos = {15,1, 23,1, 38,2, 53,2, 69,1, 77,1, 129,1, 130,1, 131,1, 132,1, 133,1, 134,1, 135,1, 136,1, 137,1,
                138,1, 139,1, 140,1, 158,3, 168,1, 174,6, 196,5, 206,2, 211,2, 218,2, 274,4, 307,1, 308,1, 312,5, 347,3, 351,1,
                352,1, 353,1, 388,1, 389,1, 390,1, 517,1, 520,1, 521,1, 522,1, 525,1, 526,1, 527,1, 528,1, 531,1, 532,1, 533,1,
                534,1, 535,1, 538,1, 539,1, 540,1};

        // Array para las posiciones de los muelles
        int[] coordMuelles = {93,1, 124,2, 170,3, 189,3, 249,1, 264,1, 410,1, 549,1};

        // Array para las posiciones de las pelotas blancas
        int[] coordPelotasBlancas = {172,5, 282,3, 309,4};

        // Array para las posiciones de las pelotas verdes
        int[] coordPelotasVerdes = {349,4, 366,2, 389,2};

        // añadimos los suelos
        for (int i = 0; i < coordSuelos.length; i+=3) {
            floorList.add(factory.createFloor(world, coordSuelos[i], coordSuelos[i+1], coordSuelos[i+2]));
        }

        // añadimos los pinchos
        for (int i = 0; i < coordPinchos.length; i+=2) {
            spikeList.add(factory.createSpikes(world, coordPinchos[i], coordPinchos[i+1]));
        }

        // En este for se añaden los pinchos de la parte del suelo en la que se flota
        for(int i = 411; i < 500; i++) {
            // no pongo pinchos donde hay suelo
            if(i != 420 && i != 421 && i != 430 && i != 431 && i != 440 && i != 441 && i != 448 && i != 449
                    && i != 456 && i != 457 && i != 470 && i != 471 && i != 473 && i != 474 && i != 476 && i != 477
                    && i != 484 && i != 485 && i != 487 && i != 488 && i != 490 && i != 491)
                spikeList.add(factory.createSpikes(world, i, 0));
        }

        // En este for se añaden los pinchos de la parte del techo en la que se flota
        for(int i = 411; i < 500; i++) {
            // no pongo pinchos donde hay techo
            if(i != 425 && i != 426  && i != 440 && i != 441 && i != 448 && i != 449
                    && i != 456 && i != 457 && i != 470 && i != 471 && i != 473 && i != 474 && i != 476 && i != 477
                    && i != 484 && i != 485 && i != 487 && i != 488 && i != 490 && i != 491)
            listaPinchosRotados.add(factory.creaPinchosRotados(world, i, 7));
        }

        // añadimos los muelles
        for (int i = 0; i < coordMuelles.length; i+=2) {
            listaMuelles.add(factory.creaMuelles(world, coordMuelles[i], coordMuelles[i+1]));
        }

        // Añadimos las pelotas blancas.
        for(int i = 0; i < coordPelotasBlancas.length; i+=2) {
            listaPelotasBlancas.add(factory.crearPelotaBlanca(world, coordPelotasBlancas[i], coordPelotasBlancas[i+1]));
        }

        // Añadimos las pelotas verdes.
        for(int i = 0; i < coordPelotasVerdes.length; i+=2) {
            listaPelotasVerdes.add(factory.crearPelotaVerde(world, coordPelotasVerdes[i], coordPelotasVerdes[i+1]));
        }

        flecha.setSize(240, 177);
        flecha.setPosition(550 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 3 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER);
        meta.setSize(240, 177);
        meta.setPosition(549 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 2.5f * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER);
        noviste.setSize(240, 177);
        noviste.setPosition(565 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER, 3 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER);

        // añadimos los actores al escenario
        for (FloorEntity floor : floorList)
            stage.addActor(floor);
        for (SpikeEntity spike : spikeList)
            stage.addActor(spike);
        for(EntidadPinchoRotado pinchoRotado : listaPinchosRotados)
            stage.addActor(pinchoRotado);
        for(EntidadMuelle muelle : listaMuelles)
            stage.addActor(muelle);
        for(EntidadPelotaBlanca pb: listaPelotasBlancas)
            stage.addActor(pb);
        for(EntidadPelotaVerde pv: listaPelotasVerdes)
            stage.addActor(pv);

        stage.addActor(flecha);
        stage.addActor(meta);
        stage.addActor(noviste);
        stage.addActor(pausar);
        stage.addActor(reanudar);

        // Add the player to the stage too.
        stage.addActor(player);

        // Reset the camera to the left. This is required because we have translated the camera
        // during the game. We need to put the camera on the initial position so that you can
        // use it again if you replay the game.
        stage.getCamera().position.set(position);
        stage.getCamera().update();

        // Everything is ready, turn the volume up.
        backgroundMusic.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        jumpSound.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        jumpSoundCR.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        dieSound.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        sonidoVictoria.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        backgroundMusic.play();
    }

    /**
     * This method will be executed when this screen is no more the active screen.
     * I use this method to destroy all the things that have been used in the stage.
     */
    @Override
    public void hide() {
        // When the screen is no more visible, you have to remember to unset the input processor.
        // Otherwise, input might act weird, because even if you aren't using this screen, you are
        // still using the stage for handling input.
        Gdx.input.setInputProcessor(null);

        // Clear the stage. This will remove ALL actors from the stage and it is faster than
        // removing every single actor one by one. This is not shown in the video but it is
        // an improvement.
        stage.clear();

        // Detach every entity from the world they have been living in.
        player.detach();
        for (FloorEntity floor : floorList)
            floor.detach();
        for (SpikeEntity spike : spikeList)
            spike.detach();
        for(EntidadPinchoRotado pinchoRotado : listaPinchosRotados)
            pinchoRotado.detach();
        for(EntidadMuelle muelle : listaMuelles)
            muelle.detach();
        for(EntidadPelotaBlanca pb : listaPelotasBlancas)
            pb.detach();
        for(EntidadPelotaVerde pv : listaPelotasVerdes)
            pv.detach();

        // Clear the lists.
        floorList.clear();
        spikeList.clear();
        listaPinchosRotados.clear();
        listaMuelles.clear();
        listaPelotasBlancas.clear();
        listaPelotasVerdes.clear();
    }

    /**
     * This method is executed whenever the game requires this screen to be rendered. This will
     * display things on the screen. This method is also used to update the game.
     */
    @Override
    public void render(float delta) {

        // Do not forget to clean the screen.
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // capturamos las teclas de subir y bajar volumen
        if(Gdx.input.isKeyPressed(Input.Keys.VOLUME_UP) && es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen < 1) {
            es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen += 0.05f;
            if(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen > 1)
                es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen = 1;
            backgroundMusic.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            jumpSound.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            jumpSoundCR.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            dieSound.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            sonidoVictoria.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.VOLUME_DOWN) && es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen > 0) {
            es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen -= 0.05f;
            if(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen < 0)
                es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen = 0;
            backgroundMusic.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            jumpSound.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            jumpSoundCR.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            dieSound.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
            sonidoVictoria.setVolume(es.danirod.jddprototype.game.modelo.VariablesGlobales.volumen);
        }

        if(!pausa) {

            // Update the stage. This will update the player speed.
            stage.act();

            // Step the world. This will update the physics and update entity positions.
            world.step(delta, 6, 2);

            // Aquí se comprueba si el personaje se ha caído por un precipicio (excluyendo el útlimo que es la meta)
            if (player.isAlive() && player.getY() < 0) {

                player.setAlive(false);
                backgroundMusic.stop();

                if (player.getX() < 550 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                    dieSound.play();

                    es.danirod.jddprototype.game.modelo.VariablesGlobales.muertes++;

                    // Add an Action. Actions are cool because they let you add animations to your
                    // game. Here I add a sequence action so that two actions happens one after
                    // the other. One action is a delay action. It just waits for 1.5 seconds.
                    // The second actions is a run action. It executes some code. Here, we go
                    // to the game over screen when we die.
                    stage.addAction(
                            Actions.sequence(
                                    Actions.delay(1.5f),
                                    Actions.run(new Runnable() {

                                        @Override
                                        public void run() {
                                            es.danirod.jddprototype.game.modelo.VariablesGlobales.posicion_muerte = player.getX() / es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER;
                                            game.setScreen(game.gameOverScreen);
                                        }
                                    })
                            )
                    );
                } else { // el jugador ha terminado el nivel y ha ganado

                    sonidoVictoria.play();

                    // Add an Action. Actions are cool because they let you add animations to your
                    // game. Here I add a sequence action so that two actions happens one after
                    // the other. One action is a delay action. It just waits for 1.5 seconds.
                    // The second actions is a run action. It executes some code. Here, we go
                    // to the game over screen when we die.
                    stage.addAction(
                            Actions.sequence(
                                    Actions.delay(1.5f),
                                    Actions.run(new Runnable() {

                                        @Override
                                        public void run() {
                                            game.setScreen(game.pantallaPuntuacion);
                                        }
                                    })
                            )
                    );
                }
            }


            // Comprobamos cuando el personaje está cerca de la pelota blanca 0.
            if (player.getX() > 171.1 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 171.6 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER &&
                    player.getY() > 3.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getY() < 5.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                if (player.isAlive()) {
                    player.jump((int) (es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * -0.5), true);
                    listaPelotasBlancas.get(0).setVisible(false);
                }
            }

            // Comprobamos cuando el personaje está cerca de la pelota blanca 1.
            else if (player.getX() > 281.1 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 281.6 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER &&
                    player.getY() > 1.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getY() < 3.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                if (player.isAlive()) {
                    player.jump((int) (es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * -0.5), true);
                    listaPelotasBlancas.get(1).setVisible(false);
                }
            }

            // Comprobamos cuando el personaje está cerca de la pelota blanca 2.
            else if (player.getX() > 308.1 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 308.6 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER &&
                    player.getY() > 3.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getY() < 5.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                if (player.isAlive()) {
                    player.jump((int) (es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * -0.5), true);
                    listaPelotasBlancas.get(2).setVisible(false);
                }
            }

            // Comprobamos cuando el personaje está cerca de la pelota verde 0.
            else if (player.getX() > 348.1 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 348.6 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER &&
                    player.getY() < 5.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                if (player.isAlive()) {
                    listaPelotasVerdes.get(0).setSize(es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER / 2, es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER / 2);
                    player.jump((int) (es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * 0.5), true);
                }
            }

            // Comprobamos cuando el personaje está cerca de la pelota verde 1.
            else if (player.getX() > 365.1 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 365.6 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER &&
                    player.getY() < 3.5 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                if (player.isAlive()) {
                    listaPelotasVerdes.get(1).setSize(es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER / 2, es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER / 2);
                    player.jump((int) (es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * 0.5), true);
                }
            }

            // Comprobamos cuando el personaje está cerca de la pelota verde 2.
            else if (player.getX() > 388.1 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 388.6 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER &&
                    player.getY() < 3.2 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {

                if (player.isAlive()) {
                    listaPelotasVerdes.get(2).setSize(es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER / 2, es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER / 2);
                    player.jump((int) (es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * 0.5), true);
                }
            }

            // Llegado a este punto, el personaje deberá seguir "flotando"
            if (player.getX() > 410 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER && player.getX() < 501 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER) {
                player.setJumping(false);   // Se establece que no se está saltando para poder dar saltos continuados
                world.setGravity(new Vector2(0, -25.5f));   // Se modifica la gravedad para hacer que se vaya "flotando"
            }

            // volvemos a poner la gravedad normal
            if (player.getX() > 500 * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER)
                world.setGravity(new Vector2(0, -10));


            // Make the camera follow the player. As long as the player is alive, if the player is
            // moving, make the camera move at the same speed, so that the player is always
            // centered at the same position.
            if (player.getX() > 150 && player.isAlive()) {
                float speed = es.danirod.jddprototype.game.modelo.Constants.PLAYER_SPEED * delta * es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER - es.danirod.jddprototype.game.modelo.Constants.VELOCITY_DIFF;
                stage.getCamera().translate(speed, 0, 0);
                // actualiza tambien la posicion de los botones pausar y reanudar
                pausar.setPosition(pausar.getX() + speed, pausar.getY());
                reanudar.setPosition(reanudar.getX() + speed, reanudar.getY());
            }

        }

        // si se ha pausado el juego, se detiene al personaje y a la camara
        if(pausa) {
            player.parar();
            stage.getCamera().translate(0,0,0);
        }

        // Render the screen. Remember, this is the last step!
        stage.draw();

    }

    // este metodo se ejecuta automaticamente en android cuando se recibe una llamada o se pulsa el boton de inicio
    @Override
    public void pause() {
        pausa = true;
        pausar.setVisible(false);
        pausar.setTouchable(Touchable.disabled);
        reanudar.setVisible(true);
        reanudar.setTouchable(Touchable.enabled);
        backgroundMusic.stop();
    }

    // este metodo lo llama android cuando la aplicacion recibe el foco desde el metodo pause()
    @Override
    public void resume() {
        //pausa = false; esta quitado porque para reanudar debe pulsar el boton
    }

    /**
     * This method is executed when the screen can be safely disposed.
     * I use this method to dispose things that have to be manually disposed.
     */
    @Override
    public void dispose() {
        skin.dispose();

        // Dispose the stage to remove the Batch references in the graphics card.
        stage.dispose();

        // Dispose the world to remove the Box2D native data (C++ backend, invoked by Java).
        world.dispose();
    }

    /**
     * This is the contact listener that checks the world for collisions and contacts.
     * I use this method to evaluate when things collide, such as player colliding with floor.
     */
    private class GameContactListener implements ContactListener {

        private boolean areCollided(Contact contact, Object userA, Object userB) {
            Object userDataA = contact.getFixtureA().getUserData();
            Object userDataB = contact.getFixtureB().getUserData();

            // This is not in the video! It is a good idea to check that user data is not null.
            // Sometimes you forget to put user data or you get collisions by entities you didn't
            // expect. Not preventing this will probably result in a NullPointerException.
            if (userDataA == null || userDataB == null) {
                return false;
            }

            // Because you never know what is A and what is B, you have to do both checks.
            return (userDataA.equals(userA) && userDataB.equals(userB)) ||
                    (userDataA.equals(userB) && userDataB.equals(userA));
        }

        /**
         * This method is executed when a contact has started: when two fixtures just collided.
         */
        @Override
        public void beginContact(Contact contact) {
            // The player has collided with the floor.
            if (areCollided(contact, "player", "floor")) {
                player.setJumping(false);

                // If the screen is still touched, you have to jump again.
                if (Gdx.input.isTouched()) {
                    if(es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje == 2)
                        jumpSoundCR.play();
                    else
                        jumpSound.play();

                    // You just can't add a force here, because while a contact is being handled
                    // the world is locked. Therefore you have to find a way to remember to make
                    // the player jump AFTER the collision has been handled. Here I update the
                    // flag value mustJump. This will make the player jump on next frame.
                    player.setMustJump(true);
                }
            }

            // The player has collided with something that hurts.
            if (areCollided(contact, "player", "spike")) {

                // Check that is alive. Sometimes you bounce, you don't want to die more than once.
                if (player.isAlive()) {
                    player.setAlive(false);

                    // Sound feedback.
                    backgroundMusic.stop();
                    dieSound.play();

                    es.danirod.jddprototype.game.modelo.VariablesGlobales.muertes++;

                    // Add an Action. Actions are cool because they let you add animations to your
                    // game. Here I add a sequence action so that two actions happens one after
                    // the other. One action is a delay action. It just waits for 1.5 seconds.
                    // The second actions is a run action. It executes some code. Here, we go
                    // to the game over screen when we die.
                    stage.addAction(
                            Actions.sequence(
                                    Actions.delay(1.5f),
                                    Actions.run(new Runnable() {

                                        @Override
                                        public void run() {
                                        es.danirod.jddprototype.game.modelo.VariablesGlobales.posicion_muerte = player.getX() / es.danirod.jddprototype.game.modelo.Constants.PIXELS_IN_METER;
                                        game.setScreen(game.gameOverScreen);
                                        }
                                    })
                            )
                    );
                }
            }

            // el jugador colisiona con un muelle y salta automaticamente con mayor impulso
            if (areCollided(contact, "player", "muelle")) {
                player.jump((int)(es.danirod.jddprototype.game.modelo.Constants.IMPULSE_JUMP * 1.5), false);
            }

        }

        /**
         * This method is executed when a contact has finished: two fixtures are no more colliding.
         */
        @Override
        public void endContact(Contact contact) {
            // The player is jumping and it is not touching the floor.
            if (areCollided(contact, "player", "floor")) {
                if (player.isAlive()) {
                    if(es.danirod.jddprototype.game.modelo.VariablesGlobales.personaje == 2)
                        jumpSoundCR.play();
                    else
                        jumpSound.play();
                }
            }
        }

        // Here two lonely methods that I don't use but have to override anyway.
        @Override public void preSolve(Contact contact, Manifold oldManifold) { }
        @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
    }
}
