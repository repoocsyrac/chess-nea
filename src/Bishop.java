
public class Bishop extends Piece {

    public Bishop(int colour) {
        super(4, colour, colour == 0 ? "/white-bishop.png" : "/black-bishop.png");
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

        int columnChange = Math.abs(endPos % 8 - startPos % 8);
        int rowChange = Math.abs(endPos / 8 - startPos / 8);
        boolean isDiagonal = columnChange == rowChange;

        if ((capturingPieceOfDifferentColour || movingToEmptySquare) && isDiagonal) {
            if (endPos % 8 < startPos % 8 && endPos / 8 < startPos / 8) { // Moving north west
                int col = startPos % 8;
                int row = startPos / 8;
                while (col - 1 >= 0 && row - 1 >= 0 && board[row - 1][col - 1] == null) {
                    col--;
                    row--;
                }
                return endPos % 8 >= col && endPos / 8 >= row || capturingPieceOfDifferentColour && endPos % 8 >= col - 1 && endPos / 8 >= row - 1;
            } else if (endPos % 8 > startPos % 8 && endPos / 8 < startPos / 8) { // Moving north east
                int col = startPos % 8;
                int row = startPos / 8;
                while (col + 1 < 8 && row - 1 >= 0 && board[row - 1][col + 1] == null) {
                    col++;
                    row--;
                }
                return endPos % 8 <= col && endPos / 8 >= row || capturingPieceOfDifferentColour && endPos % 8 <= col + 1 && endPos / 8 >= row - 1;
            } else if (endPos % 8 > startPos % 8 && endPos / 8 > startPos / 8) { // Moving south east
                int col = startPos % 8;
                int row = startPos / 8;
                while (col + 1 < 8 && row + 1 < 8 && board[row + 1][col + 1] == null) {
                    col++;
                    row++;
                }
                return endPos % 8 <= col && endPos / 8 <= row || capturingPieceOfDifferentColour && endPos % 8 <= col + 1 && endPos / 8 <= row + 1;
            } else if (endPos % 8 < startPos % 8 && endPos / 8 > startPos / 8) { // Moving south west
                int col = startPos % 8;
                int row = startPos / 8;
                while (col - 1 >= 0 && row + 1 < 8 && board[row + 1][col - 1] == null) {
                    col--;
                    row++;
                }
                return endPos % 8 >= col && endPos / 8 <= row || capturingPieceOfDifferentColour && endPos % 8 >= col - 1 && endPos / 8 <= row + 1;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.getColour() == 0) {
            return "♗";
        } else {
            return "♝";
        }
    }

}

