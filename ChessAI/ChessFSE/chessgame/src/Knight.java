import java.util.ArrayList;

/**@Knight: A child class of Piece, overrides the Piece values with the specific child piece based info
//Such as the value of the name of the piece, x position, y position, color of the piece, point score of the piece and maxMoves
 */
public class Knight extends Piece {
    private int maxMoves = 1;

    /**
     * @param x         : the x coordinate for the piece
     * @param y         : the y coordinate for the piece
     * @param isWhite   : check the color of the piece
     * @param pieceName : the name of the piece
     * @param pieceVal  : point value of piece
     */
    public Knight(int x, int y, boolean isWhite, String pieceName, int pieceVal) {
        super(x, y, isWhite, pieceName, pieceVal);
    }

    public ArrayList<int[]> getValidMoves() {
        int[][] moveSet = {{1, 2}, {-1, 2}, {2, 1}, {-2, 1}, {1, -2}, {-1, -2}, {2, -1}, {-2, -1}}; // all possible moves for the knight pieces

        return ValidMovement.checkValidMovements(getxp(), getyp(), moveSet, maxMoves); // valid moves without accounting for checks
    }

}
