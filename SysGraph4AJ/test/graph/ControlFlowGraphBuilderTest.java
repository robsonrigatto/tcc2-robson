package graph;

import graph.model.ControlFlowGraphNode;
import graph.util.ControlFlowGraphClassForTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.bcel.generic.InstructionHandle;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Classe de teste do construtor de grafo de fluxo de controle {@link ControlFlowGraphBuilder}
 * 
 * @author robson
 *
 */
public class ControlFlowGraphBuilderTest {
	
	private static final ControlFlowGraphBuilder CONTROL_FLOW_GRAPH_BUILDER = new ControlFlowGraphBuilder();

	@Test
	public void ifElseTest() throws NoSuchMethodException, IOException {
		
		ControlFlowGraphNode tree = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "ifElseMethod", Integer.TYPE);
		
		Assert.assertEquals(5, tree.getInstructions().size());
		
		List<ControlFlowGraphNode> childrenBlocks = new ArrayList<ControlFlowGraphNode>(tree.getChildNodes().keySet());
		Assert.assertEquals(2, childrenBlocks.size());
		
		
	}
	
	@Test
	public void forTest() throws NoSuchMethodException, IOException {
		
		ControlFlowGraphNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "forMethod", String.class);
		
		List<InstructionHandle> instructionsFromRoot = node0.getInstructions();
		Assert.assertEquals(2, instructionsFromRoot.size());
		
		List<ControlFlowGraphNode> childrenBlocks = new ArrayList<ControlFlowGraphNode>(node0.getChildNodes().keySet());
		Assert.assertEquals(1, childrenBlocks.size());
		
		ControlFlowGraphNode node1 = childrenBlocks.get(0);
		List<ControlFlowGraphNode> childNodesFromNode1 = new ArrayList<ControlFlowGraphNode>(node1.getChildNodes().keySet());
		Assert.assertEquals(2, childNodesFromNode1.size());
		
		ControlFlowGraphNode node3 = childNodesFromNode1.get(0);
		Assert.assertEquals(2, node3.getChildNodes().size());
		
		ControlFlowGraphNode node4 = childNodesFromNode1.get(1);
		Assert.assertEquals(1, node4.getChildNodes().size());
	}

	@Test
	public void switchTest() {
		ControlFlowGraphNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "switchMethod");
		
		Assert.assertNotNull(node0);
		
		List<ControlFlowGraphNode> childNodesFromNode0 = new ArrayList<ControlFlowGraphNode>(node0.getChildNodes().keySet());
		ControlFlowGraphNode node1 = childNodesFromNode0.get(0);
		ControlFlowGraphNode node2 = childNodesFromNode0.get(1);
		
		Assert.assertEquals(8, node1.getChildNodes().size());
		
		for(ControlFlowGraphNode childNodeFromNode1 : node1.getChildNodes().keySet()) {
			Assert.assertEquals(0, childNodeFromNode1.getChildNodes().size());
		}
		
		Assert.assertEquals(1, node2.getChildNodes().size());		
	}

	@Test
	@Ignore
	public void aspectTest() {		
		ControlFlowGraphNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "aspectMethod");
		
		Assert.assertNotNull(node0);
		
		List<ControlFlowGraphNode> childrenBlocks = new ArrayList<ControlFlowGraphNode>(node0.getChildNodes().keySet());
		//Assert.assertEquals(1, node0.getAspectInstructions().size());
		Assert.assertEquals(2, childrenBlocks.size());
		
		ControlFlowGraphNode node1 = childrenBlocks.get(0);
		Assert.assertEquals(0, node1.getChildNodes().size());
		
		ControlFlowGraphNode node2 = childrenBlocks.get(1);
		Assert.assertEquals(1, node2.getChildNodes().size());
		//Assert.assertEquals(1, node2.getAspectInstructions().size());
	}
	
	@Test
	public void tryCatchTest() {
		ControlFlowGraphNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "tryCatchMethod");
		
		Assert.assertTrue(node0.isTryStatement());
		
		List<ControlFlowGraphNode> childrenBlocks = new ArrayList<ControlFlowGraphNode>(node0.getChildNodes().keySet());
		
		ControlFlowGraphNode node1 = childrenBlocks.get(0);
		Assert.assertFalse(node1.isTryStatement());
		
		List<ControlFlowGraphNode> childNodesFromNode1 = new ArrayList<ControlFlowGraphNode>(node1.getChildNodes().keySet());
		Assert.assertEquals(1, childNodesFromNode1.size());
		ControlFlowGraphNode node3 = childNodesFromNode1.get(0);
		Assert.assertTrue(node3.isTryStatement());
		
		Assert.assertEquals(1, node3.getChildNodes().size());
		
		ControlFlowGraphNode node2 = childrenBlocks.get(1);
		
		Assert.assertFalse(node2.isTryStatement());
	}
}
