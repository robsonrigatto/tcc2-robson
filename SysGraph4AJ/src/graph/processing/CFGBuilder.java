package graph.processing;



import graph.model.CFGNode;

import java.lang.reflect.Method;


import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

/**
 * Classe responsável pela construção de um grafo de fluxo de controle através da estrutura contida em
 * {@link CFGNode} através de um método representado por seu nome e seus parâmetros ou
 * também pelo tipo {@link Method}. 
 * 
 * @author robson
 *
 */
public class CFGBuilder {

	private static final CFGProcessor CONTROL_FLOW_GRAPH_PROCESSOR = new CFGProcessor();
	
	private CFGNode currentCFGNode;
	
	private org.apache.bcel.classfile.Method currentAnalysedMethod;

	/**
	 * Constrói um grafo de fluxo de controle a partir de um {@link Method} passado por parâmetro.
	 * 
	 * @param method
	 * 		método a ser referenciadona construção do grafo
	 * 
	 * @return instância de {@link CFGNode} com o grafo de fluxo de controle
	 */
	public CFGNode build(Method method) {		
		try {
			Class<?> declaringClass = method.getDeclaringClass();
			JavaClass javaClass = Repository.lookupClass(declaringClass);
			this.currentAnalysedMethod = javaClass.getMethod(method);
			MethodGen methodGen = new MethodGen(this.currentAnalysedMethod, declaringClass.getCanonicalName(), new ConstantPoolGen(this.currentAnalysedMethod.getConstantPool()));

			this.currentCFGNode = CONTROL_FLOW_GRAPH_PROCESSOR.process(methodGen);
			return this.getCurrentCFGNode();	
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Constrói um grafo de fluxo de controle a partir do nome e a lista de tipos dos parâmetros deste.
	 * 
	 * @param clazz
	 * 		classe do método em que o método pertence
	 * @param methodName
	 * 		nome do método a ser buscado
	 * @param parameterTypes
	 * 		lista dos tipos dos parâmetros do método buscado
	 * 
	 * @return instância de {@link CFGNode} com o grafo de fluxo de controle
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CFGNode build(Class clazz, String methodName, Class<?>... parameterTypes) {
		try {
			return this.build(clazz.getMethod(methodName, parameterTypes));
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public CFGNode getCurrentCFGNode() {
		return currentCFGNode;
	}

	public org.apache.bcel.classfile.Method getCurrentAnalysedMethod() {
		return currentAnalysedMethod;
	}
}
