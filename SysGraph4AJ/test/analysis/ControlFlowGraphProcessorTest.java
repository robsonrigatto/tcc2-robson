package analysis;

import graph.BasicBlockNode;
import graph.ControlFlowGraphProcessor;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import model.SysAdvice;
import model.SysField;

import org.apache.bcel.generic.Instruction;
import org.junit.Test;

public class ControlFlowGraphProcessorTest {
	
	private static final ControlFlowGraphProcessor CONTROL_FLOW_GRAPH_PROCESSOR = new ControlFlowGraphProcessor();

	@Test
	public void ifElseTest() throws NoSuchMethodException, IOException {
		
		BasicBlockNode tree = CONTROL_FLOW_GRAPH_PROCESSOR.process(SysField.class, "getFullyQualifiedName");
		
		Assert.assertEquals(3, tree.getInstructions().size());
		
		List<BasicBlockNode> childrenBlocks = tree.getChildrenBlocks();
		Assert.assertEquals(2, childrenBlocks.size());
		
		
	}
	
	@Test
	public void forTest() throws NoSuchMethodException, IOException {
		
		BasicBlockNode tree = CONTROL_FLOW_GRAPH_PROCESSOR.process(SysAdvice.class, "makeAdvice");
		
		List<Instruction> instructionsFromRoot = tree.getInstructions();
		Assert.assertEquals(7, instructionsFromRoot.size());
		
		List<BasicBlockNode> childrenBlocks = tree.getChildrenBlocks();
		Assert.assertEquals(2, childrenBlocks.size());
		
		BasicBlockNode firstChild = childrenBlocks.get(0);
		Assert.assertEquals(2, firstChild.getChildrenBlocks().size());
		
		Assert.assertEquals(1, firstChild.getChildrenBlocks().get(0).getChildrenBlocks().size());
		
		Assert.assertEquals(1, firstChild.getChildrenBlocks().get(1).getChildrenBlocks().size());
		
		BasicBlockNode secondChild = childrenBlocks.get(1);
		Assert.assertEquals(0, secondChild.getChildrenBlocks().size());
	}

	@Test
	public void switchTest() {
		BasicBlockNode tree = CONTROL_FLOW_GRAPH_PROCESSOR.process(SysAdvice.class, "getAdviceType");
		
		Assert.assertNotNull(tree);
		
		//TODO
	}

	@Test
	public void aspectTest() {
		//TODO
		Assert.fail();
	}
	
	@Test
	public void tryCatchTest() {
		//TODO 
		Assert.fail();
	}
}
