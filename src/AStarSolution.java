import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * AStarSolution is the main class
 * 1. It contains board matrix and its size (that is 7 here)
 * 2. It has variables that denote the following state of a pegholder
 *    filled
 *    empty
 *    unusable
 *    
 * 3. It has a stack (moveStack) that stores the game state in form of a class object (PushedGameState)
 *    This stack is used to push/pop (i.e. save/restore) the game state.
 *    This stack helps us to do a dfs (with heuristics ofcourse..) in iteration.
 * 
 * 4. We also have a hashmap coordinatestoPos that converts x and y co-ordinates to 
 *    their respective Peg holder number.
 * It maps "3:3" string to 16 as 3,3 box in 7*7 matrix points to 16th peg holder
 * 
 * HEURISTIC: 
 *   In N*N matrix; for each peg there are at max 4 move possible (UP, DOWN, RIGHT, LEFT)
 *   we consider the move that is nearest (in terms of Manhatten distance) to the goal peg 16
 *   
 *   Instead of choosing the Branch in Search tree in LIFO order; we prioritize the moves according
 *   to distance from goal cell
 *   
 *   STORE STATE:
 *   furthermore; we store the state that previously has been visited in Search tree; This state is
 *   stored as board matrix serialized in form of String. If we have found that board config before it means
 *   that the search was unsuccessful and we should not add that move 
 * 
 */
public class AStarSolution {

  private char [][]board;
  private static int N;

  private final char FILLED  = 'X';
  private final char EMPTY = '0';
  private final char UNUSABLE = '-';

  boolean traceVerbose;
  private boolean gameOver = false;
  private Stack <PushedGameState> moveStack;
  private HashMap<String, PushedGameState>stored_state;
  
  private int nodeVisited;
  private Map<String, Integer> coordinatesToPos;

  /*
   * This constructor 
   *  1. Parses the input string and fills Board data structure
   *  2. Initializes moveStack and set Mapping of coordinates to Position Hashmap
   */
  public AStarSolution(int n, String input, String verbose) {
    N = n;
    board = new char[N][N];
    coordinatesToPos = new HashMap<String, Integer>();

    traceVerbose = Boolean.parseBoolean(verbose);
    moveStack = new Stack<PushedGameState>();
    nodeVisited = 0;
    mapCoordinates();

    stored_state = new HashMap<String, AStarSolution.PushedGameState>();
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
   * Calculates L1 norm distance (manhatten distance) between current position 
   * and winning goal position i.e.(3,3 in 7*7 matrix)
   * e.g 1,6 has ==> abs(3-1)+ abs(3-6) = 5 manhatten distance from goal
   */
  static int calculateL1Norm(int i, int j)  {
    return(Math.abs(N/2 - i) + Math.abs(N/2 - j));
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
   *  
   *  It also has a distance; which calculates the Manhatten distance of 
   *  current peg from peg 16 i.e. cell 3,3 of matrix using 
   *  calculateL1Norm distance function
   */
  private class PushedGameState implements Comparable<PushedGameState>{
    char [][]pushed_board;
    String move;
    int cordinates_i;
    int cordinates_j;
    char direction;
    int distance;
    String winningMove;

    public PushedGameState(char [][]board, String Str, 
        char dir, int matrix_i, int matrix_j, String setofMoves) {

      pushed_board = new char[N][N];
      for(int i=0; i<N; i++)  {
        for(int j=0; j<N; j++)  {
          pushed_board[i][j] = board[i][j];
        }
      }
      
      move = Str;
      cordinates_i = matrix_i;
      cordinates_j = matrix_j;
      direction = dir;
      winningMove = setofMoves;

      int final_i =0;
      int final_j =0;

      if(direction == 'U') {
        final_i = matrix_i-2; 
        final_j = matrix_j;
      }
      else if(direction == 'D') {
        final_i = matrix_i+2;
        final_j = matrix_j;
      }
      else if(direction == 'L') {
        final_i = matrix_i;
        final_j = matrix_j-2;
      }
      else if(direction == 'R') {
        final_i = matrix_i;
        final_j = matrix_j+2;
      }

      distance = AStarSolution.calculateL1Norm(final_i, final_j);
    }

    @Override
    public int compareTo(PushedGameState o) {
      return  o.distance - distance;
    }
  }
  
  /*
   * this call starts the AStar
   * 1. it iterates in N*N matrix over each peg (as cell in the Board)
   *   a. looks if a move Up for that peg is possible
   *   b. if Yes; we store the move in Stack and push Game State (board, move, set of Moves executed so Far) etc. 
   *   looks if a move Down is possible and again stores the state in Stack
   *      ...and so on for Left and Right
   *   
   *   HEURISTIC: 
   *   Now we SORT these 4 moves according to Manhatten distance from peg 16
   *   that is; for each peg there are at max 4 move possible (UP, DOWN, RIGHT, LEFT)
   *   we consider the move that is nearest (in terms of Manhatten distance) to the goal peg 16
   *   
   *   Instead of choosing the Branch in Search tree in LIFO order; we prioritize the moves according
   *   to distance from goal cell
   *   
   *   STORE STATE:
   *   furthermore; we store the state that previously has been visited in Search tree; This state is
   *   stored as board matrix serialized in form of String. If we have found that board config before it means
   *   that the search was unsuccessful and we should not add that move 
   *      
   * 2. After control comes out of loop; we have all the moves possible for current board configuration
   *  in stack.
   * 3. we pop the first move in stack and execute it;
   * 4. also we add that move to String in which we keep track of moves executed so far 
   * 5. then we repeat the process till stack finishes or we run out of moves (gameOver)
   */

  String AStar() {

    String setofMoves ="";
    int initialPos, finalPos;
    do 
    {
      ArrayList<PushedGameState>tempList = new ArrayList<AStarSolution.PushedGameState>();
      for(int i=0;i<N;i++)  {
        for(int j=0;j<N;j++)  {


          if ( ((j+2)<N) && (board[i][j+1] == FILLED)
              && (board[i][j+2] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j+2));

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'R',i,j, setofMoves);
            // check if this board config has already been visited in Stored State map
            if(!stored_state.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);              
          }

          if ( ((i+2)<N) && (board[i+1][j] == FILLED)
              && (board[i+2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i+2)+":"+j);

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'D',i,j, setofMoves);
            // check if this board config has already been visited in Stored State map
            if(!stored_state.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }

          if( ((j-2)>=0) && (board[i][j-1] == FILLED)
              && (board[i][j-2] == EMPTY) && board[i][j] == FILLED) {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j-2));

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'L',i,j, setofMoves);
           // check if this board config has already been visited in Stored State map
            if(!stored_state.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }

          if( ((i-2)>=0) && (board[i-1][j] == FILLED) 
              && (board[i-2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i-2)+":"+j);

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'U',i,j, setofMoves);
            // check if this board config has already been visited in Stored State map
            if(!stored_state.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }
          
          /*Sort the 4 moves according to distance
            and store in Stack
          */
          Collections.sort(tempList);
          for(PushedGameState PushedGameState : tempList) {
            moveStack.push(PushedGameState);
          }
          
          tempList.clear();
        } // j for
      } // i for
      if(gameWon())  {
        gameOver = true;
        return setofMoves;
      }
      // since this is new board state; hash it 
      stored_state.put(serializeBoard(board), null);

      PushedGameState popped = moveStack.pop();
      setofMoves = popped.winningMove;       
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
      tempList.clear();
      tempList.removeAll(tempList);
    } while(moveStack.size() !=  0);
    return setofMoves;
  }
  
  /*Serializes Board Matrix into a single string*/
  String serializeBoard(char[][] board)  {
    String str = "";
    for(int i=0;i<N;i++)  {
      for(int j=0;j<N;j++)  {
        str += board[i][j];
      }
    }
    return str;
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
      System.err.println("usage: AStarSolution <filePath> <verbose boolean>");
    }
    String input = AStarSolution.readFile(args[0]);
    long startTime = System.currentTimeMillis();
    AStarSolution game = new AStarSolution(7, input, args[1]);
    game.displayBoard();
    String winningMove ="";
    
    try {
      System.out.println("*****");
      System.out.println("Intermediate States");
      winningMove = game.AStar();
      System.out.println("*****");
    }
    catch(Exception e){ e.printStackTrace(); }
    long endTime = System.currentTimeMillis();

    if(game.gameOver)  {
      System.out.println("\n***RESULT***\nGame Won!");
      System.out.println("Final Board State:");
      game.displayBoard();
      System.out.println("Winning sequence: "+winningMove+"\n");
    }
    else  {
      System.out.println("\n***RESULT***\nGame Lost!\nSolution doesn't exist");
    }

    System.out.println("***STATS***");
    System.out.println(endTime - startTime+" milliSeconds");
    System.out.println("Memory used: "+ AStarSolution.getMemUsed()+
        " bytes\nNodes visited: "+game.nodeVisited);
  }
}
