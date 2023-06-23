/**
 * Header Comments
 * @Date: June 16th, 2023
 * @Group: Aditya Patel & Jack Jiang
 * @Project: ICS4U-Computer Science FSE: Chess AI
 * @Description: This chess game allows players to compete against AI or their friends. It encompasses all the standard chess rules,
such as castling, stalemate, promotion, and en passant. The game features a score that displays the point of each player, movement sound effect,
nice-looking buttons, as well as smart moves that prevent moves resulting in a check. While the AI is not perfect
 and makes random moves, it will continuously be looking for possible checkmate, so players should be cautious.
*/

import UI.Text;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class Main extends JFrame {
    GamePanel game = new GamePanel(); //game panel

    public Main() throws IOException, FontFormatException {
        //create the frame and put the panel inside the frame in order to display the game window
        super("Chess");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        add(game);
        pack();
        setLocationRelativeTo(null);  // game window starts right in the center on our screen
        setVisible(true); //set the window to visible
        setResizable(false); //the window is NOT resizable
    }

    public static void main(String[] arguments) throws IOException, FontFormatException {
        Main frame = new Main();
    }

    /**
     * @exit: method used for keyboard shortcut to exit the window
     */
    public void exit() {
        System.exit(getDefaultCloseOperation());
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener, MouseMotionListener, MouseListener {
    private Main window; //window to display
    private ChessBoard board; //board grid

    public final Font titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/Fonts/TitleFont.ttf")).deriveFont(150f);//get the preferred font selected earlier

    public static final int MAIN_MENU = 0, PVSAI = 1, PVSP = 2, END = 3; //Game state explanation: PVSAI-Player vs AI / PVP-Player vs Player
    public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width; //constant for the window width
    public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height; //constant for the window height
    private int screen = MAIN_MENU; // the screen is initially set to the main menu where the player chooses their preferred mode to play

    private String winner;
    private boolean[] keys; //keyboard inputs
    Timer timer;

    Image piecesArt = new ImageIcon("src/Images/ChessPiecesArt.png").getImage();
    //Instantiate the buttons and text
    UI.Button closeAppButton = new UI.Button(
            10, HEIGHT - 74,
            64, 64,
            "src/Images/CloseButton.png", null
    );
    UI.Text title = new UI.Text("Chess", titleFont, WIDTH / 100, HEIGHT * 20 / 100, Text.LEFT);
    UI.Button PVSAIButton = new UI.Button(
            WIDTH * 75 / 100, HEIGHT * 20 / 100,
            HEIGHT * 30 / 100, HEIGHT * 10 / 100,
            "src/Images/GreenButton.png", "src/Images/GreenButton_Hover.png");

    UI.Text PVSAIButtonText = new UI.Text("1 Player", new Font("SansSerif", Font.BOLD, 32), WIDTH * 75 / 100 + (HEIGHT * 30 / 100) / 2, HEIGHT * 20 / 100 + (HEIGHT * 10 / 100) / 2 + 5, Text.CENTER);

    UI.Button PVSPButton = new UI.Button(
            WIDTH * 75 / 100, HEIGHT * 20 / 100 + (HEIGHT * 10 / 100) + 20,
            HEIGHT * 30 / 100, HEIGHT * 10 / 100,
            "src/Images/GreenButton.png", "src/Images/GreenButton_Hover.png");

    UI.Text PVSPButtonText = new UI.Text("2 Player", new Font("SansSerif", Font.BOLD, 32), WIDTH * 75 / 100 + (HEIGHT * 30 / 100) / 2, HEIGHT * 20 / 100 + (HEIGHT * 10 / 100) * 150 / 100 + 25, Text.CENTER);

    UI.Button WhiteQueenButton = new UI.Button(
            ChessBoard.getBoardPosition()[0], 5,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/wQ.png", null);

    UI.Button BlackQueenButton = new UI.Button(
            ChessBoard.getBoardPosition()[0], HEIGHT - WIDTH * 5 / 100 - 10,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/bQ.png", null);

    UI.Button WhiteRookButton = new UI.Button(
            ChessBoard.getBoardPosition()[0] + WIDTH * 5 / 100 + 10, 5,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/wR.png", null);

    UI.Button BlackRookButton = new UI.Button(
            ChessBoard.getBoardPosition()[0] + WIDTH * 5 / 100 + 10, HEIGHT - WIDTH * 5 / 100 - 10,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/bR.png", null);

    UI.Button WhiteKnightButton = new UI.Button(
            ChessBoard.getBoardPosition()[0] + WIDTH * 10 / 100 + 20, 5,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/wN.png", null);

    UI.Button BlackKnightButton = new UI.Button(
            ChessBoard.getBoardPosition()[0] + WIDTH * 10 / 100 + 20, HEIGHT - WIDTH * 5 / 100 - 10,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/bN.png", null);

    UI.Button WhiteBishopButton = new UI.Button(
            ChessBoard.getBoardPosition()[0] + WIDTH * 15 / 100 + 30, 5,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/wB.png", null);

    UI.Button BlackBishopButton = new UI.Button(
            ChessBoard.getBoardPosition()[0] + WIDTH * 15 / 100 + 30, HEIGHT - WIDTH * 5 / 100 - 10,
            WIDTH * 5 / 100, WIDTH * 5 / 100,
            "src/Images/DEFAULT_PIECES/bB.png", null);

    UI.Text endScreenTitle;

    UI.Button ReturnToMainMenuButton = new UI.Button(
            WIDTH * 40 / 100, HEIGHT * 70 / 100,
            HEIGHT * 30 / 100, HEIGHT * 10 / 100,
            "src/Images/GreenButton.png", "src/Images/GreenButton_Hover.png");

    UI.Text ReturnToMainMenuButtonText = new UI.Text("Main Menu", new Font("SansSerif", Font.BOLD, 32), WIDTH * 40 / 100 + (HEIGHT * 30 / 100) / 2, HEIGHT * 70 / 100 + (HEIGHT * 10 / 100) / 2 + 5, Text.CENTER);

    /**
     * @GamePanel: constructor for the panel*/
    public GamePanel() throws IOException, FontFormatException {
        keys = new boolean[KeyEvent.KEY_LAST + 1]; //arrays for checking keyboard inputs

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //set the size of the game window
        setPreferredSize(new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight()));

        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        timer = new Timer(20, this); //initialize the timer
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (screen != END && screen != MAIN_MENU && board.getWinner() != null) {
            //draw the text to indicate the winner when they had won the game
            winner = board.getWinner();
            endScreenTitle = new UI.Text(winner, titleFont, WIDTH * 50 / 100, HEIGHT * 20 / 100, Text.CENTER);
            screen = END;
        }

        if (board != null && board.isAIMODE() && board.getBlackPromotion()) {
            //checking for pawn promotion
            Piece toPromote = null;

            for (Piece p : Piece.getPieces()) {
                //iterate through all the pieces that are alive
                if (p.name.equals("pawn") && p.getyp() == 7) {
                    //check if any pawn is being promoted
                    toPromote = p;
                }
            }

            // if in AI mode just promote to queen by default
            try {
                board.promotePawn((Pawn) toPromote, "queen");
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
        }

        repaint(); // only draw
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        keys[key] = false;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        keys[key] = true;

        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //shortcut for exit
            try {
                window = new Main();
                window.exit();
            } catch (IOException | NullPointerException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(piecesArt, WIDTH - piecesArt.getWidth(null), HEIGHT - piecesArt.getHeight(null), null);
        closeAppButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
        if (screen == MAIN_MENU) {
            g.setColor(Color.WHITE);
            title.draw(g);
            PVSAIButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
            if (PVSAIButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition()))
                PVSAIButtonText.setYPos(PVSAIButtonText.getOriginalYPos() + 5);
            else PVSAIButtonText.setYPos(PVSAIButtonText.getOriginalYPos());
            PVSAIButtonText.draw(g);

            PVSPButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
            if (PVSPButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition()))
                PVSPButtonText.setYPos(PVSPButtonText.getOriginalYPos() + 5);
            else PVSPButtonText.setYPos(PVSPButtonText.getOriginalYPos());
            PVSPButtonText.draw(g);
        }

        if (screen == PVSAI || screen == PVSP) {
            board.draw(g);
            if (board.getWhitePromotion()) {
                //promotion piece buttons for the white piece player
                WhiteQueenButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                WhiteQueenButton.drawBorder(g);
                WhiteRookButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                WhiteRookButton.drawBorder(g);
                WhiteKnightButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                WhiteKnightButton.drawBorder(g);
                WhiteBishopButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                WhiteBishopButton.drawBorder(g);
            }

            if (board.getBlackPromotion()) {
                //promotion piece buttons for the black piece player
                BlackQueenButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                BlackQueenButton.drawBorder(g);
                BlackRookButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                BlackRookButton.drawBorder(g);
                BlackKnightButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                BlackKnightButton.drawBorder(g);
                BlackBishopButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
                BlackBishopButton.drawBorder(g);
            }

            g.setColor(Color.WHITE);
            UI.Text turnIndicator = new UI.Text(board.currentColorTurn() + "'s turn", new Font("SansSerif", Font.BOLD, 40), WIDTH / 100, HEIGHT * 10 / 100, Text.LEFT);
            UI.Text whitePoints = new UI.Text("White points: " + board.getWhitePoints(), new Font("SansSerif", Font.BOLD, 20), WIDTH / 100, HEIGHT * 10 / 100 + 50, Text.LEFT);
            UI.Text blackPoints = new UI.Text("Black points: " + board.getBlackPoints(), new Font("SansSerif", Font.BOLD, 20), WIDTH / 100, HEIGHT * 10 / 100 + 80, Text.LEFT);

            turnIndicator.draw(g); //update the current turn (either white player's turn or black player's turn)

            //draw the current points for each player
            whitePoints.draw(g);
            blackPoints.draw(g);
        }

        if (screen == END) {
            g.setColor(Color.WHITE);
            endScreenTitle.draw(g);
            ReturnToMainMenuButton.draw(g, getMousePosition() == null ? new Point(0, 0) : getMousePosition());
            if (ReturnToMainMenuButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition()))
                ReturnToMainMenuButtonText.setYPos(ReturnToMainMenuButtonText.getOriginalYPos() + 5);
            else ReturnToMainMenuButtonText.setYPos(ReturnToMainMenuButtonText.getOriginalYPos());
            ReturnToMainMenuButtonText.draw(g);
        }
    }

    //user input for the pieces
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //button collision checks
        if (closeAppButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
            System.exit(0);
        }

        if (screen == MAIN_MENU && PVSAIButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
            screen = PVSAI;
            board = new ChessBoard(true);
        }

        if (screen == MAIN_MENU && PVSPButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
            screen = PVSP;
            board = new ChessBoard(false);
        }

        if (screen == END && ReturnToMainMenuButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
            board.resetBoard();
            screen = MAIN_MENU;
        }


        //Promotions for pawns
        if (board != null && (board.getWhitePromotion() || board.getBlackPromotion())) {
            Piece toPromote = null;
            String pieceName = null;

            for (Piece p : Piece.getPieces()) {
                if (p.name.equals("pawn") && (p.getyp() == 0 || p.getyp() == 7)) {
                    //when pawns (either black or white) reaches the last row of their opponent
                    toPromote = p;
                }
            }

            //check for user's decision regarding promotion for their white pawn
            if (WhiteQueenButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())
                    || BlackQueenButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
                pieceName = "queen";
            }

            if (WhiteRookButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())
                    || BlackRookButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
                pieceName = "rook";
            }

            if (WhiteKnightButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())
                    || BlackKnightButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
                pieceName = "knight";
            }

            if (WhiteBishopButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())
                    || BlackBishopButton.isHovering(getMousePosition() == null ? new Point(0, 0) : getMousePosition())) {
                pieceName = "bishop";
            }

            if (pieceName != null) {
                try {
                    board.promotePawn((Pawn) toPromote, pieceName);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        if (screen != MAIN_MENU && screen != END) {
            assert board != null;
            if (board.getSelected() != null) {
                try {
                    board.movePiece(e.getX(), e.getY()); //move the piece using its x & y positions
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else {
                try {
                    board.selectPiece(e.getX(), e.getY()); //get the user's mouse position for moving pieces
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}