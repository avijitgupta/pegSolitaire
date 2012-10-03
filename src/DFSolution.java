import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
 * This is a DFS search implementation to solve Peg solitaire
 * If solution exists; then its sequence of moves will be returned
 * in Winning move stack (and board will be also displayed)
 *  
 * If solution doesn't exist; winning sequence will be empty
 * and board will be same as input
 */
public class DFSolution {

  private char [][]board;
  private static int N;

  private final char FILLED  = 'X';
  private final char EMPTY = '0';
  private final char UNUSABLE = '-';

  private boolean gameOver = false;
  private Stack <String> winningMove;
  private int nodeVisited;
  private Map<String, Integer> coordinatesToPos;
  private boolean traceVerbose;

  /*
   * Parse input string in Board data structure
   */
  public DFSolution(int n, String input, String verbose) {
    N = n;
    board = new char[N][N];
    coordinatesToPos = new HashMap<String, Integer>();
    winningMove = new Stack<String>();
    nodeVisited = 0;
    
    mapCoordinates();
    int check = 0;
    int counter = 0;
    traceVerbose = Boolean.parseBoolean(verbose);
    
    for(int i=0; i<input.length(); i++)
    {
      if(check> 16)
        break;
      if(input.charAt(i) == UNUSABLE || input.charAt(i) == EMPTY 
          || input.charAt(i) == FILLED) {
        board[counter/N][counter%N] = input.charAt(i);
        counter++;
      }
      if(input.charAt(i)== UNUSABLE)
        check++;
    } 
  }

  /*
   * Form a map between position and board co-ordinates
   * e.g. goal position 3,3 maps to 16
   */
  void mapCoordinates() {
    int counter = 0;
    for(int i=0; i<N; i++)  {
      for(int j=0; j<N; j++)  {
        if(i<2 && (j<2 || j>= N-2)) {
          coordinatesToPos.put((i+":"+j),-1);
        }
        else if(i>= N-2 && (j<2 || j>= N-2)) {
          coordinatesToPos.put((i+":"+j),-1);
        }
        else{
          coordinatesToPos.put((i+":"+j),counter);
          counter++;
        }
      }
    }
  }

  /*
   * convert position in Board to co-ordinates
   * e.g. 9 points to {2,3} co-ordinates in Board
   */
  static String posToCoordinates(int pos)  {
    int i,j;
    if(pos > 5 && pos <26)  {
      i = (pos+1)/N + 1;
      j = (pos+1)%N;
    }
    else if(pos < 5)  {
      i = pos/3;
      j = pos%3+2;
    }
    else  {
      i = pos/3 - 4;
      j = pos%3 + 2;
    }
    String str = i+":"+j;
    return str;
  }

  void displayBoard()  {
    for(int i=0;i<N;i++)  {
      for(int j=0;j<N;j++)  {
        System.out.print(board[i][j]);
      }
      System.out.println();
    }
  }

  /*
   * see if the game is over
   */
  boolean gameWon() {
    int flag = 0;
    for(int i=0; i<N; i++)  {
      for(int j=0; j<N; j++)  {
        if(board[i][j]==FILLED)
          flag++;
        if(flag > 1)
          return false;
      }
    }
    if(board[3][3] == FILLED)
      return true;
    return false;
  }

  /*
   * Move from initialPos to finalPos 
   * it basically checks the direction
   * marks the pegs as FILLED and EMPTY
   * and pushes the move in a stack
   */
  void move(int initialPos, int finalPos, int i, int j, char direction) {
    if(direction == 'U')  {
      board[i-2][j] = FILLED;
      board[i][j] = EMPTY;
      board[i-1][j] = EMPTY;
    }
    else if(direction == 'L') {
      board[i][j-2] = FILLED;
      board[i][j] = EMPTY;
      board[i][j-1] = EMPTY;
    }
    else if(direction == 'D') {
      board[i+2][j] = FILLED;
      board[i][j] = EMPTY;
      board[i+1][j] = EMPTY;
    }
    else  {
      board[i][j+2] = FILLED;
      board[i][j] = EMPTY;
      board[i][j+1] = EMPTY; 
    }
    winningMove.push(initialPos+"->"+finalPos);
    nodeVisited++;
  }

  /*
   * Undo the move from initialPos to finalPos
   * it checks the direction
   * unmarks the peg
   * and pops out a move from winning move stack
   */
  void unmove(int initialPos, int finalPos, int i, int j, char direction) {
    if(direction == 'U')  {
      board[i-2][j] = EMPTY;
      board[i][j] = FILLED;
      board[i-1][j] = FILLED;
    }
    else if(direction == 'L') {
      board[i][j-2] = EMPTY;
      board[i][j] = FILLED;
      board[i][j-1] = FILLED; 
    }
    else if(direction == 'D') {
      board[i+2][j] = EMPTY;
      board[i][j] = FILLED;
      board[i+1][j] = FILLED;
    }
    else  {
      board[i][j+2] = EMPTY;
      board[i][j] = FILLED;
      board[i][j+1] = FILLED;
    }
    winningMove.pop();
  }

  /*
   * Executes the DFS
   * for every function call, it checks the board state
   * for every position in board see if move is possible
   * 
   * if a move (a,b) is possible
   * do that move and call the DFS again.
   * 
   * for every co-ordinate 4 such moves are possible
   * Left, Up, Down and Right
   */
  void DFS() {
    //if(traceVerbose)  {
     // System.out.println(winningMove);
    //}
    
    if(gameOver)  {
      return;
    }
    if(gameWon())  {
      gameOver = true;
      return;
    }
    int initialPos, finalPos;
    for(int i=0;i<N;i++)  {
      for(int j=0;j<N;j++)  {


        if( ((i-2)>=0) && (board[i-1][j] == FILLED) 
            && (board[i-2][j] == EMPTY) && board[i][j] == FILLED)  {

          initialPos = coordinatesToPos.get(i+":"+j);
          finalPos = coordinatesToPos.get((i-2)+":"+j);
          move(initialPos, finalPos, i, j, 'U');
          DFS();
          if(!gameOver) {
            unmove(initialPos, finalPos, i, j, 'U');
          }
        }
        if( ((j-2)>=0) && (board[i][j-1] == FILLED)
            && (board[i][j-2] == EMPTY) && board[i][j] == FILLED) {

          initialPos = coordinatesToPos.get(i+":"+j);
          finalPos = coordinatesToPos.get(i+":"+(j-2));
          move(initialPos, finalPos, i, j, 'L');
          DFS();
          if(!gameOver) {
            unmove(initialPos, finalPos, i, j, 'L');
          }
        }
        if ( ((i+2)<N) && (board[i+1][j] == FILLED)
            && (board[i+2][j] == EMPTY) && board[i][j] == FILLED)  {

          initialPos = coordinatesToPos.get(i+":"+j);
          finalPos = coordinatesToPos.get((i+2)+":"+j);
          move(initialPos, finalPos, i, j, 'D');
          DFS();
          if(!gameOver) {
            unmove(initialPos, finalPos, i, j, 'D');
          }
        } 
        if ( ((j+2)<N) && (board[i][j+1] == FILLED)
            && (board[i][j+2] == EMPTY) && board[i][j] == FILLED)  {

          initialPos = coordinatesToPos.get(i+":"+j);
          finalPos = coordinatesToPos.get(i+":"+(j+2));
          move(initialPos, finalPos, i, j, 'R');
          DFS();
          if(!gameOver) {
            unmove(initialPos, finalPos, i, j, 'R');  
          }
        }
      }
    }

    return;
  }

  /*
   *  Total amount of memory used in the Java virtual machine.
   */
  static double getMemUsed() {
    double used  = Runtime.getRuntime().totalMemory() - 
        Runtime.getRuntime().freeMemory();
    return (used/1024);
  }

  static String readFile(String path)  {
    String inputdata ="";
    
      try {
        FileInputStream fstream = new 
            FileInputStream(path);
        DataInputStream in = 
            new DataInputStream(fstream);

        while (in.available() !=0)  {
          inputdata+= in.readLine();
        }
        in.close();
      } 
      catch (Exception e) {
        System.err.println("File input error");
      }
    
      return inputdata;
  }


public static void main(String[] args) {
  long startTime = System.currentTimeMillis();
  
  String input = DFSolution.readFile(args[0]);
  DFSolution game = new DFSolution(7, input, args[1]);
  game.displayBoard();
  try {
    System.out.println("*****");
    System.out.println("Intermediate Stack");
    game.DFS();
    System.out.println("*****");
  }
  catch(Exception e){ e.printStackTrace(); }
  
  long endTime = System.currentTimeMillis();

  if(game.gameOver)  {
    System.out.println("\n***RESULT***\nGame Won!");
    System.out.println("Final Board State:");
    game.displayBoard();
    System.out.println("Winning sequence: "+game.winningMove+"\n");
  }
  else  {
    System.out.println("\n***RESULT***\nGame Lost!\nSolution doesn't exist");
  }

  System.out.println("***STATS***");
  System.out.println(endTime - startTime+" milliSeconds");
  System.out.println("Memory used: "+ DFSolution.getMemUsed()+
      " Kilobytes\nNodes visited: "+game.nodeVisited);
}
}
