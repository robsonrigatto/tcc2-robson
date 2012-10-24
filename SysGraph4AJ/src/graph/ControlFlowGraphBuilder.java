package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.apache.bcel.verifier.structurals.InstructionContext;

/**
 * Builder responsável pela construção de uma instância de {@link BasicBlockNode}
 * a partir de um grafo de fluxo de controle representado pela classe {@link ControlFlowGraph}
 * 
 * @author robson
 * 
 * @see ControlFlowGraphProcessor
 *
 */
public class ControlFlowGraphBuilder {

	/**
	 * 
	 * @param controlFlowGraph
	 * 		{@link ControlFlowGraph} representado de um método
	 * @return {@link BasicBlockNode} criado com toda a hierarquia de instruções
	 * que representam o grafo de fluxo de controle
	 */
	public BasicBlockNode build(ControlFlowGraph controlFlowGraph) {
		InstructionContext[] instructionContexts = controlFlowGraph.getInstructionContexts();
		Arrays.sort(instructionContexts, new InstructionComparator());

		InstructionHandle instruction = instructionContexts[0].getInstruction();
		return this.processInstruction(instruction);

	}

	/**
	 * @see ControlFlowGraphBuilder#processInstruction(InstructionHandle, BasicBlockNode, List)
	 * 
	 * @param instruction
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @return nó raiz com todas as instruções armazenadas em seu posigrafo
	 */
	private BasicBlockNode processInstruction(InstructionHandle instruction) {
		return this.processInstruction(instruction, null, new ArrayList<Integer>());
	}


	/**
	 * Processa uma instrução e retorna o respectivo grafo representado pela classe
	 * {@link BasicBlockNode}.
	 *  
	 * @param instructionHandle
	 * 		Uma instrução representada pela classe {@link InstructionHandle}
	 * @param root
	 * 		{@link BasicBlockNode} já criado pelo builder, pode ser nulo 	
	 * @param processedInstructionIds
	 * 		Lista de todas as posições já processadas até o momento.<br>
	 * 		É interessante ter essa informação para não adicionar nós redundantes e também isso
	 * 		evita processamentos em contínuos, gerando {@link StackOverflowError}.
	 * 
	 * @return nó raiz com todas as instruções armazenadas em seu grafo
	 */ 
	private BasicBlockNode processInstruction(InstructionHandle instructionHandle, BasicBlockNode root, List<Integer> processedInstructionIds) {
		BasicBlockNode blockNode = new BasicBlockNode();

		if(root == null) {
			root = blockNode;
		}
		boolean ifInstructionConditional, returnInstructionConditional, notNullConditional, instructionHasNotProcessedConditional;
		do {
			processedInstructionIds.add(instructionHandle.getPosition());
			blockNode.addInstruction(instructionHandle.getInstruction());
			blockNode.appendDescription(instructionHandle.toString());
			instructionHandle = instructionHandle instanceof BranchHandle ? ((BranchHandle) instructionHandle).getTarget() : instructionHandle.getNext();
			notNullConditional = instructionHandle != null;
			instructionHasNotProcessedConditional = (notNullConditional && !processedInstructionIds.contains(instructionHandle.getPosition()));
			ifInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof IfInstruction);
			returnInstructionConditional = !(notNullConditional && instructionHandle.getInstruction() instanceof ReturnInstruction);
		} while(notNullConditional && returnInstructionConditional && ifInstructionConditional && instructionHasNotProcessedConditional);

		if(notNullConditional) {
			if(!instructionHasNotProcessedConditional) {
				BasicBlockNode childNode = new BasicBlockNode();
				blockNode.addChildBlock(childNode);
				childNode.appendDescription(Integer.toString(instructionHandle.getPosition()));
			} else if(!ifInstructionConditional) {
				blockNode.addInstruction(instructionHandle.getInstruction());
				blockNode.appendDescription(instructionHandle.toString());
				BranchHandle branchHandle = (BranchHandle) instructionHandle;
				InstructionHandle ifTrueNextInstruction = branchHandle.getTarget();	

				boolean hasInstructionProcessed = processedInstructionIds.contains(ifTrueNextInstruction.getPosition());
				if(!hasInstructionProcessed) {
					blockNode.addChildBlock(this.processInstruction(ifTrueNextInstruction, root, processedInstructionIds));
				}
				blockNode.addChildBlock(this.processInstruction(instructionHandle.getNext(), root, processedInstructionIds));				
			
			} else if(!returnInstructionConditional) {
				blockNode.addInstruction(instructionHandle.getInstruction());
				blockNode.appendDescription(instructionHandle.toString());
			}
		}
		return blockNode;
	}

	/**
	 * Comparator responsável pela ordenação de um array de {@link InstructionContext}
	 * 
	 * @author robson
	 */
	private class InstructionComparator implements Comparator<InstructionContext> {

		@Override
		public int compare(InstructionContext o1, InstructionContext o2) {
			InstructionHandle firstInstruction = o1.getInstruction();
			InstructionHandle secondInstruction = o2.getInstruction();

			if(firstInstruction.getPosition() < secondInstruction.getPosition()) {
				return -1;
			} else if(firstInstruction.getPosition() > secondInstruction.getPosition()) {
				return 1;
			}
			return 0;
		}		
	}
}
