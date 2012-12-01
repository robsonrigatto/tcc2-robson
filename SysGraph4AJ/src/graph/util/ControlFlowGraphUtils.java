package graph.util;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import graph.ControlFlowGraphNode;
import graph.ControlFlowGraphTransformers;
import gui.ControlFlowGraphMouse;
import gui.GUIWindowInterface;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class ControlFlowGraphUtils {
	
	private static final ControlFlowGraphTransformers TRANSFORMERS = new ControlFlowGraphTransformers();

	public static void makeGoodVisual(VisualizationViewer<ControlFlowGraphNode, Float>  vv, GUIWindowInterface target){
		RenderContext<ControlFlowGraphNode, Float> rc = vv.getRenderContext();
		rc.setVertexFillPaintTransformer(TRANSFORMERS.getVertexPaint());		//vertex color
		rc.setEdgeStrokeTransformer(TRANSFORMERS.getEdgeStrokeTransformer());	//edge type
		rc.setVertexLabelTransformer(TRANSFORMERS.getVertexToString());		//vertex label
		rc.setEdgeLabelTransformer(TRANSFORMERS.getEdgeToString());			//edge label
		vv.setVertexToolTipTransformer(TRANSFORMERS.getToolTip());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.AUTO);
	}
	
	public static void setAtCenter(ControlFlowGraphNode vertex, AggregateLayout<ControlFlowGraphNode, Float> al, JFrame frame, VisualizationViewer<ControlFlowGraphNode, Float> vv){
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
	
	public static void makeMenuBar(VisualizationViewer<ControlFlowGraphNode, Float>  vv, GUIWindowInterface target, ControlFlowGraphNode r){
		ControlFlowGraphMouse gm = new ControlFlowGraphMouse();
		vv.setGraphMouse(gm);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = gm.getModeMenu();
		menu.setText("|Mouse Mode|");
		menu.setIcon(null);
		menu.setPreferredSize(new Dimension(90,20));
		menuBar.add(menu);
		target.setJMenuBar(menuBar);
	}
}
