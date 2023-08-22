
import java.util.ArrayList;
import javax.swing.JFrame;

public class ChessBoard {

    private Piece[][] board; // null = empty space
    private int currentTurn; // even num = white, odd num = black
    private MoveHistory moveHistory;
    private boolean playingAgainstComputer;

    private final int[][] pawnEvalBonus = {
        {-900, -900, -900, -900, -900, -900, -900, -900},
        {-50, -50, -50, -50, -50, -50, -50, -50},
        {-10, -10, -20, -30, -30, -20, -10, -10},
        {-5, -5, -10, -25, -25, -10, -5, -5},
        {0, 0, 0, -20, -20, 0, 0, 0},
        {-5, 5, 10, 0, 0, 10, 5, -5},
        {-5, -10, -10, 20, 20, -10, -10, -5},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private final int[][] rookEvalBonus = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {-5, -10, -10, -10, -10, -10, -10, -5},
        {5, 0, 0, 0, 0, 0, 0, 5},
        {5, 0, 0, 0, 0, 0, 0, 5},
        {5, 0, 0, 0, 0, 0, 0, 5},
        {5, 0, 0, 0, 0, 0, 0, 5},
        {5, 0, 0, 0, 0, 0, 0, 5},
        {0, 0, 0, -5, -5, 0, 0, 0}
    };

    private final int[][] knightEvalBonus = {
        {50, 40, 30, 30, 30, 30, 40, 50},
        {40, 20, 0, 0, 0, 0, 20, 40},
        {30, 0, -10, -15, -15, -10, 0, 30},
        {30, -5, -15, -20, -20, -15, -5, 30},
        {30, 0, -15, -20, -20, -15, 0, 30},
        {30, -5, -10, -15, -15, -10, -5, 30},
        {40, 20, 0, -5, -5, 0, 20, 40},
        {50, 40, 30, 30, 30, 30, 40, 50}
    };

    private final int[][] bishopEvalBonus = {
        {20, 10, 10, 10, 10, 10, 10, 20},
        {10, 0, 0, 0, 0, 0, 0, 10},
        {10, 0, -5, -10, -10, 5, 0, 10},
        {10, -5, -5, -10, -10, -5, -5, 10},
        {10, 0, -10, -10, -10, -10, 0, 10},
        {10, -10, -10, -10, -10, -10, -10, 10},
        {10, -5, 0, 0, 0, 0, -5, 10},
        {20, 10, 10, 10, 10, 10, 10, 20}
    };

    private final int[][] queenEvalBonus = {
        {20, 10, 10, 5, 5, 10, 10, 20},
        {10, 0, 0, 0, 0, 0, 0, 10},
        {10, 0, -5, -5, -5, -5, 0, 10},
        {5, 0, -5, -5, -5, -5, 0, 5},
        {0, 0, -5, -5, -5, -5, 0, 5},
        {10, -5, -5, -5, -5, -5, 0, 10},
        {10, 0, -5, 0, 0, 0, 0, 10},
        {20, 10, 10, 5, 5, 10, 10, 20}
    };

    private final int[][] kingEvalBonus = {
        {30, 40, 40, 50, 50, 40, 40, 30},
        {30, 40, 40, 50, 50, 40, 40, 30},
        {30, 40, 40, 50, 50, 40, 40, 30},
        {30, 40, 40, 50, 50, 40, 40, 30},
        {20, 30, 30, 40, 40, 30, 30, 20},
        {10, 20, 20, 20, 20, 20, 20, 10},
        {-20, -20, 0, 0, 0, 0, -20, -20},
        {-20, -30, -10, 0, 0, -10, -30, -20}
    };

    public ChessBoard() {
        // Setting initial layout of pieces on board:
        this.board = new Piece[][]{
            {new Rook(1), new Knight(1), new Bishop(1), new Queen(1), new King(1), new Bishop(1), new Knight(1), new Rook(1)},
            {new Pawn(1), new Pawn(1), new Pawn(1), new Pawn(1), new Pawn(1), new Pawn(1), new Pawn(1), new Pawn(1)},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {new Pawn(0), new Pawn(0), new Pawn(0), new Pawn(0), new Pawn(0), new Pawn(0), new Pawn(0), new Pawn(0)},
            {new Rook(0), new Knight(0), new Bishop(0), new Queen(0), new King(0), new Bishop(0), new Knight(0), new Rook(0)}
        };

        this.currentTurn = 0;
        this.playingAgainstComputer = false;
        this.moveHistory = new MoveHistory();
    }

    // Displays window asking user whether they are playing against computer
    public void selectPlayer(JFrame parent, boolean modal) {
        SelectPlayer selectPlayer = new SelectPlayer(parent, modal);
        this.playingAgainstComputer = selectPlayer.showDialog();
    }

    // Returns 8x8 boolean array for a particular piece, true = piece can move there
    public boolean[][] getValidEndPositions(int pos) {
        boolean[][] validPositions = new boolean[8][8];

        for (int i = 0; i < 64; i++) {
            // Generates move string
            String move = "";
            if (pos < 10) {
                move += "0";
            }
            move += pos;

            if (i < 10) {
                move += "0";
            }
            move += i;
            
            if (i != pos && this.board[pos / 8][pos % 8].isValidMove(move, this.board)) {
                // Makes the move:
                
                int startPos = Integer.valueOf(move.substring(0, 2));
                int endPos = Integer.valueOf(move.substring(2, 4));

                if (this.board[endPos / 8][endPos % 8] == null) {
                    move += "0";
                } else {
                    move += this.board[endPos / 8][endPos % 8].getType();
                }

                this.moveHistory.add(move);
                this.currentTurn++;

                this.board[endPos / 8][endPos % 8] = this.board[startPos / 8][startPos % 8];
                this.board[startPos / 8][startPos % 8] = null;

                // Only valid if move didn't put the player into check
                if (!this.isInCheck((this.currentTurn - 1) % 2)) {
                    validPositions[i / 8][i % 8] = true;
                }
                this.undoMove();
            }
        }

        return validPositions;
    }

    // Applies given move to the board
    public void makeMove(String move) {
        int startPos = Integer.valueOf(move.substring(0, 2));
        int endPos = Integer.valueOf(move.substring(2, 4));

        if (this.board[startPos / 8][startPos % 8] != null && this.board[startPos / 8][startPos % 8].isValidMove(move, this.board)) {
            if (this.board[endPos / 8][endPos % 8] == null) {
                move += "0";
            } else {
                move += this.board[endPos / 8][endPos % 8].getType();
            }

            int colour = this.board[startPos / 8][startPos % 8].getColour();
            boolean pawnPromotion = this.board[startPos / 8][startPos % 8].getType() == 1
                    && ((endPos / 8 == 0 && colour == 0) || (endPos / 8 == 7 && colour == 1));

            if (pawnPromotion) {
                move += "p";
            }

            this.moveHistory.add(move);
            this.currentTurn++;

            if (pawnPromotion) {
                this.board[endPos / 8][endPos % 8] = new Queen(colour);
            } else {
                this.board[endPos / 8][endPos % 8] = this.board[startPos / 8][startPos % 8];
            }
            this.board[startPos / 8][startPos % 8] = null;

            if (this.isInCheck((this.currentTurn - 1) % 2)) {
                this.undoMove();
            }
        }
    }

    // Makes move without promoting pawn for use by AI
    private void makeTempMove(String move) {
        int startPos = Integer.valueOf(move.substring(0, 2));
        int endPos = Integer.valueOf(move.substring(2, 4));

        if (this.board[startPos / 8][startPos % 8] != null && this.board[startPos / 8][startPos % 8].isValidMove(move, this.board)) {
            if (this.board[endPos / 8][endPos % 8] == null) {
                move += "0";
            } else {
                move += this.board[endPos / 8][endPos % 8].getType();
            }

            this.moveHistory.add(move);
            this.currentTurn++;

            this.board[endPos / 8][endPos % 8] = this.board[startPos / 8][startPos % 8];
            this.board[startPos / 8][startPos % 8] = null;

            if (this.isInCheck((this.currentTurn - 1) % 2)) {
                this.undoMove();
            }
        }
    }

    // Undoes previous move (if there is one)
    public void undoMove() {
        if (this.moveHistory.getSize() > 0) {
            String move = this.moveHistory.pop();
            this.currentTurn--;

            int startPos = Integer.valueOf(move.substring(0, 2));
            int endPos = Integer.valueOf(move.substring(2, 4));
            int typeOfCapturedPiece = Integer.valueOf(move.substring(4, 5));
            Piece piece = null;

            switch (typeOfCapturedPiece) {
                case 1:
                    piece = new Pawn((this.currentTurn - 1) % 2);
                    break;
                case 2:
                    piece = new Rook((this.currentTurn - 1) % 2);
                    break;
                case 3:
                    piece = new Knight((this.currentTurn - 1) % 2);
                    break;
                case 4:
                    piece = new Bishop((this.currentTurn - 1) % 2);
                    break;
                case 5:
                    piece = new Queen((this.currentTurn - 1) % 2);
                    break;
                default:
                    break;
            }

            if (move.length() > 5 && move.charAt(5) == 'p') {
                this.board[startPos / 8][startPos % 8] = new Pawn(this.currentTurn % 2);
            } else {
                this.board[startPos / 8][startPos % 8] = this.board[endPos / 8][endPos % 8];
            }

            this.board[endPos / 8][endPos % 8] = piece;
        }
    }

    // Determines and makes optimal move
    public void makeComputerMove() {
        String move = this.getAIBestMove(this.board, 3);

        int startPos = Integer.valueOf(move.substring(0, 2));
        int endPos = Integer.valueOf(move.substring(2, 4));
        int colour = this.board[startPos / 8][startPos % 8].getColour();
        boolean pawnPromotion = this.board[startPos / 8][startPos % 8].getType() == 1
                && ((endPos / 8 == 0 && colour == 0) || (endPos / 8 == 7 && colour == 1));

        if (pawnPromotion) {
            move += "p";
        }

        this.makeMove(move);
    }

    // Returns an int representing value of the given board
    private int evaluate(Piece[][] board) {
        int eval = 0;
        int score;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] != null) {
                    switch (this.board[i][j].getType()) {
                        // is a pawn
                        case 1:
                            score = 100;
                            if (this.board[i][j].getColour() == 0) {
                                score += this.pawnEvalBonus[i][j];
                            } else {
                                score -= this.pawnEvalBonus[7 - i][j];
                            }
                            break;
                        // is a rook
                        case 2:
                            score = 500;
                            if (this.board[i][j].getColour() == 0) {
                                score += this.rookEvalBonus[i][j];
                            } else {
                                score -= this.rookEvalBonus[7 - i][j];
                            }
                            break;
                        // is a knight
                        case 3:
                            score = 300;
                            if (this.board[i][j].getColour() == 0) {
                                score += this.knightEvalBonus[i][j];
                            } else {
                                score -= this.knightEvalBonus[7 - i][j];
                            }
                            break;
                        // is a bishop
                        case 4:
                            score = 300;
                            if (this.board[i][j].getColour() == 0) {
                                score += this.bishopEvalBonus[i][j];
                            } else {
                                score -= this.bishopEvalBonus[7 - i][j];
                            }
                            break;
                        // is a queen
                        case 5:
                            score = 900;
                            if (this.board[i][j].getColour() == 0) {
                                score += this.queenEvalBonus[i][j];
                            } else {
                                score -= this.queenEvalBonus[7 - i][j];
                            }
                            break;
                        // is a king
                        default:
                            score = 20000;
                            if (this.board[i][j].getColour() == 0) {
                                score += this.kingEvalBonus[i][j];
                            } else {
                                score -= this.kingEvalBonus[7 - i][j];
                            }
                            break;
                    }

                    if (this.board[i][j].getColour() == 0) { // if piece is white
                        score *= -1;
                    }

                    eval += score;
                }
            }
        }
        return eval;
    }

    // Used to determine best move for AI to make
    private String getAIBestMove(Piece[][] board, int depth) {
        int maxEval = Integer.MIN_VALUE;
        String maxEvalMove = "";

        ArrayList<String> computerMoves = this.getAllMovesForColour(1);

        for (int i = 0; i < computerMoves.size(); i++) {
            this.makeTempMove(computerMoves.get(i));
            int eval = this.minimax(board, depth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.undoMove();
            if (eval > maxEval) {
                maxEval = eval;
                maxEvalMove = computerMoves.get(i);
            }
        }

        return maxEvalMove;
    }

    // Looks ahead at moves and determines best possible evaluation
    private int minimax(Piece[][] board, int depth, boolean isMaximisingPlayer, int alpha, int beta) {
        if (depth == 0 || this.currentPlayerHasNoLegalmoves()) {
            return this.evaluate(board);
        }

        if (isMaximisingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            ArrayList<String> computerMoves = this.getAllMovesForColour(1);

            for (int i = 0; i < computerMoves.size(); i++) {
                this.makeTempMove(computerMoves.get(i));
                int eval = this.minimax(this.board.clone(), depth - 1, false, alpha, beta);
                if (eval > maxEval) {
                    maxEval = eval;
                }
                this.undoMove();
                if (alpha < eval) {
                    alpha = eval;
                }
                if (beta <= alpha) {
                    break;
                }
            }

            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            ArrayList<String> computerMoves = this.getAllMovesForColour(0);

            for (int i = 0; i < computerMoves.size(); i++) {
                this.makeTempMove(computerMoves.get(i));
                int eval = this.minimax(this.board.clone(), depth - 1, true, alpha, beta);
                if (eval < minEval) {
                    minEval = eval;
                }
                this.undoMove();
                if (beta > eval) {
                    beta = eval;
                }
                if (beta <= alpha) {
                    break;
                }
            }

            return minEval;
        }
    }

    // Returns true if the given coloured king is under attack
    public boolean isInCheck(int colour) {
        int kingPos = this.determineKingPosition(colour);

        if (kingPos == -1) {
            return false;
        }

        // Checking for pawns
        if (colour == 0) { // if white
            // Black pawn atacking white king from top left
            if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) - 1 >= 0 && this.board[(kingPos / 8) - 1][(kingPos % 8) - 1] != null) {
                if (this.board[(kingPos / 8) - 1][(kingPos % 8) - 1].getType() == 1 && this.board[(kingPos / 8) - 1][(kingPos % 8) - 1].getColour() != colour) {
                    return true;
                }
            }
            // Black pawn atacking white king from top right
            if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) - 1][(kingPos % 8) + 1] != null) {
                if (this.board[(kingPos / 8) - 1][(kingPos % 8) + 1].getType() == 1 && this.board[(kingPos / 8) - 1][(kingPos % 8) + 1].getColour() != colour) {
                    return true;
                }
            }
        } else { // if black
            // White pawn atacking black king from bottom left
            if ((kingPos / 8) + 1 < 8 && (kingPos % 8) - 1 >= 0 && this.board[(kingPos / 8) + 1][(kingPos % 8) - 1] != null) {
                if (this.board[(kingPos / 8) + 1][(kingPos % 8) - 1].getType() == 1 && this.board[(kingPos / 8) + 1][(kingPos % 8) - 1].getColour() != colour) {
                    return true;
                }
            }
            // White pawn atacking black king from bottom right
            // if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 1] != null) {
            if ((kingPos / 8) + 1 < 8 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 1] != null) {
                if (this.board[(kingPos / 8) + 1][(kingPos % 8) + 1].getType() == 1 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 1].getColour() != colour) {
                    return true;
                }
            }
        }

        //Checking for kings
        // Top left
        if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) - 1 >= 0 && this.board[(kingPos / 8) - 1][(kingPos % 8) - 1] != null) {
            if (this.board[(kingPos / 8) - 1][(kingPos % 8) - 1].getType() == 6 && this.board[(kingPos / 8) - 1][(kingPos % 8) - 1].getColour() != colour) {
                return true;
            }
        }
        // Above
        if ((kingPos / 8) - 1 >= 0 && this.board[(kingPos / 8) - 1][kingPos % 8] != null) {
            if (this.board[(kingPos / 8) - 1][kingPos % 8].getType() == 6 && this.board[(kingPos / 8) - 1][kingPos % 8].getColour() != colour) {
                return true;
            }
        }
        // Top right
        if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) - 1][(kingPos % 8) + 1] != null) {
            if (this.board[(kingPos / 8) - 1][(kingPos % 8) + 1].getType() == 6 && this.board[(kingPos / 8) - 1][(kingPos % 8) + 1].getColour() != colour) {
                return true;
            }
        }
        // Right
        if ((kingPos % 8) + 1 < 8 && this.board[kingPos / 8][(kingPos % 8) + 1] != null) {
            if (this.board[kingPos / 8][(kingPos % 8) + 1].getType() == 6 && this.board[kingPos / 8][(kingPos % 8) + 1].getColour() != colour) {
                return true;
            }
        }
        // Bottom right
        if ((kingPos / 8) + 1 < 8 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 1] != null) {
            if (this.board[(kingPos / 8) + 1][(kingPos % 8) + 1].getType() == 6 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 1].getColour() != colour) {
                return true;
            }
        }
        // Below
        if ((kingPos / 8) + 1 < 8 && this.board[(kingPos / 8) + 1][kingPos % 8] != null) {
            if (this.board[(kingPos / 8) + 1][kingPos % 8].getType() == 6 && this.board[(kingPos / 8) + 1][kingPos % 8].getColour() != colour) {
                return true;
            }
        }
        // Bottom left
        if ((kingPos / 8) + 1 < 8 && (kingPos % 8) - 1 >= 0 && this.board[(kingPos / 8) + 1][(kingPos % 8) - 1] != null) {
            if (this.board[(kingPos / 8) + 1][(kingPos % 8) - 1].getType() == 6 && this.board[(kingPos / 8) + 1][(kingPos % 8) - 1].getColour() != colour) {
                return true;
            }
        }
        // Left
        if ((kingPos % 8) - 1 >= 0 && this.board[kingPos / 8][(kingPos % 8) - 1] != null) {
            if (this.board[kingPos / 8][(kingPos % 8) - 1].getType() == 6 && this.board[kingPos / 8][(kingPos % 8) - 1].getColour() != colour) {
                return true;
            }
        }

        // Checking for knights
        // One right, two up
        if ((kingPos / 8) - 2 >= 0 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) - 2][(kingPos % 8) + 1] != null) {
            if (this.board[(kingPos / 8) - 2][(kingPos % 8) + 1].getType() == 3 && this.board[(kingPos / 8) - 2][(kingPos % 8) + 1].getColour() != colour) {
                return true;
            }
        }
        // Two right, one up
        if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) + 2 < 8 && this.board[(kingPos / 8) - 1][(kingPos % 8) + 2] != null) {
            if (this.board[(kingPos / 8) - 1][(kingPos % 8) + 2].getType() == 3 && this.board[(kingPos / 8) - 1][(kingPos % 8) + 2].getColour() != colour) {
                return true;
            }
        }
        // Two right, one down
        if ((kingPos / 8) + 1 < 8 && (kingPos % 8) + 2 < 8 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 2] != null) {
            if (this.board[(kingPos / 8) + 1][(kingPos % 8) + 2].getType() == 3 && this.board[(kingPos / 8) + 1][(kingPos % 8) + 2].getColour() != colour) {
                return true;
            }
        }
        // One right, two down
        if ((kingPos / 8) + 2 < 8 && (kingPos % 8) + 1 < 8 && this.board[(kingPos / 8) + 2][(kingPos % 8) + 1] != null) {
            if (this.board[(kingPos / 8) + 2][(kingPos % 8) + 1].getType() == 3 && this.board[(kingPos / 8) + 2][(kingPos % 8) + 1].getColour() != colour) {
                return true;
            }
        }
        // One left, two down
        if ((kingPos / 8) + 2 < 8 && (kingPos % 8) - 1 >= 0 && this.board[(kingPos / 8) + 2][(kingPos % 8) - 1] != null) {
            if (this.board[(kingPos / 8) + 2][(kingPos % 8) - 1].getType() == 3 && this.board[(kingPos / 8) + 2][(kingPos % 8) - 1].getColour() != colour) {
                return true;
            }
        }
        // Two left, one down
        if ((kingPos / 8) + 1 < 8 && (kingPos % 8) - 2 >= 0 && this.board[(kingPos / 8) + 1][(kingPos % 8) - 2] != null) {
            if (this.board[(kingPos / 8) + 1][(kingPos % 8) - 2].getType() == 3 && this.board[(kingPos / 8) + 1][(kingPos % 8) - 2].getColour() != colour) {
                return true;
            }
        }
        // Two left, one up
        if ((kingPos / 8) - 1 >= 0 && (kingPos % 8) - 2 >= 0 && this.board[(kingPos / 8) - 1][(kingPos % 8) - 2] != null) {
            if (this.board[(kingPos / 8) - 1][(kingPos % 8) - 2].getType() == 3 && this.board[(kingPos / 8) - 1][(kingPos % 8) - 2].getColour() != colour) {
                return true;
            }
        }
        // One left, two up
        if ((kingPos / 8) - 2 >= 0 && (kingPos % 8) - 1 >= 0 && this.board[(kingPos / 8) - 2][(kingPos % 8) - 1] != null) {
            if (this.board[(kingPos / 8) - 2][(kingPos % 8) - 1].getType() == 3 && this.board[(kingPos / 8) - 2][(kingPos % 8) - 1].getColour() != colour) {
                return true;
            }
        }

        // Checking for rooks or queens
        int col;
        int row;
        // Left
        col = kingPos % 8;
        while (col - 1 >= 0 && this.board[kingPos / 8][col - 1] == null) {
            col--;
        }
        if (col > 0 && (this.board[kingPos / 8][col - 1].getType() == 2 || this.board[kingPos / 8][col - 1].getType() == 5)
                && this.board[kingPos / 8][col - 1].getColour() != colour) {
            return true;
        }
        // Right
        col = kingPos % 8;
        while (col + 1 < 8 && this.board[kingPos / 8][col + 1] == null) {
            col++;
        }
        if (col < 7 && (this.board[kingPos / 8][col + 1].getType() == 2 || this.board[kingPos / 8][col + 1].getType() == 5)
                && this.board[kingPos / 8][col + 1].getColour() != colour) {
            return true;
        }
        // Up
        row = kingPos / 8;
        while (row - 1 >= 0 && this.board[row - 1][kingPos % 8] == null) {
            row--;
        }
        if (row > 0 && (this.board[row - 1][kingPos % 8].getType() == 2 || this.board[row - 1][kingPos % 8].getType() == 5)
                && this.board[row - 1][kingPos % 8].getColour() != colour) {
            return true;
        }
        // Down
        row = kingPos / 8;
        while (row + 1 < 8 && this.board[row + 1][kingPos % 8] == null) {
            row++;
        }
        if (row < 7 && (this.board[row + 1][kingPos % 8].getType() == 2 || this.board[row + 1][kingPos % 8].getType() == 5)
                && this.board[row + 1][kingPos % 8].getColour() != colour) {
            return true;
        }

        // Checking for bishops or queens
        // North west
        col = kingPos % 8;
        row = kingPos / 8;
        while (col - 1 >= 0 && row - 1 >= 0 && this.board[row - 1][col - 1] == null) {
            col--;
            row--;
        }
        if (col > 0 && row > 0 && (this.board[row - 1][col - 1].getType() == 4 || this.board[row - 1][col - 1].getType() == 5)
                && this.board[row - 1][col - 1].getColour() != colour) {
            return true;
        }
        // North east
        col = kingPos % 8;
        row = kingPos / 8;
        while (col + 1 < 8 && row - 1 >= 0 && this.board[row - 1][col + 1] == null) {
            col++;
            row--;
        }
        if (col < 7 && row > 0 && (this.board[row - 1][col + 1].getType() == 4 || this.board[row - 1][col + 1].getType() == 5)
                && this.board[row - 1][col + 1].getColour() != colour) {
            return true;
        }
        // South east
        col = kingPos % 8;
        row = kingPos / 8;
        while (col + 1 < 8 && row + 1 < 8 && this.board[row + 1][col + 1] == null) {
            col++;
            row++;
        }
        if (col < 7 && row < 7 && (this.board[row + 1][col + 1].getType() == 4 || this.board[row + 1][col + 1].getType() == 5)
                && this.board[row + 1][col + 1].getColour() != colour) {
            return true;
        }
        // South west
        col = kingPos % 8;
        row = kingPos / 8;
        while (col - 1 >= 0 && row + 1 < 8 && this.board[row + 1][col - 1] == null) {
            col--;
            row++;
        }

        return col > 0 && row < 7 && (this.board[row + 1][col - 1].getType() == 4 || this.board[row + 1][col - 1].getType() == 5)
                && this.board[row + 1][col - 1].getColour() != colour;
    }

    // Returns the position (0-63) of the given coloured king
    private int determineKingPosition(int colour) {
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                if (this.board[i][j] != null && this.board[i][j].getColour() == colour
                        && this.board[i][j].getType() == 6) {
                    return i * 8 + j;
                }
            }
        }
        return -1;
    }

    // Returns true if the given colour's king is under attack and they have no legal moves
    public boolean isInCheckMate(int colour) {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] != null && this.board[i][j].getColour() == colour) {
                    ArrayList<String> moves = this.getAllMovesForPiece((i * 8) + j);

                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // Returns true if the current player has no possible moves
    public boolean currentPlayerHasNoLegalmoves() {
        return this.getAllMovesForColour(this.currentTurn % 2).isEmpty();
    }

    // Returns ArrayList of strings representing all possible moves for given colour
    private ArrayList<String> getAllMovesForColour(int colour) {
        ArrayList<String> moves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] != null && this.board[i][j].getColour() == colour) {
                    moves = this.combineLists(moves, this.getAllMovesForPiece(i * 8 + j));
                }
            }
        }

        return moves;
    }

    // Returns ArrayList of strings representing all possible moves for given piece
    private ArrayList<String> getAllMovesForPiece(int pos) {
        ArrayList<String> moves = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            String move = "";
            if (pos < 10) {
                move += "0";
            }
            move += pos;

            if (i < 10) {
                move += "0";
            }
            move += i;

            if (i != pos && this.board[pos / 8][pos % 8].isValidMove(move, this.board)) {
                int startPos = Integer.valueOf(move.substring(0, 2));
                int endPos = Integer.valueOf(move.substring(2, 4));

                if (this.board[endPos / 8][endPos % 8] == null) {
                    move += "0";
                } else {
                    move += this.board[endPos / 8][endPos % 8].getType();
                }

                this.moveHistory.add(move);
                this.currentTurn++;

                this.board[endPos / 8][endPos % 8] = this.board[startPos / 8][startPos % 8];
                this.board[startPos / 8][startPos % 8] = null;

                if (!this.isInCheck((this.currentTurn - 1) % 2)) {
                    moves.add(move);
                }
                this.undoMove();
            }
        }

        return moves;
    }

    // Takes in two ArrayLists of strings, returns a single ArrayList containing all elements of both
    private ArrayList<String> combineLists(ArrayList<String> a, ArrayList<String> b) {
        if (a.size() < b.size()) {
            for (int i = 0; i < a.size(); i++) {
                b.add(a.get(i));
            }
            return b;
        } else {
            for (int i = 0; i < b.size(); i++) {
                a.add(b.get(i));
            }
            return a;
        }
    }

    // Returns a string representing the layout of the board
    public String getGameString() {
        String game = "";

        int blank; // Used to count number of blank spaces in a row

        for (int i = 0; i < 8; i++) {
            blank = 0;
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] == null) {
                    blank++;
                } else {
                    if (blank != 0) {
                        game += blank;
                        blank = 0;
                    }
                    switch (this.board[i][j].getType()) {
                        // Capital = white, lower-case = black
                        // is a pawn
                        case 1:
                            game += this.board[i][j].getColour() == 0 ? "P" : "p";
                            break;
                        // is a rook
                        case 2:
                            game += this.board[i][j].getColour() == 0 ? "R" : "r";
                            break;
                        // is a knight
                        case 3:
                            game += this.board[i][j].getColour() == 0 ? "N" : "n";
                            break;
                        // is a bishop
                        case 4:
                            game += this.board[i][j].getColour() == 0 ? "B" : "b";
                            break;
                        // is a queen
                        case 5:
                            game += this.board[i][j].getColour() == 0 ? "Q" : "q";
                            break;
                        // is a king
                        case 6:
                            game += this.board[i][j].getColour() == 0 ? "K" : "k";
                            break;
                    }
                }
            }
            if (blank != 0) {
                game += blank;
            }
            if (i != 7) {
                game += "/";
            }
        }

        game += this.currentTurn % 2 == 0 ? " w" : " b"; // If current turn is even - i.e. white, append " w"

        return game;
    }

    // Takes a string representing the layout of the board and sets the board to this layout
    public void loadGameString(String str) {
        if (str.matches("([rnbqkpRNBQKP1-8]{1,8}\\/){7}([rnbqkpRNBQKP1-8]{1,8}) [wb]")) { // Checks string is in correct format
            String[] parts = str.split(" ");
            String[] rows = parts[0].split("/");
            
            for (int i = 0; i < rows.length; i++) {
                for (int j = 0, col = 0; j < rows[i].length(); j++) {
                    switch (rows[i].charAt(j)) {
                        case 'p':
                            this.board[i][col] = new Pawn(1);
                            col++;
                            break;
                        case 'P':
                            this.board[i][col] = new Pawn(0);
                            col++;
                            break;
                        case 'r':
                            this.board[i][col] = new Rook(1);
                            col++;
                            break;
                        case 'R':
                            this.board[i][col] = new Rook(0);
                            col++;
                            break;
                        case 'n':
                            this.board[i][col] = new Knight(1);
                            col++;
                            break;
                        case 'N':
                            this.board[i][col] = new Knight(0);
                            col++;
                            break;
                        case 'b':
                            this.board[i][col] = new Bishop(1);
                            col++;
                            break;
                        case 'B':
                            this.board[i][col] = new Bishop(0);
                            col++;
                            break;
                        case 'q':
                            this.board[i][col] = new Queen(1);
                            col++;
                            break;
                        case 'Q':
                            this.board[i][col] = new Queen(0);
                            col++;
                            break;
                        case 'k':
                            this.board[i][col] = new King(1);
                            col++;
                            break;
                        case 'K':
                            this.board[i][col] = new King(0);
                            col++;
                            break;
                        default:
                            int num = Integer.valueOf(rows[i].substring(j, j + 1));
                            for (int k = 0; k < num; k++) {
                                this.board[i][col] = null;
                                col++;
                            }
                    }

                }
            }
            
            this.moveHistory = new MoveHistory();
            this.currentTurn = parts[1].equals("w") ? 0 : 1;
            
            if(this.currentTurn % 2 == 1 && this.playingAgainstComputer) {
                this.makeComputerMove();
            }
        }
    }

    // Returns 1 if piece is black, 0 if white, -1 if none
    public int getColourOfPieceAtPosition(int pos) {
        if(this.board[pos / 8][pos % 8] != null) {
            return this.board[pos / 8][pos % 8].getColour();
        } else {
            return -1;
        }
    }
    
    // Returns true if there is a piece, false otherwise
    public boolean hasPieceAtPosition(int pos) {
        return this.board[pos / 8][pos % 8] != null;
    }

    // Returns the board as a 2D array of type Piece
    public Piece[][] getBoard() {
        return this.board;
    }

    // Returns the current turn
    public int getCurrentTurn() {
        return this.currentTurn;
    }

    // Returns true if user is playing against computer
    public boolean isPlayingAgainstComputer() {
        return this.playingAgainstComputer;
    }

    // Returns string representing last move made (or empty string if none have been made)
    public String getLastMove() {
        if (this.moveHistory.getSize() > 0) {
            return this.moveHistory.peek();
        } else {
            return "";
        }
    }

}
