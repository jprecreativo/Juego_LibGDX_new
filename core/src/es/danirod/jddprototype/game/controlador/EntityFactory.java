/*
 * This file is part of Jump Don't Die
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

package es.danirod.jddprototype.game.controlador;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import es.danirod.jddprototype.game.modelo.entidad.EntidadMuelle;
import es.danirod.jddprototype.game.modelo.entidad.EntidadPelotaBlanca;
import es.danirod.jddprototype.game.modelo.entidad.EntidadPelotaVerde;
import es.danirod.jddprototype.game.modelo.entidad.EntidadPinchoRotado;
import es.danirod.jddprototype.game.modelo.entidad.FloorEntity;
import es.danirod.jddprototype.game.modelo.entidad.PlayerEntity;
import es.danirod.jddprototype.game.modelo.entidad.SpikeEntity;
import es.danirod.jddprototype.game.modelo.VariablesGlobales;

/**
 * This class creates entities using Factory Methods.
 */
public class EntityFactory {

    private AssetManager manager;

    /**
     * Create a new entity factory using the provided asset manager.
     * @param manager   the asset manager used to generate things.
     */
    public EntityFactory(AssetManager manager) {
        this.manager = manager;
    }

    /**
     * Create a player using the default texture.
     * @param world     world where the player will have to live in.
     * @param position  initial position ofr the player in the world (meters,meters).
     * @return          a player.
     */
    public PlayerEntity createPlayer(World world, Vector2 position) {
        Texture playerTexture;
        if(VariablesGlobales.personaje == 0)
            playerTexture = manager.get("player.png");
        else if(VariablesGlobales.personaje == 1)
            playerTexture = manager.get("player2.png");
        else
            playerTexture = manager.get("player3.png");
        return new PlayerEntity(world, playerTexture, position);
    }

    /**
     * Create floor using the default texture set.
     * @param world     world where the floor will live in.
     * @param x         horizontal position for the spikes in the world (meters).
     * @param width     width for the floor (meters).
     * @param y         vertical position for the top of this floor (meters).
     * @return          a floor.
     */
    public FloorEntity createFloor(World world, float x, float width, float y) {
        Texture floorTexture = manager.get("floor.png");
        Texture overfloorTexture = manager.get("overfloor.png");
        return new FloorEntity(world, floorTexture, overfloorTexture, x, width, y);
    }

    /**
     * Create spikes using the default texture.
     * @param world     world where the spikes will live in.
     * @param x         horizontal position for the spikes in the world (meters).
     * @param y         vertical position for the base of the spikes in the world (meters).
     * @return          some spikes.
     */
    public SpikeEntity createSpikes(World world, float x, float y) {
        Texture spikeTexture = manager.get("spike.png");
        return new SpikeEntity(world, spikeTexture, x, y);
    }

    public EntidadPinchoRotado creaPinchosRotados(World world, float x, float y) {
        Texture spikeTexture = manager.get("spike.png");
        return new EntidadPinchoRotado(world, spikeTexture, x, y);
    }

    public EntidadMuelle creaMuelles(World world, float x, float y) {
        Texture texturaMuelle = manager.get("muelle.png");
        return new EntidadMuelle(world, texturaMuelle, x, y);
    }

    public EntidadPelotaBlanca crearPelotaBlanca(World world, float x, float y) {
        Texture texturaPelotaBlanca = manager.get("pelotaBlancaPequeña.png");
        return new EntidadPelotaBlanca(world, texturaPelotaBlanca, x, y);
    }

    public EntidadPelotaVerde crearPelotaVerde(World world, float x, float y) {
        Texture texturaPelotaBlanca = manager.get("pelotaVerde.png");
        return new EntidadPelotaVerde(world, texturaPelotaBlanca, x, y);
    }
}
