/**
 * @ Transformers.java
 * date 02/25/2012    <>      mm/dd/yyyy
 * 
 */
package gui;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

/**
 * @author Felipe Capodifoglio Zanichelli
 *
 */

public abstract class Transformers <T> {

	protected static float[] dash_slashes = {10.0f};
	protected static float[] dependency_slashes = {20.0f};

	//edge visual
	private static Transformer<Float, Stroke> edgeStrokeTransformer = new Transformer<Float, Stroke>(){
		public Stroke transform(Float arg0) {
			if(arg0 % 1 == 0.5) return new  BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dependency_slashes, 100.0f);
			return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dash_slashes, 100.0f);
		}
	};

	private static Transformer<Float, String> edgeToString = new Transformer<Float, String>(){
		public String transform(Float arg0){
			if(arg0 % 1 == 0.5) {
				return new Integer((int)(arg0/1)).toString();
			}
			return "";
		}
	};

	public abstract Transformer<T, Paint> getVertexPaint();

	/**
	 * @return the vertexToString
	 */
	public abstract Transformer<T, String> getVertexToString();

	/**
	 * @return the toolTip
	 */
	public abstract Transformer<T, String> getToolTip();

	/**
	 * @return the edgeToString
	 */
	public final Transformer<Float, String> getEdgeToString() {
		return edgeToString;
	}

	/**
	 * @return the edgeStrokeTransformer
	 */
	public final Transformer<Float, Stroke> getEdgeStrokeTransformer() {
		return edgeStrokeTransformer;
	}

	/**
	 * @return the dash
	 */
	public final float[] getDash() {
		return dash_slashes;
	}
}
