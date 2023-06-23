import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Piece: Super Class for all the piece type
 */
public class Piece {

    public static final HashMap<Integer, Boolean> pieceColors = new HashMap<>();

    public final String name;
    private int xp, yp;  //piece's x & y coordinate (a tile index from 0-7)

    public final int pieceValue; //value of the piece
    private int x, y;
    public final boolean isWhite; //the boolean to determine th color of the piece
    private static final ArrayList<Piece> ps = new ArrayList<>(); //an array list of all the pieces

    /**
     * @param x        ,y: the x and y coordinate for the piece
     * @param isWhite: check the color of the piece
     * @Pieces: constructor for this piece class
     */
    public Piece(int x, int y, boolean isWhite, String pieceName, int pointVal) {
        this.xp = x;
        this.yp = y;
        this.x = xp * GamePanel.HEIGHT * 80 / 100;  //convert the x
        this.y = yp * GamePanel.HEIGHT * 80 / 100;  //convert the y
        this.isWhite = isWhite; //piece color
        this.name = pieceName;  //piece name
        this.pieceValue = pointVal; //value of the piece (points)
        Piece.ps.add(this); //add the piece object into the arraylist
    }

    /**
     * @move: method for moving piece
     * @remove: boolean for whether or not to kill piece, use for generating move simulations
     */
    public void move(int xp, int yp, boolean remove) {
        final int boardSize = GamePanel.HEIGHT * 80 / 100; //the size of the board
        Piece killPiece = null; //piece being captured

        for (Piece p : ps) {
            if (p.xp == xp && p.yp == yp) killPiece = p; // kill the piece at the destination, if any
        }

        if (killPiece != null && remove) killPiece.kill();

        //update the piece's x and y position
        this.xp = xp;
        this.yp = yp;
        this.x = xp * boardSize / 8 + (GamePanel.WIDTH - boardSize) / 2;
        this.y = yp * boardSize / 8 + (GamePanel.HEIGHT - boardSize) / 2;
    }

    /**
     * @kill: method used for delete the chess piece
     */
    public void kill() {
        ps.remove(this);
    }


    //Getter methods
    public ArrayList<int[]> getValidMoves() {
        return null;
    }

    public static ArrayList<Piece> getPieces() { return ps; }

    public int getxp() {
        return xp;
    }

    public int getyp() {
        return yp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "Name: " + name + "\nIsWhite: " + isWhite + "\nX Pos: " + xp + "\nY Pos: " + yp + "\nPoints: " + pieceValue;
    }
}
