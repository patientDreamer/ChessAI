import java.util.ArrayList;

/**
 * @ValidMovement: a handy class with utility methods for testing
 * legitimate moves for pieces other than pawns with the parameters that are passed in.*/
public class ValidMovement {
    static ArrayList <int[]>validMoves; //an arraylist to store all the valid moves

    //static method for checking valid movement
    public static ArrayList<int[]> checkValidMovements(int xp, int yp, int[][] moveSet, int maxMoves){
        //black piece: odd number for encoding
        //white piece: even number for encoding
        validMoves = new ArrayList<>();
        boolean trigger = false; //trigger for detecting opposite piece colors (ie. selected_piece = white and the detected piece = black)

        for(int[] curMoves: moveSet){
            for (int i = 1; i <= maxMoves; i++){
                //calculate the new position that this rook can move based on the step that we are on
                int newX = xp + curMoves[0]*i;
                int newY = yp + curMoves[1]*i;
                if (!ChessBoard.inBoard(newX, newY))
                    //if the position is out of bound
                    break;
                else{
                    if (ChessBoard.getBoardState(newX, newY) != 0) {
                        // if the tile is not empty
                        if(ChessBoard.getBoardState(xp, yp)%2 != 0 && ChessBoard.getBoardState(newX, newY)%2 == 0){
                            //black rook detects white pieces, enable the trigger
                            trigger = true;
                        }
                        else if(ChessBoard.getBoardState(xp, yp)%2 == 0 && ChessBoard.getBoardState(newX, newY)%2 != 0){
                            //white rook detects black pieces, enable the trigger
                            trigger = true;
                        }
                        else{
                            //same color pieces, break right away (ie. white rook detects a white piece)
                            break;
                        }
                    }
                    validMoves.add(new int[]{newX, newY});
                    if(trigger){
                        trigger = false;  //reset the trigger for checking the tiles behind the piece
                        break;
                    }
                }
            }
        }
        return validMoves;
    }

    public static void deleteMove(int[] pos){
        //delete the valid move at the position where the parameter is
        validMoves.remove(pos);
    }
}
