
public class Rook extends Piece {
    
    public Rook(int colour) {
        super(2, colour, colour == 0 ? "/white-rook.png" : "/black-rook.png");
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
        
        boolean inSameRow = endPos / 8 == startPos / 8;
        boolean inSameColumn = endPos % 8 == startPos % 8;
        
        if((capturingPieceOfDifferentColour || movingToEmptySquare) && (inSameRow || inSameColumn)) {
            if(inSameRow && endPos % 8 < startPos % 8) { // Moving left
                int col = startPos % 8;
                while(col - 1 >= 0 && board[startPos / 8][col-1] == null) {
                    col--;
                }
                return endPos % 8 >= col || capturingPieceOfDifferentColour && endPos % 8 >= col-1;
            } else if (inSameRow && endPos % 8 > startPos % 8) { // Moving right
                int col = startPos % 8;
                while(col + 1 < 8 && board[startPos / 8][col+1] == null) {
                    col++;
                }
                return endPos % 8 <= col || capturingPieceOfDifferentColour && endPos % 8 <= col+1;
            } else if (inSameColumn && endPos / 8 < startPos / 8) { // Moving up
                int row = startPos / 8;
                while(row - 1 >= 0 && board[row-1][startPos % 8] == null) {
                    row--;
                }
                return endPos / 8 >= row || capturingPieceOfDifferentColour && endPos / 8 >= row-1;
            } else if (inSameColumn && endPos / 8 > startPos / 8) { // Moving down
                int row = startPos / 8;
                while(row + 1 < 8 && board[row+1][startPos % 8] == null) {
                    row++;
                }
                return endPos / 8 <= row || capturingPieceOfDifferentColour && endPos / 8 <= row+1;
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        if (this.getColour() == 0) {
            return "♖";
        } else {
            return "♜";
        }
    }
    
}



