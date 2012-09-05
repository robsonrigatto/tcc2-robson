/**
 * @ Utils.java
 * date 02/25/2012    <>      mm/dd/yyyy
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;

import model.Element;
import model.SysRoot;
import visualization.DoubleClickGraphMouse;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/**
 * @author Felipe Capodifoglio Zanichelli
 *
 */

public class SysGraph4AJUtils {
	
	/**
	 * Sets the given container and puts it in the center of the GUIWindow*/
	public static void setCenter(Container center, GUIWindowInterface target) {
		target.setCenter(center);
	}
	
	/**@return the text area of the GUIWindow...it may be a hidden text area....*/
	public static JTextArea getTextArea(GUIWindowInterface target){
		return target.getTextArea();
	}

	/**
	 * Same thing as setCenter but this time it makes a good visual for the Visualization Viewer*/
	public static void setCenterPanel(Container pane, GUIWindowInterface target){
		Container c = target.getContentPane();
		c.remove(target.getCenter()); 
		c.add(pane, BorderLayout.CENTER);
		target.setCenter(pane);
		if(pane instanceof VisualizationViewer){
			VisualizationViewer<Element, Float> vv = (VisualizationViewer<Element,Float>) pane;
			target.makeGoodVisual(vv);
			vv.updateUI();
		}
	}
	
	/**
	 * Makes a good visual, i.e., set transformers to put color in the graph, set names, and tooltip*/
	public static void makeGoodVisual(VisualizationViewer<Element, Float>  vv, GUIWindowInterface target){
		RenderContext<Element, Float> rc = vv.getRenderContext();
		rc.setVertexFillPaintTransformer(Transformers.getVertexPaint());		//vertex color
		rc.setEdgeStrokeTransformer(Transformers.getEdgeStrokeTransformer());	//edge type
		rc.setVertexLabelTransformer(Transformers.getVertexToString());		//vertex label
		rc.setEdgeLabelTransformer(Transformers.getEdgeToString());			//edge label
		vv.setVertexToolTipTransformer(Transformers.getToolTip());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.AUTO);
		/*care about the mouse plugin*/
		target.makeMenuBar(vv);
	}
	
	/**
	 * makes a menu bar for the GUI, based on the VV*/
	public static void makeMenuBar(VisualizationViewer<Element, Float>  vv, GUIWindowInterface target, SysRoot r){
		DoubleClickGraphMouse<Element, Float> gm = new DoubleClickGraphMouse<Element, Float>(target,r);
		vv.setGraphMouse(gm);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = gm.getModeMenu();
		menu.setText("|Mouse Mode|");
		menu.setIcon(null);
		menu.setPreferredSize(new Dimension(90,20));
		menuBar.add(menu);
		target.setJMenuBar(menuBar);
	}
	
	/**center the vertex in screen*/
	public static void setAtCenter(Element vertex, AggregateLayout<Element, Float> al, JFrame frame, VisualizationViewer<Element, Float> vv){
		Point2D vertexPoint = al.transform(vertex);
		Rectangle frame_area = frame.getBounds();
		double calculatedDeltaY = 120.0d;
		Point2D ctr = new Point(
				(int)(frame_area.getX()+frame_area.getWidth()/2), 
				(int) (frame_area.getY()+calculatedDeltaY )); //create a logical center point X_0+width/2, Y_0+120. 
		//Note: X_0 and Y_0 are both 0, i.e., they are the left top pixel
		double deltaX = (ctr.getX() - vertexPoint.getX());
		double deltaY = (ctr.getY() - vertexPoint.getY());
		MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
		layout.translate(deltaX, deltaY);
		
	}
	

}
