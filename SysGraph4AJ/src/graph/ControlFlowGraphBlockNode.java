package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;

public class ControlFlowGraphBlockNode {
	
	private List<Instruction> instructions;
	
	private String description;
	
	private List<ControlFlowGraphBlockNode> childrenBlocks;
	
	private Set<INVOKEVIRTUAL> aspectInstructions;
	
	private boolean tryStatement;
	
	public ControlFlowGraphBlockNode() {
		this.instructions = new ArrayList<Instruction>();
		this.description = new String();
		this.childrenBlocks = new ArrayList<ControlFlowGraphBlockNode>();
		this.aspectInstructions = new HashSet<>();
		this.tryStatement = false;
	}
	
	public void addInstruction(Instruction instruction) {
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

	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public void appendDescription(String description) {
		this.description += description.trim() + "\n";
	}

	/**
	 * adiciona um nó filho no grafo.
	 */
	public void addChildBlock(ControlFlowGraphBlockNode childNode) {
		this.childrenBlocks.add(childNode);
	}

	public List<ControlFlowGraphBlockNode> getChildrenBlocks() {
		return childrenBlocks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isTryStatement() {
		return tryStatement;
	}

	public void setTryStatement(boolean tryStatement) {
		this.tryStatement = tryStatement;
	}

	@Override
	public String toString() {
		return this.description.trim();
	}
}
