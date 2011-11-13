package org.encog.ml.world.grid;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.EncogMath;
import org.encog.ml.world.Action;
import org.encog.ml.world.ActionProbability;
import org.encog.ml.world.WorldAgent;
import org.encog.ml.world.basic.BasicAction;
import org.encog.ml.world.basic.BasicWorld;

public class GridWorld extends BasicWorld {
	
	public static final Action ACTION_NORTH = new BasicAction("NORTH");
	public static final Action ACTION_SOUTH = new BasicAction("SOUTH");
	public static final Action ACTION_EAST = new BasicAction("EAST");
	public static final Action ACTION_WEST = new BasicAction("WEST");
	
	private GridState[][] state;
	private ActionProbability actionEvaluation;
	
	public GridWorld(int rows, int columns) {
		addAction(ACTION_NORTH);
		addAction(ACTION_SOUTH);
		addAction(ACTION_EAST);
		addAction(ACTION_WEST);
		this.state = new GridState[rows][columns];
		
		for(int row = 0; row<rows; row++) {
			for(int col = 0; col<columns; col++) {
				this.state[row][col] = new GridState(this, row, col, false);
				this.state[row][col].setPolicyValueSize(getActions().size());
			}
		}
		
	}
	
	public static boolean isStateBlocked(GridState state) {
		if( state==null || state.isBlocked())
			return true;
		else
			return false;
	}
	
	public int getRows() {
		return this.state.length;
	}
	
	public int getColumns() {
		return this.state[0].length;
	}
	
	public GridState getState(int row, int column) {
		if( row<0 || row>=getRows() ) {
			return null;
		} else if( column<0 || column>=getColumns() ) {
			return null;
		}
		return this.state[row][column];
	}
	
	public static Action leftOfAction(Action action) {
		if( action==GridWorld.ACTION_NORTH) {
			return GridWorld.ACTION_WEST;
		} else if( action==GridWorld.ACTION_SOUTH) {
			return GridWorld.ACTION_EAST;
		} else if( action==GridWorld.ACTION_EAST) {
			return GridWorld.ACTION_NORTH;
		}else if( action==GridWorld.ACTION_WEST) {
			return GridWorld.ACTION_SOUTH;
		}
		return null;
	}
	
	public static Action rightOfAction(Action action) {
		if( action==GridWorld.ACTION_NORTH) {
			return GridWorld.ACTION_EAST;
		} else if( action==GridWorld.ACTION_SOUTH) {
			return GridWorld.ACTION_WEST;
		} else if( action==GridWorld.ACTION_EAST) {
			return GridWorld.ACTION_SOUTH;
		}else if( action==GridWorld.ACTION_WEST) {
			return GridWorld.ACTION_NORTH;
		}
		return null;
	}
	
	public static Action reverseOfAction(Action action) {
		if( action==GridWorld.ACTION_NORTH) {
			return GridWorld.ACTION_SOUTH;
		} else if( action==GridWorld.ACTION_SOUTH) {
			return GridWorld.ACTION_NORTH;
		} else if( action==GridWorld.ACTION_EAST) {
			return GridWorld.ACTION_WEST;
		}else if( action==GridWorld.ACTION_WEST) {
			return GridWorld.ACTION_EAST;
		}
		return null;
	}

	public List<GridState> getAdjacentStates(GridState s) {
		List<GridState> result = new ArrayList<GridState>();
		GridState northState = this.getState(s.getRow()-1, s.getColumn());
		GridState southState = this.getState(s.getRow()+1, s.getColumn());
		GridState eastState = this.getState(s.getRow(), s.getColumn()+1);
		GridState westState = this.getState(s.getRow(), s.getColumn()-1);
		
		if( !isStateBlocked(northState) ) {
			result.add(northState);
		}
		
		if( !isStateBlocked(southState) ) {
			result.add(southState);
		}
		
		if( !isStateBlocked(eastState) ) {
			result.add(eastState);
		}
		
		if( !isStateBlocked(westState) ) {
			result.add(westState);
		}
		
		if( !isStateBlocked(s) ) {
			result.add(s);
		}
		
		return result;
	}
	
	public static double euclideanDistance(GridState s1, GridState s2) {
		double d = EncogMath.square(s1.getRow()-s2.getRow())+EncogMath.square(s1.getColumn()-s2.getColumn());
		return Math.sqrt(d);
	}

	public GridState findClosestStateTo(List<GridState> states, GridState goalState) {
		double min = Double.POSITIVE_INFINITY;
		GridState minState = null;
		
		for(GridState state : states) {
			double d = euclideanDistance(state,goalState);
			if( d<min ) {
				min = d;
				minState = state;
			}
		}
		
		return minState;
	}

	public Action determineActionToState(GridState currentState,
			GridState targetState) {
		int rowDiff = currentState.getRow() - targetState.getRow();
		int colDiff = currentState.getColumn() - targetState.getColumn();
		
		if( rowDiff==0 && colDiff==0 )
			return null;
		
		if( Math.abs(rowDiff)>= Math.abs(colDiff) ) {
			if( rowDiff<0) 
				return GridWorld.ACTION_SOUTH;
			else
				return GridWorld.ACTION_NORTH;
		} else {
			if( colDiff<0) 
				return GridWorld.ACTION_EAST;
			else
				return GridWorld.ACTION_WEST;
		}
	}

	public void tick() {
		for(WorldAgent agent: getAgents()) {
			agent.tick();
		}
	}
}