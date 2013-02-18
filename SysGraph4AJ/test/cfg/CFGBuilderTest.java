package cfg;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.Assert;

import org.apache.bcel.generic.InstructionHandle;
import org.junit.Test;

import cfg.model.CFGNode;
import cfg.processing.CFGBuilder;
import cfg.util.CFGClassForTestUtils;

/**
 * Classe de teste do construtor de grafo de fluxo de controle {@link CFGBuilder}
 * 
 * @author robson
 *
 */
public class CFGBuilderTest {

	private static final CFGBuilder CONTROL_FLOW_GRAPH_BUILDER = new CFGBuilder();

	@Test
	public void ifElseTest() throws NoSuchMethodException, IOException {
		
		CFGNode tree = CONTROL_FLOW_GRAPH_BUILDER.build(CFGClassForTestUtils.class, "ifElseMethod", Integer.TYPE);
		
		Assert.assertEquals(5, tree.getInstructions().size());
		
		List<CFGNode> childrenBlocks = new ArrayList<CFGNode>(tree.getChildNodes().keySet());
		Assert.assertEquals(2, childrenBlocks.size());
		
		
	}
	
	@Test
	public void forTest() throws NoSuchMethodException, IOException {
		
		CFGNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(CFGClassForTestUtils.class, "forMethod", String.class);
		
		List<InstructionHandle> instructionsFromRoot = node0.getInstructions();
		Assert.assertEquals(2, instructionsFromRoot.size());
		
		List<CFGNode> childrenBlocks = new ArrayList<CFGNode>(node0.getChildNodes().keySet());
		Assert.assertEquals(1, childrenBlocks.size());
		
		CFGNode node1 = childrenBlocks.get(0);
		List<CFGNode> childNodesFromNode1 = new ArrayList<CFGNode>(node1.getChildNodes().keySet());
		Collections.sort(childNodesFromNode1, this.getComparatorToSortChildList());
		Assert.assertEquals(2, childNodesFromNode1.size());
		
		CFGNode node3 = childNodesFromNode1.get(0);
		Assert.assertEquals(1, node3.getChildNodes().size());
		
		CFGNode node4 = childNodesFromNode1.get(1);
		Assert.assertEquals(2, node4.getChildNodes().size());
	}

	@Test
	public void switchTest() {
		CFGNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(CFGClassForTestUtils.class, "switchMethod");
		
		Assert.assertNotNull(node0);
		
		List<CFGNode> childNodesFromNode0AsList = new ArrayList<CFGNode>(node0.getChildNodes().keySet());
		Collections.sort(childNodesFromNode0AsList, this.getComparatorToSortChildList());
		
		CFGNode node1 = childNodesFromNode0AsList.get(0);
		CFGNode node2 = childNodesFromNode0AsList.get(1);
		
		Assert.assertEquals(1, node1.getChildNodes().size());
		
		for(CFGNode childNodeFromNode1 : node2.getChildNodes().keySet()) {
			Assert.assertEquals(0, childNodeFromNode1.getChildNodes().size());
		}
		
		Assert.assertEquals(8, node2.getChildNodes().size());		
	}
	
	@Test
	public void tryCatchTest() {
		CFGNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(CFGClassForTestUtils.class, "tryCatchMethod");
		
		Assert.assertTrue(node0.isTryStatement());
		
		List<CFGNode> childrenBlocks = new ArrayList<CFGNode>(node0.getChildNodes().keySet());
		
		CFGNode node1 = childrenBlocks.get(0);
		Assert.assertFalse(node1.isTryStatement());
		
		List<CFGNode> childNodesFromNode1 = new ArrayList<CFGNode>(node1.getChildNodes().keySet());
		Assert.assertEquals(1, childNodesFromNode1.size());
		CFGNode node3 = childNodesFromNode1.get(0);
		Assert.assertTrue(node3.isTryStatement());
		
		Assert.assertEquals(1, node3.getChildNodes().size());
		
		CFGNode node2 = childrenBlocks.get(1);
		
		Assert.assertFalse(node2.isTryStatement());
	}
	
	@Test
	public void oneInstructionTest() {
		CFGNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(CFGClassForTestUtils.class, "oneInstructionMethod");
		Assert.assertEquals(0, node0.getChildElements().size());
		
		List<InstructionHandle> instructions = node0.getInstructions();
		Assert.assertTrue(instructions.size() > 0);
	}
	
	public Comparator<CFGNode> getComparatorToSortChildList() {
		return new Comparator<CFGNode>() {

			@Override
			public int compare(CFGNode o1, CFGNode o2) {
				int size1 = o1.getChildNodes().size();
				int size2 = o2.getChildNodes().size();
				if(size1 == size2) {
					return 0;
				} 
				if(size1 < size2) {
					return -1;
				}
				return 1;
			}
			
		};
	}
}
