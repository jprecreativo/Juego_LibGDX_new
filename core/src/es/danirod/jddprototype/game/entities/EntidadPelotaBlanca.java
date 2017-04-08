package es.danirod.jddprototype.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import es.danirod.jddprototype.game.Constants;

/**
 * Created by jose_ on 01/04/2017.
 */

public class EntidadPelotaBlanca extends Actor {

    // textura del muelle
    private Texture texture;

    // el mundo
    private World world;

    // el cuerpo asignado al muelle
    private Body body;

    // caracteristicas asignadas al muello
    private Fixture fixture;



    public EntidadPelotaBlanca(World world, Texture texture, float x, float y) {
        this.world = world;
        this.texture = texture;


        // creamos el cuerpo
        BodyDef def = new BodyDef();
        def.position.set(x, y + 0.1f);
        body = world.createBody(def);

        // le damos un contorno. está comentado para que traspase el objeto
        /*PolygonShape box = new PolygonShape();
        box.setAsBox(0.1f, 0.1f);
        fixture = body.createFixture(box, 1);
        fixture.setUserData("pelotaBlanca");
        box.dispose();*/

        // posicionamos el actor en la pantalla
        setPosition((x - 0.1f) * Constants.PIXELS_IN_METER, y * Constants.PIXELS_IN_METER);
        setSize(Constants.PIXELS_IN_METER / 3, Constants.PIXELS_IN_METER / 3);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void detach() {
        body.destroyFixture(fixture);
        world.destroyBody(body);
    }


}
