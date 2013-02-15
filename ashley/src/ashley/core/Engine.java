package ashley.core;

import ashley.signals.Listener;
import ashley.signals.Signal;
import ashley.utils.Array;
import ashley.utils.ObjectMap;
import ashley.utils.ObjectMap.Entries;
import ashley.utils.ObjectMap.Entry;

/**
 * The Engine class is the heart of the Entity framework. It is responsible for keeping track of entities and
 * managing EntitySystems. The Engine should be updated every tick via the update() method.
 * 
 * With the Engine you can:
 * 
 * - Add/Remove Entities
 * - Add/Remove EntitySystems
 * - Obtain a list of entities for a specific Family
 * - Update the main loop
 * 
 * @author Stefan Bachmann
 */
public class Engine {
	/** An unordered array that holds all entities in the Engine */
	private Array<Entity> entities;
	/** An ordered list of EntitySystem */
	private Array<EntitySystem> systems;
	/** A hashmap that organises all entities into family buckets */
	private ObjectMap<Family, Array<Entity>> families;
	
	/** A listener for the Engine that's called everytime a component is added. */
	private final Listener<Entity> componentAdded;
	/** A listener for the Engine that's called everytime a component is removed. */
	private final Listener<Entity> componentRemoved;
	
	public Engine(){
		entities = new Array<Entity>(false, 16);
		systems = new Array<EntitySystem>();
		families = new ObjectMap<Family, Array<Entity>>();
		
		componentAdded = new Listener<Entity>(){
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				componentAdded(object);
			} 
		};
		
		componentRemoved = new Listener<Entity>(){
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				componentRemoved(object);
			} 
		};
	}
	
	/**
	 * Add an entity to this Engine
	 * @param entity The Entity to add
	 */
	public void addEntity(Entity entity){
		entities.add(entity);
		
		Entries<Family, Array<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, Array<Entity>> entry = entries.next();
			if(entry.key.matches(entity)){
				entry.value.add(entity);
				entity.getFamilyBits().set(entry.key.getFamilyIndex());
			}
		}
		
		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
	}
	
	/**
	 * Remove an entity from this Engine
	 * @param entity The Entity to remove
	 */
	public void removeEntity(Entity entity){
		entities.removeValue(entity, true);
		
		Entries<Family, Array<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, Array<Entity>> entry = entries.next();
			if(entry.key.matches(entity)){
				entry.value.removeValue(entity, true);
				entity.getFamilyBits().clear(entry.key.getFamilyIndex());
			}
		}
		
		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
	}
	
	/**
	 * Add the EntitySystem to this Engine
	 * @param system The system to add
	 */
	public void addSystem(EntitySystem system){
		systems.add(system);
		system.addedToEngine(this);
	}
	
	/**
	 * Removes the EntitySystem from this Engine
	 * @param system The system to remove
	 */
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true))
			system.removedFromEngine(this);
	}
	
	/**
	 * Returns an Array of entities for the specified Family. Will return the same instance every time.
	 * @param family The Family
	 * @return An Array of Entities
	 */
	public Array<Entity> getEntitiesFor(Family family){
		Array<Entity> entities = families.get(family, null);
		if(entities == null){
			entities = new Array<Entity>();
			for(Entity e:entities){
				if(family.matches(e))
					entities.add(e);
			}
			families.put(family, entities);
		}
		return families.get(family);
	}
	
	/**
	 * Internal listener for when a Component is added to an entity
	 * @param entity The Entity that had a component added to
	 */
	private void componentAdded(Entity entity){
		Entries<Family, Array<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, Array<Entity>> entry = entries.next();
			if(!entity.getFamilyBits().get(entry.key.getFamilyIndex())){
				if(entry.key.matches(entity)){
					entry.value.add(entity);
					entity.getFamilyBits().set(entry.key.getFamilyIndex());
				}
			}
		}
	}
	

	/**
	 * Internal listener for when a Component is removed from an entity
	 * @param entity The Entity that had a component removed from
	 */
	private void componentRemoved(Entity entity){
		Entries<Family, Array<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, Array<Entity>> entry = entries.next();
			if(entity.getFamilyBits().get(entry.key.getFamilyIndex())){
				if(!entry.key.matches(entity)){
					entry.value.removeValue(entity, true);
					entity.getFamilyBits().set(entry.key.getFamilyIndex());
				}
			}
		}
	}
	
	/**
	 * Updates all the systems in this Engine
	 * @param deltaTime The time passed since the last frame
	 */
	public void update(float deltaTime){
		for(int i=0; i<systems.size; i++){
			systems.get(i).update(deltaTime);
		}
	}
}
