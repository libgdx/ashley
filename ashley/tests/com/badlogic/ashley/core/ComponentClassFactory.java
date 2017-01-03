package com.badlogic.ashley.core;

import org.mockito.asm.ClassWriter;
import org.mockito.asm.MethodVisitor;
import org.mockito.asm.Opcodes;

/**
 * Class loader allowing dynamic {@link Component} class definitions.
 * 
 * Useful for tests that need several different component types.
 * 
 * Adapted from https://dzone.com/articles/fully-dynamic-classes-with-asm
 * 
 * @author mgsx
 *
 */
public class ComponentClassFactory extends ClassLoader 
{
	/**
	 * create new {@link Component} type
	 * @param name name of the class to create
	 * @return created class
	 */
    @SuppressWarnings("unchecked")
	public Class<? extends Component> createComponentType(String name){
    	
    	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		
    	String interfacePath = Component.class.getName().replaceAll("\\.", "/");
    	
    	// create public class (default package) implementing Component
    	
    	cw.visit(Opcodes.V1_6, // Java 1.6
				Opcodes.ACC_PUBLIC, // public class
				name, // package and name
		        null, // signature (null means not generic)
		        "java/lang/Object", // superclass
		        new String[]{ interfacePath }); // interfaces
		
    	// create public no-arg constructor
    	
		MethodVisitor con = cw.visitMethod(
				Opcodes.ACC_PUBLIC,                 // public method
		        "<init>",                           // method name 
		        "()V",                              // descriptor
		        null,                               // signature (null means not generic)
		        null);                              // exceptions (array of strings)
		
		// define constructor body : call super constructor (java.lang.Object)
		
		con.visitCode();                            // Start the code for this method
		con.visitVarInsn(Opcodes.ALOAD, 0);         // Load "this" onto the stack
		con.visitMethodInsn(Opcodes.INVOKESPECIAL,  // Invoke an instance method (non-virtual)
		        "java/lang/Object",                 // Class on which the method is defined
		        "<init>",                           // Name of the method
		        "()V");                             // Descriptor
		con.visitInsn(Opcodes.RETURN);              // End the constructor method
		con.visitMaxs(1, 1);                        // Specify max stack and local vars
		
		// close class definition.
		
		cw.visitEnd();
		
		// load and return class.
		byte[] b = cw.toByteArray();
		return (Class<? extends Component>)defineClass(name, b, 0, b.length);
    }
}