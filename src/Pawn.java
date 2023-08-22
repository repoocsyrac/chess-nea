
public class Pawn extends Piece {
    
    public Pawn(int colour) {
        super(1, colour, colour == 0 ? "/white-pawn.png" : "/black-pawn.png");
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
        
        boolean isWhite = this.getColour() == 0;
        
        boolean hasNotMoved = (isWhite && startPos / 8 == 6) || (!isWhite && startPos / 8 == 1);
        
        boolean oneRowAway = Math.abs(endPos / 8 - startPos / 8) == 1;
        boolean twoRowsAway = Math.abs(endPos / 8 - startPos / 8) == 2;
        boolean inSameColumn = endPos % 8 == startPos % 8;
        boolean oneColumnAway = Math.abs(endPos % 8 - startPos % 8) == 1;
        
        boolean squareInFrontIsEmpty = (isWhite && (startPos / 8) - 1 >= 0 && board[(startPos / 8) - 1][startPos % 8] == null) 
                || (!isWhite && (startPos / 8) + 1 < 8 && board[(startPos / 8) + 1][startPos % 8] == null);
        
        boolean isMovingForwards = (isWhite && endPos / 8 < startPos / 8) || (!isWhite && endPos / 8 > startPos / 8);
        
        return (capturingPieceOfDifferentColour && oneColumnAway && oneRowAway || movingToEmptySquare && inSameColumn 
                && (oneRowAway || hasNotMoved && twoRowsAway && squareInFrontIsEmpty)) && isMovingForwards;
    }
    
    @Override
    public String toString() {
        if (this.getColour() == 0) {
            return "♙";
        } else {
            return "♟";
        }
    }
    
}

