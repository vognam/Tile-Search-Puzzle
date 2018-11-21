/**
 * @author Vignesh Nallathambipillai
 * @studentID 28287177
 */

import java.util.Arrays;

public class Board {
	
	private char[][] board;
	private Board parent;
	private int depth;
	
	public Board(Board parent, char[][] board, int depth){
		/**
		 * parent Parent node
		 * board Char array of tiles
		 * depth How deep the board state is
		 */
		this.depth = depth;
		this.parent = parent;
		this.board = board;
	}
	
	public void setBoard(char[][] board){
		this.board = board;
	}
	
	public void setBoardValue(int x, int y, char value){
		board[x][y] = value;
	}
	
	public int getDepth(){
		return depth;
	}
	
	public char[][] getBoard(){
		return board;
	}
	
	public char getBoardValue(int x, int y){
		return board[x][y];
	}
	
	public Board getParent(){
		return parent;
	}
	
	public void setParent(Board parent){
		this.parent = parent;
	}
	
	public void incrementDepth(){
		depth++;
	}
	
	public int[] getAgent(){
		/**
		 * finds where the agent 'X' is on the board
		 * @param None
		 * @return two integers in an array for x and y co-od of agent respectively
		 * @return null if nothing can be found
		 */
		
		for(int i=0; i<Blocksworld.BOARDSIZE; i++){
			for (int j=0; j<Blocksworld.BOARDSIZE; j++){
				if (board[i][j] == 'X'){
					int[] newBoard = {i, j};
					return newBoard;
				}
			}
		}
		
		return null;
	}
	
	//for extra heuristic for A*
	public int misPlacedTiles(){
		/**
		 * find how many tiles are misplaced
		 */
		
		char[] values = {'A', 'B', 'C'};
		int misplacedTiles = 0;
		
		//for each tile, see if it's misplaced 
		for(char value: values){
			int[] pos1 = findBoardValue(value, this);
			int[] pos2 = findBoardValue(value, Blocksworld.FINALBOARD);
			if(pos1 != null && pos2 != null){
				if(!(Arrays.equals(pos1, pos2))){
					misplacedTiles++;
				}
			}
		}
		
		return misplacedTiles;
		
	}
	
	//for A* search
	public int estimatedCost(){
		/**
		 * find how far away the blocks are to their final position
		 * @param None
		 * @return int The combined Manhatten distance of the blocks to their final position
		 */
		
		char[] values = {'A', 'B', 'C'};
		int totalManhattanDistance = 0;
		
		//for each tile, total the Manhattan distance between this board and the final board 
		for(char value: values){
			int[] pos1 = findBoardValue(value, this);
			int[] pos2 = findBoardValue(value, Blocksworld.FINALBOARD);
			if(pos1 != null && pos2 != null){
				totalManhattanDistance += findManhattanDistance(pos1, pos2);
			}
		}
		
		return totalManhattanDistance;
	}
	
	private int findManhattanDistance(int[] pos1, int[] pos2){
		/**
		 * Find Manhattan distance between two coordinates
		 * @param two int[] containing x and y co-od
		 * @return the total manhattan distance
		 */
		return Math.abs(pos1[0]-pos2[0]) + Math.abs(pos1[1]-pos2[1]);
	}
	
	private int[] findBoardValue(char value, Board board){
		/**
		 * find first occurrence of value in the 2D array
		 * @param value The value to be found
		 * @return int[] x and y coordinates of the value
		 */
		
		for(int i=0; i<Blocksworld.BOARDSIZE; i++){
			for(int j=0; j<Blocksworld.BOARDSIZE; j++){
				if(board.getBoard()[i][j] == value){
					int[] position = {i, j};
					return position;
				}
			}
		}
		return null;
	}
	
	
	public void printSolution(){
		printSolution(this, Integer.MAX_VALUE);
	}
	
	//for depth first search to prevent stack overflow
	public void printSolution(int limit){
		printSolution(this, limit);
	}
	
	private Board printSolution(Board board, int limit){
		System.out.println("Board depth: " + board.depth);
		System.out.println(board);
		if(board.parent == null || limit == 0){
			return board;
		}else{
			limit--;
			return printSolution(board.parent, limit);
		}
	}
	
	
	@Override
	public String toString(){
		/**
		 * prints the current board state in a grid format
		 */
		String str = "";
		for(int i=0; i<Blocksworld.BOARDSIZE; i++){
			for(int j=0; j<Blocksworld.BOARDSIZE; j++){
				str = str + board[j][Blocksworld.BOARDSIZE - (i+1)] + " ";
			}
			str = str + "\n";
		}
		
		return str;
	}
}
