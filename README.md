# Ashley

A tiny entity framework in Java. It's inspired by frameworks like
[Ash](http://www.ashframework.org/) (hence the name) and
[Artemis](http://gamadu.com/artemis/). Ashley tries to be a high-performance
entity framework  without the use of black-magic and thus making the API easy
and transparent to use.

## Examples

There are some examples that are located in the
[Tests Directory](ashley-tests/src/ashley/tests)

Defining a component is rather simple. It is just a data structure. Whether you
want to use getter and setters is up to you. There is no enforced convention for
this.

```java
public class Position extends Component {
  public float x, y, dir;

  public Position(float x, float y, float dir) {
    this.x = x;
    this.y = y;
    this.dir = dir;
  }
}
```

Defining a system is also just as easy. A basic `IteratingSystem` exists for
quick work. However, you will find that you will have to use `EntitySystem` and
write your own.

```java
public class MovementSystem extends IteratingSystem {
  public MovementSystem () {
    super(Family.getFamilyFor(Position.class, Velocity.class));
  }

  public void processEntity (Entity entity, float deltaTime) {
    Position position = entity.getComponent(Position.class);
    Velocity velocity = entity.getComponent(Velocity.class);
    float dx = MathUtils.cos(position.x) * velocity.speed;
    float dy = MathUtils.sin(position.y) * velocity.speed;
    position.add(dx, dy);
  }
}
```

Here you'll see a demonstration on how to use the basic `EntitySystem`

```java
public class RenderingSystem extends EntitySystem {
  private IntMap<Entity> entities;

  public RenderingSystem () {
    // setup the rendering system. Maybe pass in a camera or something
  }

  public void addedToEngine (Engine engine) {
    entities = engine.getEntitiesFor(Family.getFamilyFor(Position.class, Display.class));
  }

  public void update (float deltaTime) {
    Keys keys = entities.keys();
    while (keys.hasNext) {
      Entity e = entities.get(keys.next());
      Position position = e.getComponent(Position.class);
      Display display = e.getComponent(Display.class);

      display.draw(position.x, position.y, position.dir);
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
    entity.add(new Position(0,0,0));
    entity.add(new Velocity(3));
    entity.add(new Health(100));
    entity.add(new Display("some value"));

    engine.addEntity(entity);
  }

  public void update(float delta) {
    /* some magic here */

    engine.update(delta)

    /* more magic */
  }
}
```

## Contributing

Fork this repo and send pull requests.
