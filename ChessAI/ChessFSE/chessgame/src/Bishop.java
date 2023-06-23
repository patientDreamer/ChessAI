import java.util.ArrayList;

/**@Bishop: A child class of Piece, overrides the Piece values with the specific child piece based info
//Such as the value of the name of the piece, x position, y position, color of the piece, point score of the piece and maxMoves
 */
public class Bishop extends Piece {
    private int maxMoves = 7;

    /**
     * @param x: the x and y coordinate for the piece
     * @param y
     * @param isWhite   : check the color of the piece
     * @param pieceName
     * @param pointVal : point val of piece
     * @Bishop constructor for this piece class
     */
    public Bishop(int x, int y, boolean isWhite, String pieceName, int pointVal) {
        super(x, y, isWhite, pieceName, pointVal);
    }

    public ArrayList<int[]> getValidMoves() {
        int[][] moveSet = new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}}; //the move set of the bishop pieces

        return ValidMovement.checkValidMovements(getxp(), getyp(), moveSet, 7);
    }
}