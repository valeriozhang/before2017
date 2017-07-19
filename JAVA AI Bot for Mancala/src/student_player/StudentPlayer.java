package student_player;

import java.util.ArrayList;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import bohnenspiel.BohnenspielMove.MoveType;
import student_player.mytools.MyTools;

/** A Hus player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super(""); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class
bohnenspiel.RandomPlayer
     * for another example agent. */
    public BohnenspielMove chooseMove(BohnenspielBoardState board_state)
    {
        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();

        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
        int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];

        // Use code stored in ``mytools`` package.
        MyTools.getSomething();

        // Get the legal Nodes for the current board state.
        ArrayList<BohnenspielMove> Nodes = board_state.getLegalMoves();
        
        thisPath thisNode = alphabetaminimax(board_state, null, 10, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        // But since this is a placeholder algorithm, we won't act on that information.
        return  (thisNode != null) ? thisNode.getMove() : Nodes.get(0);
    }
    
    public int myHeuristic(BohnenspielBoardState board_state, int mancalaPlayerID){
    	int theSeedsSowed = 0;
    	for (int j = 0; j < 6; j++){
    		theSeedsSowed = theSeedsSowed + board_state.getPits()[mancalaPlayerID][j]; 
    	}	
    	return theSeedsSowed;
    }
    
    public thisPath alphabetaminimax (BohnenspielBoardState board_state, BohnenspielMove thisNode, int depth, int alpha, int beta, boolean maximizingPlayer) {
    	ArrayList<BohnenspielMove> totalNodes  = board_state.getLegalMoves();
    	if (depth == 0 || totalNodes.isEmpty()) {
    		thisPath vPrime = new thisPath();
    		vPrime.setMove(thisNode);
    		vPrime.setHeuristic((int)Math.floor((myHeuristic(board_state,player_id) - myHeuristic(board_state,opponent_id))/2+(board_state.getScore(player_id) - board_state.getScore(opponent_id))));
    		return vPrime;
    	} 
    	else if (maximizingPlayer) {
    		int v = (int)Double.NEGATIVE_INFINITY;
    		thisPath vBest = null;
    		for (BohnenspielMove childNodes : totalNodes) {
    			BohnenspielBoardState child = (BohnenspielBoardState) board_state.clone();
    	        child.move(childNodes);
    	        thisPath vPrime = alphabetaminimax(child, childNodes, depth - 1, alpha, beta, false);
    			alpha = Math.max(alpha, vPrime.getHOfN());
    			if (vPrime.getHOfN() > v) {
    				v = vPrime.getHOfN();
    				vBest = new thisPath();
    				vBest.setMove(childNodes);
    				vBest.setHeuristic(v);
    			}
    			else if (beta <= alpha) {
    				break;
    			}
    		}
    		return vBest;
    	} else {
    		int v = (int)Double.POSITIVE_INFINITY;
    		thisPath vBest = null;
    		for (BohnenspielMove childNodes : totalNodes) {
    			BohnenspielBoardState child = (BohnenspielBoardState) board_state.clone();
    	        child.move(childNodes);
    			thisPath vPrime = alphabetaminimax(child, childNodes, (depth - 1), alpha, beta, true);
    			beta = Math.min(beta, vPrime.getHOfN());
    			if (vPrime.getHOfN() < v) {
    				v = vPrime.getHOfN();
    				vBest = new thisPath();
    				vBest.setMove(childNodes);
    				vBest.setHeuristic(v);
    			}
    			else if (beta <= alpha) {
    				break;
    			}
    		}
    		return vBest;
    	}
    }
}

class thisPath {
	private BohnenspielMove thisNode; 
	private int hOfN;
	
	public BohnenspielMove getMove() { 
		return thisNode; 
		}
	
    public int getHOfN() { 
    	return hOfN; 
    	}
     
    public void setMove(BohnenspielMove newMove) { 
    	this.thisNode = newMove; 
    	}
    
    public void setHeuristic(int hOfN) { 
    	this.hOfN = hOfN;
    	}
 	
}
