package visualization;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.IElement;
import model.SysClass;
import model.SysElement;
import model.SysMethod;
import model.SysPackage;
import model.SysRoot;
import analysis.ClassAnalysis2;
import analysis.MethodAnalysis;
import analysis.SysAnalysis;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import gui.CallChainWindow;
import gui.ControlFlowGraphWindow;
import gui.GUIWindowInterface;
import gui.MainWindow;
import gui.SysUtils;

public class DoubleClickGraphMouse<V,E> extends DefaultModalGraphMouse<V,E> {

	private static final int CALLCAIN_INDICATOR = 1;
	private static final int MAINWINDOW_INDICATOR = 2;
	private long lastTimeClicked=0l;
	private long doubleClickTime = 400l;
	private IElement root;
	private GUIWindowInterface windowInterface;
	private int deltaX = 100;
	private int deltaY = 80;


	public DoubleClickGraphMouse(GUIWindowInterface f, IElement r){
		this.root = r;
		this.windowInterface = f;
	}

	/**
	 * right click action
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private void rightClick(MouseEvent e) {
		final VisualizationViewer<V,E> visualizationViewer = (VisualizationViewer<V,E>)e.getSource();
		Point2D p = e.getPoint();
		GraphElementAccessor<V,E> pickSupport = visualizationViewer.getPickSupport();
		if(pickSupport != null) {
			final Layout l = visualizationViewer.getModel().getGraphLayout();
			SysElement vertex = (SysElement)pickSupport.getVertex(l, p.getX(), p.getY());
			JPopupMenu popup = new JPopupMenu();
			final SysElement el = vertex;
			if(vertex instanceof SysMethod){
				final SysMethod m = (SysMethod)vertex;
				popup.add(new AbstractAction("View Call Chain"){
					public void actionPerformed(ActionEvent arg0) {
						CallChainWindow w = new CallChainWindow(m, (SysRoot) root);
						w.setVisible(true);
					}
				});
				popup.add(new AbstractAction("View Call Chain analysing methods recursively"){

					public void actionPerformed(ActionEvent arg0) {
						CallChainWindow w2 = new CallChainWindow(m,(SysRoot) root,true);
						w2.setVisible(true);
					}
				});
				popup.add(this.getViewControlFlowGraphScreen(el));
			}
			if(vertex!=null){
				popup.add(this.getViewPropertiesScreen(el));
			}
			popup.show(visualizationViewer, e.getX(), e.getY());
		}
	}

	@SuppressWarnings("serial")
	private AbstractAction getViewPropertiesScreen(final SysElement el) {
		return new AbstractAction("View Properties"){
			public void actionPerformed(ActionEvent arg0) {
				JFrame info = new JFrame(el.getFullyQualifiedName());
				JTextArea area = new JTextArea(el.viewState());
				JScrollPane scroll = new JScrollPane(area);
				info.add(scroll);
				area.setEditable(false);
				info.pack();
				info.setVisible(true);
			}
		};
	}

	@SuppressWarnings("serial")
	private AbstractAction getViewControlFlowGraphScreen(final SysElement el) {
		return new AbstractAction("View Graph Flow Control"){
			public void actionPerformed(ActionEvent arg0) {
				ControlFlowGraphWindow w = new ControlFlowGraphWindow((SysMethod) el);
				w.setVisible(true);
			}
		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void mouseClicked(MouseEvent e) {
		long clickedNow = System.currentTimeMillis();
		if(e.getButton()==MouseEvent.BUTTON3 && windowInterface.rightClickEnabled()) {
			this.rightClick(e);
			return;
		} 

		if(clickedNow - this.lastTimeClicked <= this.doubleClickTime) {
			this.lastTimeClicked = clickedNow;
			VisualizationViewer<V,E> visualizationViewer = (VisualizationViewer<V,E>) e.getSource();
			Point2D p = e.getPoint();
			GraphElementAccessor<V,E> pickSupport = visualizationViewer.getPickSupport();
			if(pickSupport != null) {
				SysElement vertex = (SysElement)pickSupport.getVertex(visualizationViewer.getModel().getGraphLayout(), p.getX(), p.getY());
				boolean isWorkingOnVisualizationViewer = false;

				if(vertex instanceof SysPackage && !((SysPackage) vertex).isAnalysed()) {
					String fullPath = File.separatorChar + ((SysPackage)vertex).getFullyQualifiedName();
					fullPath = fullPath.replace(".", File.separator);
					fullPath = ((SysRoot)this.root).getPath()+fullPath;
					if(fullPath.contains(File.separator + File.separator)) {
						JOptionPane.showMessageDialog(null, "filePath has two File.separator.");
					}	
					SysAnalysis.analysePackage((SysPackage)vertex, fullPath, true);
					((SysPackage)vertex).setIsAnalysed(true);
					isWorkingOnVisualizationViewer = true;
				} else if(vertex instanceof SysClass && !((SysClass) vertex).isAnalysed()){
					SysClass c = ((SysClass)vertex);
					c = ClassAnalysis2.analyseClass(c, (SysRoot) this.root);
					c.setIsAnalysed(true);
					isWorkingOnVisualizationViewer = true;
				} else {
					if(vertex instanceof SysMethod && !((SysMethod)vertex).isAnalysed()){
						SysMethod m = ((SysMethod)vertex);
						MethodAnalysis.analyseMethod(m, (SysRoot) this.root);
						m.setIsAnalysed(true);
						isWorkingOnVisualizationViewer = true;
					}
				}
				if(isWorkingOnVisualizationViewer) {
					Component c = this.windowInterface.getContentPane();
					int indicator = MAINWINDOW_INDICATOR;
					while(!(c instanceof JFrame)){
						c = c.getParent();
						if(c instanceof CallChainWindow){
							indicator = CALLCAIN_INDICATOR;
							break;
						} else if (c instanceof MainWindow){
							indicator = MAINWINDOW_INDICATOR;
							break;
						}
					}
					if(indicator == CALLCAIN_INDICATOR) {
						CallChainM2G cc = new CallChainM2G();
						AggregateLayout al = cc.doAggregateLayout((SysRoot) root,((CallChainWindow)c).getM());
						VisualizationViewer<IElement, Object> vv_callchain = cc.makeVV(al);
						this.windowInterface.setCenterPanel(vv_callchain);
						this.windowInterface.makeGoodVisual(vv_callchain);
					} else {
						DelegateTree<IElement, Object> delegateTree = new  DelegateTree<IElement, Object>();
						delegateTree.addVertex(this.root);
						delegateTree = ModelToGraph.putAllChildren_SysRoot(delegateTree, (SysRoot) this.root);
						DelegateForest<IElement, Object> delegateForest = ModelToGraph.tree_to_forest(delegateTree);
						AggregateLayout aggregateLayout = new AggregateLayout(new TreeLayout<IElement, Object>(delegateForest, this.deltaX, this.deltaY));
						VisualizationViewer anotherVisualizationViewer = new VisualizationViewer<SysElement, Float>(aggregateLayout);
						this.windowInterface.setCenterPanel(anotherVisualizationViewer);
						((VisualizationViewer) this.windowInterface.getCenter()).updateUI();
						this.windowInterface.makeGoodVisual((VisualizationViewer<IElement, Object>) this.windowInterface.getCenter());
						this.windowInterface.makeMenuBar((VisualizationViewer<IElement, Object>) this.windowInterface.getCenter());
						EspecialEdgesTable<IElement, Object> et = ModelToGraph.getEspecialEdges((SysRoot) this.root, delegateForest);
						ModelToGraph.addEspecialEdges(delegateForest, et);
						((VisualizationViewer) this.windowInterface.getCenter()).updateUI();
						this.windowInterface.getTextArea().append("Analysing: "+vertex.toString()+"\n");
						//centering the vertex
						SysUtils.setAtCenter(vertex,
								aggregateLayout, 
								this.windowInterface.getFrame(), 
								((VisualizationViewer) this.windowInterface.getCenter()));
					}
				}
			}
		}
		else {
			this.lastTimeClicked = clickedNow;
			super.mouseClicked(e);
		}
	}

}
