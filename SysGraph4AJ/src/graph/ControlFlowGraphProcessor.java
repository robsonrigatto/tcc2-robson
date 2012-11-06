package graph;

import java.util.ArrayList;
import java.util.List;

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
 * Classe responsável pelo processamento de uma instância de {@link ControlFlowGraphBlockNode}
 * a partir de um grafo de fluxo de controle representado pela classe {@link ControlFlowGraph}
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
	 * @return {@link ControlFlowGraphBlockNode} criado com toda a hierarquia de instruções
	 * que representam o grafo de fluxo de controle
	 */
	public ControlFlowGraphBlockNode process(MethodGen methodGen) {
		InstructionHandle instruction = methodGen.getInstructionList().getStart();
		return this.processInstruction(instruction);

	}

	/**
	 * @see ControlFlowGraphProcessor#processInstruction(InstructionHandle, ControlFlowGraphBlockNode, List)
	 * 
	 * @param instruction
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @return nó raiz com todas as instruções armazenadas em seu posigrafo
	 */
	private ControlFlowGraphBlockNode processInstruction(InstructionHandle instruction) {
		return this.processInstruction(instruction, null, new ArrayList<Integer>());
	}


	/**
	 * Processa uma instrução e retorna o respectivo grafo representado pela classe
	 * {@link ControlFlowGraphBlockNode}.
	 *  
	 * @param instructionHandle
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param root
	 * 		{@link ControlFlowGraphBlockNode} já criado pelo builder, pode ser nulo 	
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * 
	 * @return nó raiz com todas as instruções armazenadas em seu grafo
	 */ 
	private ControlFlowGraphBlockNode processInstruction(InstructionHandle instructionHandle, ControlFlowGraphBlockNode root, List<Integer> processedInstructionIds) {
		ControlFlowGraphBlockNode blockNode = new ControlFlowGraphBlockNode();

		if(root == null || root.getInstructions().size() == 0) {
			root = blockNode;
		}
		boolean ifInstructionConditional, switchInstructionConditional, returnInstructionConditional, 
			notNullConditional, instructionHasNotProcessedConditional;
		do {
			CodeExceptionGen exceptionBlock = this.getExceptionBlock(instructionHandle, processedInstructionIds);
			//exceptionBlock != null && !processedInstructionIds.contains(instructionHandle.getPosition())
			if(false) {
				processedInstructionIds.add(instructionHandle.getPosition());
				
				blockNode.setTryStatement(true);
				
				InstructionHandle startPC = exceptionBlock.getStartPC();
				blockNode.addChildBlock(this.processInstruction(startPC, root, processedInstructionIds));
				
				InstructionHandle handlerPC = exceptionBlock.getHandlerPC();
				blockNode.addChildBlock(this.processInstruction(handlerPC, root, processedInstructionIds));
				
				instructionHandle = exceptionBlock.getEndPC();
			} else {
				processedInstructionIds.add(instructionHandle.getPosition());				
				blockNode.addInstruction(instructionHandle.getInstruction());
				blockNode.appendDescription(instructionHandle.toString());
				instructionHandle = instructionHandle instanceof BranchHandle ? ((BranchHandle) instructionHandle).getTarget() : instructionHandle.getNext();
			}
			
			notNullConditional = instructionHandle != null;
			instructionHasNotProcessedConditional = (notNullConditional && !processedInstructionIds.contains(instructionHandle.getPosition()));
			
			ifInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof IfInstruction);
			switchInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof Select);
			returnInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof ReturnInstruction);
			
		} while(notNullConditional && returnInstructionConditional && ifInstructionConditional && instructionHasNotProcessedConditional && switchInstructionConditional);

		if(notNullConditional) {
			
			if(!instructionHasNotProcessedConditional) {
				ControlFlowGraphBlockNode childNode = new ControlFlowGraphBlockNode();
				blockNode.addChildBlock(childNode);
				childNode.appendDescription(Integer.toString(instructionHandle.getPosition()));
				
			} else if(!ifInstructionConditional) {
				this.processIfInstruction(instructionHandle, root, processedInstructionIds, blockNode);				
			
			} else if(!returnInstructionConditional) {
				blockNode.addInstruction(instructionHandle.getInstruction());
				blockNode.appendDescription(instructionHandle.toString());
				
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
	 * 		{@link ControlFlowGraphBlockNode} já criado pelo builder
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * @param blockNode
	 * 		{@link ControlFlowGraphBlockNode} processado no momento
	 * 		
	 */
	private void processIfInstruction(InstructionHandle instructionHandle, ControlFlowGraphBlockNode root, List<Integer> processedInstructionIds, ControlFlowGraphBlockNode blockNode) {
		
		blockNode.addInstruction(instructionHandle.getInstruction());
		blockNode.appendDescription(instructionHandle.toString());
		BranchHandle branchHandle = (BranchHandle) instructionHandle;
		InstructionHandle ifTrueNextInstruction = branchHandle.getTarget();	

		boolean hasInstructionProcessed = processedInstructionIds.contains(ifTrueNextInstruction.getPosition());
		if(!hasInstructionProcessed) {
			blockNode.addChildBlock(this.processInstruction(ifTrueNextInstruction, root, processedInstructionIds));
		}
		blockNode.addChildBlock(this.processInstruction(instructionHandle.getNext(), root, processedInstructionIds));
	}
	
	/**
	 * 
	 * @param instructionHandle
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param root
	 * 		{@link ControlFlowGraphBlockNode} já criado pelo builder
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * @param blockNode
	 * 		{@link ControlFlowGraphBlockNode} processado no momento
	 * 		
	 */
	private void processSwitchInstruction(InstructionHandle instructionHandle, ControlFlowGraphBlockNode root, List<Integer> processedInstructionIds, ControlFlowGraphBlockNode blockNode) {
		
		TABLESWITCH switchInstruction = (TABLESWITCH) instructionHandle.getInstruction();
		blockNode.addInstruction(instructionHandle.getInstruction());
		blockNode.appendDescription(instructionHandle.toString());
		InstructionHandle[] caseInstructions = switchInstruction.getTargets();
		for(InstructionHandle caseInstruction : caseInstructions) {
			blockNode.addChildBlock(this.processInstruction(caseInstruction, root, processedInstructionIds));
		}
		InstructionHandle defaultCaseInstruction = switchInstruction.getTarget();
		blockNode.addChildBlock(this.processInstruction(defaultCaseInstruction, root, processedInstructionIds));
	}
	
	private CodeExceptionGen getExceptionBlock(InstructionHandle instructionHandle, List<Integer> processedInstructionIds) {
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
}
