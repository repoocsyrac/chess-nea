
public class Knight extends Piece {

    public Knight(int colour) {
        super(3, colour, colour == 0 ? "/white-knight.png" : "/black-knight.png");
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

        boolean twoColumnsAway = Math.abs(endPos % 8 - startPos % 8) == 2;
        boolean oneRowAway = Math.abs(endPos / 8 - startPos / 8) == 1;

        boolean twoRowsAway = Math.abs(endPos / 8 - startPos / 8) == 2;
        boolean oneColumnAway = Math.abs(endPos % 8 - startPos % 8) == 1;

        return (capturingPieceOfDifferentColour || movingToEmptySquare) 
                && (twoRowsAway && oneColumnAway || twoColumnsAway && oneRowAway);
    }

    @Override
    public String toString() {
        if (this.getColour() == 0) {
            return "♘";
        } else {
            return "♞";
        }
    }

}
