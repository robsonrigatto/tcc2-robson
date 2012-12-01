package graph;

import gui.Transformers;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

public class ControlFlowGraphTransformers extends Transformers<ControlFlowGraphNode>{
	
	//vertex visual
	private static Transformer<ControlFlowGraphNode, Paint> vertexPaint = new Transformer<ControlFlowGraphNode, Paint>(){
		public Paint transform(ControlFlowGraphNode arg0) {
			return arg0.isReference() ? Color.BLUE : Color.GREEN;
		}
	};
	
	//displaying labels
	private static Transformer<ControlFlowGraphNode, String> vertexToString = new Transformer<ControlFlowGraphNode, String>(){
		public String transform(ControlFlowGraphNode arg0){
			return arg0.toString();
		}
	};

	//displaying tooltip
	private static Transformer<ControlFlowGraphNode, String> toolTip = new Transformer<ControlFlowGraphNode, String>(){
		public String transform(ControlFlowGraphNode e){
			return e.toString();
		}
	};

	@Override
	public Transformer<ControlFlowGraphNode, Paint> getVertexPaint() {
		return vertexPaint;
	}

	@Override
	public Transformer<ControlFlowGraphNode, String> getVertexToString() {
		return vertexToString;
	}

	@Override
	public Transformer<ControlFlowGraphNode, String> getToolTip() {
		return toolTip;
	}

}
