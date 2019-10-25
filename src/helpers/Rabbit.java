package helpers;

import model.Board;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rabbit implements Piece {

    private Map<Move, List<Move>> getMoves(Board board, Point start, Point offset) {
        boolean c = true;
        Map<Move, List<Move>> moves = new HashMap<>();
        Move move;
        Point point = new Point(start);
        while (c) {
            point = new Point(point.x + offset.x, point.y + offset.y);
            c = board.hasPiece(point);
        }
        if (!start.equals(new Point(point.x - offset.x, point.y - offset.y)) && point.y <= board.getMax().y && point.x <= board.getMax().x && point.y >= 0 && point.x >= 0) {
            move = new Move(start, point);
            moves.put(move, List.of(move));
        }
        return moves;
    }

    @Override
    public Map<Move, List<Move>> getMoves(Board board, Point start) {
        Map<Move, List<Move>> moves = new HashMap<>();
        moves.putAll(getMoves(board, start, new Point(0, 1)));
        moves.putAll(getMoves(board, start, new Point(0, -1)));
        moves.putAll(getMoves(board, start, new Point(1, 0)));
        moves.putAll(getMoves(board, start, new Point(-1, 0)));
        return moves;
    }

    @Override
    public String toString() {
        return "R";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }
}