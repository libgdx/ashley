package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * RenderSystem to easily render entities using a batch.
 * calls batch.begin() and end() before and after entities update
 * @param <T>   The batch type used
 */
public abstract class RenderSystem<T extends Batch> extends IteratingSystem {
    /**
     * The batch used to render things
     */
    private T batch;

    /**
     * Constructor. Same as IteratingSystem but extended with a batch parameter
     * @param family    The Entity Family to look for
     * @param batch     The Batch used to render entities
     */
    public RenderSystem(Family family, T batch) {
        super(family);
        this.batch = batch;
    }

    /**
     * Constructor. Same as IteratingSystem but extended with a batch parameter
     * @param family    The Entity Family to look for
     * @param priority  The Priority of the system
     * @param batch     The Batch used to render entities
     */
    public RenderSystem(Family family, int priority, T batch) {
        super(family, priority);
        this.batch = batch;
    }

    /**
     * Injects batch.begin() and batch.end() into the update of IteratingSystem
     * @param deltaTime The timedelta
     */
    @Override
    public void update(float deltaTime){
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    /**
     * Method to get the Batch from inside and outside
     * @return  The Batch used to render entities
     */
    public T getBatch(){
        return batch;
    }

    /**
     * Method to set the Batch after object creation
     * @param batch The Batch used to render entities
     */
    public void setBatch(T batch){
        this.batch = batch;
    }
}
