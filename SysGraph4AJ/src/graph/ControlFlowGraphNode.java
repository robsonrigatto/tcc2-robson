package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;

public class ControlFlowGraphNode {
	
	private List<InstructionHandle> instructions;
	
	private List<ControlFlowGraphNode> childNodes;
	
	private Set<INVOKEVIRTUAL> aspectInstructions;
	
	private boolean tryStatement;
	
	private boolean isReference;
	
	private ControlFlowGraphNode parentNode;
	
	public ControlFlowGraphNode() {
		this.instructions = new ArrayList<InstructionHandle>();
		this.childNodes = new ArrayList<ControlFlowGraphNode>();
		this.aspectInstructions = new HashSet<>();
		this.tryStatement = false;
		this.isReference = false;
		this.parentNode = null;
	}
	
	public void addInstruction(InstructionHandle instruction) {
		this.instructions.add(instruction);
	}
	
	public void addAspectInstruction(Instruction instruction) {
		if(instruction instanceof INVOKEVIRTUAL) {
			this.aspectInstructions.add((INVOKEVIRTUAL) instruction);
		}
	}
	
	public Set<INVOKEVIRTUAL> getAspectInstructions() {
		return aspectInstructions;
	}

	public List<InstructionHandle> getInstructions() {
		return instructions;
	}

	/**
	 * adiciona um nó filho no grafo.
	 */
	public void addChildNode(ControlFlowGraphNode childNode) {
		this.childNodes.add(childNode);
		childNode.setParentNode(this);
	}

	public List<ControlFlowGraphNode> getChildNodes() {
		return childNodes;
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

	public ControlFlowGraphNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(ControlFlowGraphNode parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder("[");
		
		for(InstructionHandle instruction : this.instructions) {
			sb.append(instruction.getPosition()).append(",");
		}
		String str = sb.toString();
		return new StringBuffer(str.substring(0, str.length() - 1)).append("]").toString();
	}

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
}
