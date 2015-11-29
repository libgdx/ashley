package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.core.ComponentOperationHandler.BooleanInformer;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;

public class ComponentOperationHandlerTests {

	private static class BooleanInformerMock implements BooleanInformer {
		public boolean delayed = false;
		
		@Override
		public boolean value () {
			return delayed;
		}
	}
	
	private static class ComponentSpy implements Listener<Entity> {
		public boolean called;
		
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			called = true;
		}
	}
	
	@Test
	public void add() {
		ComponentSpy spy = new ComponentSpy();
		BooleanInformerMock informer = new BooleanInformerMock();
		ComponentOperationHandler handler = new ComponentOperationHandler(informer);
		
		Entity entity = new Entity();
		entity.componentOperationHandler = handler;
		entity.componentAdded.add(spy);
		
		handler.add(entity);
		
		assertTrue(spy.called);
	}

	@Test
	public void addDelayed() {
		ComponentSpy spy = new ComponentSpy();
		BooleanInformerMock informer = new BooleanInformerMock();
		ComponentOperationHandler handler = new ComponentOperationHandler(informer);
		
		informer.delayed = true;
		
		Entity entity = new Entity();
		entity.componentOperationHandler = handler;
		entity.componentAdded.add(spy);
		
		handler.add(entity);
		
		assertFalse(spy.called);
		handler.processOperations();
		assertTrue(spy.called);
	}
	
	@Test
	public void remove() {
		ComponentSpy spy = new ComponentSpy();
		BooleanInformerMock informer = new BooleanInformerMock();
		ComponentOperationHandler handler = new ComponentOperationHandler(informer);
		
		Entity entity = new Entity();
		entity.componentOperationHandler = handler;
		entity.componentRemoved.add(spy);
		
		handler.remove(entity);
		
		assertTrue(spy.called);
	}
	
	@Test
	public void removeDelayed() {
		ComponentSpy spy = new ComponentSpy();
		BooleanInformerMock informer = new BooleanInformerMock();
		ComponentOperationHandler handler = new ComponentOperationHandler(informer);
		
		informer.delayed = true;
		
		Entity entity = new Entity();
		entity.componentOperationHandler = handler;
		entity.componentRemoved.add(spy);
		
		handler.remove(entity);
		
		assertFalse(spy.called);
		handler.processOperations();
		assertTrue(spy.called);
	}
}
