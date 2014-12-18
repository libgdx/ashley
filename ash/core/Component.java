package ash.core;

/**
 * <p>
 * Component mother class
 * 
 * Components are simple value objects that contain data relevant to the entity. Entities with similar functionality
 * will have instances of the same components. So we might have a position component
 * </p>
 * 
 * <p>
 * <code>public class PositionComponent
 * {
 *   public var x : Number;
 *   public var y : Number;
 * }</code>
 * </p>
 * 
 * we add components like
 * <p>
 * <code>
 * entity = new Entity().add(new Position(0,1)).add(new Display("something"));
 * engine.add(entity);
 * </code>
 * </p>
 * 
 * NOTE Erik Borgers: In Ash action script or eg C# implementations, there is no Component mother class. We do this here for better type checking 
 * 
 * 
 * 
 * @author Erik Borgers
 * 
 */
public class Component {

	// Ash does not remember the Entity in a Component. 
	// It implies an Entity "owns" a Component, while in Ash Components may be shared (says the blog)!
	// It would also encourage the programmer to start iterating over entities again and having references through the entity of components!
}
