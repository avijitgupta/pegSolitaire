import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * DFSIterative is the main class
 * 1. It contains board matrix and its size (that is 7 here)
 * 2. It has variables that denote the following state of a pegholder
 *    filled
 *    empty
 *    unusable
 *    
 * 3. It has a stack (moveStack) that stores the game state in form of a class object (PushedGameState)
 *    This stack is used to push/pop (i.e. save/restore) the game state.
 *    This stack helps us to do a dfs in iteration.
 * 
 * 4. We also have a hashmap coordinatestoPos that converts x and y co-ordinates to 
 *    their respective Peg holder number.
 * It maps "3:3" string to 16 as 3,3 box in 7*7 matrix points to 16th peg holder
 */
public class DFSIterative {

  private char [][]board;
  private static int N;

  private final char FILLED  = 'X';
  private final char EMPTY = '0';
  private final char UNUSABLE = '-';

  boolean traceVerbose;
  private boolean gameOver = false;
  private Stack <PushedGameState> moveStack;

  private int nodeVisited;
  private Map<String, Integer> coordinatesToPos;


  /*
   * This constructor 
   *  1. Parses the input string and fills Board data structure
   *  2. Initializes moveStack and set Mapping of coordinates to Position Hashmap
   */
  public DFSIterative(int n, String input, String verbose) {
    N = n;
    board = new char[N][N];

    coordinatesToPos = new HashMap<String, Integer>();
    traceVerbose = Boolean.parseBoolean(verbose);
    moveStack = new Stack<PushedGameState>();
    nodeVisited = 0;

    mapCoordinates();

    int check = 0;
    int counter = 0;
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
   * This function executes a move X->Y in a board
   * where X and Y are peg holders
   * 
   * input is co-ordinates of box (as i,j) and direction (d)
   * e.g. 3,3 in direction 'U' executes an Up move 
   * for pegholder 16 (represented as 3,3 in 7*7 matrix) to pegholder 4
   * (16->4)
   */
  void move(int i, int j, char direction) {
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
    nodeVisited++;
  }
  
  /*
   * A helper class to keep track of Game State
   * We have a stack of PushedGameState type of object.
   * It has following data structure:
   *  -- winningMoves: set of moves that have been executed
   *  -- pushed_board: the board state after "winningMoves" have been executed
   *  -- move: the next move to be executed
   *  -- direction: direction of next Move (Up, Down, Left, Right)
   */
  private class PushedGameState {
    char [][]pushed_board;
    String move;
    int cordinates_i;
    int cordinates_j;
    char direction;
    String winningMoves;

    public PushedGameState(char [][]board, String Str, 
        char dir, int matrix_i, int matrix_j, String setOfMoves) {

      pushed_board = new char[N][N];
      for(int i=0; i<N; i++)  {
        for(int j=0; j<N; j++)  {
          pushed_board[i][j] = board[i][j];
        }
      }
      cordinates_i = matrix_i;
      cordinates_j = matrix_j;
      direction = dir;
      move = Str;
      winningMoves = setOfMoves;
      }
  }

  /*
   * this call starts the DFS
   * 1. it iterates in N*N matrix over each peg (as cell in the Board)
   *   a. looks if a move Up for that peg is possible
   *   b. if Yes; we store the move in Stack and push Game State (board, move, set of Moves executed so Far) etc. 
   *   looks if a move Down is possible and again stores the state in Stack
   *      ...and so on for Left and Right
   *      
   * 2. After control comes out of loop; we have all the moves possible for curent board configuration
   *  in stack.
   * 3. we pop the first move in stack and execute it;
   * 4. also we add that move to String in which we keep track of moves executed so far 
   * 5. then we repeat the process till stack finishes or we run out of moves (gameOver)
   */
  String DFS() {

    int initialPos, finalPos;
    String setofMoves = "";
    
    do 
    {
      ArrayList<PushedGameState>tempList = new ArrayList<PushedGameState>();
      tempList.clear();
      for(int i=0;i<N;i++)  {
        for(int j=0;j<N;j++)  {
          
          // Add Right move to stack
          if ( ((j+2)<N) && (board[i][j+1] == FILLED)
              && (board[i][j+2] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j+2));

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'R',i,j, setofMoves);
            tempList.add(temp);
          }
          // Add Down move to stack
          if ( ((i+2)<N) && (board[i+1][j] == FILLED)
              && (board[i+2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i+2)+":"+j);

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'D',i,j, setofMoves);
            tempList.add(temp);
          }
          // Add Left move to stack
          if( ((j-2)>=0) && (board[i][j-1] == FILLED)
              && (board[i][j-2] == EMPTY) && board[i][j] == FILLED) {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j-2));

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'L',i,j, setofMoves);
            tempList.add(temp);
          }
          // Add Up move to stack
          if( ((i-2)>=0) && (board[i-1][j] == FILLED) 
              && (board[i-2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i-2)+":"+j);

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'U',i,j, setofMoves);
            tempList.add(temp);
          } 

        } // j for
      } // i for
      
      if(gameWon())  {
        gameOver = true;
        return setofMoves;
      }
      
      for(PushedGameState PushedGameState : tempList) {
        moveStack.push(PushedGameState);
      }
      // pop first state
      PushedGameState popped = moveStack.pop();
      setofMoves = popped.winningMoves;       
      // copy stored board config to current board
      copyBoard(popped);
      move(popped.cordinates_i, popped.cordinates_j, popped.direction);
    
      if(setofMoves =="")
        setofMoves += popped.move;
      else
        setofMoves += ", "+popped.move;
      if(traceVerbose)  {
        System.out.println("Next Move: "+ popped.move);
        System.out.println("Move so far: "+setofMoves);  
      }
    } while(moveStack.size() !=  0);
    return setofMoves;
  }

  /*copies stores board state to current board (kind of restoration process)*/
  void copyBoard(PushedGameState data)  {
    for(int i=0;i<N;i++)  {
      for(int j=0;j<N;j++)  {
        board[i][j] = data.pushed_board[i][j];
      }
    }
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

    if(args.length!=2)  {
      System.err.println("usage: DFSIterative <filePath> <verbose boolean>");
    }
    String input = DFSIterative.readFile(args[0]);
    long startTime = System.currentTimeMillis();
    DFSIterative game = new DFSIterative(7, input, args[1]);
    game.displayBoard();
    String str ="";

    try {
      System.out.println("*****");
      System.out.println("Intermediate Trace");
      str = game.DFS();
      System.out.println(str);
      System.out.println("*****");
    }
    catch(Exception e){ e.printStackTrace(); }
    long endTime = System.currentTimeMillis();

    if(game.gameOver)  {
      System.out.println("\n***RESULT***\nGame Won!");
      System.out.println("Final Board State:");
      game.displayBoard();
      System.out.println("Winning sequence: "+str+"\n");
    }
    else  {
      System.out.println("\n***RESULT***\nGame Lost!\nSolution doesn't exist");
    }

    System.out.println("***STATS***");
    System.out.println(endTime - startTime+" milliSeconds");
    System.out.println("Memory used: "+ DFSIterative.getMemUsed()+
        " bytes\nNodes visited: "+game.nodeVisited);
  }
}
