## Changelog

### Ashley 1.7.3

* **Update**: uses Libgdx 1.9.2.

### Ashley 1.7.2

* ** Bug fix**: `Engine` doesn't use `EntitySystem` `iterator()`. Issue #209.
* ** Bug fix**: Fixes `Engine` left unusable, in the updating state, after an uncaught exception in a subsystem. Issue #210.
* ** Bug fix**: Fixes `FamilyManager` not cleaning up properly when a triggering a listener that throws. Issue #210.

### Ashley 1.7.1

* **API addition**: expose `IntervalSystem` interval value (read-only). Commit 5277cbe83264d0ddc851a823232f51c350e9c387.
* **Bug fix**: fixed pending entity operations not being processed in the right order. Issue #197.
* **Bug fix**: fixed adding component in empty family `EntityListener` causing wrong `EntityListener` calls. Issue #203.
* **Bug fix**: fixed some entity operations not being performed under special circumstances. Commit c45ba3b34860121571eb9a660a76bc82ac0a1a96.
* **Internals**: split Engine internals into loosely coupled, single responsibility smaller classes. Issue #178.
* **Update**: uses Libgdx 1.8.0

### Ashley 1.7.0

* **API change**: removed UUIDs from entities, users should implement their own ID system when needed. This helps with networked games. Issue #182.
* **Update**: components are added/removed immediately, listeners are notified after the current system finishes updating. Commit 84de67991b343828c668985bd1cd5a4a1309cafa.
* **Bug fix**: fixed entity listeners not being called if a component is added inside another listener. Commit b611dd98e28f4dc15d64b7340c8d507ba29153d5.
* **Bug fix**: does not allocate memory inside `updateFamilyMembership()`. Commit 9ae5e459b437d7a51bfc48283cf5cd20e82989f9.
* **Update**: uses Libgdx 1.7.1. Commit 4799007c457f714ad9fe0ad606a4a0f25b825ab7.

### Ashley 1.6.0

* **API change**: `Component` is now an interface. Issue #170.
* **API addition**: adds `getEngine()` to `EntitySystem`. Issue #167.
* **API addition**: `EntityListeners` now are notified by priority order. Commit f9152e61d30907000bbc483de6fa0afb61c16c31.
* **API consistency**: defines what happens when an `Entity` is added twice to an `Engine` and when we try do nested updates. We throw an exception in both cases. Issue #173.
* **API consistency**: adding an `EntitySystem` of the same class twice replaces the previous one, just like it has always happened when adding `Components` to an `Entity`. Commit 1dbfcb4dc1ea0ea0a0fdb0d481c00b7dd9b8dd8f.
* **Update**: uses Libgdx 1.6.4. Commit 0284510e759c7d97c7d1e7acaf7d29d6071434cc.

### Ashley 1.5.0

* **API addition**: adds `getFamily()` method to `IntervalIteratingSystem` and `IteratingSystem`. Commit 55241f5256c0ec186992262c7d598811bc4664fe.
* **API change**: `IntervalSystem`'s `update()` method is now `final`, as it doesn't make sense to override it. Commit bffa44cd5a59ca156e63b2ce7919869e41538907.
* **Update**: updated Libgdx version to 1.6.0. Commit 9b0eb90d1a73f8e9ce5e42435a81bf49eb0ed29b.
* **Enhancement**: allows `IntervalSystem` to update faster than the main loop. Issue #151.
* **Enhancement**: improved `hashCode()` and `equals()` implementation of `Family`. Commit b01cb15082df1358e620214f4bdef272cad700c4.
* **Bug fix**: fixed GWT build and made a [Jenkins job](http://libgdx.badlogicgames.com:8080/job/ashley-superjumper/) to make sure we also test GWT integrity. Commit e2be43e39c1634c3ef0c64fcb8f84f2e3e0ad3b1.
* **Bug fix**: empty `Family` now matches empty entities. Commit db1286f69c22a5bb4b9e7055d8f65d3063824a9b.

### Ashley 1.4.0

* **API deprecation**: finally removed the `Family.getFor()` methods. In order to retrieve a family, you can do `Family.all().one().exclude().get()`. Commit d6222d9ee0cca6f06dd61b04bae6d092497425a5.
* **API change**: makes family `Builder` package protected. Issue #137.
* **Bug fix**: `EntityOperation` and `ComponentOperation` are now properly reset when put back into their pools. This doesn't affect the API at all and it's an implementation detail fix. Commit 356cf4097f7c870cccc67cffca62ff3f32f103da.
* **Bug fix**: fixes `PooledEngine` always returning new components. Commit eff243f65e98a57c5e1a8045f85465cd1acf085d.
* **Bug fix**: fixes GWT build. Issue #135.
* **Bug fix**: avoids removing a `PooledEntity` twice. Commit cf7a02006d2269cd64976de6e7b56bd7553708de.

### Ashley 1.3.3

* **API addition**: added `getEntity()` method to `Engine`, it is now possible to retrieve entities by their ID. This will ease networked entity systems. Commit 1c21f972998df9d5125617f3e6b0c6cc279829d2.
* **API addition**: added `getEntities()` method to `Engine`, which returns an immutable array of all the entities registered with said engine. Commit ed3d3b7ef040023c8bd4d64efb6e9fcad69dc9aa.
* **Bug fix**: remove old component when adding a new one of the same type to an entity. Commit 3910be7b4ae472f415c22c0f5109e4f140056f7d.
* **Bug fix**: fixes component not being added to an entity when done from an `entityRemoved()` family entity listener handler. Commit 9f6aa51229544f9a66c12f7f7fdcff7e6bf5dbde.

### Ashley 1.3.2

* **API**: we now use a builder pattern to create `Family` objects. More about it on the [wiki](https://github.com/libgdx/ashley/wiki/How-to-use-Ashley#entity-families). Commit addition974f12f6d53c5d92992ddd6bf09edd44937d9e66.
* **API addition**: new [`SortedIteratingSystem`](http://libgdx.badlogicgames.com/ashley/docs/com/badlogic/ashley/systems/SortedIteratingSystem.html) by [Lusito](https://github.com/libgdx/ashley/commits/master?author=Lusito). Commit 905b26895536c57d9d42d994e62237c60f909e0c.
* **API addition**: now `ImmutableArray` implements the `Iterable` interface, which makes it a lot easier to iterate over entity collections. Commit c39b09772a4514c180846204ce55cdc2eae71cc5.
* **Bug fix**: avoid double entity removal by accident. Commit 1c861a27b85d8b98b854814c2820f80d001ca715.
* **Bug fix**: fixes `StackOverflowError` when processing entity operations. Issue #103.
* **Bug fixes**: fixes freeze when calling `removeAllEntities()`. Issue #101.
* **Improvement**: we made a bunch of changes that increase performance.

### Ashley 1.3.1

**Bug fix**: fixed `IllegalArgumentException` thrown when trying to remove components from an already reset entity. The call has no effect now. Thanks for that [SgtCoDFish](https://github.com/SgtCoDFish). Issue #77.
**Bug fix**: fixed family match failure due to silly silly hashing. Issue #78.
**Bug fix**: fixed `entity.getId()` always returning `0` for recycled pooled entities. Issue #82.
**Bug fix**: fixed nested iteration problem caused by adding/removing entities from an entity added/removed listener handler. Issue #81.
**Bug fix**: fixed missing type error in GWT when using `PooledEngine`. GWT should be quite smooth now. Commit cb3347239504972b86653efab8d9051ec5366760.

### Ashley 1.3.0

* **API addition**: adds `IntervalSystem` and `IntervalIteratingSystem`, which are updated at a fixed interval. Commit 47bf907b15ad8ed4297a10eb6b6b311e1542dcb8.
* **API addition**: adds `getEntities()` to `IteratingSystem` and `IntervalIteratingSystem`. Commit f1ccdbea63a175f2a76c26b46661998b6a131c59.
* **API change**: entities use `long` as ID. Changes `entity.getIndex()` for `entity.getId()`. Ids are reset to 0 after the entity is removed from the engine. Commit 6f9d2b78c34f72d03e76d40c8d9704f1c763e59a.
* **Bug fix**: we finally got rid of the issues related to deleting entities and adding/removing components mid system processing. Commit a2a63f4e42e09e3221331b2333e675b3a4ab6fe3.
* **Bug fix**: fixes problem with removing pooled entities. #64.
* **Bug fix**: fixes pooled entities not being fully reset. Issue #72.
* **Bug fix**: fixes broken GWT compatibility. Commit 9a01938713946cad0c31d21ecb723443ccf6b2ff.

### Ashley 1.2.0

* **API addition**: people requested to put `Entity#getComponent(Class)` back in, so we accepted [SgtCODFish](https://github.com/libgdx/ashley/commits/master?author=SgtCoDFish)'s PR. `ComponentMapper` is still the most efficient and encouraged method to retrieve an entity's components. Issue #51.
* **API addition**: added `PooledEngine#clearPools()` to delete unused entity and component pool memory. Commit 28372993d60d6e2f460f36e27df953936e550933.
* **API addition**: adds `EntitySystem#setProcessing(boolean)` so as to be able to enable/disable systems at will. Commit 188239aeaa3e99c9bd7ebab268a9e9959d2a09c2.
* **API addition**: adds `Engine#getSystems()`. Commit 8f54a2a5ee97c87cba57bd187b4aeb2aef7ec831.
* **API addition**: you can now listen to entity events on a per family basis. Commit f90d12932bd958061eb0ad33b925cf6457880c46.
* **Crash fix**: things would blow up if you deleted entities from `IteratingSystem#processEntity()`. Commit d5ace4e43a32c27fec82821b90b3e55c89ad373f.
* **Bug fix**: removing listeners while dispatching a `Signal` would make other listeners miss the event. Thanks to [vlaaad](https://github.com/libgdx/ashley/commits/master?author=vlaaad) for the PR! Issue #52.
* **Bug fix**: some entities were skipped if the user removed an entity from the engine mid system update. Removing an entity from a system by changing its components mid iteration also caused problems, which are now solved. Issue #44.
* **Bug fix**: there was a problem with family membership not being properly updated when removing a component from an entity was supposed to result in said entity being added to the family. This only affected families that exclude components. Issue #56.
* **Internals**: small performance improvement. Issue #59.
* **Others**: added benchmark suite to compare Ashley's performance with Atermis'. Commit d6fec987e1ce493c559a7e8fdea7fc8f6cd82d7b.

### Ashley 1.1.0

* **Performance improvements**: massive performance gains with O(1) component retrieval through `ComponentMapper` and the use of [`Array<Entity>`](https://github.com/libgdx/ashley/commit/db641f9697719e97d31017e46ea2003a43eb83cd) rather than `IntMap<Entity>`. Unfortunately this changes the API slightly, but hopefully you guys will appreciate the benefits. Commit 8c98e9799bb6cc65cf61669eed36d7f8f04a2da4.
* **[Complete Ashley manual](https://github.com/libgdx/ashley/wiki)**: now the wiki has a lot more content, it fully covers Ashley. The rest is sugar.
* **API Cleanup**: renames `Family.getFamilyFor()` to `Family.getFor()` for simplicity. Commit 3182782dfd71e13f0bf1c03490512d91921117a3.
* **Configurable pools**: `PooledEngine` now accepts the initial and maximum sizes for the entity and components pools. This will add a lot more flexibility to bigger games. Commit 569f089f9236dc8c2da09f4fe3b4d33880745d8a.
* **Libgdx 1.3.0**: Ashley now depends on gdx-core 1.3.0. Commit aa97ecbf8344ea6de5afeb608ac65ccd12e80965.

### Ashley 1.0.1

* **Gradle**: we got rid of the Eclipse projects and now use Gradle to manage our dependencies and build process. Commit 3cd93f2c17fd65470a60dfa2af7d84c2ee7e7865.
* **Maven Central**: Ashley is now available from Maven Central, which makes it dead easy for your project to depend on it. In Gradle, add the following dependency: `compile "com.badlogicgames.ashley:ashley:1.0.1"`.
* **[Jenkins build](http://libgdx.badlogicgames.com:8080/job/ashley/)**: Mario kindly offered us some server time to make sure Ashley is always stable.
*** [Unit tests](https://github.com/libgdx/ashley/tree/master/ashley/tests/com/badlogic/ashley)**: there are unit tests for pretty much every component in Ashley. They are run after every commit by our Jenkins job.
* **[Immutable collections](https://github.com/libgdx/ashley/tree/master/ashley/src/com/badlogic/ashley/utils)**: core Ashley classes now return `ImmutableArray` and `ImmutableIntMap` references, making it harder for client code to break the system.
* **Family filtering**: now we get the collection of entities that have a set of components, have at least one component from a given set and do not have a single component from another given set. Commit 9492d14a3e5cf4ad0d87305f8f7bb298bb8d687a.
* **[GWT](http://www.gwtproject.org/) compatibility**: you can now use Ashley for HTML5 games through the magic of GWT and Libgdx.
* **Depends on gdx core**: this has allowed us to remove all the duplicated optimised container classes and gives us a GWT compatible reflection API. Some might say it's a big dependency but we do believe the pros outweigh the cons. Commit 81d9a2e5f38df186f4b147b82ede274c950795b6.
* Cleanup and bug fixes.
