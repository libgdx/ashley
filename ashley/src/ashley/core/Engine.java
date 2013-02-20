package ashley.core;

import java.util.Comparator;

import ashley.signals.Listener;
import ashley.signals.Signal;
import ashley.utils.Array;
import ashley.utils.IntMap;
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
	private static SystemComparator comparator = new SystemComparator();
	
	/** An unordered array that holds all entities in the Engine */
	private Array<Entity> entities;
	/** An unordered list of EntitySystem */
	private Array<EntitySystem> systems;
	/** A hashmap that organises all entities into family buckets */
	private ObjectMap<Family, IntMap<Entity>> families;
	
	/** A listener for the Engine that's called everytime a component is added. */
	private final Listener<Entity> componentAdded;
	/** A listener for the Engine that's called everytime a component is removed. */
	private final Listener<Entity> componentRemoved;
	
	public Engine(){
		entities = new Array<Entity>();
		systems = new Array<EntitySystem>();
		families = new ObjectMap<Family, IntMap<Entity>>();
		
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
		
		Entries<Family, IntMap<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, IntMap<Entity>> entry = entries.next();
			if(entry.key.matches(entity)){
				entry.value.put(entity.getIndex(), entity);
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
		
		if(!entity.getFamilyBits().isEmpty()){
			Entries<Family, IntMap<Entity>> entries = families.entries();
			while(entries.hasNext){
				Entry<Family, IntMap<Entity>> entry = entries.next();
				if(entry.key.matches(entity)){
					entry.value.remove(entity.getIndex());
					entity.getFamilyBits().clear(entry.key.getFamilyIndex());
				}
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
		
		systems.sort(comparator);
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
	 * Returns an IntMap of entities for the specified Family. Will return the same instance every time.
	 * @param family The Family
	 * @return An IntMap of Entities
	 */
	public IntMap<Entity> getEntitiesFor(Family family){
		IntMap<Entity> entities = families.get(family, null);
		if(entities == null){
			entities = new IntMap<Entity>();
			for(Entity e:this.entities){
				if(family.matches(e))
					entities.put(e.getIndex(), e);
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
		Entries<Family, IntMap<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, IntMap<Entity>> entry = entries.next();
			if(!entity.getFamilyBits().get(entry.key.getFamilyIndex())){
				if(entry.key.matches(entity)){
					entry.value.put(entity.getIndex(), entity);
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
		Entries<Family, IntMap<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, IntMap<Entity>> entry = entries.next();
			if(entity.getFamilyBits().get(entry.key.getFamilyIndex())){
				if(!entry.key.matches(entity)){
					entry.value.remove(entity.getIndex());
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
	
	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}
}
