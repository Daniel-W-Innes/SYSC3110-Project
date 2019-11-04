package helpers;

import model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Rabbits implements Piece  {

    public static final String imageIconLocation = "./resources/Rabbit_white.png";

    private Point boardSpot;
    private static final ImageIcon icon = new ImageIcon(imageIconLocation);

    public Rabbits(Point boardSpot) {
        this.boardSpot = boardSpot;
    }

    @Override
    public void updateBoardSpotUsed(Point newLocation) {
        boardSpot = new Point(newLocation);
    }

    @Override
    public Set<Point> boardSpotsUsed() {
        return Set.of(this.boardSpot);
    }

    @Override
    public List<Move> getMoves(Board board, Point clickedPoint) {
        List<Move> possibleMoves = new ArrayList<>();
        // Possible moves could exist in any of the four directions depending on the obstacles of the board
        // Thus all directions have to be checked.
        possibleMoves.addAll(getMoveDirection(board, boardSpot, new Point(1, 0)));
        possibleMoves.addAll(getMoveDirection(board, boardSpot, new Point(-1, 0)));
        possibleMoves.addAll(getMoveDirection(board, boardSpot, new Point(0, 1)));
        possibleMoves.addAll(getMoveDirection(board, boardSpot, new Point(0, -1)));

        return possibleMoves;
    }

    private List<Move> getMoveDirection(Board board, Point start, Point direction) {

        /*
             To find possible moves for the rabbits, search the given direction for obstacles. While
             there are obstacles, continue searching the next square in the given direction. Once no more
             obstacles are found, then the end point for a move has been found.
         */

        Point startingPointCopy = new Point(start);
        ArrayList<Move> possibleMoves = new ArrayList<>();
        boolean objectToJump = true;
        while(objectToJump){
            startingPointCopy.x += direction.x;
            startingPointCopy.y += direction.y;
            objectToJump = board.hasPiece(startingPointCopy);
        }

        // If there are no pieces in the immediate square adjacent to the rabbit in the given rabbit, there are no moves
        if(start.equals(new Point(startingPointCopy.x - direction.x, startingPointCopy.y - direction.y))) {
            return possibleMoves;
        }

        // Make sure that the possible move is within valid coordinates of the game. The '-1' seen is because
        // the coordinates star at 0, not 1.
        if (startingPointCopy.x >= 0 && startingPointCopy.x <= Board.maxBoardLength.x - 1 &&
                startingPointCopy.y >= 0 && startingPointCopy.y <= Board.maxBoardLength.y - 1) {

                possibleMoves.add(new Move(new Point(start), new Point(startingPointCopy)));
                return possibleMoves;
        }

        return possibleMoves;
    }

    @Override
    public ImageIcon getImageIcon(Point p) {
        return Rabbits.icon;
    }
}