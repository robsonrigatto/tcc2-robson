package gui;

import java.awt.Color;
import java.awt.Paint;

import model.SysElement;
import model.SysAdvice;
import model.SysAspect;
import model.SysClass;
import model.SysException;
import model.SysField;
import model.SysMethod;
import model.SysPackage;
import model.SysPointcut;

import org.apache.commons.collections15.Transformer;

public class SysTransformers extends Transformers<SysElement> {

	//vertex visual
	private static Transformer<SysElement, Paint> vertexPaint = new Transformer<SysElement, Paint>(){
		public Paint transform(SysElement arg0) {
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
	private static Transformer<SysElement, String> vertexToString = new Transformer<SysElement, String>(){
		public String transform(SysElement arg0){
			if(arg0 instanceof SysAdvice){
				return ((SysAdvice)arg0).prettyName();
			}
			if(arg0 instanceof SysPointcut){
				return ((SysPointcut)arg0).prettyName();
			}
			return arg0.getName();
		}
	};

	//displaying tooltip
	private static Transformer<SysElement, String> toolTip = new Transformer<SysElement, String>(){
		public String transform(SysElement e){
			return e.toString();
		}
	};
	
	@Override
	public Transformer<SysElement, String> getToolTip() {
		return toolTip;
	}

	@Override
	public Transformer<SysElement, Paint> getVertexPaint() {
		return vertexPaint;
	}


	@Override
	public Transformer<SysElement, String> getVertexToString() {
		return vertexToString;
	}
}
