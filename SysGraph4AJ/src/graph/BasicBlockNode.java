package graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.generic.Instruction;

public class BasicBlockNode {
	
	private List<Instruction> instructions;
	
	private int positionLimit;
	
	private String description;
	
	private boolean isVisited;
	
	private List<BasicBlockNode> childrenBlocks;
	
	public BasicBlockNode() {
		this.instructions = new ArrayList<Instruction>();
		this.description = new String();
		this.isVisited = false;
		this.childrenBlocks = new ArrayList<BasicBlockNode>();
	}
	
	public void addInstruction(Instruction instruction) {
		this.instructions.add(instruction);
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public void appendDescription(String description) {
		this.description += description.trim() + "\n";
	}

	/**
	 * adiciona um nó filho no grafo.
	 */
	public void addChildBlock(BasicBlockNode childNode) {
		this.childrenBlocks.add(childNode);
	}

	public List<BasicBlockNode> getChildrenBlocks() {
		return childrenBlocks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPositionLimit() {
		return positionLimit;
	}

	public void setPositionLimit(Integer positionLimit) {
		this.positionLimit = positionLimit;
	}
	
	@Override
	public String toString() {
		return this.description.trim();
	}
}
