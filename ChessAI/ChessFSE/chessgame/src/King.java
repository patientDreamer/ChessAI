import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**@King: A child class of Piece, overrides the Piece values with the specific child piece based info
//Such as the value of the name of the piece, x position, y position, color of the piece, point score of the piece and maxMoves
 */
public class King extends Piece {

    private boolean hasMoved = false; // if the king has moved or not (use for castle move)

    /**
     * @param x         : the x coordinate for the piece
     * @param y         : the y coordinate for the piece
     * @param isWhite   : check the color of the piece
     * @param pieceName : the name of the piece
     * @param pieceVal  : point value of piece
     */
    public King(int x, int y, boolean isWhite, String pieceName, int pieceVal) {
        super(x, y, isWhite, pieceName, pieceVal);
    }

    public ArrayList<int[]> getValidMoves() {
        int[][] moveSet = new int[][]{
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {-1, 1},
                {1, -1},
                {-1, -1}
        }; //the move set of the piece

        ArrayList<int[]> validMoves = ValidMovement.checkValidMovements(this.getxp(), this.getyp(), moveSet, 1); //get the valid moves of the king piece

        //check for valid castle movement
        if (canCastle()[0]) validMoves.add(new int[]{getxp() + 2, getyp()});
        if (canCastle()[1]) validMoves.add(new int[]{getxp() - 2, getyp()});

        return validMoves;
    }


    public boolean[] canCastle() {
        // innocent until proven guilty
        boolean shortCastle = true;
        boolean longCastle = true;

        // find the rooks
        for (Piece p : Piece.getPieces()) {
            //find the same color rook
            if (p.isWhite == isWhite && p.name.equals("rook")) {
                assert (p instanceof Rook);
                // if they have moved set to corresponding castle type to false
                if (p.getxp() < getxp() && ((Rook) p).getMoved()) longCastle = false;
                if (p.getxp() > getxp() && ((Rook) p).getMoved()) shortCastle = false;
            }
        }

        if (hasMoved) return new boolean[]{false, false}; //if the king has moved

        // make sure the tiles in between the rook and the king are empty
        if (ChessBoard.getBoardState(getxp() - 1, getyp()) != 0 || ChessBoard.getBoardState(getxp() - 2, getyp()) != 0 || ChessBoard.getBoardState(getxp() - 3, getyp()) != 0)
            longCastle = false;
        if (ChessBoard.getBoardState(getxp() + 1, getyp()) != 0 || ChessBoard.getBoardState(getxp() + 2, getyp()) != 0)
            shortCastle = false;

        return new boolean[]{shortCastle, longCastle};
    }

    /**
     * This means the king has moved and cannot castle
     */
    public void hasMoved() {
        hasMoved = true;
    }

    // find the specified color king
    public static King getKing(boolean white) {
        return (King) getPieces().stream().filter(piece -> (piece instanceof King) && (white == piece.isWhite)).collect(Collectors.toCollection(ArrayList::new)).get(0);
    }

    /**
     * Check if the specified color king is in check
     * @param kingIsWhite  : the color of the king
     * @param piecesAlive  : all the pieces (Piece objects) that are currently on the board
     * @return true if the king is in check
     */
    public boolean inCheck(ArrayList<Piece> piecesAlive, boolean kingIsWhite) {
        ArrayList<Piece> opponentPieces = new ArrayList<>();
        HashSet<int[]> badTiles = new HashSet<>();

        // Get all opponent pieces
        for (Piece piece : piecesAlive) {
            if (piece.isWhite != kingIsWhite && !(piece instanceof King)) {
                opponentPieces.add(piece);
            }
        }

        // Get all bad tiles from each opponent piece's valid moves
        for (Piece opponent : opponentPieces) {
            ArrayList<int[]> tempMoves = opponent.getValidMoves();
            badTiles.addAll(tempMoves);
        }

        //current position for the king
        int kingX = this.getxp();
        int kingY = this.getyp();

        // Check if any bad tile is in the king's valid moves or if king's current tile is under CHECK by the opponent
        for (int[] bt : badTiles) {
            if (Arrays.equals(bt, new int[]{kingX, kingY})) {
                return true;
            }
        }
        return false; // King is not in check
    }

}
