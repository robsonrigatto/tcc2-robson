/**
 * @ Transformers.java
 * date 02/25/2012    <>      mm/dd/yyyy
 * 
 */
package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import model.Element;
import model.SysAdvice;
import model.SysAspect;
import model.SysClass;
import model.SysException;
import model.SysField;
import model.SysMethod;
import model.SysPackage;
import model.SysPointcut;

import org.apache.commons.collections15.Transformer;

/**
 * @author Felipe Capodifoglio Zanichelli
 *
 */

public class Transformers {

	private static float[] dash_slashes = {10.0f};
	private static float[] dependency_slashes = {20.0f};


	//edge visual
	private static Transformer<Float, Stroke> edgeStrokeTransformer = new Transformer<Float, Stroke>(){
		public Stroke transform(Float arg0) {
			if(arg0 % 1 == 0.5) return new  BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dependency_slashes, 100.0f);
			return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dash_slashes, 100.0f);
		}
	};
	
	//vertex visual
	private static Transformer<Element, Paint> vertexPaint = new Transformer<Element, Paint>(){
		public Paint transform(Element arg0) {
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
			return Color.BLUE;
		}
	};
	
	//displaying labels
	private static Transformer<Element, String> vertexToString = new Transformer<Element, String>(){
		public String transform(Element arg0){
			if(arg0 instanceof SysAdvice){
				return ((SysAdvice)arg0).prettyName();
			}
			if(arg0 instanceof SysPointcut){
				return ((SysPointcut)arg0).prettyName();
			}
			return arg0.getName();
		}
	};

	private static Transformer<Float, String> edgeToString = new Transformer<Float, String>(){
		public String transform(Float arg0){
			//JOptionPane.showMessageDialog(null, arg0);
			if(arg0%1==0.5) return new Integer((int)(arg0/1)).toString();
			return "";
		}
	};

	//displaying tooltip
	private static Transformer<Element, String> toolTip = new Transformer<Element, String>(){
		public String transform(Element e){
			return e.toString();
		}
	};

	/**
	 * @return the dash
	 */
	public static float[] getDash() {
		return dash_slashes;
	}

	/**
	 * @return the edgeStrokeTransformer
	 */
	public static Transformer<Float, Stroke> getEdgeStrokeTransformer() {
		return edgeStrokeTransformer;
	}

	/**
	 * @return the vertexPaint
	 */
	public static Transformer<Element, Paint> getVertexPaint() {
		return vertexPaint;
	}

	/**
	 * @return the vertexToString
	 */
	public static Transformer<Element, String> getVertexToString() {
		return vertexToString;
	}

	/**
	 * @return the edgeToString
	 */
	public static Transformer<Float, String> getEdgeToString() {
		return edgeToString;
	}

	/**
	 * @return the toolTip
	 */
	public static Transformer<Element, String> getToolTip() {
		return toolTip;
	}

}
