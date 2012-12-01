package gui;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.ControlFlowGraphBuilder;
import graph.ControlFlowGraphNode;
import graph.util.ControlFlowGraphUtils;

import java.awt.Container;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.bcel.generic.InstructionHandle;

import model.SysElement;
import model.SysMethod;

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

		DelegateForest<ControlFlowGraphNode, Float> df = new DelegateForest<ControlFlowGraphNode, Float>();
		DelegateTree<ControlFlowGraphNode, Float> delegateTree = new  DelegateTree<ControlFlowGraphNode, Float>();

		this.addChildToGraph(root, delegateTree);
		
		df.addTree(delegateTree);
		
		VisualizationViewer<ControlFlowGraphNode, Float> visualizationViewer = new VisualizationViewer<ControlFlowGraphNode, Float>(
				new TreeLayout<ControlFlowGraphNode, Float>(
						df, 100, 80));
		
		ControlFlowGraphUtils.makeGoodVisual(visualizationViewer, this);
		visualizationViewer.setSize(this.getSize());
		AggregateLayout<ControlFlowGraphNode, Float> al = new AggregateLayout<ControlFlowGraphNode, Float>(new TreeLayout<ControlFlowGraphNode, Float>(df, 100, 80));
		ControlFlowGraphUtils.setAtCenter(root, al, this, visualizationViewer);
		ControlFlowGraphUtils.makeMenuBar(visualizationViewer, this, root);

		this.addReferenceEdges(root, df);
		
		this.add(visualizationViewer);
		this.pack();
	}

	private void addChildToGraph(ControlFlowGraphNode root, DelegateTree<ControlFlowGraphNode, Float> delegateTree) {
		Random r = new Random();

		if(delegateTree.getVertexCount() == 0) {
			delegateTree.addVertex(root);
		}

		List<ControlFlowGraphNode> childNodes = root.getChildNodes(); 
		for(ControlFlowGraphNode childNode : childNodes) {

			if(!childNode.isReference() && !delegateTree.containsVertex(childNode)) {

				delegateTree.addChild((float) r.nextDouble(), root, childNode);
				this.addChildToGraph(childNode, delegateTree);

			}
		}
	}

	private void addReferenceEdges(final ControlFlowGraphNode root, DelegateForest<ControlFlowGraphNode, Float> delegateForest) {
		Random r = new Random();

		List<ControlFlowGraphNode> childNodes = root.getChildNodes(); 
		for(ControlFlowGraphNode childNode : childNodes) {

			if(childNode.isReference()) {
				ControlFlowGraphNode parentNode = root.getParentNode();
				
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
						parentNode = parentNode.getParentNode();
					}
					
				} while(parentNode != null && !referenceFound);
				if(referenceFound) {
					delegateForest.addEdge((float) r.nextDouble(), childNode.getParentNode(), referencedNode);
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
	public void makeGoodVisual(VisualizationViewer<SysElement, Float> vv) {
		// TODO Auto-generated method stub

	}

	@Override
	public void makeMenuBar(VisualizationViewer<SysElement, Float> vv) {
		// TODO Auto-generated method stub

	}
}
