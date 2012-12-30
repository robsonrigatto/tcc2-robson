package gui;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.ControlFlowGraphBuilder;
import graph.model.ControlFlowGraphEdge;
import graph.model.ControlFlowGraphEdgeType;
import graph.model.ControlFlowGraphNode;

import java.awt.Container;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import model.IElement;
import model.SysMethod;

import org.apache.bcel.generic.InstructionHandle;

public class ControlFlowGraphWindow extends JFrame implements GUIWindowInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final ControlFlowGraphBuilder CONTROL_FLOW_GRAPH_BUILDER = new ControlFlowGraphBuilder();


	private Container center = this.getContentPane();
	private JTextArea textArea = new JTextArea();

	public ControlFlowGraphWindow(SysMethod m) {

		super("Grafo de Fluxo de Controle >>> " + m);

		ControlFlowGraphNode root = CONTROL_FLOW_GRAPH_BUILDER.build(m.getMethod());

		DelegateForest<IElement, Object> df = new DelegateForest<IElement, Object>();
		DelegateTree<IElement, Object> delegateTree = new DelegateTree<IElement, Object>();

		this.addChildToGraph(root, delegateTree);
		
		df.addTree(delegateTree);
		
		VisualizationViewer<IElement, Object> visualizationViewer = new VisualizationViewer<IElement, Object>(
				new TreeLayout<IElement, Object>(
						df, 100, 80));
		
		SysUtils.makeGoodVisual(visualizationViewer, this);
		visualizationViewer.setSize(this.getSize());
		AggregateLayout<IElement, Object> al = new AggregateLayout<IElement, Object>(new TreeLayout<IElement, Object>(df, 100, 80));
		SysUtils.setAtCenter(root, al, this, visualizationViewer);
		SysUtils.makeMenuBar(visualizationViewer, this, root);

		this.addReferenceEdges(root, df);
		
		this.add(visualizationViewer);
		this.pack();
	}

	private void addChildToGraph(ControlFlowGraphNode root, DelegateTree<IElement, Object> delegateTree) {
		if(delegateTree.getVertexCount() == 0) {
			delegateTree.addVertex(root);
		}

		Set<ControlFlowGraphNode> childNodes = root.getChildNodes(); 
		for(ControlFlowGraphNode childNode : childNodes) {
	
			if(!childNode.isReference() && !delegateTree.containsVertex(childNode)) {
				ControlFlowGraphEdge edge = new ControlFlowGraphEdge(root, childNode, ControlFlowGraphEdgeType.DEFAULT);
				delegateTree.addChild(edge, root, childNode);
				this.addChildToGraph(childNode, delegateTree);

			}
		}
	}

	private void addReferenceEdges(final ControlFlowGraphNode root, DelegateForest<IElement, Object> delegateForest) {
		Set<ControlFlowGraphNode> childNodes = root.getChildNodes(); 

		for(ControlFlowGraphNode childNode : childNodes) {

			if(childNode.isReference() && root.getOwner() != null) {
				ControlFlowGraphNode parentNode = (ControlFlowGraphNode) root.getOwner();
				
				ControlFlowGraphNode referencedNode = null;
				boolean referenceFound = false;
				InstructionHandle instructionToBeReferenced = childNode.getInstructions().get(0);
				
				do {
					for(ControlFlowGraphNode childNodesToCheck : parentNode.getChildNodes()) {
						
						if(childNodesToCheck.getInstructions().contains(instructionToBeReferenced)) {
							referenceFound = true;
							referencedNode = childNodesToCheck;
							break;
						}
					}		
					
					if(!referenceFound) {
						parentNode = (ControlFlowGraphNode) parentNode.getOwner();
					}
					
				} while(parentNode != null && !referenceFound);
				if(referenceFound) {
					ControlFlowGraphNode parentNodeFromChild = (ControlFlowGraphNode) childNode.getOwner();
					ControlFlowGraphEdge edge = new ControlFlowGraphEdge(parentNodeFromChild, referencedNode, ControlFlowGraphEdgeType.DEFAULT);
					delegateForest.addEdge(edge, parentNodeFromChild, referencedNode);
				}
			} else {
				this.addReferenceEdges(childNode, delegateForest);
			}
			
		}
	}

	@Override
	public Container getCenter() {
		return this.center;
	}

	@Override
	public void setCenter(Container c) {
		this.center = c;
	}

	@Override
	public JTextArea getTextArea() {
		return this.textArea;
	}

	@Override
	public void setCenterPanel(Container pane) {
		this.setCenter(pane);
	}

	@Override
	public boolean rightClickEnabled() {
		return false;
	}

	@Override
	public JFrame getFrame() {
		return this;
	}

	@Override
	public void makeGoodVisual(VisualizationViewer<IElement, Object> vv) {
		// TODO Auto-generated method stub

	}

	@Override
	public void makeMenuBar(VisualizationViewer<IElement, Object> vv) {
		// TODO Auto-generated method stub

	}
}
