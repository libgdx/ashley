# Ashley

A tiny entity framework written in Java. It's inspired by frameworks like
[Ash](http://www.ashframework.org/) (hence the name) and
[Artemis](http://gamadu.com/artemis/). Ashley tries to be a high-performance
entity framework  without the use of black-magic and thus making the API easy
and transparent to use.

Licensed under the Apache 2.0

## Examples

There are some examples that are located in the
[Tests Directory](ashley-tests/src/ashley/tests)

Components are data holders and shouldn't contain any logic. How you structure your components is completely up to you, as long as it extends the base "Component" class.

```java
public class Position extends Component {
  public float x, y;

  public Position(float x, float y, float dir) {
    this.x = x;
    this.y = y;
  }
}
```

Systems are processing classes used in Ashley. They might iterate through entities or perform some other task every tick.

Ashley provides a basic `IteratingSystem` that simplifies the process of entity processing systems. However you can always define your own custom implementation via `EntitySystem`.

```java
public class MovementSystem extends IteratingSystem {
  public MovementSystem () {
    super(Family.getFamilyFor(Position.class, Velocity.class));
  }

  public void processEntity (Entity entity, float deltaTime) {
    Position position = entity.getComponent(Position.class);
    Velocity velocity = entity.getComponent(Velocity.class);
    
    position.x += velocity.x * deltaTime;
    position.y += velocity.y * deltaTime;
  }
}
```

Here you'll see a demonstration on how to use the basic `EntitySystem`

```java
public class RenderingSystem extends EntitySystem {
  private IntMap<Entity> entities;

  public RenderingSystem () {
    // setup the rendering system.
  }

  public void addedToEngine (Engine engine) {
    // returns a reference to all entities within the Engine that match the family of components
    entities = engine.getEntitiesFor(Family.getFamilyFor(Position.class, Display.class));
  }

  public void update (float deltaTime) {
    Keys keys = entities.keys();
    while (keys.hasNext) {
      // render your entities
    }
  }
}
```

Meshing it all together:

```java
class SomeGame {
  public SomeGame() {
    Engine engine = new Engine();
    engine.addSystem(new MovementSystem());
    engine.addSystem(new RenderingSystem());

    Entity entity = new Entity();
    entity.add(new Position(0.0f,0.0f));
    entity.add(new Velocity(3.0f));
    entity.add(new Display("hero.png"));

    engine.addEntity(entity);
  }

  public void update(float deltaTime) {
    /* your main loop */

    engine.update(deltaTime);

    /* more of your main loop */
  }
}
```

## Contributing

If you'd like to contribute or submit a bugfix please fork this repo and send a pull requests. Any input & fixes are appreciated!
