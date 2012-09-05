/**
 * @ CallChainWindow.java
 * date 02/01/2012    <>      mm/dd/yyyy
 * 
 */
package gui;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import analysis.MethodAnalysis;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import visualization.CallChainM2G;

import model.SysRoot;
import model.Element;
import model.SysMethod;

/**
 * @author Felipe Capodifoglio Zanichelli
 *
 */
public class CallChainWindow extends JFrame implements GUIWindowInterface{

	private SysRoot root;
	private SysMethod m;
	private Container center = this.getContentPane();
	private JTextArea textArea = new JTextArea();

	/**@param m the SysMethod that will start the call chain graph
	 * @param root the SysRoot in which the SysMethod is child*/
	public CallChainWindow(SysMethod m, SysRoot root) {
		super("CallChain >>> "+m);
		this.m=m;
		this.root=root;
		CallChainM2G cc = new CallChainM2G();
		VisualizationViewer<Element,Float> vv;
		AggregateLayout<Element, Float> al = cc.doAggregateLayout(root,m); 
		vv = new CallChainM2G().makeVV(al);
		this.add(vv);
		SysGraph4AJUtils.makeGoodVisual(vv, this);
		makeMenuBar(vv);
		vv.setSize(this.getSize());
		this.pack();
		SysGraph4AJUtils.setAtCenter(m, al, this, vv);

	}


	/**
	 * Open a new CallChainWindow analysing the given method recursively
	 * @param m the SysMethod that will start the call chain graph
	 * @param root the SysRoot in which the SysMethod is a child of
	 * */
	public CallChainWindow(SysMethod m, SysRoot root, boolean b) {
		super("CallChain >>> "+m);
		this.m=m;
		this.root=root;
		VisualizationViewer<Element,Float> vv;
		if(b){
			recur(root,m);
		}
		CallChainM2G cc = new CallChainM2G();
		AggregateLayout<Element, Float> al = cc.doAggregateLayout(root,m); 
		vv = new CallChainM2G().makeVV(al);
		this.add(vv);
		SysGraph4AJUtils.makeGoodVisual(vv, this);
		makeMenuBar(vv);
		this.pack();
		SysGraph4AJUtils.setAtCenter(m, al, this, vv);
	}



	/**analyse the method {@param m2} recursively */
	private void recur(SysRoot root2, SysMethod m2) {
		if(m2.isAnalysed() && !m.equals(m2)) return;
		MethodAnalysis.analyseMethod(m2, root);
		for(SysMethod m : m2.getCalls()){
			recur(root2,m);
		}

	}

	
	/**gets the center panel*/
	public Container getCenter() {
		return this.center;
	}

	
	/**set a center panel*/
	public void setCenter(Container c) {
		this.remove(this.center);
		this.setContentPane(c);
		this.center = c;
	}

	
	/**return the textArea*/
	public JTextArea getTextArea() {
		return this.textArea ;
	}

	
	/**sets a center panel*/
	public void setCenterPanel(Container pane) {
		this.setCenter(pane);
	}

	
	public void makeGoodVisual(VisualizationViewer<Element, Float> vv) {
		SysGraph4AJUtils.makeGoodVisual(vv, this);
	}

	
	/**makes a menu bar given a visualization viewer*/
	public void makeMenuBar(VisualizationViewer<Element, Float> vv) {
		SysGraph4AJUtils.makeMenuBar(vv, this, this.root);

	}

	
	/**@return boolean whether the right click is enabled*/
	public boolean rightClickEnabled() {
		return true;
	}


	/**
	 * @return the SysMethod object of this class
	 */
	public SysMethod getM() {
		return m;
	}



	
	public JFrame getFrame() {
		return this;
	}


}
