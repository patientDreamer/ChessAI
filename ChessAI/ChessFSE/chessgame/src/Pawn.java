import java.util.ArrayList;

/**@Pawn: A child class of Piece, overrides the Piece values with the specific child piece based info
//Such as the value of the name of the piece, x position, y position, color of the piece, point score of the piece and maxMoves
 */
public class Pawn extends Piece {

    private int[] enpassant;

    /**
     * @param x         : the x coordinate for the piece
     * @param y         : the y coordinate for the piece
     * @param isWhite   : check the color of the piece
     * @param pieceName : the name of the piece
     * @param pieceVal  : point value of piece
     */
    public Pawn(int x, int y, boolean isWhite, String pieceName, int pieceVal) {
        super(x, y, isWhite, pieceName, pieceVal);
    }

    public ArrayList<int[]> getValidMoves() {
        // if the pawn is not its starting row, then it must have moved
        boolean hasMoved = false;
        if (isWhite) hasMoved = getyp() != 6;
        else hasMoved = getyp() != 1;

        int maxMoves = hasMoved ? 1 : 2;  //initial move: at most 2 squares
        int direction = isWhite ? -1 : 1;  //direction of the movement based on the pawn's color
        ArrayList<int[]> validMoves = new ArrayList<>();  //arraylist of all the valid moves for the pawn

        for (int i = 1; i <= maxMoves; i++) {
            //iterate through and find all the possible valid moves, then add those valid moves into the validMoves arraylist
            if (ChessBoard.getBoardState(getxp(), getyp() + i * direction) == 0) {
                validMoves.add(new int[]{getxp(), getyp() + i * direction});
            } else {
                break;
            }
        }

        //check captures for the current pawn
        int leftPiece = ChessBoard.getBoardState(getxp() - 1, getyp() + direction);
        int rightPiece = ChessBoard.getBoardState(getxp() + 1, getyp() + direction);

        // if it is not null and not the same color then it is a valid capture
        if (Piece.pieceColors.get(leftPiece) != null && Piece.pieceColors.get(leftPiece) != isWhite) {
            validMoves.add(new int[]{getxp() - 1, getyp() + direction});
        }

        if (Piece.pieceColors.get(rightPiece) != null && Piece.pieceColors.get(rightPiece) != isWhite) {
            validMoves.add(new int[]{getxp() + 1, getyp() + direction});
        }

        //em passant movement
        if (ChessBoard.getEnpassant() != null) {
            enpassant = ChessBoard.getEnpassant();
        }

        if (enpassant != null && enpassant[0] == getxp() + 1 && enpassant[1] == getyp()) {
            validMoves.add(new int[]{getxp() + 1, getyp() + direction});
        }

        if (enpassant != null && enpassant[0] == getxp() - 1 && enpassant[1] == getyp()) {
            validMoves.add(new int[]{getxp() - 1, getyp() + direction});
        }

        return validMoves;
    }
}
