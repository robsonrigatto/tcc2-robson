package graph.model;

import graph.gui.CFGEdge;

/**
 * Representa o tipo relacionado a um nó pai com um nó filho.
 * 
 * @author robson
 *
 * @see CFGNode
 * @see CFGEdge
 */
public enum CFGEdgeType {
	
	TRY,
	
	CATCH,
	
	FINALLY,
	
	IF,
	
	ELSE,
	
	GOTO,
	
	CASE,
	
	DEFAULT,
	
	REFERENCE

}
