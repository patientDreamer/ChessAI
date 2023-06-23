import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class ChessBoard {
    // sound effect object
    private final SoundEffects sfx;
    // dictionary used to store images of pieces for drawing
    private final HashMap<Integer, Image> imagesDictonary;
    // size of the board, always 80% of the screen height
    private final static int boardSize = GamePanel.HEIGHT * 80 / 100;
    // x, y coordinates of the left and top sides board respectively
    private final static int[] boardPosition = {(GamePanel.WIDTH - boardSize) / 2, (GamePanel.HEIGHT - boardSize) / 2};
    // check for promotion for each side
    private boolean whitePromotion, blackPromotion;
    // stores the winner of each game
    private String winner;
    // current move number, the move enpassant is valid, amount of points each side has
    private int moveNum = 1, enpassantMove = 0, whitePoints = 0, blackPoints = 0;
    // coordinates of the pawn that can be taken in enpassant
    private static int[] enpassant;
    // currently selected piece
    private Piece selected;
    // used to store the valid moves for the currently selected piece
    private ArrayList<int[]> validMoves = new ArrayList<>();
    // if in AIMode or not
    private boolean AIMODE;
    // random object for ai moves
    private final Random rand = new Random();
    /*
     * board for keeping track of what pieces are where
     *
     * Key:
     * 1 = rook (black)
     * 3 = knight (black)
     * 5 = bishop (black)
     * 7 = queen (black)
     * 9 = king (black)
     * 11 = pawn (black)
     * ----------------------
     * 2 = rook (white)
     * 4 = knight (white)
     * 6 = bishop (white)
     * 8 = queen (white)
     * 10 = king (white)
     * 12 = pawn (white)
     */
    private static int[][] board = {
            {1, 3, 5, 7, 9, 5, 3, 1},
            {11, 11, 11, 11, 11, 11, 11, 11},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {12, 12, 12, 12, 12, 12, 12, 12},
            {2, 4, 6, 8, 10, 6, 4, 2},
    }; //the grid of the board

    // image directory for chess pieces
    private String[] DEFAULT_PIECES = {
            "src/Images/DEFAULT_PIECES/bR.png",
            "src/Images/DEFAULT_PIECES/wR.png",
            "src/Images/DEFAULT_PIECES/bN.png",
            "src/Images/DEFAULT_PIECES/wN.png",
            "src/Images/DEFAULT_PIECES/bB.png",
            "src/Images/DEFAULT_PIECES/wB.png",
            "src/Images/DEFAULT_PIECES/bQ.png",
            "src/Images/DEFAULT_PIECES/wQ.png",
            "src/Images/DEFAULT_PIECES/bK.png",
            "src/Images/DEFAULT_PIECES/wK.png",
            "src/Images/DEFAULT_PIECES/bP.png",
            "src/Images/DEFAULT_PIECES/wP.png",
    };


    /**
     * @ChessBoard: constructor of the board
     * @param AIMode : whether the current mode is player vs player or player vs AI*/
    public ChessBoard(boolean AIMode) {
        sfx = new SoundEffects();  //instantiate the SoundEffect object for sound effect
        imagesDictonary = new HashMap<>();

        this.AIMODE = AIMode;

        for (int i = 0; i < DEFAULT_PIECES.length; i++) {
            //add all the Image objects to the hashmap with the corresponding encoding value for each piece
            Image img = new ImageIcon(DEFAULT_PIECES[i]).getImage();
            imagesDictonary.put(i + 1, img.getScaledInstance(boardSize / 8, boardSize / 8, Image.SCALE_SMOOTH));
            Piece.pieceColors.put(i + 1, i % 2 == 1);
        }

        setPieces(); //instantiate the piece objects
    }

    private void setPieces() {
        //instantiate black pieces
        new Rook(0, 0, false, "rook", 5);
        new Knight(1, 0, false, "knight", 3);
        new Bishop(2, 0, false, "bishop", 3);
        new Queen(3, 0, false, "queen", 9);
        new King(4, 0, false, "king", 999);
        new Bishop(5, 0, false, "bishop", 3);
        new Knight(6, 0, false, "knight", 3);
        new Rook(7, 0, false, "rook", 5);
        for (int i = 0; i < 8; i++) {
            new Pawn(i, 1, false, "pawn", 1);
        }

        //instantiate white pieces
        new Rook(0, 7, true, "rook", 5);
        new Knight(1, 7, true, "knight", 3);
        new Bishop(2, 7, true, "bishop", 3);
        new Queen(3, 7, true, "queen", 9);
        new King(4, 7, true, "king", 999);
        new Bishop(5, 7, true, "bishop", 3);
        new Knight(6, 7, true, "knight", 3);
        new Rook(7, 7, true, "rook", 5);
        for (int i = 0; i < 8; i++) {
            new Pawn(i, 6, true, "pawn", 1);
        }
    }

    /**
     * Get the piece clicked on
     *
     * @param x x coordinate of cursor
     * @param y y coordinate of cursor
     */
    public void selectPiece(int x, int y) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (whitePromotion || blackPromotion) return;

        if (AIMODE && moveNum % 2 == 0) return; // AI is always black

        int xp = (x - boardPosition[0]) / (boardSize / 8);
        int yp = (y - boardPosition[1]) / (boardSize / 8);

        if (!inBoard(xp, yp)) return; // make sure position is in board

        if (getBoardState(xp, yp) != 0) {
            for (Piece p : Piece.getPieces()) {
                //iterate through the list, compare the x and y positions of the piece
                if (p.getxp() == xp && p.getyp() == yp) {
                    if (p.isWhite && moveNum % 2 != 1) return; // %2 == 0 for white player's turn
                    if (!p.isWhite && moveNum % 2 != 0) return;

                    selected = p; //current selected piece
                    validMoves = p.getValidMoves();

                    // make sure that none of the valid moves result in your king being checked
                    validMoves = getTrueValidMoves(selected);

                    return;
                }
                selected = null;
            }
        }

    }

    /**
     * Get piece from given position
     * @param xp x position
     * @param yp y position
     * @return Piece object if there is one, else null if the tile is empty
     */
    public Piece getPieceFromPos(int xp, int yp) {
        for (Piece p : Piece.getPieces()) {
            if (p.getxp() == xp && p.getyp() == yp) {
                return p;
            }
        }
        return null;
    }

    /**
     * Check if any of the valid moves from this piece are invalid by putting the king in check
     *
     * @param pieceToCheck piece to check
     * @return the true valid moves of the piece
     */
    public ArrayList<int[]> getTrueValidMoves(Piece pieceToCheck) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        int tempX = pieceToCheck.getxp(), tempY = pieceToCheck.getyp(); //pre-simulate coordinate for the selected piece
        ArrayList<int[]> newValidMoves = new ArrayList<>();

        //Simulate valid moves to ensure that the move will not put the king in check
        for (int[] move : pieceToCheck.getValidMoves()) {
            // simulate the move
            pieceToCheck.move(move[0], move[1], false);
            int destinationBoardState = getBoardState(move[0], move[1]);  // store the original state of the move tile to revert to
            setBoardState(move[0], move[1], getBoardState(tempX, tempY), true);
            int originalBoardState = getBoardState(tempX, tempY);
            setBoardState(tempX, tempY, 0, true);
            if (!King.getKing(pieceToCheck.isWhite).inCheck(Piece.getPieces(), pieceToCheck.isWhite)) {
                //if the king is not in check after this valid movement, then it's a legal move
                newValidMoves.add(move);
            }

            // undo the move
            setBoardState(move[0], move[1], destinationBoardState, true);
            setBoardState(tempX, tempY, originalBoardState, true);
        }

        pieceToCheck.move(tempX, tempY, false);
        return newValidMoves;
    }

    /**
     * Check if one side in a checkmate position
     *
     * @param white the color of the pieces to check for
     * @return if there are no valid moves and the king is in check
     */
    public boolean isCheckmate(boolean white) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        ArrayList<int[]> allValidMoves = new ArrayList<>();

        for (Piece p : Piece.getPieces()) {
            if (white == p.isWhite) allValidMoves.addAll(getTrueValidMoves(p));
        }

        return allValidMoves.isEmpty() && King.getKing(white).inCheck(Piece.getPieces(), white);
    }

    /**
     * Check if one side in a stalemate position
     *
     * @param white the color of the pieces to check for
     * @return if there are no valid moves and the king is not in check
     */
    public boolean isStalemate(boolean white) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        ArrayList<int[]> allValidMoves = new ArrayList<>();

        for (Piece p : Piece.getPieces()) {
            if (white == p.isWhite) allValidMoves.addAll(getTrueValidMoves(p));
        }

        return allValidMoves.isEmpty() && !King.getKing(white).inCheck(Piece.getPieces(), white);
    }

    /**
     * Move a piece to the specified coordinates on the board
     * @param x mouseX
     * @param y mouseY
     */
    public void movePiece(int x, int y) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (selected == null) return; // if no piece is selected, return
        // find the position numbers (0,0) is top left corner
        int xp = (x - boardPosition[0]) / (boardSize / 8);
        int yp = (y - boardPosition[1]) / (boardSize / 8);

        // if position is not on the board, return
        if (!inBoard(xp, yp)) return;

        // removeIf returns true if anything has been removed,
        // and if nothing gets removed then that means the clicked
        // position is not one of the valid moves
        if (!validMoves.removeIf(move -> move[0] == xp && move[1] == yp)) {
            selected = null;
            return;
        }

        // if the tile is empty or has a piece of the opposite color
        if (getBoardState(xp, yp) == 0 || selected.isWhite != getPieceFromPos(xp, yp).isWhite) {
            int points = 0; // keep track of the points acquired this move, default none

            // find the piece at the destination position
            if (getPieceFromPos(xp, yp) != null) {
                points = getPieceFromPos(xp, yp).pieceValue;
            }

            int direction = selected.isWhite ? -1 : 1; // get the direction that the piece is heading (for pawns only)

            // if a pawn just is making 2-step move, then en passant is valid and stores the position of the move
            if (selected.name.equals("pawn") && yp == selected.getyp() + 2 * direction) {
                enpassant = new int[]{xp, yp};
                enpassantMove = moveNum + 1; // only valid for one move, which is enpassantmove
            }

            // if the move is valid and the player decides to do it then handle it accordingly
            if (enpassantMove == moveNum && selected.name.equals("pawn") && enpassant[0] == xp && enpassant[1] == yp - direction) {
                getPieceFromPos(enpassant[0], enpassant[1]).kill(); // remove from pieces list to avoid a ghost piece
                setBoardState(enpassant[0], enpassant[1], 0, false);
                points = 1;
                enpassant = null;
                enpassantMove = -1;
            }

            // make enpassant invalid if the turn limit has passed
            if (enpassantMove < moveNum) {
                enpassant = null;
                enpassantMove = -1;
            }

            //Promotion for pawns
            if (selected.name.equals("pawn")) {
                if (selected.isWhite && yp == 0) {
                    whitePromotion = true;
                    //white pawn
                }
                if (!selected.isWhite && yp == 7) {
                    blackPromotion = true;
                    //black pawn
                }
            }

            if (selected.name.equals("king")) {
                assert (selected instanceof King);

                ((King) selected).hasMoved(); // king has moved

                // castling right
                if (xp == selected.getxp() + 2) {
                    // find the right rook and update its coordinates
                    for (Piece p : Piece.getPieces()) {
                        if (p.getxp() == selected.getxp() + 3 && p.getyp() == yp) p.move(xp - 1, yp, true);
                    }
                    // move rook to its new spot
                    setBoardState(xp - 1, yp, getBoardState(selected.getxp() + 3, yp), false);
                    setBoardState(selected.getxp() + 3, yp, 0, false);
                }

                // castling left
                if (xp == selected.getxp() - 2) {
                    // find the left rook and update its coordinates
                    for (Piece p : Piece.getPieces()) {
                        if (p.getxp() == selected.getxp() - 4 && p.getyp() == yp) p.move(xp + 1, yp, false);
                    }
                    //move rook to its new spot
                    setBoardState(xp + 1, yp, getBoardState(selected.getxp() - 4, yp), false);
                    setBoardState(selected.getxp() - 4, yp, 0, false);
                }
            }

            if (selected.name.equals("rook")) {
                assert (selected instanceof Rook);

                ((Rook) selected).hasMoved(); // rook has moved (for checking if castling is valid)
            }


            // sets the piece to the new position
            setBoardState(xp, yp, getBoardState(selected.getxp(), selected.getyp()), false);
            // sets the origin tile of the piece to empty
            setBoardState(selected.getxp(), selected.getyp(), 0, false);

            selected.move(xp, yp, true); // update the coordinates for the pieces

            if (selected.isWhite) whitePoints += points; // add the points depending on white color moved
            else blackPoints += points;

            // cases:
            // 1 - not white promotion and not ai mode -> true
            // 2 - not white promotion and ai mode -> true
            // 3 - white promotion and not ai mode -> true
            // 4 - white promotion and ai mode -> false
            if (!whitePromotion || !AIMODE) {
                afterMoveChecksAndCleanUp();
            }

            if (AIMODE && moveNum % 2 == 0) randomAIMove();
        }
    }

    // all checks after making a move and cleaning up
    private void afterMoveChecksAndCleanUp() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        boolean checkmate = isCheckmate(!selected.isWhite); // check if the move resulted in a checkmate for the other color
        boolean stalemate = isStalemate(!selected.isWhite); // check if the move resulted in a stalemate for the other color
        if (checkmate) winner = selected.isWhite ? "White Won" : "Black Won";
        if (stalemate) winner = "Stalemate";

        if (King.getKing(!selected.isWhite).inCheck(Piece.getPieces(), !selected.isWhite)) {
            // the check sound effect will only be played when the moved piece checks the enemy king
            sfx.playSoundEffect("check");
        }

        selected = null; // remove selected
        moveNum++;
    }

    public void randomAIMove() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // get all pieces of the AI's color, which is always black
        ArrayList<Piece> aiPieces = Piece.getPieces().stream().filter(p -> !p.isWhite).collect(Collectors.toCollection(ArrayList::new));

        int availableMoves = 0;
        ArrayList<int[]> validAIMoves = new ArrayList<>();
        while (!aiPieces.isEmpty()) {
            selected = aiPieces.get(rand.nextInt(aiPieces.size())); //getting a random piece that the AI player currently has
            aiPieces.remove(selected); // remove from list once picked because if its invalid we dont want to repick
            validMoves = selected.getValidMoves(); // get intital valid moves
            validAIMoves = getTrueValidMoves(selected); // get valid moves accounting for checks
            if (validAIMoves.size() > 0) {  //check if that random piece is movable or not
                availableMoves = validAIMoves.size(); //use for selecting a random position later within all the "valid moves" inside the array(called by .getValidMoves() method)
                break;
            }
        }

        int randMoveIndex = rand.nextInt(availableMoves); // pick a random valid move
        int xp = validAIMoves.get(randMoveIndex)[0];  //get the x position for AI's valid move
        int yp = validAIMoves.get(randMoveIndex)[1]; //get the y position for AI's valid move

        validMoves = validAIMoves;

        // move the piece to the given position (converted to screen coordinates)
        movePiece(xp * (boardSize / 8) + boardPosition[0], yp * (boardSize / 8) + boardPosition[1]);
    }

    /**
     * Promote pawn to given piece
     * @param pawn Pawn object
     * @param pieceToPromoteTo "queen", "rook", "knight", or "bishop"
     */
    public void promotePawn(Pawn pawn, String pieceToPromoteTo) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // create new specified piece object at the pawns position and update the board to reflect the change

        if (pieceToPromoteTo.equals("queen")) {
            new Queen(pawn.getxp(), pawn.getyp(), pawn.isWhite, pieceToPromoteTo, 9);
            setBoardState(pawn.getxp(), pawn.getyp(), !pawn.isWhite ? 7 : 8, false);
        }

        if (pieceToPromoteTo.equals("rook")) {
            new Rook(pawn.getxp(), pawn.getyp(), pawn.isWhite, pieceToPromoteTo, 5);
            setBoardState(pawn.getxp(), pawn.getyp(), !pawn.isWhite ? 1 : 2, false);
        }

        if (pieceToPromoteTo.equals("knight")) {
            new Knight(pawn.getxp(), pawn.getyp(), pawn.isWhite, pieceToPromoteTo, 3);
            setBoardState(pawn.getxp(), pawn.getyp(), !pawn.isWhite ? 3 : 4, false);
        }

        if (pieceToPromoteTo.equals("bishop")) {
            new Bishop(pawn.getxp(), pawn.getyp(), pawn.isWhite, pieceToPromoteTo, 3);
            setBoardState(pawn.getxp(), pawn.getyp(), !pawn.isWhite ? 5 : 6, false);
        }

        // kill the pawn to remove it from the list of pieces
        pawn.kill();

        // in ai mode, wait until the player (white) has promoted to check for checkmate, stalemate, check and make the next ai move
        if (AIMODE && whitePromotion) {
            afterMoveChecksAndCleanUp();
            whitePromotion = false;
            randomAIMove();
        }

        // set these to false to continue playing and stop drawing the options, as the promotion is done
        if (pawn.isWhite) whitePromotion = false;
        else blackPromotion = false;
    }

    /**
     * Checks if coordinates are within the board (0-7, 0-7)
     *
     * @param x coordinate
     * @param y coordinate
     * @return if the coordinates are within the board
     */
    public static boolean inBoard(int x, int y) {
        return !((x < 0 || x > 7) || (y < 0 || y > 7));
    }

    /**
     * Get the value of the board at the specified position
     *
     * @param xp x position
     * @param yp y position
     * @return state of the board at that position given it is in bounds
     */
    public static int getBoardState(int xp, int yp) {
        if (inBoard(xp, yp)) return board[yp][xp];
        return -1;
    }

    /**
     * Get coordinates of the pawn for en passant
     * @return coordinates, null if not currently valid
     */
    public static int[] getEnpassant() {
        return enpassant;
    }

    /**
     * Set the state of the board at the given postion
     * @param xp x position
     * @param yp y position
     * @param val value to set
     * @param simulation whether this is a real move or not
     */
    public void setBoardState(int xp, int yp, int val, boolean simulation) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        board[yp][xp] = val;
        // to ensure the sound isn't played during a move simulation
        if (!simulation) {
            sfx.playSoundEffect("move");
        }
    }

    // if the current game is in AI mode or not
    public boolean isAIMODE() {
        return AIMODE;
    }

    // if white promotion is valid or not
    public boolean getWhitePromotion() {
        return whitePromotion;
    }

    // if black promotion is valid or not
    public boolean getBlackPromotion() {
        return blackPromotion;
    }

    // get the selected piece
    public Piece getSelected() {
        return selected;
    }

    // get the winner
    public String getWinner() {
        if (winner != null) {
            return winner;
        }

        return null;
    }

    // string for whichever colors turn it is
    public String currentColorTurn() {
        return moveNum % 2 == 1 ? "White" : "Black";
    }

    public int getWhitePoints() {
        return whitePoints;
    }

    public int getBlackPoints() {
        return blackPoints;
    }

    public static int[] getBoardPosition() { return boardPosition; }

    // reset the board to its original state
    public void resetBoard() {
        board = new int[][]{
                {1, 3, 5, 7, 9, 5, 3, 1},
                {11, 11, 11, 11, 11, 11, 11, 11},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {12, 12, 12, 12, 12, 12, 12, 12},
                {2, 4, 6, 8, 10, 6, 4, 2},
        };
        moveNum = 1;
        winner = null;
        selected = null;
        validMoves = new ArrayList<>();
        enpassant = null;
        whitePoints = 0;
        blackPoints = 0;

        setPieces();
    }

    public void draw(Graphics g) {
        drawBoard(g);
        drawPieces(g);
    }

    /**
     * @drawBoard: method use to draw the grid
     */
    private void drawBoard(Graphics g) {
        final int boardSize = GamePanel.HEIGHT * 80 / 100; //640 pixels by 640 pixels for the board; 80 pixels by 80 pixels for each tile
        int squareColorSwitch; // variable to switch between square colors
        for (int row = 0; row < 8; row++) {
            squareColorSwitch = (row % 2 != 0) ? 0 : 1; // switch color at the beginning of each row
            for (int column = 0; column < 8; column++) {
                if (squareColorSwitch % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY); // set color to black for even-colored squares
                } else {
                    g.setColor(Color.WHITE);
                }
                // fill the rects
                g.fillRect((boardSize / 8) * column + boardPosition[0], (boardSize / 8) * row + boardPosition[1], boardSize / 8, boardSize / 8);
                squareColorSwitch++; // switch the color for the next square in the row
            }
        }
    }

    /**
     * @param g : graphics component (use the override paint method from the GamePanel class)
     *          /*
     *          * 1 = rook (black)
     *          * 3 = knight (black)
     *          * 5 = bishop (black)
     *          * 7 = queen (black)
     *          * 9 = king (black)
     *          * 11 = pawn (black)
     *          * ----------------------
     *          * 2 = rook (white)
     *          * 4 = knight (white)
     *          * 6 = bishop (white)
     *          * 8 = queen (white)
     *          * 10 = king (white)
     *          * 12 = pawn (white)
     * @drawPiece: method that is used to blit chess piece onto the board
     */


    //Draws the pieces
    private void drawPieces(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (imagesDictonary.get(board[row][col]) != null) { //check if the position on the grid has a piece
                    if (selected != null && col == selected.getxp() && row == selected.getyp()) {
                        g.setColor(new Color(25, 200, 25, 90));
                        g.fillRect((boardSize / 8) * col + boardPosition[0] + 5, (boardSize / 8) * row + boardPosition[1] + 5, boardSize / 8 - 10, boardSize / 8 - 10);
                    }
                    // get king and draws red square if in check
                    King king = King.getKing(moveNum % 2 == 1);
                    if (king.inCheck(Piece.getPieces(), moveNum % 2 == 1) && king.getxp() == col && king.getyp() == row) {
                        g.setColor(new Color(200, 25, 25, 90));
                        g.fillRect((boardSize / 8) * col + boardPosition[0] + 5, (boardSize / 8) * row + boardPosition[1] + 5, boardSize / 8 - 10, boardSize / 8 - 10);
                    }
                    Image currentPiece = imagesDictonary.get(board[row][col]);
                    g.drawImage(currentPiece,
                            (boardSize / 8) * col + boardPosition[0],
                            (boardSize / 8) * row + boardPosition[1],
                            null);
                }
            }
        }
        g.setColor(new Color(50, 115, 198, 80));
        for (int[] point : validMoves) {
            if (selected != null) {
                g.fillOval((boardSize / 8) * point[0] + boardPosition[0] + (boardSize / 32), (boardSize / 8) * point[1] + boardPosition[1] + (boardSize / 32), (boardSize / 8) / 2, (boardSize / 8) / 2);
            }
        }
    }
}
