package graph;


import graph.model.ControlFlowGraphEdgeType;
import graph.model.ControlFlowGraphNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.TABLESWITCH;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;

/**
 * Classe responsável pelo processamento de uma instância de {@link ControlFlowGraphNode}
 * a partir de um método representado pela classe {@link MethodGen}
 * 
 * @author robson
 * 
 * @see ControlFlowGraphBuilder
 *
 */
public class ControlFlowGraphProcessor {

	/**
	 * 
	 * @param methodGen
	 * 		{@link ControlFlowGraph} representado de um método
	 * @return 
	 * 		{@link ControlFlowGraphNode} criado com toda a hierarquia de instruções
	 * 		que representam o grafo de fluxo de controle
	 */
	public ControlFlowGraphNode process(MethodGen methodGen) {
		InstructionHandle instruction = methodGen.getInstructionList().getStart();
		return this.processInstruction(instruction);

	}

	/**
	 * @see ControlFlowGraphProcessor#processInstruction(InstructionHandle, ControlFlowGraphNode, List)
	 * 
	 * @param instruction
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @return 
	 * 		Nó raiz com todas as instruções armazenadas em seu grafo
	 */
	private ControlFlowGraphNode processInstruction(InstructionHandle instruction) {
		return this.processInstruction(instruction, null, new HashSet<Integer>());
	}


	/**
	 * Processa uma instrução e retorna o respectivo grafo representado pela classe
	 * {@link ControlFlowGraphNode}.
	 *  
	 * @param instructionHandle
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param root
	 * 		{@link ControlFlowGraphNode} já criado pelo builder, pode ser nulo 	
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * 
	 * @return 
	 * 		Nó raiz com todas as instruções armazenadas em seu grafo
	 */ 
	private ControlFlowGraphNode processInstruction(InstructionHandle instructionHandle, ControlFlowGraphNode root, Set<Integer> processedInstructionIds) {
		
		boolean ifInstructionConditional, switchInstructionConditional, returnInstructionConditional, 
		notNullConditional, instructionHasNotProcessedConditional;
		
		ControlFlowGraphNode blockNode = new ControlFlowGraphNode();

		if(root == null || root.getInstructions().size() == 0) {
			root = blockNode;
		}
		
		do {
			CodeExceptionGen exceptionBlock = this.getExceptionBlock(instructionHandle, processedInstructionIds);
			if(exceptionBlock != null && !processedInstructionIds.contains(exceptionBlock.getStartPC().getPosition())) {
				processedInstructionIds.add(instructionHandle.getPosition());
				
				blockNode.setTryStatement(true);
				
				InstructionHandle tryInstruction = exceptionBlock.getStartPC();
				
				ControlFlowGraphNode tryBlock = this.processInstruction(tryInstruction, root, processedInstructionIds);				
				blockNode.addChildNode(tryBlock, ControlFlowGraphEdgeType.TRY);
				
				InstructionHandle catchInstruction = exceptionBlock.getHandlerPC();
				ControlFlowGraphNode catchBlock = this.processInstruction(catchInstruction, root, processedInstructionIds);
				
				this.addNodeToAllChildNodesFromRoot(tryBlock, catchBlock);
				
				InstructionHandle finallyInstruction = exceptionBlock.getEndPC();
				ControlFlowGraphNode finallyBlock = this.processInstruction(finallyInstruction, root, processedInstructionIds);
				blockNode.addChildNode(finallyBlock, ControlFlowGraphEdgeType.FINALLY);
				
				instructionHandle = finallyInstruction;
				
			} else {
				processedInstructionIds.add(instructionHandle.getPosition());				
				blockNode.addInstruction(instructionHandle);
				instructionHandle = instructionHandle instanceof BranchHandle ? ((BranchHandle) instructionHandle).getTarget() : instructionHandle.getNext();
			}
			
			notNullConditional = instructionHandle != null;
			instructionHasNotProcessedConditional = (notNullConditional && !processedInstructionIds.contains(instructionHandle.getPosition()));
			
			ifInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof IfInstruction);
			switchInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof Select);
			returnInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof ReturnInstruction);
		} while(notNullConditional && returnInstructionConditional && ifInstructionConditional && instructionHasNotProcessedConditional && switchInstructionConditional);

		if(notNullConditional) {
			
			if(!instructionHasNotProcessedConditional && !blockNode.isTryStatement()) {
				ControlFlowGraphNode childNode = new ControlFlowGraphNode();
				blockNode.addChildNode(childNode, ControlFlowGraphEdgeType.REFERENCE);
				childNode.addInstruction(instructionHandle);
				childNode.setReference(true);
				
			} else if(!ifInstructionConditional) {
				this.processIfInstruction(instructionHandle, root, processedInstructionIds, blockNode);				
			
			} else if(!returnInstructionConditional) {
				blockNode.addInstruction(instructionHandle);
				
			} else if(!switchInstructionConditional) {
				this.processSwitchInstruction(instructionHandle, root, processedInstructionIds, blockNode);
			}
		}
		return blockNode;
	}

	/**
	 * 
	 * @param instructionHandle
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param root
	 * 		{@link ControlFlowGraphNode} já criado pelo builder
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * @param blockNode
	 * 		{@link ControlFlowGraphNode} processado no momento
	 * 		
	 */
	private void processIfInstruction(InstructionHandle instructionHandle, ControlFlowGraphNode root, Set<Integer> processedInstructionIds, ControlFlowGraphNode blockNode) {
		
		blockNode.addInstruction(instructionHandle);
		BranchHandle branchHandle = (BranchHandle) instructionHandle;
		InstructionHandle ifTrueNextInstruction = branchHandle.getTarget();	

		boolean hasInstructionProcessed = processedInstructionIds.contains(ifTrueNextInstruction.getPosition());
		if(!hasInstructionProcessed) {
			blockNode.addChildNode(this.processInstruction(ifTrueNextInstruction, root, processedInstructionIds), 
				ControlFlowGraphEdgeType.IF);
		}
		blockNode.addChildNode(this.processInstruction(instructionHandle.getNext(), root, processedInstructionIds), 
				ControlFlowGraphEdgeType.ELSE);
	}
	
	/**
	 * 
	 * @param instructionHandle
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param root
	 * 		{@link ControlFlowGraphNode} já criado pelo builder
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * @param blockNode
	 * 		{@link ControlFlowGraphNode} processado no momento
	 * 		
	 */
	private void processSwitchInstruction(InstructionHandle instructionHandle, ControlFlowGraphNode root, Set<Integer> processedInstructionIds, ControlFlowGraphNode blockNode) {
		
		TABLESWITCH switchInstruction = (TABLESWITCH) instructionHandle.getInstruction();
		blockNode.addInstruction(instructionHandle);
		InstructionHandle[] caseInstructions = switchInstruction.getTargets();
		for(InstructionHandle caseInstruction : caseInstructions) {
			blockNode.addChildNode(this.processInstruction(caseInstruction, root, processedInstructionIds), 
				ControlFlowGraphEdgeType.CASE);
		}
		InstructionHandle defaultCaseInstruction = switchInstruction.getTarget();
		blockNode.addChildNode(this.processInstruction(defaultCaseInstruction, root, processedInstructionIds), 
				ControlFlowGraphEdgeType.DEFAULT);
	}
	
	/**
	 * Obtém uma exceção a ser tratada em um block try/catch
	 * 
	 * @param instructionHandle 
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param processedInstructionIds 
	 * 		Lista de instruções já processadas
	 * @return 
	 * 		Instância de {@link CodeExceptionGen} com a respectiva exceçao, mas pode ser um valor nulo
	 */
	private CodeExceptionGen getExceptionBlock(InstructionHandle instructionHandle, Set<Integer> processedInstructionIds) {
		InstructionTargeter[] targeters = instructionHandle.getTargeters();
		if(targeters != null) {
			for(InstructionTargeter targeter : targeters) {
				if(targeter instanceof CodeExceptionGen) {
					CodeExceptionGen exceptionBlock = (CodeExceptionGen) targeter;
					if(!processedInstructionIds.contains(exceptionBlock.getStartPC().getPosition())) {
						return exceptionBlock;	
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Adiciona um bloco como filho ao nó raiz {@code root} e seus respectivos filhos.
	 * 
	 * @param root
	 * 		bloco raiz que irá referenciar o block catch
	 * @param targetBlock
	 * 		block catch que será referenciado por todos os blocks pertencentes ao bloco raiz
	 */
	private void addNodeToAllChildNodesFromRoot(ControlFlowGraphNode root, ControlFlowGraphNode targetBlock) {
		if(root == null || targetBlock == null) {
			return;
		}
		
		Set<ControlFlowGraphNode> childNodes = root.getChildNodes();
		for(ControlFlowGraphNode node : childNodes) {
			this.addNodeToAllChildNodesFromRoot(node, targetBlock);
			node.addChildNode(targetBlock, ControlFlowGraphEdgeType.CATCH);
		}
	}
}
