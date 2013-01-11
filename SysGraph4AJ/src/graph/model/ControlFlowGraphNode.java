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
	
	private Boolean tryStatement;
	
	private Boolean isReference;
	
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
		if(childNode != null) {
			this.childNodes.put(childNode, edgeType);
			childNode.setOwner(this);
		}
	}

	public Map<ControlFlowGraphNode, ControlFlowGraphEdgeType> getChildNodes() {
		return childNodes;
	}
	
	public void setChildNodes(Map<ControlFlowGraphNode, ControlFlowGraphEdgeType> childNodes) {
		this.childNodes = childNodes;
		
		for(ControlFlowGraphNode childNode : this.childNodes.keySet()) {
			childNode.setOwner(this);
		}
	}

	public Boolean isTryStatement() {
		return tryStatement;
	}

	public void setTryStatement(Boolean tryStatement) {
		this.tryStatement = tryStatement;
	}

	public Boolean isReference() {
		return isReference;
	}

	public void setReference(Boolean isReference) {
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
		return this.instructions.size() == 0 || this.tryStatement ? "" : 
			new StringBuffer("[").append(this.instructions.get(0).getPosition()).append("]").toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instructions == null) ? 0 : instructions.hashCode());
		result = prime * result
				+ ((tryStatement == null) ? 0 : tryStatement.hashCode());
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
		if (tryStatement == null) {
			if (other.tryStatement != null)
				return false;
		} else if (!tryStatement.equals(other.tryStatement))
			return false;
		return true;
	}

	@Override
	public void addChild(IElement e) {
		
	}

	@Override
	public Set<? extends IElement> getChildElements() {
		return this.getChildNodes().keySet();
	}
	
	public ControlFlowGraphEdgeType getChildTypeByNode(ControlFlowGraphNode childNode) {
		return this.childNodes.get(childNode);
	}
}
