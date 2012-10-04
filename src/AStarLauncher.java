
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/*
 * This program uses a heuristic to solve pegSolitaire. The heuristic makes use of 
 * classical AI techniques - Forward checking and failing early. At each stage, we look 
 * to the next set of possible states and arrange them such that the one with lesser number
 * of moves possible is picked first. This enables us to fail early, hence reducing 
 * the excess overhead of expanding nodes which would eventually fail. 
 */
public class AStarLauncher {
	
	
	//Size of the board - in this case 7
	private int N;
	//The actual board
	private char board[][];
	//Indicated if a valid path is found
	private boolean done;
	//number of pegs on board
	private int numPegs;
	//stack to store moves
	Stack<pair> moves;
	//Direction numbering 
	private final int up=1;
	private final int right=2;
	private final int down=3;
	private final int left=4;
	//Counter for expanded nodes
	public static int expandedNodes= 0;
	public static int verbose = 0;
	//Default constructor, initializes the board
	public AStarLauncher(int dimension, String boardState[])
	{
		//Allocating space/initializing variables
		numPegs = 0;
		moves = new Stack<pair>();
		N = dimension;
		done = false;
		board = new char[N][N];
	
		//Setting board to input board
		for(int i = 0 ; i < N ; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				board[i][j] = boardState[i].charAt(j);
				if(board[i][j]=='X')
					numPegs++;
			}
		}
	}
	
	//Displays the solution once the gabe has been completed
	int displaySolution()
	{
		displayMoves(moves);
		return expandedNodes;
	}
	
	//Displays the winning set of moves. If no winning moves are possible, shows a message
	public void displayMoves(Stack<pair>moveStore)
	{
		if(!done)
		{
			System.out.print("No Winning path on this configuration");			
		}
		System.out.println("Winning Configuration:");
		while(!moveStore.empty())
		{
			//Pop the moves from a stack
			pair top = moveStore.pop();
			System.out.print("( "+top.x+","+top.y+" )  ");
		}
		
	}
	
	//Forward checking to see how many moves are possible in the next step.
	// We want to minimize the number of moves in the next state, to enable 
	// failing early
	public int getNumberOfMovesPossible(char[][] testBoard)
	{
		//stores the number of moves possible in from a particular stage
		int numMoves = 0;
		for(int  i =0  ; i < N ; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				//For every peg
				if(testBoard[i][j]=='X')
				{
					//Check possible move down
					if(i+2 < N)
					{
						if(testBoard[i+2][j]=='0' && testBoard[i+1][j]=='X')
							numMoves++;
					}
					//check possible move up
					if(i-2 >=0)
					{
						if(testBoard[i-2][j]=='0' && testBoard[i-1][j]=='X')
							numMoves++;
					}
					//check possible move right
					if(j+2 < N)
					{
						if(testBoard[i][j+2]=='0' && testBoard[i][j+1]=='X')
							numMoves++;
					}
					//check possible move left
					if(j-2 >=0)
					{
						if(testBoard[i][j-2]=='0' && testBoard[i][j-1]=='X')
							numMoves++;
					}
				}
			}
		}
		return numMoves;
	}
	
	//Returns a number representative required by the assignment
	//of the (i,j) coordinate system that we use for our board
	public int getPegNumber(int i , int j)throws Exception
	{
		
		if(i < 2)
		{
			if(j>1 && j <5)
			{
				return i*3 + j -2;
			}
		}
		else if(i>=2 && i<=4)
		{
			return 5 + (i-2)*7 + j + 1;			
		}
		else if (i>4)
		{
			if(j>1 && j <5)
			{
				return 27 + (i-5)*3 + j -2 ; 
			}
		}
		
		throw new Exception();
		
	}
	//Invokes the heuristic DFS 
	public void solveGame() throws Exception
	{
		Stack<pair> moveStore = new Stack<pair>();
		doDfs(numPegs,moveStore);
	}
	
	// A custom comparator used for sorting
	static class KeyComparator implements Comparator<pair>
	 {
	     public int compare(pair c1, pair c2)
	     {
	         return c1.x - c2.x;
	     }
	 }

	public void displayBoard()
	{
		for(int i = 0 ; i < N; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				System.out.print(board[i][j]);
			}
		}
		System.out.print("\n");
	}
	//Implements heuristic DFS - A* algorithm based on the heuristic we have used
	public void doDfs(int nPegs, Stack<pair>moveStore) throws Exception
	{
		if(done)return;
		
		//Incrementing the expanded nodes
		expandedNodes++;
		if(verbose!=0)
		{
			System.out.println("*****");
		    System.out.println("Intermediate Trace");
		    displayBoard();
		    
		}
		// The terminal state. If the # of pegs is one, 
		// and we are at the center of board, we are done, else not
		if(nPegs==1)
		{
			if(board[N/2][N/2]=='X')
			{	
				done = true;
				moves= (Stack<pair>) moveStore.clone();
			}
			return;
		}

		// Doing a DFS on the board, choosing all possible pegs 
		for(int i = 0 ; i < N ; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				if(board[i][j]=='X')
				{
					char testBoard[][] = new char[N][N];
					//4 possible moves
					List<pair> moveArray = new ArrayList<pair>();
					
					copyArray(board, testBoard);
					
					/*Simulating possible moves for a particular peg.
					 We get the number of moves possible in the next stage, 
					 after moving this peg in all possible ways.
					 After gaining the stats, we can use out heuristic
					 and arrange moves in order ascending of the number of moves
					possible in the next step*/
					
					//Check down
					if(i+2 < N)
					{
						if(testBoard[i+2][j]=='0' && testBoard[i+1][j]=='X')
						{
							//simulate the move
							
							testBoard[i+2][j] = 'X';
							testBoard[i][j] = '0';
							testBoard[i+1][j] = '0';
							int moves = getNumberOfMovesPossible(testBoard);
							moveArray.add(new pair(moves, down));
							testBoard[i+2][j] = '0';
							testBoard[i][j] = 'X';
							testBoard[i+1][j] = 'X';
						}
						else
						{
							moveArray.add(new pair(0, down));
						}
					}
					else
					{
						moveArray.add(new pair(0, down));
					}
					
					//Check up
					if(i-2 >=0)
					{
						if(testBoard[i-2][j]=='0' && testBoard[i-1][j]=='X')
						{
							//simulate the move
							
							testBoard[i-2][j] = 'X';
							testBoard[i][j] = '0';
							testBoard[i-1][j] = '0';
							int moves = getNumberOfMovesPossible(testBoard);
							moveArray.add(new pair(moves, up));
							testBoard[i-2][j] = '0';
							testBoard[i][j] = 'X';
							testBoard[i-1][j] = 'X';
						}
						else
						{
							moveArray.add(new pair(0, up));
						}
					}
					else
					{
						moveArray.add(new pair(0, up));
					}
					
					//Check right
					if(j+2 < N)
					{
						if(testBoard[i][j+2]=='0' && testBoard[i][j+1]=='X')
						{
							//simulate the move
							
							testBoard[i][j+2] = 'X';
							testBoard[i][j] = '0';
							testBoard[i][j+1] = '0';
							int moves = getNumberOfMovesPossible(testBoard);
							moveArray.add(new pair(moves, right));
							testBoard[i][j+2] = '0';
							testBoard[i][j] = 'X';
							testBoard[i][j+1] = 'X';
						}
						else
						{
							moveArray.add(new pair(0, right));
						}
					}
					else
					{
						moveArray.add(new pair(0, right));
					}
					
					//Check left
					if(j-2 >=0)
					{
						if(testBoard[i][j-2]=='0' && testBoard[i][j-1]=='X')
						{
							//simulate the move
							testBoard[i][j-2] = 'X';
							testBoard[i][j] = '0';
							testBoard[i][j-1] = '0';
							int moves = getNumberOfMovesPossible(testBoard);
							moveArray.add(new pair(moves, left));
							testBoard[i][j-2] = '0';
							testBoard[i][j] = 'X';
							testBoard[i][j-1] = 'X';
						}
						else
						{
							moveArray.add(new pair(0, left));
						}
					}
					else
					{
						moveArray.add(new pair(0, left));
					}					
					
					//Arrange moves in ascending order of moves possible in the next stage
					Collections.sort(moveArray, new KeyComparator());
					
					//Invoke DFS on this new sorted array, choosing the state with the minimum number
					// of moves in the next state
					for(pair move: moveArray)
					{
						if(move.x > 0 || (nPegs-1 == 1))
						{
							int pegInit = getPegNumber(i,j);;
							int pegFinal = 0;
							//Make the actual move
							switch(move.y)
							{
									case up:	
											if(i-2 >=0)
											{
												if(board[i-2][j]=='0' && board[i-1][j]=='X')
												{
													//manipulate the board after the move
													board[i-2][j] = 'X';
													board[i][j] = '0';
													board[i-1][j] = '0';
													pegFinal = getPegNumber(i-2, j);
													moveStore.push(new pair(pegInit, pegFinal));
													doDfs(nPegs-1, moveStore);
													board[i-2][j] = '0';
													board[i][j] = 'X';
													board[i-1][j] = 'X';
													moveStore.pop();
												}
											}
											break;
											
								case down:	
											if(i+2 < N)
											{
												if(board[i+2][j]=='0' && board[i+1][j]=='X')
												{
													//manipulate the board after the move
													board[i+2][j] = 'X';
													board[i][j] = '0';
													board[i+1][j] = '0';
													pegFinal = getPegNumber(i+2, j);
													moveStore.push(new pair(pegInit, pegFinal));
													doDfs(nPegs-1, moveStore);
													board[i+2][j] = '0';
													board[i][j] = 'X';
													board[i+1][j] = 'X';
													moveStore.pop();
												}
											}
											break;
											
								case left:	
											if(j-2 >=0)
											{
												if(board[i][j-2]=='0' && board[i][j-1]=='X')
												{
													//manipulate the board after the move
													board[i][j-2] = 'X';
													board[i][j] = '0';
													board[i][j-1] = '0';
													pegFinal = getPegNumber(i, j-2);
													moveStore.push(new pair(pegInit, pegFinal));
													doDfs(nPegs-1, moveStore);
													board[i][j-2] = '0';
													board[i][j] = 'X';
													board[i][j-1] = 'X';
													moveStore.pop();
												}
											}	
											break;
											
								case right:	
											if(j+2 < N)
											{
												if(board[i][j+2]=='0' && board[i][j+1]=='X')
												{
													//manipulate the board after the move
													board[i][j+2] = 'X';
													board[i][j] = '0';
													board[i][j+1] = '0';
													pegFinal = getPegNumber(i, j+2);
													moveStore.push(new pair(pegInit, pegFinal));
													doDfs(nPegs-1, moveStore);
													board[i][j+2] = '0';
													board[i][j] = 'X';
													board[i][j+1] = 'X';
													moveStore.pop();
												}
											}
											break;
							}
						}
					}
					
					
				}

			}
			
		}
		return;
}

	//A helper routine to copy a 2D array
public void copyArray(char src[][], char dest[][])
{
	for(int i = 0 ; i < N ; i ++)
		for(int  j = 0; j < N ; j ++)
			dest[i][j] = src[i][j];
}

static double getMemUsed() {
    double used  = Runtime.getRuntime().totalMemory() - 
        Runtime.getRuntime().freeMemory();
    return (used/1024);
  }

	public static void main(String[] args) throws Exception {
	
		int N = 7;
	    if(args.length!=2)  {
	      System.err.println("usage: DFSIterative <filePath> <verbose boolean>");
	      return;
	    }
	    String input[]= new String[N];
	    FileInputStream fstream = new FileInputStream(args[0]);
		 DataInputStream in = new DataInputStream(fstream);
		for(int i = 0 ; i < N ; i ++)
		{
			 input[i] = in.readLine();
			 System.out.println(input[i]);
		}
		verbose = Integer.parseInt(args[1]);
		AStarLauncher solver = new AStarLauncher(N, input);
		long startTime = System.currentTimeMillis();
		solver.solveGame();
		long endTime = System.currentTimeMillis();
		
		int expandedNodes = solver.displaySolution();
	    System.out.println("\n***STATS***");
	    System.out.println(endTime - startTime+" milliSeconds");
	    System.out.println("Memory used: "+ getMemUsed()+ " bytes\nNodes visited: "+expandedNodes);
	}
}
