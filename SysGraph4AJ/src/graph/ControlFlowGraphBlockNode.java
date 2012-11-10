package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;

public class ControlFlowGraphBlockNode {
	
	private List<Instruction> instructions;
	
	private List<ControlFlowGraphBlockNode> childBlocks;
	
	private Set<INVOKEVIRTUAL> aspectInstructions;
	
	private boolean tryStatement;
	
	public ControlFlowGraphBlockNode() {
		this.instructions = new ArrayList<Instruction>();
		this.childBlocks = new ArrayList<ControlFlowGraphBlockNode>();
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

	/**
	 * adiciona um nó filho no grafo.
	 */
	public void addChildBlock(ControlFlowGraphBlockNode childNode) {
		this.childBlocks.add(childNode);
	}

	public List<ControlFlowGraphBlockNode> getChildNodes() {
		return childBlocks;
	}
	
	public boolean isTryStatement() {
		return tryStatement;
	}

	public void setTryStatement(boolean tryStatement) {
		this.tryStatement = tryStatement;
	}

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		
		for(Instruction instruction : this.instructions) {
			sb.append(instruction.toString().trim()).append("\n");
		}
		
		return sb.toString().trim();
	}
}
