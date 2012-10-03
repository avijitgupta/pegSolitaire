import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AStarSolution {

  private char [][]board;
  private static int N;

  private final char FILLED  = 'X';
  private final char EMPTY = '0';
  private final char UNUSABLE = '-';

  boolean traceVerbose;
  private boolean gameOver = false;
  private Stack <PushedGameState> moveStack;
  private HashMap<String, PushedGameState>old_visited;
  
  private int nodeVisited;
  private Map<String, Integer> coordinatesToPos;
  
  /*
   * Parse input string in Board data structure
   */
  public AStarSolution(int n, String input, String verbose) {
    N = n;
    board = new char[N][N];
    coordinatesToPos = new HashMap<String, Integer>();

    traceVerbose = Boolean.parseBoolean(verbose);
    moveStack = new Stack<PushedGameState>();
    nodeVisited = 0;
    mapCoordinates();

    old_visited = new HashMap<String, AStarSolution.PushedGameState>();
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
   * a helper class to keep track of Stack
   */
  private class PushedGameState implements Comparable<PushedGameState>{
    char [][]pushed_board;
    String move;
    int cordinates_i;
    int cordinates_j;
    char direction;
    int distance;
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

      distance = DFSIterative.calculateL1Norm(final_i, final_j);
    }

    @Override
    public int compareTo(PushedGameState o) {
      return  o.distance - this.distance;
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
   * Move from initialPos to finalPos 
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

  String AStar() {

    int initialPos, finalPos;
    String setofMoves = "";
    
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
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);              
          }

          if ( ((i+2)<N) && (board[i+1][j] == FILLED)
              && (board[i+2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i+2)+":"+j);

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'D',i,j, setofMoves);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }

          if( ((j-2)>=0) && (board[i][j-1] == FILLED)
              && (board[i][j-2] == EMPTY) && board[i][j] == FILLED) {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j-2));

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'L',i,j, setofMoves);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }

          if( ((i-2)>=0) && (board[i-1][j] == FILLED) 
              && (board[i-2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i-2)+":"+j);

            PushedGameState temp = new PushedGameState(board, initialPos+"->"+finalPos,
                'U',i,j, setofMoves);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }
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
      old_visited.put(serializeBoard(board), null);
      
      PushedGameState popped = moveStack.pop();
      setofMoves = popped.winningMoves;
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
  
  String serializeBoard(char[][] board)  {
    String str = "";
    for(int i=0;i<N;i++)  {
      for(int j=0;j<N;j++)  {
        str += board[i][j];
      }
    }
    return str;
  }

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
   
    String input = AStarSolution.readFile(args[0]);
    long startTime = System.currentTimeMillis();
    AStarSolution game = new AStarSolution(7, input, args[1]);
    game.displayBoard();
    String winningSequence ="";
    
    try {
      System.out.println("*****");
      System.out.println("Intermediate Stack");
      winningSequence = game.AStar();
      System.out.println("*****");
    }
    catch(Exception e){ e.printStackTrace(); }
    long endTime = System.currentTimeMillis();

    if(game.gameOver)  {
      System.out.println("\n***RESULT***\nGame Won!");
      System.out.println("Final Board State:");
      game.displayBoard();
      System.out.println("Winning sequence: "+winningSequence+"\n");
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
