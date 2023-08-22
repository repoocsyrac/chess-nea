
public class King extends Piece {
    
    public King(int colour) {
        super(6, colour, colour == 0 ? "/white-king.png" : "/black-king.png");
    }
    
    // Determines if the move is pseudo-legal
    @Override
    public boolean isValidMove(String move, Piece[][] board) {
        // Gets start position and end position as ints from the move string
        int startPos = Integer.valueOf(move.substring(0, 2));
        int endPos = Integer.valueOf(move.substring(2, 4));

        boolean capturingPieceOfDifferentColour = (board[endPos / 8][endPos % 8] != null)
                && (board[startPos / 8][startPos % 8].getColour() != board[endPos / 8][endPos % 8].getColour());
        boolean movingToEmptySquare = board[endPos / 8][endPos % 8] == null;
        
        boolean notMoreThanOneRowAway = Math.abs(endPos / 8 - startPos / 8) <= 1;
        boolean notMoreThanOneColumnAway = Math.abs(endPos % 8 - startPos % 8) <= 1;
        
        return (capturingPieceOfDifferentColour || movingToEmptySquare) && (notMoreThanOneColumnAway && notMoreThanOneRowAway);
    }
    
    @Override
    public String toString() {
        if (this.getColour() == 0) {
            return "♔";
        } else {
            return "♚";
        }
    }
    
}


