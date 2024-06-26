package Model;

import Controller.Piece;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {

    public static class Builder {
        private Point size;
        private Set<Point> tunnels;
        private Set<Point> raisedSquares;
        private Map<Point, Piece> pieces;

        public Builder(Point size) {
            this.size = size;
            tunnels = new HashSet<>();
            pieces = new HashMap<>();
            raisedSquares = new HashSet<>();
        }

        public Builder addTunnel(Point Loc) {
            tunnels.add(Loc);
            return this;
        }

        public Builder addRaisedSquare(Point Loc) {
            raisedSquares.add(Loc);
            return this;
        }

        public Builder addPieces(Point Loc, Piece piece) {
            pieces.put(Loc, piece);
            return this;
        }

        public Board build() {
            Square[][] board = new Square[size.y][size.x];
            for (int x = 0; x < size.y; x++) {
                for (int y = 0; y < size.x; y++) {
                    board[x][y] = null;
                }
            }
            for (Point point : tunnels) {
                board[point.y][point.x] = new Square(true, true, pieces.get(point));
                pieces.remove(point);
            }
            for (Point point : raisedSquares) {
                board[point.y][point.x] = new Square(false, true, pieces.get(point));
                pieces.remove(point);
            }
            for (Map.Entry<Point, Piece> entry : pieces.entrySet()) {
                board[entry.getKey().y][entry.getKey().x] = new Square(false, false, entry.getValue());
            }
            for (int x = 0; x < size.y; x++) {
                for (int y = 0; y < size.x; y++) {
                    if (board[x][y] == null) {
                        board[x][y] = new Square(false, false, null);
                    }
                }
            }
            return new Board(board);
        }
    }

    private Square[][] board;

    private Board(Square[][] board) {
        this.board = board;
    }

    public Square getSquare(Point loc) {
        return board[loc.y][loc.x];
    }

    public Point getSize(){
        return new Point(board[0].length, board.length);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Square[] squares : board) {
            for (Square square : squares) {
                stringBuilder.append(square.toString());
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
        // return Arrays.deepToString(board);
    }
}
