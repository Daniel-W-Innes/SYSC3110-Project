package helpers;

import model.Board;

import java.awt.*;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Graph {

    private final ArrayList<Move> solution;
    private final ArrayList<Board> intermediateBoards;
    private int solutionIndex;
    private Tree<Board> traversalPath;
    public Graph(Board startingBoard) {
        //Keep track of which nodes we visited
        Set<Board> visited = new HashSet<>();

        //Two queues, one for the current depth and another for the next one. This is so we can distinguish at which depth is the node at.
        Queue<TreeNode> currQueue = new ConcurrentLinkedQueue<>();
        Queue<TreeNode> nextQueue = new ConcurrentLinkedQueue<>();

        //TODO: This thing is running forever... (Frank Y.)
        int branchCount = 0;
        //Create a tree to track it's path
        traversalPath = new Tree<>(new TreeNode<>(startingBoard.cloneBoard()));
        currQueue.add(traversalPath.root); //Add starting root node

        outer:
        while (!currQueue.isEmpty()) {
            while (!currQueue.isEmpty()) {
                TreeNode<Board> currNode = currQueue.poll(); //Get node to expand

                if (currNode.contents.isVictory()) {
                    //System.out.println(start);
                    //System.out.println(start.getPieces());
                    System.out.println("Branch count: " + branchCount);
                    traversalPath.solution = currNode;
                    break outer;
                    //return new Level(ImmutableNetwork.copyOf(mutableNetwork), this.start);
                }
                //Stop traversing if you've already seen this node
                if (visited.contains(currNode.contents)) {
                    continue;
                } else {
                    //Mark as visited
                    visited.add(currNode.contents);
                }

                //For each piece on the board
                for (Piece piece : currNode.contents.getPieces().values()) {
                    //For every point the piece occupies
                    for (Point point : piece.boardSpotsUsed())
                        //For every move that the piece has
                        for (Move move : piece.getMoves(currNode.contents, point)) {

                            Board end = currNode.contents.cloneBoard();

                            end.movePiece(piece.clonePiece(), move.getEndPoint(), false);

                            //Get a Board object that represents the result of the move
                            //  Board end = new Board.Mover(currNode.contents).movePiece(move).build();

                            //Add each new Board state to the next queue to process
                            //TODO: remove this
                            branchCount++;

                            //Make a new node
                            TreeNode<Board> newNode = new TreeNode<>(end);
                            newNode.move = move; //Assign it the move that caused the board state
                            //Add it to the tree
                            currNode.addNode(newNode);
                            newNode.parent = currNode;

                            //Add it to the queue to expand
                            nextQueue.add(newNode);
                        }
                }
            }

            currQueue = nextQueue;
            nextQueue = new ConcurrentLinkedQueue<>();
        }

        solution = new ArrayList<>();
        intermediateBoards = new ArrayList<>();
        TreeNode<Board> currentNode = traversalPath.solution;

        while (currentNode != null) {
            solution.add(currentNode.move);
            intermediateBoards.add(currentNode.contents);
            currentNode = currentNode.parent;
        }

        solutionIndex = solution.size() - 2;
    }

    public Move getHintMove() {

        return solutionIndex == -1 ? null : solution.get(solutionIndex);
    }

    public Board getCurrentBoard() {
        return solutionIndex == -1 ? null : intermediateBoards.get(solutionIndex);
    }

    public Move getUndoMove() {
        if (solutionIndex == solution.size() - 2) {
            return null;
        }


        return solutionIndex == solution.size() - 2 ? null : solution.get(solutionIndex).getReverse();
    }

    public void advanceSolutionIndex() {
        if (solutionIndex != 0) {
            solutionIndex -= 1;
        }
    }

    public void backtrackSolutionIndex() {
        if (solutionIndex != solution.size() - 2) {
            solutionIndex += 1;
        }
    }

    public void changeSolution(int numberMoveRemove, ArrayList<Move> newMoves, ArrayList<Board> newBoards) {

        for (int i = numberMoveRemove + 1; i < newMoves.size(); i++) {
            solution.remove(i);
        }

        Collections.reverse(newMoves);
        Collections.reverse(newBoards);

        solution.addAll(newMoves);
        intermediateBoards.addAll(newBoards);
    }

    public int equivalentBoardIndex(Board board) {
        return intermediateBoards.indexOf(board);
    }

    //Tree that represents the BFS traversal of the graph.
    static class Tree<E> {
        final TreeNode<E> root;
        TreeNode<E> solution;

        Tree(TreeNode<E> root) {
            this.root = root;
        }
    }

    static class TreeNode<E> {
        final E contents;
        final Set<TreeNode<E>> children; //TODO: I don't need this, I only need to backtrack.
        Move move; //The move done for the parent to become this node
        TreeNode<E> parent; //Parent for backtracking

        TreeNode(E e) {
            this.contents = e;
            this.children = new HashSet<>();
        }

        void addNode(TreeNode<E> node) {
            this.children.add(node);
            node.parent = this;
        }
    }
}