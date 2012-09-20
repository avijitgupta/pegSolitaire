import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;



class AStarLauncher {
	private int N;
	private char board[][];
	private boolean done;
	private int numPegs;
	Stack<pair> moves;
	private final int up=1;
	private final int right=2;
	private final int down=3;
	private final int left=4;
	public AStarLauncher(int dimension, String boardState[])
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
	
	public int getNumberOfMovesPossible(char[][] testBoard)
	{
		int numMoves = 0;
		for(int  i =0  ; i < N ; i ++)
		{
			for(int j = 0 ; j < N ; j ++)
			{
				if(testBoard[i][j]=='X')
				{
					if(i+2 < N)
					{
						if(testBoard[i+2][j]=='0' && testBoard[i+1][j]=='X')
							numMoves++;
					}
					if(i-2 >=0)
					{
						if(testBoard[i-2][j]=='0' && testBoard[i-1][j]=='X')
							numMoves++;
					}
					if(j+2 < N)
					{
						if(testBoard[i][j+2]=='0' && testBoard[i][j+1]=='X')
							numMoves++;
					}
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
	
	static class KeyComparator implements Comparator<pair>
	 {
	     public int compare(pair c1, pair c2)
	     {
	         return c2.x - c1.x;
	     }
	 }

	
	public void doDfs(int nPegs, Stack<pair>moveStore) throws Exception
	{
		if(done)return;
		
		if(nPegs==1)
		{
			if(board[N/2][N/2]=='X')
			{	
				done = true;
				//System.out.print("Reached here");
				//Stack<pair> out = (Stack<pair>) moveStore.clone();
				//displayMoves(out);
				//System.out.print("Reached here");
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
					char testBoard[][] = new char[N][N];
					//4 possible moves
					List<pair> moveArray = new ArrayList<pair>();
					
					copyArray(board, testBoard);
					
					if(i+2 < N)
					{
						if(testBoard[i+2][j]=='0' && testBoard[i+1][j]=='X')
						{
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
					
					
					if(i-2 >=0)
					{
						if(testBoard[i-2][j]=='0' && testBoard[i-1][j]=='X')
						{
							//make the move
							
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
					
					
					if(j+2 < N)
					{
						if(testBoard[i][j+2]=='0' && testBoard[i][j+1]=='X')
						{
							//make the move
							
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

					if(j-2 >=0)
					{
						if(testBoard[i][j-2]=='0' && testBoard[i][j-1]=='X')
						{
							//make the move
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
					
					Collections.sort(moveArray, new KeyComparator());
					
					/*for(pair move: moveArray)
					{
						System.out.print(move.x+" "+move.y+" | ");
					}
					System.out.println();*/
					///////////////////////////////////////////////////////
					
					for(pair move: moveArray)
					{
						if(move.x > 0 || (nPegs-1 == 1))
						{
							int pegInit = getPegNumber(i,j);;
							int pegFinal = 0;
							switch(move.y)
							{
									case up:	
											if(i-2 >=0)
											{
												if(board[i-2][j]=='0' && board[i-1][j]=='X')
												{
													board[i-2][j] = 'X';
													board[i][j] = '0';
													board[i-1][j] = '0';
													pegFinal = getPegNumber(i-2, j);
													//System.out.println(pegInit+" " + pegFinal);
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

public void copyArray(char src[][], char dest[][])
{
	for(int i = 0 ; i < N ; i ++)
		for(int  j = 0; j < N ; j ++)
			dest[i][j] = src[i][j];
}

	
}

public class PegSolitaireAStarH1 {
	static int N = 7;
	public void runPegSolitaireAStarH1() throws Exception
	{
		String input[]= new String[N];
		for(int i = 0 ; i < N ; i ++)
		{
			DataInputStream in = new DataInputStream(System.in);
			input[i] = in.readLine();
		}
		AStarLauncher solver = new AStarLauncher(N, input);
		solver.solveGame();
		solver.displaySolution();
		//solver.displayMoves();
	}
}
