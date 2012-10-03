import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class AStarSolution {

  private char [][]board;
  private static int N;

  private final char FILLED  = 'X';
  private final char EMPTY = '0';
  private final char UNUSABLE = '-';

  boolean traceVerbose;
  private boolean gameOver = false;
  private Stack <PushedData> moveStack;
  private HashMap<String, PushedData>old_visited;
  private Stack <String> winningMove;
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
    moveStack = new Stack<PushedData>();
    winningMove = new Stack<String>();
    nodeVisited = 0;
    mapCoordinates();

    old_visited = new HashMap<String, AStarSolution.PushedData>();
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
  
  final int LIMIT=4;
  
  /*
   * a helper class to keep track of Stack
   */
  private class PushedData implements Comparable<PushedData>{
    char [][]pushed_board;
    int depth;
    int cordinates_i;
    int cordinates_j;
    char direction;
    int distance;

    public PushedData(char [][]board, String Str, 
        char dir, int matrix_i, int matrix_j, int d) {

      pushed_board = new char[N][N];
      for(int i=0; i<N; i++)  {
        for(int j=0; j<N; j++)  {
          pushed_board[i][j] = board[i][j];
        }
      }
      cordinates_i = matrix_i;
      cordinates_j = matrix_j;
      direction = dir;
      depth = d;

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
    public int compareTo(PushedData o) {
      return  o.distance - distance;
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

  void AStar() {

    int initialPos, finalPos;
    int depth = 0;
    do 
    {
      ArrayList<PushedData>tempList = new ArrayList<AStarSolution.PushedData>();
      for(int i=0;i<N;i++)  {
        for(int j=0;j<N;j++)  {


          if ( ((j+2)<N) && (board[i][j+1] == FILLED)
              && (board[i][j+2] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j+2));

            PushedData temp = new PushedData(board, initialPos+"->"+finalPos,
                'R',i,j, depth);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);              
          }

          if ( ((i+2)<N) && (board[i+1][j] == FILLED)
              && (board[i+2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i+2)+":"+j);

            PushedData temp = new PushedData(board, initialPos+"->"+finalPos,
                'D',i,j, depth);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }

          if( ((j-2)>=0) && (board[i][j-1] == FILLED)
              && (board[i][j-2] == EMPTY) && board[i][j] == FILLED) {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get(i+":"+(j-2));

            PushedData temp = new PushedData(board, initialPos+"->"+finalPos,
                'L',i,j, depth);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }

          if( ((i-2)>=0) && (board[i-1][j] == FILLED) 
              && (board[i-2][j] == EMPTY) && board[i][j] == FILLED)  {

            initialPos = coordinatesToPos.get(i+":"+j);
            finalPos = coordinatesToPos.get((i-2)+":"+j);

            PushedData temp = new PushedData(board, initialPos+"->"+finalPos,
                'U',i,j, depth);
            if(!old_visited.containsKey(serializeBoard(temp.pushed_board)))
              tempList.add(temp);
          }
          Collections.sort(tempList);
          for(PushedData pushedData : tempList) {
            moveStack.push(pushedData);
          }
          tempList.clear();
        } // j for
      } // i for
      if(gameWon())  {
        gameOver = true;
        return;
      }
      old_visited.put(serializeBoard(board), null);
      
            /*
      for(PushedData pushedData : tempList) {
      System.out.println(pushedData.move+": "+pushedData.distance);
      }
      System.out.println("**");*/
      
      PushedData popped = moveStack.pop();

      //if(winningMove.size()!=0 && depth <= popped.depth)
      //  winningMove.pop();
      //winningMove.push(popped.move);
      copyBoard(popped);
      move(popped.cordinates_i, popped.cordinates_j, popped.direction);
      //System.out.println(popped.move);

      depth = popped.depth+1;
      tempList.clear();
      tempList.removeAll(tempList);
    } while(moveStack.size() !=  0);
    return;
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

  void copyBoard(PushedData data)  {
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
    try {
      System.out.println("*****");
      System.out.println("Intermediate Stack");
      game.AStar();
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
    System.out.println("Memory used: "+ AStarSolution.getMemUsed()+
        " bytes\nNodes visited: "+game.nodeVisited);
  }
}
