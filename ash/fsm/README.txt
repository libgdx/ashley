h1. Finite State Machine

This package contains classes for creating and using finite state machines with Ash. Specifically, it contains
classes for creating states based on the components on an entity, and for switching between those states.

h2. An example

bc. var fsm : EntityStateMachine = new EntityStateMachine( entity );
fsm.createState( "guard" )
   .add( Patrol ).withInstance( new Patrol( guardPath ) );
fsm.createState( "investigate" )
   .add( Investigate );
fsm.createState( "defend" )
   .add( Defend );
fsm.changeState( "guard" );

h2. The methods

A finite state machine is an instance of the EntityStateMachine class. You pass it a reference to the entity it will
manage when constructing it. You will usually store the state machine in a component on the entity so it can be
recovered from within any system that is operating on the entity.

A state machine is configured with states, and the state can be changed by calling the state machine's @changeState()@ 
method. States are identified by a string, which is assigned when the state is created and used to identify the state 
when calling the @changeState()@ method.

States are instances of the EntityState class. They may be added to the EntityStateMachine using the 
@EntityStateMachine.addState()@ method, or they may be created and added in one call using the 
@EntityStateMachine.createState()@ method.

A state is a set of components that should be added to the entity when that state is entered, and removed when that
state exits (unless they are also required for the next state).

States within the state machine are configured with component providers. A component provider has a method to
provide a component to the entity, and a second method to identify if two component providers are equivalent -
i.e. if they would provide an equivalent component.

A component provider implements the ComponentProvider interface.

The standard method for adding a component provider to a state is

bc. state.add( componentType : Class ).withProvider( provider : ComponentProvider );

There are shortcuts for using the standard component providers

bc. state.add( componentType : Class ).withType( instanceType : Class );

uses the ComponentTypeProvider to provide a new instance of instanceType. instanceType should be the same as or componentType of a type that extends 
componentType.

bc. state.add( componentType : class );

If instanceType is the same as componentType then it may be omitted.

bc. state.add( componentType : Class ).withInstance( component : * );

uses the ComponentInstanceProvider to provide the same instance every time it is called.

bc. state.add( componentType : Class ).withSingleton( instanceType : Class );

uses the ComponentSingletonProvider to create a new instance of instanceType and provide that same instance every 
time it is called. The instance is not created until it is first required.

h2. Chaining

The methods can be chained together to create a fluent interface. For example

bc.. var fsm : EntityStateMachine = new EntityStateMachine( entity );
			
fsm.createState( "playing" )
   .add( Motion ).withInstance( new Motion( 0, 0, 0, 15 ) )
   .add( MotionControls ).withInstance( new MotionControls( Keyboard.LEFT, Keyboard.RIGHT, Keyboard.UP, 100, 3 ) )
   .add( Gun ).withInstance( new Gun( 8, 0, 0.3, 2 ) )
   .add( GunControls ).withInstance( new GunControls( Keyboard.SPACE ) )
   .add( Collision ).withInstance( new Collision( 9 ) )
   .add( Display ).withInstance( new Display( new SpaceshipView() ) );
			
var deathView : SpaceshipDeathView = new SpaceshipDeathView();
fsm.createState( "destroyed" )
   .add( DeathThroes ).withInstance( new DeathThroes( 5 ) )
   .add( Display ).withInstance( new Display( deathView ) )
   .add( Animation ).withInstance( new Animation( deathView ) );
			
fsm.changeState( "playing" );
