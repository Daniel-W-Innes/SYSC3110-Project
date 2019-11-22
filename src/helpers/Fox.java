package helpers;

import model.Board;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static controller.Game.resourcesFolder;

/**
 * Class to represent a Fox of the game.
 */

public class Fox implements Piece {

    static final String vertexHeadImageLocation = resourcesFolder + File.separator + "pieces" + File.separator + "fox" + File.separator + "Fox_down.png";
    static final String verticalTailImageLocation = resourcesFolder + File.separator + "pieces" + File.separator + "fox" + File.separator + "Fox_up.png";
    static final String horizontalHeadImageLocation = resourcesFolder + File.separator + "pieces" + File.separator + "fox" + File.separator + "Fox_right.png";
    static final String horizontalTailImageLocation = resourcesFolder + File.separator + "pieces" + File.separator + "fox" + File.separator + "Fox_left.png";

    private final ImageIcon headIcon;
    private final ImageIcon tailIcon;

    private final Direction direction;
    private Point headLocation;
    private Point tailLocation;
    private Set<Point> occupiedBoardSpots;

    /**
     * Constructor that initializes the Fox to the given location and direction.
     *
     * @param direction    The direction the fox faces
     * @param headLocation The location of the head of the fox
     */

    public Fox(Direction direction, Point headLocation) {
        /* Functionally, it does not matter the order of the fox's tail and head, as it can move both directions along its axis.
         * Because of this, and due to the images available, the tail of the fox is either:
         *
         *  > To the left of the head, if the direction of the fox is along the x-axis
         *  > To the bottom of the head (with the origin being the top-left corner. If the origin was the bottom left, then the
         *    tail would be to the top of the head).
         *
         * Due to this tail placement, if the head was along the first row or column, the index of the tail would be -1 along the respective
         * axis, which is not a valid index.
         */
        if (Direction.X_AXIS == direction) {
            headIcon = new ImageIcon(vertexHeadImageLocation);
            tailIcon = new ImageIcon(verticalTailImageLocation);
            if (0 == headLocation.x) {
                throw new IllegalArgumentException("The head of the fox must not be along the first column!");
            }
        } else {
            headIcon = new ImageIcon(horizontalHeadImageLocation);
            tailIcon = new ImageIcon(horizontalTailImageLocation);
            if (0 == headLocation.y) {
                throw new IllegalArgumentException("The head of the fox must not be along the first row!");
            }
        }
        this.direction = direction;
        this.headLocation = headLocation;
        tailLocation = calculateTailLocation();
        occupiedBoardSpots = Set.of(headLocation, tailLocation);
    }

    /**
     * Constructor that initializes the Fox to a copy of a fox.
     *
     * @param fox The fox to copy
     */
    private Fox(Fox fox) {
        direction = fox.direction;
        headLocation = fox.headLocation;
        headIcon = fox.headIcon;
        tailLocation = fox.tailLocation;
        tailIcon = fox.tailIcon;
        occupiedBoardSpots = Set.copyOf(fox.occupiedBoardSpots);
    }

    private Fox(Proto.Fox fox) {
        this(new Point(fox.getHeadLocation()).x == new Point(fox.getTailLocation()).x ? Direction.Y_AXIS : Direction.X_AXIS, new Point(fox.getHeadLocation()));
    }


    public static Map<Point, Fox> fromListOfProtos(Collection<Proto.Fox> foxes) {
        return foxes.stream()
                .flatMap(foxProto -> {
                    Fox fox = new Fox(foxProto);
                    return Stream.of(Map.entry(foxProto.getHeadLocation(), fox), Map.entry(foxProto.getTailLocation(), fox));
                })
                .collect(Collectors.toMap(entry -> new Point(entry.getKey()), Map.Entry::getValue));
    }

    public Proto.Fox toProto() {
        return Proto.Fox.newBuilder()
                .setHeadLocation(headLocation.toProto())
                .setTailLocation(tailLocation.toProto())
                .build();
    }

    /**
     * Returns the String representation of the fox.
     *
     * @return String showing a textual representation of the fox
     */

    @Override
    public String toString() {
        return "F";
    }

    /**
     * Determines if two fox objects are either the same object or are logically equivalent.
     *
     * @param obj the object to compare
     * @return true if the two are the same
     */

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (null == obj || obj.getClass() != getClass()) {
            return false;
        }
        Fox fox = (Fox) obj;
        return direction.equals(fox.direction) && headLocation.equals(fox.headLocation);
    }

    /**
     * The hash code for the fox object.
     *
     * @return the hashcode for this fox object.
     */

    @Override
    public int hashCode() {
        return occupiedBoardSpots.hashCode() ^ toString().hashCode();
    }

    /**
     * Get a new equivalent copy of this fox. The new object is a separate object.
     *
     * @return a copy of this fox object
     */

    @Override
    public Piece clonePiece() {
        return new Fox(this);
    }

    /**
     * Update the internal variables holding the location of the Fox
     *
     * @param newLocation the new location of the head of the fox
     */

    @Override
    public void updateBoardSpotUsed(Point newLocation) {
        headLocation = new Point(newLocation);
        tailLocation = calculateTailLocation();
        occupiedBoardSpots = Set.of(newLocation, tailLocation);
    }

    /**
     * Get all of the board spots used by the fox piece.
     *
     * @return points taken up by the fox piece
     */

    @Override
    public Set<Point> boardSpotsUsed() {
        return occupiedBoardSpots;
    }

    /**
     * Get the location of the head of the fox.
     *
     * @return the point representing the location of the fox head.
     */

    public Point getHeadLocation() {
        return headLocation;
    }

    /**
     * Get the location of the tail of the fox.
     *
     * @return the point representing the location of the fox tail.
     */

    public Direction getDirection() {
        return direction;
    }

    /**
     * Find the possible moves the fox can take given the current state of the board.
     *
     * @param board        the model of the board
     * @param clickedPoint the point the user click in the BoardPanel
     * @return list of moves that the fox can take
     */

    @Override
    public List<Move> getMoves(Board board, Point clickedPoint) {
        if (clickedPoint.equals(tailLocation)) {
            return new ArrayList<>();
        }
        Point headPointCopy = new Point(headLocation);
        List<Move> possibleMoves = new ArrayList<>();
        /*
            To find valid moves for the fox, a loop is done in both directions along the fox's direction.
            For example, if the fox is facing positive X, and the head is located at (3, 0), then the squares
             (4, 0) are checked for pieces and squares. While there are no obstacles, a valid move is found and stored.

            Continuing with the above example, when searching in the other direction, to the negative X, the search
            begins from the tail. Thus the squares (1, 0) and (0, 0) are checked in that order, as the tail is located
            at (2, 0). For each move that is found, however, the move must be translated so that it is relative to the head.
            Thus, if the square (1, 0) is a valid move, then the move would contain an end point of (2, 0), as this would
            result in the tail being at (1, 0) and the head at (2, 0), which was found to be clear of any pieces and squares.
         */
        switch (direction) {
            case X_AXIS -> {
                headPointCopy = headPointCopy.moveX(1);
                while (headPointCopy.x != board.maxBoardSize.x) {
                    if (board.hasPiece(headPointCopy) || board.hasSquare(headPointCopy)) {
                        break;
                    }
                    possibleMoves.add(new Move(new Point(headLocation), new Point(headPointCopy)));
                    headPointCopy = headPointCopy.moveX(1);
                }
                headPointCopy = new Point(tailLocation);
                headPointCopy = headPointCopy.moveX(-1);
                while (0 <= headPointCopy.x) {
                    if (board.hasPiece(headPointCopy) || board.hasSquare(headPointCopy)) {
                        break;
                    }
                    // Note the translation so that the move is relative to the head
                    headPointCopy = headPointCopy.moveX(1);
                    possibleMoves.add(new Move(new Point(headLocation), new Point(headPointCopy)));
                    // To continue looping starting at the next square to the left, not only does a '-1' have to be done,
                    // but so does another '-1' to account for the translation made earlier
                    headPointCopy = headPointCopy.moveX(-2);
                }
            }
// Same ideas a X_AXIS
            case Y_AXIS -> {
                headPointCopy = headPointCopy.moveY(1);
                while (headPointCopy.y != board.maxBoardSize.y) {
                    if (board.hasPiece(headPointCopy) || board.hasSquare(headPointCopy)) {
                        break;
                    }
                    possibleMoves.add(new Move(new Point(headLocation), new Point(headPointCopy)));
                    headPointCopy = headPointCopy.moveY(1);
                }
                headPointCopy = new Point(tailLocation);
                headPointCopy = headPointCopy.moveY(-1);
                while (0 <= headPointCopy.y) {
                    if (board.hasPiece(headPointCopy) || board.hasSquare(headPointCopy)) {
                        break;
                    }
                    headPointCopy = headPointCopy.moveY(1);
                    possibleMoves.add(new Move(new Point(headLocation), new Point(headPointCopy)));
                    headPointCopy = headPointCopy.moveY(-2);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Get the texture that should be drawn to represent the fox at a given point.
     *
     * @param location the location specifying where the fox texture needs to be drawn
     * @return ImageIcon that holds the texture needed to be drawn
     */

    @Override
    public ImageIcon getImageIcon(Point location) {
        if (headLocation.equals(location)) {
            return headIcon;
        } else if (tailLocation.equals(location)) {
            return tailIcon;
        } else {
            throw new IllegalStateException("Illegal fox state");
        }
    }

    /**
     * Calculate the location of the fox tail given the positions of its head and the direction of the fox.
     *
     * @return the point representing the location of the fox tail
     */

    private Point calculateTailLocation() {
        switch (direction) {
            case X_AXIS -> {
                return new Point(headLocation.x - 1, headLocation.y);
            }
            case Y_AXIS -> {
                return new Point(headLocation.x, headLocation.y - 1);
            }
            default -> throw new IllegalStateException("Illegal fox state");
        }
    }
}
