package helpers;

import model.Board;

import java.util.*;

public class Graph {
    private final Map<Board, Map<Move, List<Move>>> graph;
    private final Map<Board, Boolean> isVictories;

    private Graph(Map<Board, Map<Move, List<Move>>> graph, Map<Board, Boolean> isVictories) {
        Map<Board, Map<Move, List<Move>>> boards = new HashMap<>();
        Map<Move, List<Move>> moves;
        for (Map.Entry<Board, Map<Move, List<Move>>> move : graph.entrySet()) {
            moves = new HashMap<>();
            for (Map.Entry<Move, List<Move>> entry : move.getValue().entrySet()) {
                moves.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
            }
            boards.put(move.getKey(), Collections.unmodifiableMap(moves));
        }
        this.graph = Collections.unmodifiableMap(boards);
        this.isVictories = Collections.unmodifiableMap(isVictories);
    }

    boolean containsMove(Board board, Move move) {
        return getGraph().containsKey(board) && getGraph().get(board).containsKey(move);
    }

    List<Move> getMoves(Board board, Move move) {
        return getGraph().get(board).get(move);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Graph graph = (Graph) obj;
        return getGraph().equals(graph.getGraph());
    }

    private Map<Board, Map<Move, List<Move>>> getGraph() {
        return graph;
    }

    @Override
    public int hashCode() {
        return getGraph().hashCode();
    }

    public boolean IsVictory(Board board) {
        return isVictories.get(board);
    }

    static class Builder {
        private final Map<Board, Map<Move, List<Move>>> graph;
        private final Map<Board, Boolean> isVictories;

        Builder() {
            graph = new HashMap<>();
            isVictories = new HashMap<>();
        }

        Builder addMoves(Board board, Move move, List<Move> moves) {
            if (!graph.containsKey(board)) {
                graph.put(board, new HashMap<>());
            }
            graph.get(board).put(move, moves);
            return this;
        }

        Builder addIsVictory(Board board, boolean isVictory) {
            isVictories.put(board, isVictory);
            return this;
        }

        Graph build() {
            return new Graph(graph, isVictories);
        }
    }

    List<Move> BFS(Board start) {
        Queue<Board> queue = new LinkedList<>();
        Set<Board> expanded = new HashSet<>();
        expanded.add(start);
        Map<Board, List<Move>> listOfStepsPerBoard = new HashMap<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Board board = queue.poll();
            if (isVictories.get(board)) {
                return listOfStepsPerBoard.get(board);
            }
            for (List<Move> moves : graph.get(board).values()) {
                Board newBoard = new Board(board);
                newBoard.movePieces(moves);
                if (!expanded.contains(newBoard)) {
                    expanded.add(newBoard);
                    if (listOfStepsPerBoard.containsKey(board)) {
                        List<Move> newMoves = new ArrayList<>(listOfStepsPerBoard.get(board));
                        newMoves.addAll(moves);
                        if (!isVictories.containsKey(newBoard)) {
                            System.out.println(moves);
                            System.out.println(newBoard.toString());
                        }
                        listOfStepsPerBoard.put(newBoard, newMoves);
                    } else {
                        listOfStepsPerBoard.put(newBoard, moves);
                    }
                    queue.add(newBoard);
                }
            }
        }
        return null;
    }
}
