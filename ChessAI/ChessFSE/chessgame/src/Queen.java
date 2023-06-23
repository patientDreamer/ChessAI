import java.util.ArrayList;

/**@Queen: A child class of Piece, overrides the Piece values with the specific child piece based info
Such as the value of the name of the piece, x position, y position, color of the piece, point score of the piece and maxMoves
 */
public class Queen extends Piece {
    /**
     * @param x,y: the x and y coordinate for the piece
     * @param isWhite   : check the color of the piece
     * @param pieceName: the piece name
     * @param pieceVal  : point value of piece
     * @Queen: constructor for this piece class
     */
    public Queen(int x, int y, boolean isWhite, String pieceName, int pieceVal) {
        super(x, y, isWhite, pieceName, pieceVal);
    }

    public ArrayList<int[]> getValidMoves(){
        int[][] moveSet = new int[][]{
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {-1, 1},
                {1, -1},
                {-1, -1}
        }; //move set for queen pieces

        return ValidMovement.checkValidMovements(this.getxp(), this.getyp(), moveSet, 7);

    }
}
