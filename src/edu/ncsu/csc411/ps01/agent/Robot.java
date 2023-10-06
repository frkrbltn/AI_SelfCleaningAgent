package edu.ncsu.csc411.ps01.agent;

import edu.ncsu.csc411.ps01.environment.*;

import java.util.*;

/**
Represents a simple-reflex agent cleaning a particular room.	
The robot only has one sensor - the ability to retrieve the 
the status of all its neighboring tiles, including itself.
*/
public class Robot {
	
    private Environment env;
    // Queue to keep track of visited positions
    private Queue<Position> visitQueue;
    // Store the last move the robot made.
    private Action lastMove = null;
    // Store the last tile the robot visited.
    private Tile lastVisitedTile = null;
    // Count how many times the robot stays on the same tile
    private int sameTileCount = 0;

    /** Initializes a Robot on a specific tile in the environment. */
    public Robot(Environment env) {
        this.env = env;
        this.visitQueue = new LinkedList<>();
        visitQueue.add(env.getRobotPosition(this));
    }

    /**
    Problem Set 01 - Modify the getAction method below in order to
    simulate the passage of a single time-step. At each time-step, the Robot 
    decides whether to clean the current tile or move tiles.
    
    Your task for this Problem Set is to modify the method below such that
    the Robot agent is able to clean at least 70% of the available tiles on
    a given Environment. 5 out of the 10 graded test cases, with explanations
    on how to create new Environments, are available under the test package.
    
    This method should return a single Action from the Action class.
    	- Action.CLEAN
    	- Action.DO_NOTHING
    	- Action.MOVE_UP
    	- Action.MOVE_DOWN
    	- Action.MOVE_LEFT
    	- Action.MOVE_RIGHT
     */
    
    /**
     * 
     * Determines the action the robot should take: clean, move in a direction,
     * or do nothing. This method utilizes a reactive algorithm to decide on the next
     * action for the robot.
     * 
     * Algorithm Breakdown:
     * 1. Check the current tile: If it's dirty, clean it.
     * 2. If the current tile is clean:
     *    a. Check neighboring tiles with the following moves: Right, Down, Left, Up.(from lecture asignment 1)
     *    b. Move to the first neighboring tile that is dirty.
     * 3. If no dirty neighboring tiles are found:
     *    a. Avoid revisiting the same tile consecutively more than twice
     *    b. If on the same tile too many times, reset the counter.
     *    c. Avoid going back immediately to the last visited tile.
     *    d. Try to move in a direction, non-blocked tiles.
     * 4. If no valid moves are found, do nothing.
     * 
     * robot remains reactive to its environment,cleaning dirty tiles within exploring further.
     * 
     * Next step adding breadFirstSearch method with node and graphs
     * @return The action the robot should take.
     */
    public Action getAction() {
        Map<String, Tile> around = env.getNeighborTiles(this);
        Tile currentTile = around.get("self");

        // If on the same tile again, add to the count
        if (currentTile.equals(lastVisitedTile)) {
            sameTileCount++;
        } else {
            sameTileCount = 0;
        }

        lastVisitedTile = currentTile;

        if (currentTile.getStatus() == TileStatus.DIRTY) return Action.CLEAN;

        List<Action> moves = new ArrayList<>(Arrays.asList(
            Action.MOVE_RIGHT, Action.MOVE_DOWN, Action.MOVE_LEFT, Action.MOVE_UP
        ));

        // Check around for dirty tiles
        for (Action move : moves) {
            Tile nextTo = getTileFromMove(around, move);
            if (nextTo != null && nextTo.getStatus() == TileStatus.DIRTY) {
                lastMove = move;
                return move;
            }
        }

        // If on the same tile too many times, reset count
        if (sameTileCount > 2) {
            sameTileCount = 0;
        } else {
            // Avoid going back
            moves.remove(goBack(lastMove));
        }

        // Try to move, but avoid blocked tiles
        for (Action move : moves) {
            Tile nextTo = getTileFromMove(around, move);
            if (nextTo != null && nextTo.getStatus() != TileStatus.IMPASSABLE) {
                lastMove = move;
                return move;
            }
        }

        return Action.DO_NOTHING;
    }
    
    /**
     * Helper method to fetch the tile corresponding to a move.
     * 
     * @param around Map of neighboring tiles.
     * @param move The move direction.
     * @return The tile corresponding to the move.
     */
    private Tile getTileFromMove(Map<String, Tile> around, Action move) {
        if (move == Action.MOVE_UP) return around.get("above");
        if (move == Action.MOVE_DOWN) return around.get("below");
        if (move == Action.MOVE_LEFT) return around.get("left");
        if (move == Action.MOVE_RIGHT) return around.get("right");
        return null;
    }
    
    
    /**
     * Helper method to get the opposite direction of a given move.
     * 
     * @param move The move direction.
     * @return The opposite direction of the given move.
     */
    private Action goBack(Action move) {
        if (move == Action.MOVE_UP) return Action.MOVE_DOWN;
        if (move == Action.MOVE_DOWN) return Action.MOVE_UP;
        if (move == Action.MOVE_LEFT) return Action.MOVE_RIGHT;
        if (move == Action.MOVE_RIGHT) return Action.MOVE_LEFT;
        return null;
    }
    
    @Override
	public String toString() {
		return "Robot [neighbors=" + env.getNeighborTiles(this) + "]";
	}
}
