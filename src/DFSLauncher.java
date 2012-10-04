import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Stack;

/*
 * This is a simple implementation of recursive DFS. This essentially makes all the moves
 * possible at a given time and arbitrarily lands upon the solution.
 */

//Pair of coordinates (i,j)
class pair
{
	int x;
	int y;
	public pair(int x, int y)
	{
		this.x  = x;
		this.y  = y;
	}
}

/*
 * The class that launches DFS
 * 
 */
class DFSLauncher {
	// The size of the board
	private int N;
	//The intermediate board
	private char board[][];
	//Checks if the path has been found 
	private boolean done;
	private int numPegs;
	Stack<pair> moves;
	//verbose mode variable
	static int verbose = 0;
	//keeps a count of the expanded nodes
	public static int expandedNodes = 0;
	// The default constructor
	public DFSLauncher(int dimension, String boardState[])
	{
		//Initialisingg variables
		numPegs = 0;
		moves = new Stack<pair>();
		N = dimension;
		done = false;
		board = new char[N][N];
		//Initializing board configuration
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
	
	//wrapper to display the winning path
	public int displaySolution()
	{
		displayMoves(moves);
		return expandedNodes;
	}
	
	//Displays the winning path
	public void displayMoves(Stack<pair>moveStore)
	{
		if(!done)
		{
			System.out.print("No Winning path on this configuration");			
		}
		while(!moveStore.empty())
		{
			pair top = moveStore.pop();
			System.out.print("( "+top.x+","+top.y+" )  ");
		}
		
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
	
	//Invokes DFS
	public void solveGame() throws Exception
	{
		Stack<pair> moveStore = new Stack<pair>();
		doDfs(numPegs,moveStore);
	}
	
	//display the state of the board
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

	
	//The actual DFS without any heuristic
	public void doDfs(int nPegs, Stack<pair>moveStore) throws Exception
	{
		if(done)return;
		
		//incrementing the expanded nodes
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

		for(int i = 0 ; i < N ; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				//For every peg on the board do all possible moves
				if(board[i][j]=='X')
				{
					//Down move
					if(i+2 < N)
					{
						if(board[i+2][j]=='0' && board[i+1][j]=='X')
						{
							//make the move
							
							board[i+2][j] = 'X';
							board[i][j] = '0';
							board[i+1][j] = '0';
							int pegInit = getPegNumber(i,j);
							int pegFinal = getPegNumber(i+2, j);
							moveStore.push(new pair(pegInit, pegFinal));
							doDfs(nPegs-1, moveStore);
							board[i+2][j] = '0';
							board[i][j] = 'X';
							board[i+1][j] = 'X';
							moveStore.pop();
						}
					}
					//Up move
					if(i-2 >=0)
					{
						if(board[i-2][j]=='0' && board[i-1][j]=='X')
						{
							//make the move
							
							board[i-2][j] = 'X';
							board[i][j] = '0';
							board[i-1][j] = '0';
							int pegInit = getPegNumber(i,j);
							int pegFinal = getPegNumber(i-2, j);
							moveStore.push(new pair(pegInit, pegFinal));
							doDfs(nPegs-1, moveStore);
							board[i-2][j] = '0';
							board[i][j] = 'X';
							board[i-1][j] = 'X';
							moveStore.pop();
						}
					}
					//Right move
					if(j+2 < N)
					{
						if(board[i][j+2]=='0' && board[i][j+1]=='X')
						{
							//make the move
							
							board[i][j+2] = 'X';
							board[i][j] = '0';
							board[i][j+1] = '0';
							int pegInit = getPegNumber(i,j);
							int pegFinal = getPegNumber(i, j+2);
							moveStore.push(new pair(pegInit, pegFinal));
							doDfs(nPegs-1, moveStore);
							board[i][j+2] = '0';
							board[i][j] = 'X';
							board[i][j+1] = 'X';
							moveStore.pop();
						}
					}
					
					//Left move
					if(j-2 >=0)
					{
						if(board[i][j-2]=='0' && board[i][j-1]=='X')
						{
							//make the move
							board[i][j-2] = 'X';
							board[i][j] = '0';
							board[i][j-1] = '0';
							int pegInit = getPegNumber(i,j);
							int pegFinal = getPegNumber(i, j-2);
							moveStore.push(new pair(pegInit, pegFinal));
							doDfs(nPegs-1, moveStore);
							board[i][j-2] = '0';
							board[i][j] = 'X';
							board[i][j-1] = 'X';
							moveStore.pop();
						}
					}
					
				}

			}
			
		}
		return;
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
		DFSLauncher solver = new DFSLauncher(N, input);
		long startTime = System.currentTimeMillis();
		solver.solveGame();
		long endTime = System.currentTimeMillis();
		
		int expandedNodes = solver.displaySolution();
	    System.out.println("\n***STATS***");
	    System.out.println(endTime - startTime+" milliSeconds");
	    System.out.println("Memory used: "+ getMemUsed()+ " bytes\nNodes visited: "+expandedNodes);
	}
	
}
/*
public class pegSolitaireDFS {
	static int N = 7;
	public void runPegSolitaireDFS() throws Exception
	{
		String input[]= new String[N];
		for(int i = 0 ; i < N ; i ++)
		{
			DataInputStream in = new DataInputStream(System.in);
			input[i] = in.readLine();
		}
		DFSLauncher solver = new DFSLauncher(N, input);
		solver.solveGame();
		int expandedNodes = solver.displaySolution();
		System.out.println("Nodes Expanded: "+expandedNodes);
		//solver.displayMoves();
	}
	
}
*/
