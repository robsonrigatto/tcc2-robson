package gui;

import graph.model.ControlFlowGraphEdge;
import graph.model.ControlFlowGraphEdgeType;
import graph.model.ControlFlowGraphNode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import model.IElement;
import model.SysAdvice;
import model.SysAspect;
import model.SysClass;
import model.SysElement;
import model.SysException;
import model.SysField;
import model.SysMethod;
import model.SysPackage;
import model.SysPointcut;

import org.apache.commons.collections15.Transformer;

public class SysTransformers {
	
	protected static float[] dash_slashes = {10.0f};
	protected static float[] dependency_slashes = {20.0f};

	//edge visual
	private static Transformer<Object, Stroke> edgeStrokeTransformer = new Transformer<Object, Stroke>(){
		public Stroke transform(Object arg0) {
			if(arg0 instanceof Float) {
				Float num = (Float) arg0;
				if(num % 1 == 0.5)  {
					return new  BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dependency_slashes, 100.0f);
				}			
			}
			return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dash_slashes, 100.0f);
		}
	};

	//edge label
	private static Transformer<Object, String> edgeToString = new Transformer<Object, String>(){
		public String transform(Object arg0){
			if(arg0 instanceof Float) {
				Float num = (Float) arg0;
				if(num % 1 == 0.5) {
					return new Integer((int)(num/1)).toString();
				}
			} else if(arg0 instanceof ControlFlowGraphEdge) {
				ControlFlowGraphEdge edge = (ControlFlowGraphEdge) arg0;
				ControlFlowGraphEdgeType edgeType = edge.getEdgeType();
				return edgeType == null || ControlFlowGraphEdgeType.REFERENCE.equals(edgeType) ? 
						ControlFlowGraphEdgeType.GOTO.name() : edgeType.toString();
			}
			return "";
		}
	};

	//vertex visual
	private static Transformer<IElement, Paint> vertexPaint = new Transformer<IElement, Paint>(){
		public Paint transform(IElement arg0) {
			if(arg0 instanceof SysAdvice)
				return Color.WHITE;
			if(arg0 instanceof SysPackage)
				return Color.RED;
			if(arg0 instanceof SysAspect)
				return Color.MAGENTA;
			if(arg0 instanceof SysException)
				return Color.ORANGE;
			if(arg0 instanceof SysClass)
				return Color.GREEN;
			if(arg0 instanceof SysMethod)
				return Color.CYAN;
			if(arg0 instanceof SysField)
				return Color.GRAY;
			
			if(arg0 instanceof ControlFlowGraphNode) {
				ControlFlowGraphNode node = (ControlFlowGraphNode) arg0;
				return node.isReference() ? Color.GREEN : node.isTryStatement() ? Color.BLUE : Color.ORANGE;
			}
			
			return Color.BLUE;
		}
	};
	
	//displaying labels
	private static Transformer<IElement, String> vertexToString = new Transformer<IElement, String>(){
		public String transform(IElement arg0){
			if(arg0 instanceof SysElement) {
				if(arg0 instanceof SysAdvice){
					return ((SysAdvice)arg0).prettyName();
				}
				if(arg0 instanceof SysPointcut){
					return ((SysPointcut)arg0).prettyName();
				}
				return ((SysElement)arg0).getName();
			}
			return arg0.toString();
		}
	};

	//displaying tooltip
	private static Transformer<IElement, String> toolTip = new Transformer<IElement, String>(){
		public String transform(IElement e){
			return e.toString();
		}
	};
	
	public Transformer<IElement, String> getToolTip() {
		return toolTip;
	}

	public Transformer<IElement, Paint> getVertexPaint() {
		return vertexPaint;
	}


	public Transformer<IElement, String> getVertexToString() {
		return vertexToString;
	}
	
	public final Transformer<Object, String> getEdgeToString() {
		return edgeToString;
	}

	public final Transformer<Object, Stroke> getEdgeStrokeTransformer() {
		return edgeStrokeTransformer;
	}

	public final float[] getDash() {
		return dash_slashes;
	}
}
