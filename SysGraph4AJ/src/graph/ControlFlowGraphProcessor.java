package graph;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import junit.framework.Assert;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;

public class ControlFlowGraphProcessor {

	private static final ControlFlowGraphBuilder CONTROL_FLOW_GRAPH_BUILDER = new ControlFlowGraphBuilder();

	@SuppressWarnings("rawtypes")
	public BasicBlockNode process(Class clazz, Method method) {
		org.apache.bcel.classfile.Method methodBcelFromClass = this.getMethodFromClass(clazz, method);
		MethodGen methodGen = new MethodGen(methodBcelFromClass, clazz.getCanonicalName(), new ConstantPoolGen(methodBcelFromClass.getConstantPool()));
		ControlFlowGraph controlFlowGraph = new ControlFlowGraph(methodGen);	

		return CONTROL_FLOW_GRAPH_BUILDER.build(controlFlowGraph);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BasicBlockNode process(Class clazz, String methodName, Class<?>... parameterTypes) {
		try {
			return this.process(clazz, clazz.getMethod(methodName, parameterTypes));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	@SuppressWarnings({ "rawtypes"})
	private org.apache.bcel.classfile.Method getMethodFromClass(Class clazz, Method method) {
		try {
			File f = new File("");
			//TODO generalizar
			String elementClassPath = f.getCanonicalPath() + "/bin/" + clazz.getCanonicalName().replace(".", "/") + ".class";			
			
			Assert.assertFalse(elementClassPath.isEmpty());

			ClassParser classParser = new ClassParser(elementClassPath);
			JavaClass javaClass = classParser.parse();

			org.apache.bcel.classfile.Method methodBcel = javaClass.getMethod(method);
			return methodBcel;
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

}
