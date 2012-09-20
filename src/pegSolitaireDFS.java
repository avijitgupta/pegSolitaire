import java.io.DataInputStream;
import java.util.Stack;

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


class DFSLauncher {
	private int N;
	private char board[][];
	private boolean done;
	private int numPegs;
	Stack<pair> moves;
	
	public DFSLauncher(int dimension, String boardState[])
	{
		numPegs = 0;
		moves = new Stack<pair>();
		N = dimension;
		done = false;
		board = new char[N][N];
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
	
	public void displaySolution()
	{
		displayMoves(moves);
	}
	
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
	
	public void solveGame() throws Exception
	{
		Stack<pair> moveStore = new Stack<pair>();
		doDfs(numPegs,moveStore);
	}
	
	public void doDfs(int nPegs, Stack<pair>moveStore) throws Exception
	{
		if(done)return;
		
		if(nPegs==1)
		{
			if(board[N/2][N/2]=='X')
			{	
				done = true;
				//Stack<pair> out = (Stack<pair>) moveStore.clone();
				//displayMoves(out);
				moves= (Stack<pair>) moveStore.clone();
			}
			return;
		}

		for(int i = 0 ; i < N ; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				if(board[i][j]=='X')
				{
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
	
}

public class pegSolitaireDFS {
	static int N = 7;
	public static void main(String args[]) throws Exception
	{
		String input[]= new String[N];
		for(int i = 0 ; i < N ; i ++)
		{
			DataInputStream in = new DataInputStream(System.in);
			input[i] = in.readLine();
		}
		DFSLauncher solver = new DFSLauncher(N, input);
		solver.solveGame();
		solver.displaySolution();
		//solver.displayMoves();
	}
	
}

