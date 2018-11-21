/**
 * @author Vignesh Nallathambipillai
 * @studentID 28287177
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

public class Blocksworld {
	
	public static int BOARDSIZE = 4;
	public static Board FINALBOARD = new Board(null, configureFinalState(), 0);;
	public static Board STARTBOARD = new Board(null, configureStartState(), 0 );;
	
	//used for testing and output
	private int runLimit = 11; // till what depth to print board states out in testing
	private boolean test = false; //print board states out or not
	private boolean printSummary = false; //print search summary or not
	private boolean weakHeuristic = false; //choose which heuristic to use for A* search
	private int totalNodesExpanded; // access nodes expanded as a variable outside the local method (output purposes)
	
	//for EXTRAS - graph search to keep track of visited nodes
	private ArrayList<Board> visitedNodes = new ArrayList<Board>(); //track visited nodes
	private boolean graphSearch = false; //turn on graph search or not
	
	public static void main(String[] args){
		Blocksworld bw = new Blocksworld();
		//bw.depthFirstSearch();
		//bw.breadthFirstSearch();
		//bw.iterativeDeepeningSearch();
		//bw.aStarSearch();
		bw.plotResults();
		
		
	}
	
	private void plotResults(){
		/**
		 * print out number of nodes expanded for each search for each difficulty
		 * take care for BFS and IDS as they may not complete running for higher problem difficulties
		 */
		
		int bfsDifficultLimit = 14;
		int idsDifficultyLimit = 18;
		
		//depth first
		System.out.println("Depth First");
		for(int i=2; i<21; i += 2){
			STARTBOARD.setBoard(configureStartState(i));
			
			//take an average of 20 because each solution is different
			int runs = 1;
			int[] depthNodesExpanded = new int[runs];
			for(int j=0; j<runs; j++){
				depthFirstSearch();
				depthNodesExpanded[j] = totalNodesExpanded;
				}
			
			System.out.print("Depths : " + i);
			System.out.println(" Nodes Expanded: " + findArrayAverage(depthNodesExpanded));
		}
		
		
		
		//breadth first 
		System.out.println("Breadth First");
		for(int k=2; k<bfsDifficultLimit; k += 2){
			STARTBOARD.setBoard(configureStartState(k));
			breadthFirstSearch();
			System.out.print("Depths : " + k);
			System.out.println(" Nodes Expanded: " + totalNodesExpanded);
		}
		
		//A*
		System.out.println("A* Search");
		for(int k=2; k<21; k += 2){
			STARTBOARD.setBoard(configureStartState(k));
			aStarSearch();
			System.out.print("Depths : " + k);
			System.out.println(" Nodes Expanded: " + totalNodesExpanded);
		}
		
		//IDS
		System.out.println("Iterative Deepening");
		for(int k=2; k<idsDifficultyLimit; k += 2){
			STARTBOARD.setBoard(configureStartState(k));
			iterativeDeepeningSearch();
			System.out.print("Depths : " + k);
			System.out.println(" Nodes Expanded: " + totalNodesExpanded);
		}
		
	}
	
	//find average of array of ints - for DFS
	private int findArrayAverage(int[] arr){
		int size = 0;
		for(int i=0; i<arr.length; i++){
			size += arr[i];
		}
		return size/arr.length;
	}
	
	private void testSearch(int runNumber, Board board){
		/**
		 * as long as the test is still 'on' print out what the current board state is like
		 * @param runNumber Number of runs you want to print for
		 * @param board Board configuration
		 */
		if(!test){return;}
		
		if(runNumber <= runLimit){
			//print what the board state is like
			System.out.println("Board depth: " + board.getDepth());
			System.out.println(board.toString());
		}else{test = false;}
		
	}
	
	private Board aStarSearch(){
		/**
		 * A STAR SEARCH
		 * pretty much same as breadth first search but uses a priority queue
		 * priority queue priorities board with smallest (depth + Manhattan distance)
		 * @param None
		 * @return Board Solution board
		 */
		
		//other tools
		int nodesExpanded = 0;
		visitedNodes = new ArrayList<Board>();
		PriorityQueue<Board> pq;
		
		if(weakHeuristic){
			pq = new PriorityQueue<Board>(new WeakBoardComparator());
		}else{
			pq = new PriorityQueue<Board>(new BoardComparator());
		}
		
		pq.add(STARTBOARD);
		
		Board board;
		while(!(pq.isEmpty())){
			board = pq.poll();
			nodesExpanded++;
			testSearch(nodesExpanded, board);
			
			//check if solution has been reached
			if(isReached(board)){
				if(printSummary){
					System.out.println("A Star search");
					System.out.println("Nodes expanded: " + nodesExpanded);
					System.out.println("Depth: " + board.getDepth());
					System.out.println("Size of PriorityQueue: " +  pq.size() + "\n");
				}
				totalNodesExpanded = nodesExpanded;
				return board;
			}
			
			//find the successors - no need to randomize
			ArrayList<Board> successors = findSuccessors(board);
			
			//add them to the priority queue
			for(Board bd : successors){
				pq.add(bd);
			}
		}
		totalNodesExpanded = nodesExpanded;
		return null;
	}
	
	private Board breadthFirstSearch(){
		/**
		 * BREADTH FIRST SEARCH
		 * uses a queue, where successors are added to
		 * then head is removed, checked, then its successors are added to the end again
		 * stops when the goal state has been reached OR queue is empty
		 * @param None
		 * @return Board if solution is found, null otherwise
		 */
		
		//other tools
		int nodesExpanded = 0;
		visitedNodes = new ArrayList<Board>();
		
		//create new stack
		LinkedList<Board> ll = new LinkedList<Board>();
		ll.add(STARTBOARD);
		
		Board board = ll.getFirst();
		//runs out of memory so catch the exception
		
		try{
			
		while(!(ll.isEmpty())){
			board = ll.removeFirst();
			nodesExpanded++;
			testSearch(nodesExpanded, board);
			
			//check if solution has been reached
			if(isReached(board)){
				if(printSummary){
					System.out.println("Breadth first search");
					System.out.println("Nodes expanded: " + nodesExpanded);
					System.out.println("Depth: " + board.getDepth());
					System.out.println("Size of Queue: " +  ll.size() + "\n");
				}
				totalNodesExpanded = nodesExpanded;
				return board;
			}
			
			//find the successors - no need to randomize
			ArrayList<Board> successors = findSuccessors(board);
			
			//add them onto the end of the queue
			for(Board bd : successors){
				ll.addLast(bd);
			}	
		}
		}catch (OutOfMemoryError E){
			if(printSummary){
				System.out.println("Breadth first search - OUT OF MEMORY EXCEPTION");
				System.out.println("Nodes expanded: " + nodesExpanded);
				System.out.println("Depth: " + board.getDepth());
				System.out.println("Size of Queue: " +  ll.size() + "\n");
			}
			totalNodesExpanded = nodesExpanded;
			return board;
		}
		
		totalNodesExpanded = nodesExpanded;
		return null;
	}
	
	private Board iterativeDeepeningSearch(){
		/**
		 * ITERATIVE DEEPENING SEARCH
		 * Calls depthLimitedSearch for increasing depth from
		 * if a solution is found return it, otherwise return null (in theory this should never be reached)
		 * @param None
		 * @return Board solution
		 */
		
		if(printSummary){System.out.println("Iterative Deepening Search:");}
		
		for(int depth=0; depth < Integer.MAX_VALUE; depth++){
			Board solution = depthLimitedSearch(depth);
			if(solution != null){
				return solution;
			}
		}
		
		return null;
	}
	
	private Board depthLimitedSearch(int limit){
		/**
		 * DEPTH LIMITED SEARCH
		 * uses a stack, where successors are pushed on
		 * then one is popped off, checked, then it successors are pushed on again
		 * stops when the goal state has been reached OR depth limit has been reached
		 * @param None
		 * @return Board if solution is found, null otherwise
		 */
		
		//other tools
		int nodesExpanded = 0;
		visitedNodes = new ArrayList<Board>();
		
		//create new stack
		Stack<Board> st = new Stack<Board>();
		st.push(STARTBOARD);
		
		Board board;
		
		//loop until stack is empty
		while(!(st.isEmpty())){
			board = st.pop();
			//every time we expand, we increment the counter
			nodesExpanded++;
			
			testSearch(nodesExpanded, board);
			
			//check if solution has been reached, if so return true
			if(isReached(board)){
				if(printSummary){
					System.out.println("Depth Limited Search: " + limit + " / Iterative Deepening");
					System.out.println("Nodes expanded: " + nodesExpanded);
					System.out.println("Depth: " + board.getDepth());
					System.out.println("Size of stack: " +  st.size() + "\n");
				}
				totalNodesExpanded = nodesExpanded;
				return board;
			}
			
			//find the successors and randomize their order
			ArrayList<Board> successors = findSuccessors(board);
			Collections.shuffle(successors);
			
			//push them onto the stack ONLY IF their depth limit hasn't been reached
			for(Board bd : successors){
				if(bd.getDepth() <= limit){
					st.push(bd);
				}
			}
			
		}
		
		//if no solution found, return null
		totalNodesExpanded = nodesExpanded;
		return null;
	}
	
	private Board depthFirstSearch(){
		/**
		 * DEPTH FIRST SEARCH
		 * uses a stack, where successors are pushed on
		 * then one is popped off, checked, then it successors are pushed on again
		 * stops when the goal state has been reached
		 * @param None
		 * @return Board if solution found, otherwise return null
		 */
		
		//other tools
		int nodesExpanded = 0;
		visitedNodes = new ArrayList<Board>();
		
		//create new stack
		Stack<Board> st = new Stack<Board>();
		st.push(STARTBOARD);
		
		Board board;
		//loop until 'stack is empty'
		while(!(st.isEmpty())){
			//System.out.println(counter);
			
			//every time we expand, we increment the counter
			nodesExpanded++;
			
			board = st.pop();
			
			testSearch(nodesExpanded, board);
			
			//check if solution has been reached, if so return it
			if(isReached(board)){
				if(printSummary){
					System.out.println("Depth first search");
					System.out.println("Nodes expanded: " + nodesExpanded);
					System.out.println("Depth: " + board.getDepth());
					System.out.println("Size of stack: " +  st.size() + "\n");
				}
				totalNodesExpanded = nodesExpanded;
				return board;
			}
			
			//find the successors and randomize their order
			ArrayList<Board> successors = findSuccessors(board);
			Collections.shuffle(successors);
			
			//push them onto the stack
			for(Board bd : successors){
				st.push(bd);
			}
			
		}
		totalNodesExpanded = nodesExpanded;
		return null;
	}
	
	public static char[][] configureFinalState(){
		/**
		 * configures final state of board
		 * 'E' for empty, 'A', 'B', 'C' for the tiles and 'X' for the agent
		 * BUT no agent is present
		 * @param None
		 * @return final board state
		 */
		
		char[][] finalBoard = new char[BOARDSIZE][BOARDSIZE];
		
		//set all blocks to empty
		for(int i=0; i<BOARDSIZE; i++){
			for(int j=0; j<BOARDSIZE; j++){
				finalBoard[i][j] = 'E';
			}
		}
		//set tiles
		finalBoard[1][2] = 'A';
		finalBoard[1][1] = 'B';
		finalBoard[1][0] = 'C';
		//don't set agent
		//finalBoard[BOARDSIZE-1][0] = 'X';
		
		return finalBoard;
	}
	
	private ArrayList<Board> findSuccessors(Board board){
		/**
		 * finds which are valid positions for the agent to move to
		 * if statement checks whether to do a graph search or not (i.e. use 'visitedNodes')
		 * @param board - board
		 * @return ArrayList of boards for different successors
		 */
		
		int x = board.getAgent()[0];
		int y = board.getAgent()[1];
		Board newBoard;
		ArrayList<Board> positions = new ArrayList<Board>();
		
		//check if agent is not at the top row
		if(y!=BOARDSIZE-1){
			newBoard = replicateBoard(board);
			newBoard.incrementDepth();
			newBoard.setParent(board);
			newBoard = swap(newBoard, "up");
			if(!hasNodeBeenVisited(newBoard)){
				positions.add(newBoard);
			}
		}
		//check if agent is not at the bottom row
		if(y!=0){
			newBoard = replicateBoard(board);
			newBoard.incrementDepth();
			newBoard.setParent(board);
			newBoard = swap(newBoard, "down");
			if(!hasNodeBeenVisited(newBoard)){
				positions.add(newBoard);
			}
		}
		//check if agent is not at the leftmost column
		if(x!=0){
			newBoard = replicateBoard(board);
			newBoard.incrementDepth();
			newBoard.setParent(board);
			newBoard = swap(newBoard, "left");
			if(!hasNodeBeenVisited(newBoard)){
				positions.add(newBoard);
			}
		}
		//check if agent is not at the rightmost column
		if(x!=BOARDSIZE-1){
			newBoard = replicateBoard(board);
			newBoard.incrementDepth();
			newBoard.setParent(board);
			newBoard = swap(newBoard, "right");
			if(!hasNodeBeenVisited(newBoard)){
				positions.add(newBoard);
			}
		}
		
		return positions;
	}
	
	//EXTRAS - GRAPH SEARCH - check if board state already exists
	private boolean hasNodeBeenVisited(Board board){
		/**
		 * check if board has been visited already, if so return true, else false
		 */
		
		//CHECK IF GRAPH SEARCH HAS BEEN ENABLED FIRST
		if(!graphSearch){
			return false;
		}
		
		for(Board node : visitedNodes){
			if(Arrays.deepEquals(node.getBoard(), board.getBoard())){
				return true;
			}
		}
		visitedNodes.add(board);
		return false;
	}
	
	private Board swap(Board board, String movement){
		/**
		 * swaps the agent with the position it's going to move to
		 * @param board - Board state
		 * @param movement - Where the agent wants to move
		 * @return new board state
		 * @exception IllegalArguementException if not a valid move
		 */
		
		char space;
		int x = board.getAgent()[0];
		int y = board.getAgent()[1];
		
		switch (movement){
		case "left":  
			space = board.getBoardValue(x-1, y);
			board.setBoardValue(x, y, space);
			board.setBoardValue(x-1, y, 'X');
			return board;
		case "right":
			space = board.getBoardValue(x+1, y);
			board.setBoardValue(x, y, space);
			board.setBoardValue(x+1, y, 'X');
			return board;
		case "up":
			space = board.getBoardValue(x, y+1);
			board.setBoardValue(x, y, space);
			board.setBoardValue(x, y+1, 'X');
			return board;
		case "down":
			space = board.getBoardValue(x, y-1);
			board.setBoardValue(x, y, space);
			board.setBoardValue(x, y-1, 'X');
			return board;
		default:
			throw new IllegalArgumentException("Not a valid movement");
		}
	}
	
	
	private boolean isReached(Board board){
		/**
		 * Checks if the given board has reached the final state
		 * BUT remove the agent and check
		 * @param board Board state
		 * @return either true or false
		 */
		Board newBoard = replicateBoard(board);
		int x = board.getAgent()[0];
		int y = board.getAgent()[1];
		newBoard.setBoardValue(x, y, 'E');
				
		return Arrays.deepEquals(FINALBOARD.getBoard(), newBoard.getBoard());
	}
	
	private Board replicateBoard(Board board){
		/**
		 * creates a copy of the provided board
		 * @param board Board state
		 * @return new board with the same state
		 */
		
		char[][] newCharBoard = new char[Blocksworld.BOARDSIZE][Blocksworld.BOARDSIZE];
		for(int i=0; i<BOARDSIZE; i++){
			for(int j=0; j<BOARDSIZE; j++){
				newCharBoard[i][j] = board.getBoardValue(i, j);
			}
		}
		
		Board newBoard = new Board(board.getParent(), newCharBoard, board.getDepth());
		
		return newBoard;
	}
	
	public static char[][] configureStartState(){
		//standard difficulty (14)
		return configureStartState(14);
	}
	
	public static char[][] configureStartState(int difficulty){
		/**
		 * configures initial state of board
		 * 'E' for empty, 'A', 'B', 'C' for the tiles and 'X' for the agent
		 * difficulty changes the initial configuration to max depth of the difficulty
		 * ONLY WORKS FOR BOARD SIZE 4
		 * @param None
		 * @return initial board state
		 */
		
		if(BOARDSIZE != 4){
			difficulty = 14;
		}
		
		char[][] startBoard = new char[BOARDSIZE][BOARDSIZE];
		
		//set all blocks to empty
		for(int i=0; i<BOARDSIZE; i++){
			for(int j=0; j<BOARDSIZE; j++){
				startBoard[i][j] = 'E';
			}
		}
		switch(difficulty){
		//set tiles
		case 20:
			startBoard[0][3] = 'A';
			startBoard[1][3] = 'B';
			startBoard[2][3] = 'C';
			startBoard[0][0] = 'X';
			break;
		case 18:
			startBoard[0][3] = 'A';
			startBoard[1][3] = 'B';
			startBoard[2][3] = 'C';
			startBoard[1][1] = 'X';
			break;
		case 16:
			startBoard[0][3] = 'A';
			startBoard[1][2] = 'B';
			startBoard[2][3] = 'C';
			startBoard[1][3] = 'X';
			break;
		case 14:
			startBoard[0][0] = 'A';
			startBoard[1][0] = 'B';
			startBoard[2][0] = 'C';
			startBoard[BOARDSIZE-1][0] = 'X';
			break;
		case 12: 
			startBoard[0][0] = 'A';
			startBoard[1][0] = 'B';
			startBoard[2][0] = 'C';
			startBoard[2][1] = 'X';
			break;
		case 10:
			startBoard[0][0] = 'A';
			startBoard[1][1] = 'B';
			startBoard[2][0] = 'C';
			startBoard[1][0] = 'X';
			break;
		case 8:
			startBoard[1][0] = 'A';
			startBoard[1][1] = 'B';
			startBoard[2][0] = 'C';
			startBoard[0][1] = 'X';
			break;
		case 6:
			startBoard[1][1] = 'A';
			startBoard[0][1] = 'B';
			startBoard[2][0] = 'C';
			startBoard[1][0] = 'X';
			break;
		case 4:
			startBoard[1][1] = 'A';
			startBoard[0][1] = 'B';
			startBoard[1][0] = 'C';
			startBoard[2][1] = 'X';
			break;
		case 2:
			startBoard[1][1] = 'A';
			startBoard[0][1] = 'B';
			startBoard[1][0] = 'C';
			startBoard[1][2] = 'X';
			break;
		}
		
		return startBoard;
	}
	
	
	private class BoardComparator implements Comparator<Board>{
		/**
		 * Comparator class to compare (depth + Manhattan distance) of two boards
		 */
		@Override
		public int compare(Board board1, Board board2) {
			int board1Cost = board1.getDepth() + board1.estimatedCost();
			int board2Cost = board2.getDepth() + board2.estimatedCost(); 
			return board1Cost - board2Cost;
		}
	}
	
	private class WeakBoardComparator implements Comparator<Board>{
		/**
		 * Comparator class to compare misplaced tiles of two boards
		 */
		
		@Override
		public int compare(Board board1, Board board2){
			int board1Cost = board1.getDepth() + board1.misPlacedTiles();
			int board2Cost = board2.getDepth() + board2.misPlacedTiles();
			return board1Cost - board2Cost;
		}
	}
}
