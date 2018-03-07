package mazes.generators.maze;

import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.graphs.Graph;

import java.util.Random;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.
        Random rand = new Random();
        ISet<Room> rooms = maze.getRooms();
        ISet<Wall> walls = maze.getWalls();
        
        for (Wall wall:walls) {
            wall.setDistance(wall.getDistance() * rand.nextInt(100000));
        }
        
        Graph<Room, Wall> mazeGraph = new Graph<Room, Wall>(rooms, walls);
        return mazeGraph.findMinimumSpanningTree();
    }
}
