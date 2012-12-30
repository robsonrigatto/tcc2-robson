package graph.model;

public class ControlFlowGraphEdge {

	private ControlFlowGraphNode parentNode;
	
	private ControlFlowGraphNode childNode;
	
	private ControlFlowGraphEdgeType edgeType;

	public ControlFlowGraphEdge(ControlFlowGraphNode parentNode, ControlFlowGraphNode childNode, ControlFlowGraphEdgeType edgeType) {
		this.parentNode = parentNode;
		this.childNode = childNode;
		this.edgeType = edgeType;
	}

	public ControlFlowGraphNode getParentNode() {
		return parentNode;
	}

	public ControlFlowGraphNode getChildNode() {
		return childNode;
	}

	public ControlFlowGraphEdgeType getEdgeType() {
		return edgeType;
	}

}
