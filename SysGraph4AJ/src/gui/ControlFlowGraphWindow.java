package gui;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import model.Element;
import model.SysMethod;
import model.SysRoot;
import visualization.ModelToGraph;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class ControlFlowGraphWindow extends JFrame implements
GUIWindowInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SysRoot root;
	private SysMethod m;
	private Container center = this.getContentPane();
	private JTextArea textArea = new JTextArea();

	public ControlFlowGraphWindow(SysMethod m, SysRoot root) {
		super("Grafo de Fluxo de Controle >>> " + m);
		//TODO mostrar grafo de fluxo		
		this.m = m;
		this.root=root;
		DelegateTree<Element, Float> delegateTree = new  DelegateTree<Element, Float>();
		delegateTree.addVertex(root);
		delegateTree = ModelToGraph.putAllChildren_SysRoot(delegateTree, root);
		DelegateForest<Element, Float> delegateForest = ModelToGraph.tree_to_forest(delegateTree);
		AggregateLayout aggregateLayout = new AggregateLayout(new TreeLayout<Element, Float>(delegateForest, 100, 80));
		VisualizationViewer<Element, Float> visualizationViewer = SysGraph4AJUtils.createVisualizationViewerBySysRoot(root, 100, 100);
		this.add(visualizationViewer);
		SysGraph4AJUtils.makeGoodVisual(visualizationViewer, this);
		makeMenuBar(visualizationViewer);
		visualizationViewer.setSize(this.getSize());
		SysGraph4AJUtils.setAtCenter(root,
				aggregateLayout, 
				this, visualizationViewer);
		this.pack();
		//SysGraph4AJUtils.setAtCenter(m, al, this, visualizationViewer);

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
	public void makeGoodVisual(VisualizationViewer<Element, Float> vv) {
		SysGraph4AJUtils.makeGoodVisual(vv, this);
	}

	@Override
	public void makeMenuBar(VisualizationViewer<Element, Float> vv) {
		SysGraph4AJUtils.makeMenuBar(vv, this, this.root);
	}

	@Override
	public boolean rightClickEnabled() {
		return true;
	}

	@Override
	public JFrame getFrame() {
		return this;
	}

}
