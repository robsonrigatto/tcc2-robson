package graph.gui;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.model.CFGEdge;
import graph.model.CFGNode;
import graph.processing.CFGBuilder;
import gui.GUIWindowInterface;
import gui.SysUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import model.IElement;
import model.SysMethod;

import org.apache.bcel.generic.InstructionHandle;

/**
 * Classe responsável pelas operações que relacionam um {@link CFGNode} e alguma interface gráfica.
 *  
 * @author robson
 *
 */
public class CFGUIHelper {
	
	public static final CFGBuilder CFG_BUILDER = new CFGBuilder();
	
	/**
	 * @param root
	 * 		nó raiz que está sendo renderizado na {@link GUIWindowInterface}
	 * @param method
	 * 		nó filho do tipo {@link SysMethod} que contém o método que será utilzado na construção da CFG
	 * @param windowInterface
	 * 		interface gráfica que será atualizada com as informações do CFG
	 */
	@SuppressWarnings("unchecked")
	public static void addCFGToWindowInterface(IElement root, SysMethod method, GUIWindowInterface windowInterface) {
		VisualizationViewer<IElement, Object> visualizationViewer = (VisualizationViewer<IElement, Object>) windowInterface.getCenter();
		Layout<IElement, Object> graphLayout = visualizationViewer.getGraphLayout();
		DelegateForest<IElement, Object> delegateForest = (DelegateForest<IElement, Object>) graphLayout.getGraph();
		removeVertexFromCFG(delegateForest, CFGUIHelper.CFG_BUILDER.getCurrentCFGNode());
		DelegateTree<IElement, Object> delegateTree = (DelegateTree<IElement, Object>) delegateForest.getTrees().toArray()[0];
		
		CFGNode cfg = CFGUIHelper.CFG_BUILDER.build(method.getMethod());	
		delegateForest = new DelegateForest<IElement, Object>();
		delegateTree.addChild(UUID.randomUUID(), method, cfg);
		CFGUIHelper.addNodeAndItsChildrenToTree(cfg, delegateTree);
		
		delegateForest.addTree(delegateTree);
		
		AggregateLayout<IElement, Object> aggregateLayout = new AggregateLayout<IElement, Object>(new TreeLayout<IElement, Object>(delegateForest, 100, 100));
		VisualizationViewer<IElement, Object> newVisualizationViewer = new VisualizationViewer<IElement, Object>(aggregateLayout);
		windowInterface.setCenterPanel(newVisualizationViewer);
		
		SysUtils.makeGoodVisual(newVisualizationViewer, windowInterface);
		SysUtils.makeMenuBar(newVisualizationViewer, windowInterface, root);
		SysUtils.setAtCenter(CFGUIHelper.CFG_BUILDER.getCurrentCFGNode(), aggregateLayout, windowInterface.getFrame(), newVisualizationViewer);
		
		CFGUIHelper.addReferenceEdgesToForest(cfg, delegateForest);
	}
	
	/**
	 * 
	 * @param delegateForest
	 * 		floresta que será atualizada sem o nó parametrizado
	 * @param node
	 * 		nó do tipo {@link CFGNode} que será removido da árvore
	 */
	private static void removeVertexFromCFG(DelegateForest<IElement, Object> delegateForest, CFGNode node) {
		
		if(node == null || delegateForest == null) {
			return;
		}
		
		Collection<Object> incidentEdges = delegateForest.getIncidentEdges(node);
		if(incidentEdges != null) {
			for(Object edge : incidentEdges) {
				delegateForest.removeEdge(edge, false);
			}
		}
		delegateForest.removeVertex(node);
		
		for(CFGNode childNode : node.getChildNodes().keySet()) {
			removeVertexFromCFG(delegateForest, childNode);
		}
	}

	/**
	 * 
	 * @param root
	 * 		nó que será adicionado na árvore
	 * @param delegateTree
	 * 		árvore que será atualizada com o nó e seus respectivos filhos
	 */
	@SuppressWarnings("unchecked")
	public static void addNodeAndItsChildrenToTree(CFGNode root, DelegateTree<IElement, Object> delegateTree) {
		if(delegateTree.getVertexCount() == 0) {
			delegateTree.addVertex(root);
		}

		Set<CFGNode> childNodes = (Set<CFGNode>) root.getChildElements(); 
		for(CFGNode childNode : childNodes) {
			
			if(!childNode.isReference() && !delegateTree.containsVertex(childNode)) {
				CFGEdge edge = new CFGEdge(root, childNode, root.getChildTypeByNode(childNode));
				delegateTree.addChild(edge, root, childNode);
				addNodeAndItsChildrenToTree(childNode, delegateTree);

			}
		}
	}

	/**
	 * 
	 * @param root
	 * 		nó raiz
	 * @param delegateForest
	 * 		floresta que será atualizada com as arestas remanescentes
	 */
	@SuppressWarnings("unchecked")
	public static void addReferenceEdgesToForest(final CFGNode root, DelegateForest<IElement, Object> delegateForest) {
		Iterator<CFGNode> childNodes = (Iterator<CFGNode>) root.getChildElements().iterator(); 

		while(childNodes.hasNext()) {

			CFGNode childNode = childNodes.next();
			
			if(childNode.isReference() && root.getOwner() != null) {
				CFGNode parentNode = (CFGNode) root.getOwner();
				
				CFGNode referencedNode = null;
				boolean referenceFound = false;
				InstructionHandle instructionToBeReferenced = childNode.getInstructions().get(0);
				
				do {
					for(CFGNode childNodesToCheck : parentNode.getChildNodes().keySet()) {
						
						if(!childNodesToCheck.isReference() 
						&& !childNodesToCheck.getInstructions().isEmpty() 
						&& childNodesToCheck.getInstructions().get(0).equals(instructionToBeReferenced)
						&& !childNodesToCheck.isTryStatement()) {
							referenceFound = true;
							referencedNode = childNodesToCheck;
							break;
						}
					}		
					
					if(!referenceFound) {
						parentNode = (CFGNode) parentNode.getOwner();
					}
					
				} while(parentNode != null && !referenceFound);
				
				if(referenceFound) {
					CFGNode parentNodeFromChild = (CFGNode) childNode.getOwner();
					CFGEdge edge = new CFGEdge(parentNodeFromChild, referencedNode, parentNodeFromChild.getChildTypeByNode(childNode));
					
					if(!parentNodeFromChild.equals(referencedNode)) {				
						delegateForest.addEdge(edge, parentNodeFromChild, referencedNode);
					
					} else {
						delegateForest.addEdge(edge, parentNodeFromChild, parentNodeFromChild);
					}
				}
			} else {
				addReferenceEdgesToForest(childNode, delegateForest);
			}
			
		}
	}
}
