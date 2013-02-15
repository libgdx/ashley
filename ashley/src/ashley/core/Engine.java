package ashley.core;

import ashley.signals.Listener;
import ashley.signals.Signal;
import ashley.utils.Array;
import ashley.utils.ObjectMap;
import ashley.utils.ObjectMap.Entries;
import ashley.utils.ObjectMap.Entry;

public class Engine {
	private Array<Entity> entities;
	private Array<EntitySystem> systems;
	private ObjectMap<Family, Array<Entity>> families;
	
	public final Listener<Entity> componentAdded;
	public final Listener<Entity> componentRemoved;
	
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
	
	public void addSystem(EntitySystem system){
		systems.add(system);
		system.addedToEngine(this);
	}
	
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true))
			system.removedFromEngine(this);
	}
	
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
	
	public void update(float deltaTime){
		for(int i=0; i<systems.size; i++){
			systems.get(i).update(deltaTime);
		}
	}
}
