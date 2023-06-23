import java.util.ArrayList;

/**@Rook: A child class of Piece, overrides the Piece values with the specific child piece based info
Such as the value of the name of the piece, x position, y position, color of the piece, point score of the piece and maxMoves
 */
public class Rook extends Piece{

    private boolean hasMoved = false; //boolean to keep on track the first move (use for castle check)

    /**
     * @param x         ,y: the x and y coordinate for the piece
     * @param y
     * @param isWhite   : check the color of the piece
     * @param pieceName
     * @param pointVal  : point value of piece
     * @Rook: constructor for this piece class
     */
    public Rook(int x, int y, boolean isWhite, String pieceName, int pointVal){
        super(x, y, isWhite, pieceName, pointVal);
    }

    public ArrayList<int[]> getValidMoves(){
        int[][] moveSet = {
                {1,0},
                {-1,0},
                {0,1},
                {0,-1}
        };  //move set for rook pieces

        return ValidMovement.checkValidMovements(this.getxp(), this.getyp(), moveSet, 7);
    }

    public void hasMoved() {
        hasMoved = true;
    }

    public boolean getMoved() {
        return hasMoved;
    }

}
