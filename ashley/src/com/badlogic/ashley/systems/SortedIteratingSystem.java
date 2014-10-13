package com.badlogic.ashley.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

/**
 * A simple EntitySystem that sorts each entity based on a provided comparator and then iterates over each 
 * entity and calls processEntity() for each entity every time the EntitySystem is updated.
 * 
 * @author Neil Urwin
 */

public abstract class SortedIteratingSystem extends IteratingSystem {

	/** The entities used by this system sorted by declared comparator */
	private ArrayList<Entity> sortedEntities;
	/** The comparator used to sort the entities */
	private Comparator<Entity> comparator;
	
	
	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with an
	 * order determined by a comparator.
	 * @param family The family of entities iterated over in this System
	 * @param comparator The comparator used to sort the entities
	 */
	public SortedIteratingSystem(Family family, Comparator<Entity> comparator) {
		this(family, comparator, 0);
	}

	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a 
	 * specific priority.
	 * @param family The family of entities iterated over in this System
	 * @param comparator The comparator used to sort the entities
	 * @param priority The priority to execute this system with (lower means higher priority)
	 */
	public SortedIteratingSystem(Family family, Comparator<Entity> comparator, int priority) {
		super(family, priority);
		this.comparator = comparator;
		sortedEntities = new ArrayList<Entity>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		for(int i = 0; i < getEntities().size(); i++){
			sortedEntities.add(getEntities().get(i));
		}
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		sortedEntities = null;
	}
	
	@Override
	public void update(float deltaTime) {
		int numEntities = getEntities().size();
		
		if(numEntities != sortedEntities.size()){
			sortedEntities.clear();
			for(int i = 0; i < getEntities().size(); i++){
				sortedEntities.add(getEntities().get(i));
			}
		}
		
		Collections.sort(sortedEntities, comparator);
			 	
		for (int i = 0; i < numEntities; ++i) {
			processEntity(sortedEntities.get(i), deltaTime);
		}
	}
	
	/**
	 * @return set of sorted entities processed by the system
	 */
	public ArrayList<Entity> getSortedEntities() {
		return sortedEntities;
	}

}
