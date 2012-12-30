package graph.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.IElement;

import org.apache.bcel.generic.InstructionHandle;

public class ControlFlowGraphNode implements IElement {
	
	private List<InstructionHandle> instructions;
	
	private Map<ControlFlowGraphNode, ControlFlowGraphEdgeType> childNodes;
	
	private boolean tryStatement;
	
	private boolean isReference;
	
	private ControlFlowGraphNode parentNode;
	
	public ControlFlowGraphNode() {
		this.instructions = new ArrayList<InstructionHandle>();
		this.childNodes = new HashMap<ControlFlowGraphNode, ControlFlowGraphEdgeType>();
		this.tryStatement = false;
		this.isReference = false;
		this.parentNode = null;
	}
	
	public void addInstruction(InstructionHandle instruction) {
		this.instructions.add(instruction);
	}

	public List<InstructionHandle> getInstructions() {
		return instructions;
	}

	/**
	 * adiciona um nó filho no grafo.
	 */
	public void addChildNode(ControlFlowGraphNode childNode, ControlFlowGraphEdgeType edgeType) {
		this.childNodes.put(childNode, edgeType);
		childNode.setOwner(this);
	}

	public Set<ControlFlowGraphNode> getChildNodes() {
		return childNodes.keySet();
	}
	
	public boolean isTryStatement() {
		return tryStatement;
	}

	public void setTryStatement(boolean tryStatement) {
		this.tryStatement = tryStatement;
	}

	public boolean isReference() {
		return isReference;
	}

	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}

	public IElement getOwner() {
		return parentNode;
	}

	public void setOwner(IElement parentNode) {
		this.parentNode = (ControlFlowGraphNode) parentNode;
	}

	@Override
	public String toString() {		
//		if(this.instructions.size() == 0) {
//			return "";
//		}
//		
//		StringBuffer sb = new StringBuffer("[");
//		for(InstructionHandle instruction : this.instructions) {
//			sb.append(instruction.getPosition()).append(", ");
//		}
//		
//		String str = sb.toString();
//		
//		return str.substring(0, str.length() - 2).concat("]");
		
		return this.instructions.size() == 0 ? "" : 
			new StringBuffer("[").append(this.instructions.get(0).getPosition()).append("]").toString();
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((instructions == null || instructions.size() == 0) ? 0 : instructions.get(0).hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ControlFlowGraphNode other = (ControlFlowGraphNode) obj;
//		if (instructions == null) {
//			if (other.instructions != null)
//				return false;
//		} else if (instructions.size() == 0 && other.instructions.size() == 0) {
//			return true;
//		} else if(!(instructions.size() > 0 && other.instructions.size() > 0)) 
//			return false;
//		
//		InstructionHandle instructionHandle = instructions.get(0);
//		InstructionHandle otherInstructionHandle = other.instructions.get(0);
//		
//		return instructionHandle.getPosition() == otherInstructionHandle.getPosition();
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instructions == null) ? 0 : instructions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControlFlowGraphNode other = (ControlFlowGraphNode) obj;
		if (instructions == null) {
			if (other.instructions != null)
				return false;
		} else if (!instructions.equals(other.instructions))
			return false;
		return true;
	}

	@Override
	public void addChild(IElement e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<IElement> getChildElements() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ControlFlowGraphEdgeType getChildTypeByNode(ControlFlowGraphNode childNode) {
		return this.childNodes.get(childNode);
	}
}
