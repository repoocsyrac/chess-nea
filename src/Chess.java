
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;

public class Chess extends javax.swing.JFrame {

    private ChessBoard board;
    private int startPos; // Location of piece user selects to move, -1 = not yet chosen
    private int endPos; // Location user selects to move piece to, -1 = not yet chosen
    private Color pieceSelectionColour;
    private Color validPositionColour;
    private Color noAvailableMovesColour;
    private Color previousMoveColour;
    private Color lightSquareColour;
    private Color darkSquareColour;

    private JButton[][] buttons;

    /**
     * Creates new form NewJFrame
     */
    public Chess() {
        initComponents();
        this.board = new ChessBoard();
        this.board.selectPlayer(this, true); // Asks user whether they are playing against the computer

        this.startPos = -1;
        this.endPos = -1;

        this.pieceSelectionColour = Color.CYAN;
        this.validPositionColour = new Color(244, 255, 82); // Yellow
        this.noAvailableMovesColour = new Color(255, 0, 53); // Red
        this.previousMoveColour = Color.ORANGE;
        this.lightSquareColour = new Color(219, 228, 238); // Light blue
        this.darkSquareColour = new Color(127, 158, 195); // Dark blue

        this.buttons = new JButton[][]{
            {this.jButtonA8, this.jButtonB8, this.jButtonC8, this.jButtonD8, this.jButtonE8, this.jButtonF8, this.jButtonG8, this.jButtonH8},
            {this.jButtonA7, this.jButtonB7, this.jButtonC7, this.jButtonD7, this.jButtonE7, this.jButtonF7, this.jButtonG7, this.jButtonH7},
            {this.jButtonA6, this.jButtonB6, this.jButtonC6, this.jButtonD6, this.jButtonE6, this.jButtonF6, this.jButtonG6, this.jButtonH6},
            {this.jButtonA5, this.jButtonB5, this.jButtonC5, this.jButtonD5, this.jButtonE5, this.jButtonF5, this.jButtonG5, this.jButtonH5},
            {this.jButtonA4, this.jButtonB4, this.jButtonC4, this.jButtonD4, this.jButtonE4, this.jButtonF4, this.jButtonG4, this.jButtonH4},
            {this.jButtonA3, this.jButtonB3, this.jButtonC3, this.jButtonD3, this.jButtonE3, this.jButtonF3, this.jButtonG3, this.jButtonH3},
            {this.jButtonA2, this.jButtonB2, this.jButtonC2, this.jButtonD2, this.jButtonE2, this.jButtonF2, this.jButtonG2, this.jButtonH2},
            {this.jButtonA1, this.jButtonB1, this.jButtonC1, this.jButtonD1, this.jButtonE1, this.jButtonF1, this.jButtonG1, this.jButtonH1}
        };
        
        this.updateGUI();
    }

    // Updates text of buttons & JLabels, resets button colours, displays dialog if game has ended
    public void updateGUI() {
        Piece[][] cb = this.board.getBoard(); // Gets the array of Pieces
        // Sets icon of buttons to show the board to the user
        for (int i = 0; i < cb.length; i++) {
            for (int j = 0; j < cb[i].length; j++) {
                if (cb[i][j] != null) { // If there is a piece at this location
                    this.buttons[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource(cb[i][j].getImage())));
                } else {
                    this.buttons[i][j].setIcon(null);
                }
            }
        }

        // Sets JLabel telling user whose turn it is
        if (this.board.getCurrentTurn() % 2 == 0) {
            this.turnLabel.setText("White To Move");
        } else {
            this.turnLabel.setText("Black To Move");
        }

        this.resetButtonColours();

        // Determines whether the game has ended
        if (this.board.isInCheck(0)) { // If white is in check
            this.checkLabel.setText("White Is In Check");
            if (this.board.isInCheckMate(0)) {
                ShowWinner winnerDialog = new ShowWinner(new javax.swing.JFrame(), true);
                winnerDialog.setWinner(1);
                winnerDialog.setVisible(true);
                this.endGame();
            }
        } else if (this.board.isInCheck(1)) { // If black is in check
            this.checkLabel.setText("Black Is In Check");
            if (this.board.isInCheckMate(1)) {
                ShowWinner winnerDialog = new ShowWinner(new javax.swing.JFrame(), true);
                winnerDialog.setWinner(0);
                winnerDialog.setVisible(true);
                this.endGame();
            }
        } else if (this.board.currentPlayerHasNoLegalmoves()) {
            ShowWinner winnerDialog = new ShowWinner(new javax.swing.JFrame(), true);
            winnerDialog.setWinner(2);
            winnerDialog.setVisible(true);
            this.endGame();
        } else {
            this.checkLabel.setText("");
        }
    }

    // Allows user to select positions on the board and make moves
    public void buttonPressed(int pos) {
        // If piece to move not chosen and location is not empty and piece belongs to current player
        if (this.startPos == -1 && this.board.hasPieceAtPosition(pos)
                && this.board.getColourOfPieceAtPosition(pos) == this.board.getCurrentTurn() % 2) {
            this.buttons[pos / 8][pos % 8].setBackground(this.pieceSelectionColour); // Highlight selected piece in different colour
            this.showPossibleEndPositions(pos);

            this.startPos = pos;
        } else if (this.endPos == -1 && this.startPos != -1) { // If piece to move is chosen but location to move it to is not chosen
            this.endPos = pos;

            this.board.makeMove(this.generateMoveString());
            this.updateGUI();

            this.startPos = -1;
            this.endPos = -1;

            // If user is playing against the computer and it is blacks turn to move, allow computer to move
            if (this.board.isPlayingAgainstComputer() && this.board.getCurrentTurn() % 2 == 1) {
                this.board.makeComputerMove();
                this.updateGUI();
            }
        }
    }

    // Generates string representing move using startPos and endPos
    private String generateMoveString() {
        String move = "";

        // Each position should be two digits long so add leading "0" if < 10
        if (this.startPos < 10) {
            move += "0";
        }
        move += this.startPos;

        if (this.endPos < 10) {
            move += "0";
        }
        move += this.endPos;

        return move;
    }

    // Highlights places piece can move to in different colour
    private void showPossibleEndPositions(int pos) {
        boolean[][] validPositions = this.board.getValidEndPositions(pos);
        boolean canMakeMove = false;

        for (int i = 0; i < this.buttons.length; i++) {
            for (int j = 0; j < this.buttons[i].length; j++) {
                if (validPositions[i][j]) {
                    this.buttons[i][j].setBackground(this.validPositionColour); // Highlight valid end position in different colour
                    canMakeMove = true;
                }
            }
        }

        if (!canMakeMove) {
            this.buttons[pos / 8][pos % 8].setBackground(this.noAvailableMovesColour); // Highlight piece in different colour
        }
    }

    // Resets button colours to default
    private void resetButtonColours() {
        // Resets background colour of all buttons:
        for (int i = 0; i < this.buttons.length; i++) {
            for (int j = 0; j < this.buttons[i].length; j++) {
                if (i % 2 == 0 && j % 2 == 0 || i % 2 != 0 && j % 2 != 0) {
                    this.buttons[i][j].setBackground(this.lightSquareColour);
                } else {
                    this.buttons[i][j].setBackground(this.darkSquareColour);
                }
            }
        }
        
        // Sets border of all buttons
        for (int i = 0; i < 64; i++) {
            if ((i / 8) % 2 == 0 && (i % 8) % 2 != 0 || (i / 8) % 2 != 0 && (i % 8) % 2 == 0) {
                this.buttons[i / 8][i % 8].setBorder(javax.swing.BorderFactory.createLineBorder(this.darkSquareColour, 10));
            } else {
                this.buttons[i / 8][i % 8].setBorder(javax.swing.BorderFactory.createLineBorder(this.lightSquareColour, 10));
            }
        }

        String lastMove = this.board.getLastMove(); // Last move made, empty = move has not yet been made
        if (!lastMove.isEmpty()) {
            int startPos = Integer.valueOf(lastMove.substring(0, 2)); // First two chars = start position
            int endPos = Integer.valueOf(lastMove.substring(2, 4)); // Next two chars = end position
            // Highlights previous move in different colour:
            this.buttons[startPos / 8][startPos % 8].setBackground(this.previousMoveColour);
            this.buttons[endPos / 8][endPos % 8].setBackground(this.previousMoveColour);
        }
    }

    // Resets the board / restarts the game
    private void endGame() {
        this.board = new ChessBoard();
        this.board.selectPlayer(this, true); // Asks user whether they are playing against the computer
        this.startPos = -1;
        this.endPos = -1;
        this.updateGUI();
    }

    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonA8 = new javax.swing.JButton();
        jButtonB8 = new javax.swing.JButton();
        jButtonC8 = new javax.swing.JButton();
        jButtonD8 = new javax.swing.JButton();
        jButtonE8 = new javax.swing.JButton();
        jButtonF8 = new javax.swing.JButton();
        jButtonG8 = new javax.swing.JButton();
        jButtonH8 = new javax.swing.JButton();
        jButtonA7 = new javax.swing.JButton();
        jButtonB7 = new javax.swing.JButton();
        jButtonC7 = new javax.swing.JButton();
        jButtonD7 = new javax.swing.JButton();
        jButtonE7 = new javax.swing.JButton();
        jButtonF7 = new javax.swing.JButton();
        jButtonG7 = new javax.swing.JButton();
        jButtonH7 = new javax.swing.JButton();
        jButtonA6 = new javax.swing.JButton();
        jButtonB6 = new javax.swing.JButton();
        jButtonC6 = new javax.swing.JButton();
        jButtonD6 = new javax.swing.JButton();
        jButtonE6 = new javax.swing.JButton();
        jButtonF6 = new javax.swing.JButton();
        jButtonG6 = new javax.swing.JButton();
        jButtonH6 = new javax.swing.JButton();
        jButtonA5 = new javax.swing.JButton();
        jButtonB5 = new javax.swing.JButton();
        jButtonC5 = new javax.swing.JButton();
        jButtonD5 = new javax.swing.JButton();
        jButtonE5 = new javax.swing.JButton();
        jButtonF5 = new javax.swing.JButton();
        jButtonG5 = new javax.swing.JButton();
        jButtonH5 = new javax.swing.JButton();
        jButtonA4 = new javax.swing.JButton();
        jButtonB4 = new javax.swing.JButton();
        jButtonC4 = new javax.swing.JButton();
        jButtonD4 = new javax.swing.JButton();
        jButtonE4 = new javax.swing.JButton();
        jButtonF4 = new javax.swing.JButton();
        jButtonG4 = new javax.swing.JButton();
        jButtonH4 = new javax.swing.JButton();
        jButtonA3 = new javax.swing.JButton();
        jButtonB3 = new javax.swing.JButton();
        jButtonC3 = new javax.swing.JButton();
        jButtonD3 = new javax.swing.JButton();
        jButtonE3 = new javax.swing.JButton();
        jButtonF3 = new javax.swing.JButton();
        jButtonG3 = new javax.swing.JButton();
        jButtonH3 = new javax.swing.JButton();
        jButtonA2 = new javax.swing.JButton();
        jButtonB2 = new javax.swing.JButton();
        jButtonC2 = new javax.swing.JButton();
        jButtonD2 = new javax.swing.JButton();
        jButtonE2 = new javax.swing.JButton();
        jButtonF2 = new javax.swing.JButton();
        jButtonG2 = new javax.swing.JButton();
        jButtonH2 = new javax.swing.JButton();
        jButtonA1 = new javax.swing.JButton();
        jButtonB1 = new javax.swing.JButton();
        jButtonC1 = new javax.swing.JButton();
        jButtonD1 = new javax.swing.JButton();
        jButtonE1 = new javax.swing.JButton();
        jButtonF1 = new javax.swing.JButton();
        jButtonG1 = new javax.swing.JButton();
        jButtonH1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        turnLabel = new javax.swing.JLabel();
        checkLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openGameMenuItem = new javax.swing.JMenuItem();
        saveGameMenuItem = new javax.swing.JMenuItem();
        gameMenu = new javax.swing.JMenu();
        surrenderMenuItem = new javax.swing.JMenuItem();
        undoMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        bkgdColMenuItem = new javax.swing.JMenuItem();
        lightSquareColMenuItem = new javax.swing.JMenuItem();
        darkSquareColMenuUtem = new javax.swing.JMenuItem();
        textColMenuItem = new javax.swing.JMenuItem();
        selectedPieceColMenuItem = new javax.swing.JMenuItem();
        validPosColMenuItem = new javax.swing.JMenuItem();
        noMovesColMenuItem = new javax.swing.JMenuItem();
        prevMoveColMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chess");
        setResizable(false);

        backgroundPanel.setBackground(new java.awt.Color(189, 208, 229));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setMaximumSize(new java.awt.Dimension(750, 750));
        jPanel2.setMinimumSize(new java.awt.Dimension(750, 750));
        jPanel2.setPreferredSize(new java.awt.Dimension(750, 750));
        jPanel2.setLayout(new java.awt.GridLayout(8, 8));

        jButtonA8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonA8.setFocusPainted(false);
        jButtonA8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA8);

        jButtonB8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonB8.setFocusPainted(false);
        jButtonB8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB8);

        jButtonC8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonC8.setFocusPainted(false);
        jButtonC8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC8);

        jButtonD8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonD8.setFocusPainted(false);
        jButtonD8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD8);

        jButtonE8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonE8.setFocusPainted(false);
        jButtonE8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE8);

        jButtonF8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonF8.setFocusPainted(false);
        jButtonF8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF8);

        jButtonG8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonG8.setFocusPainted(false);
        jButtonG8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG8);

        jButtonH8.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH8.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonH8.setFocusPainted(false);
        jButtonH8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH8ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH8);

        jButtonA7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonA7.setFocusPainted(false);
        jButtonA7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA7);

        jButtonB7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonB7.setFocusPainted(false);
        jButtonB7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB7);

        jButtonC7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonC7.setFocusPainted(false);
        jButtonC7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC7);

        jButtonD7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonD7.setFocusPainted(false);
        jButtonD7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD7);

        jButtonE7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonE7.setFocusPainted(false);
        jButtonE7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE7);

        jButtonF7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonF7.setFocusPainted(false);
        jButtonF7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF7);

        jButtonG7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonG7.setFocusPainted(false);
        jButtonG7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG7);

        jButtonH7.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH7.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonH7.setFocusPainted(false);
        jButtonH7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH7);

        jButtonA6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonA6.setFocusPainted(false);
        jButtonA6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA6);

        jButtonB6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonB6.setFocusPainted(false);
        jButtonB6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB6);

        jButtonC6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonC6.setFocusPainted(false);
        jButtonC6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC6);

        jButtonD6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonD6.setFocusPainted(false);
        jButtonD6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD6);

        jButtonE6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonE6.setFocusPainted(false);
        jButtonE6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE6);

        jButtonF6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonF6.setFocusPainted(false);
        jButtonF6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF6);

        jButtonG6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonG6.setFocusPainted(false);
        jButtonG6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG6);

        jButtonH6.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH6.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonH6.setFocusPainted(false);
        jButtonH6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH6);

        jButtonA5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonA5.setFocusPainted(false);
        jButtonA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA5);

        jButtonB5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonB5.setFocusPainted(false);
        jButtonB5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB5);

        jButtonC5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonC5.setFocusPainted(false);
        jButtonC5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC5);

        jButtonD5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonD5.setFocusPainted(false);
        jButtonD5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD5);

        jButtonE5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonE5.setFocusPainted(false);
        jButtonE5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE5);

        jButtonF5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonF5.setFocusPainted(false);
        jButtonF5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF5);

        jButtonG5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonG5.setFocusPainted(false);
        jButtonG5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG5);

        jButtonH5.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH5.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonH5.setFocusPainted(false);
        jButtonH5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH5);

        jButtonA4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonA4.setFocusPainted(false);
        jButtonA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA4);

        jButtonB4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonB4.setFocusPainted(false);
        jButtonB4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB4);

        jButtonC4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonC4.setFocusPainted(false);
        jButtonC4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC4);

        jButtonD4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonD4.setFocusPainted(false);
        jButtonD4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD4);

        jButtonE4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonE4.setFocusPainted(false);
        jButtonE4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE4);

        jButtonF4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonF4.setFocusPainted(false);
        jButtonF4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF4);

        jButtonG4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonG4.setFocusPainted(false);
        jButtonG4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG4);

        jButtonH4.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH4.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonH4.setFocusPainted(false);
        jButtonH4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH4);

        jButtonA3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonA3.setFocusPainted(false);
        jButtonA3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA3);

        jButtonB3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonB3.setFocusPainted(false);
        jButtonB3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB3);

        jButtonC3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonC3.setFocusPainted(false);
        jButtonC3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC3);

        jButtonD3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonD3.setFocusPainted(false);
        jButtonD3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD3);

        jButtonE3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonE3.setFocusPainted(false);
        jButtonE3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE3);

        jButtonF3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonF3.setFocusPainted(false);
        jButtonF3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF3);

        jButtonG3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonG3.setFocusPainted(false);
        jButtonG3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG3);

        jButtonH3.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH3.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonH3.setFocusPainted(false);
        jButtonH3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH3);

        jButtonA2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonA2.setFocusPainted(false);
        jButtonA2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA2);

        jButtonB2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonB2.setFocusPainted(false);
        jButtonB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB2);

        jButtonC2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonC2.setFocusPainted(false);
        jButtonC2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC2);

        jButtonD2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonD2.setFocusPainted(false);
        jButtonD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD2);

        jButtonE2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonE2.setFocusPainted(false);
        jButtonE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE2);

        jButtonF2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonF2.setFocusPainted(false);
        jButtonF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF2);

        jButtonG2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonG2.setFocusPainted(false);
        jButtonG2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG2);

        jButtonH2.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH2.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonH2.setFocusPainted(false);
        jButtonH2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH2);

        jButtonA1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonA1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonA1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonA1.setFocusPainted(false);
        jButtonA1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonA1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonA1);

        jButtonB1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonB1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonB1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonB1.setFocusPainted(false);
        jButtonB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonB1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonB1);

        jButtonC1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonC1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonC1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonC1.setFocusPainted(false);
        jButtonC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonC1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonC1);

        jButtonD1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonD1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonD1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonD1.setFocusPainted(false);
        jButtonD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonD1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonD1);

        jButtonE1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonE1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonE1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonE1.setFocusPainted(false);
        jButtonE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonE1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonE1);

        jButtonF1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonF1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonF1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonF1.setFocusPainted(false);
        jButtonF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonF1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonF1);

        jButtonG1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonG1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonG1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 46, 80), 10));
        jButtonG1.setFocusPainted(false);
        jButtonG1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonG1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonG1);

        jButtonH1.setBackground(new java.awt.Color(182, 199, 198));
        jButtonH1.setFont(new java.awt.Font("Arial Unicode MS", 0, 50)); // NOI18N
        jButtonH1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 228, 238), 10));
        jButtonH1.setFocusPainted(false);
        jButtonH1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonH1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonH1);

        jLabel1.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel1.setText("8");

        jLabel2.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel2.setText("7");

        jLabel3.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel3.setText("6");

        jLabel4.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel4.setText("5");

        jLabel5.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel5.setText("4");

        jLabel6.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel6.setText("3");

        jLabel7.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel7.setText("2");

        jLabel8.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel8.setText("1");

        jLabel9.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel9.setText("8");

        jLabel10.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel10.setText("7");

        jLabel11.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel11.setText("6");

        jLabel12.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel12.setText("5");

        jLabel13.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel13.setText("4");

        jLabel14.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel14.setText("3");

        jLabel15.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel15.setText("2");

        jLabel16.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel16.setText("1");

        jLabel17.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel17.setText("a");

        jLabel18.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel18.setText("b");

        jLabel19.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel19.setText("c");

        jLabel20.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel20.setText("d");

        jLabel21.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel21.setText("e");

        jLabel22.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel22.setText("f");

        jLabel23.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel23.setText("g");

        jLabel24.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel24.setText("h");

        jLabel25.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel25.setText("a");

        jLabel26.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel26.setText("b");

        jLabel27.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel27.setText("c");

        jLabel28.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel28.setText("d");

        jLabel29.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel29.setText("e");

        jLabel30.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel30.setText("f");

        jLabel31.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel31.setText("g");

        jLabel32.setFont(new java.awt.Font("Rubik", 1, 18)); // NOI18N
        jLabel32.setText("h");

        turnLabel.setFont(new java.awt.Font("Microsoft JhengHei", 1, 24)); // NOI18N
        turnLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        turnLabel.setText("White To Move");

        checkLabel.setFont(new java.awt.Font("Microsoft JhengHei", 1, 24)); // NOI18N
        checkLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        checkLabel.setText("-");

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(turnLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(checkLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgroundPanelLayout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backgroundPanelLayout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 750, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)))
                            .addGroup(backgroundPanelLayout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(jLabel17)
                                .addGap(82, 82, 82)
                                .addComponent(jLabel18)
                                .addGap(80, 80, 80)
                                .addComponent(jLabel19)
                                .addGap(80, 80, 80)
                                .addComponent(jLabel20)
                                .addGap(80, 80, 80)
                                .addComponent(jLabel21)
                                .addGap(84, 84, 84)
                                .addComponent(jLabel22)
                                .addGap(82, 82, 82)
                                .addComponent(jLabel23)
                                .addGap(81, 81, 81)
                                .addComponent(jLabel24))))
                    .addGroup(backgroundPanelLayout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jLabel25)
                        .addGap(84, 84, 84)
                        .addComponent(jLabel26)
                        .addGap(77, 77, 77)
                        .addComponent(jLabel27)
                        .addGap(85, 85, 85)
                        .addComponent(jLabel28)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel29)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel30)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel31)
                        .addGap(81, 81, 81)
                        .addComponent(jLabel32)))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgroundPanelLayout.createSequentialGroup()
                        .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backgroundPanelLayout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 750, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel32)
                                    .addComponent(jLabel31))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(72, 72, 72)
                                .addComponent(jLabel2)
                                .addGap(70, 70, 70)
                                .addComponent(jLabel3)
                                .addGap(71, 71, 71)
                                .addComponent(jLabel4)
                                .addGap(71, 71, 71)
                                .addComponent(jLabel5)
                                .addGap(69, 69, 69)
                                .addComponent(jLabel6)
                                .addGap(72, 72, 72)
                                .addComponent(jLabel7)
                                .addGap(76, 76, 76)
                                .addComponent(jLabel8)
                                .addGap(76, 76, 76))))
                    .addGroup(backgroundPanelLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel9)
                        .addGap(71, 71, 71)
                        .addComponent(jLabel10)
                        .addGap(75, 75, 75)
                        .addComponent(jLabel11)
                        .addGap(69, 69, 69)
                        .addComponent(jLabel12)
                        .addGap(71, 71, 71)
                        .addComponent(jLabel13)
                        .addGap(70, 70, 70)
                        .addComponent(jLabel14)
                        .addGap(71, 71, 71)
                        .addComponent(jLabel15)
                        .addGap(72, 72, 72)
                        .addComponent(jLabel16)
                        .addGap(33, 33, 33)))
                .addComponent(turnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(checkLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fileMenu.setText("File");

        openGameMenuItem.setText("Open Game...");
        openGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openGameMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openGameMenuItem);

        saveGameMenuItem.setText("Save Game...");
        saveGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGameMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveGameMenuItem);

        jMenuBar1.add(fileMenu);

        gameMenu.setText("Game");

        surrenderMenuItem.setText("Surrender");
        surrenderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surrenderMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(surrenderMenuItem);

        undoMenuItem.setText("Undo");
        undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(undoMenuItem);

        jMenuBar1.add(gameMenu);

        viewMenu.setText("View");

        bkgdColMenuItem.setText("Change Background Colour");
        bkgdColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkgdColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(bkgdColMenuItem);

        lightSquareColMenuItem.setText("Change Light Square Colour");
        lightSquareColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightSquareColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(lightSquareColMenuItem);

        darkSquareColMenuUtem.setText("Change Dark Square Colour");
        darkSquareColMenuUtem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkSquareColMenuUtemActionPerformed(evt);
            }
        });
        viewMenu.add(darkSquareColMenuUtem);

        textColMenuItem.setText("Change Text Colour");
        textColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(textColMenuItem);

        selectedPieceColMenuItem.setText("Change Piece Selection Colour");
        selectedPieceColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedPieceColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(selectedPieceColMenuItem);

        validPosColMenuItem.setText("Change Valid Position Colour");
        validPosColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validPosColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(validPosColMenuItem);

        noMovesColMenuItem.setText("Change No Available Moves Colour");
        noMovesColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noMovesColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(noMovesColMenuItem);

        prevMoveColMenuItem.setText("Change Previous Move Colour");
        prevMoveColMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevMoveColMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(prevMoveColMenuItem);

        jMenuBar1.add(viewMenu);

        helpMenu.setText("Help");

        helpMenuItem.setText("View Help");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        // TODO add your handling code here:
        // Shows help dialog to user
        Help helpDialog = new Help(new javax.swing.JFrame(), false);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_helpMenuItemActionPerformed

    private void bkgdColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkgdColMenuItemActionPerformed
        // TODO add your handling code here:
        // Asks user to select a colour
        Color newColour = JColorChooser.showDialog(this, "Select Background Colour", new Color(189, 208, 229));
        if (newColour != null) { // If they picked a colour
            // Sets colour of JPanel acting as background to this colour
            this.backgroundPanel.setBackground(newColour);
        }
    }//GEN-LAST:event_bkgdColMenuItemActionPerformed

    private void jButtonA8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(0);
    }//GEN-LAST:event_jButtonA8ActionPerformed

    private void jButtonB8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(1);
    }//GEN-LAST:event_jButtonB8ActionPerformed

    private void jButtonC8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(2);
    }//GEN-LAST:event_jButtonC8ActionPerformed

    private void jButtonD8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(3);
    }//GEN-LAST:event_jButtonD8ActionPerformed

    private void jButtonE8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(4);
    }//GEN-LAST:event_jButtonE8ActionPerformed

    private void jButtonF8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(5);
    }//GEN-LAST:event_jButtonF8ActionPerformed

    private void jButtonG8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(6);
    }//GEN-LAST:event_jButtonG8ActionPerformed

    private void jButtonH8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH8ActionPerformed
        // TODO add your handling code here:
        buttonPressed(7);
    }//GEN-LAST:event_jButtonH8ActionPerformed

    private void jButtonA7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(8);
    }//GEN-LAST:event_jButtonA7ActionPerformed

    private void jButtonB7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(9);
    }//GEN-LAST:event_jButtonB7ActionPerformed

    private void jButtonC7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(10);
    }//GEN-LAST:event_jButtonC7ActionPerformed

    private void jButtonD7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(11);
    }//GEN-LAST:event_jButtonD7ActionPerformed

    private void jButtonE7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(12);
    }//GEN-LAST:event_jButtonE7ActionPerformed

    private void jButtonF7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(13);
    }//GEN-LAST:event_jButtonF7ActionPerformed

    private void jButtonG7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(14);
    }//GEN-LAST:event_jButtonG7ActionPerformed

    private void jButtonH7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH7ActionPerformed
        // TODO add your handling code here:
        buttonPressed(15);
    }//GEN-LAST:event_jButtonH7ActionPerformed

    private void jButtonA6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(16);
    }//GEN-LAST:event_jButtonA6ActionPerformed

    private void jButtonB6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(17);
    }//GEN-LAST:event_jButtonB6ActionPerformed

    private void jButtonC6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(18);
    }//GEN-LAST:event_jButtonC6ActionPerformed

    private void jButtonD6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(19);
    }//GEN-LAST:event_jButtonD6ActionPerformed

    private void jButtonE6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(20);
    }//GEN-LAST:event_jButtonE6ActionPerformed

    private void jButtonF6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(21);
    }//GEN-LAST:event_jButtonF6ActionPerformed

    private void jButtonG6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(22);
    }//GEN-LAST:event_jButtonG6ActionPerformed

    private void jButtonH6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH6ActionPerformed
        // TODO add your handling code here:
        buttonPressed(23);
    }//GEN-LAST:event_jButtonH6ActionPerformed

    private void jButtonA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(24);
    }//GEN-LAST:event_jButtonA5ActionPerformed

    private void jButtonB5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(25);
    }//GEN-LAST:event_jButtonB5ActionPerformed

    private void jButtonC5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(26);
    }//GEN-LAST:event_jButtonC5ActionPerformed

    private void jButtonD5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(27);
    }//GEN-LAST:event_jButtonD5ActionPerformed

    private void jButtonE5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(28);
    }//GEN-LAST:event_jButtonE5ActionPerformed

    private void jButtonF5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(29);
    }//GEN-LAST:event_jButtonF5ActionPerformed

    private void jButtonG5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(30);
    }//GEN-LAST:event_jButtonG5ActionPerformed

    private void jButtonH5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH5ActionPerformed
        // TODO add your handling code here:
        buttonPressed(31);
    }//GEN-LAST:event_jButtonH5ActionPerformed

    private void jButtonA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(32);
    }//GEN-LAST:event_jButtonA4ActionPerformed

    private void jButtonB4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(33);
    }//GEN-LAST:event_jButtonB4ActionPerformed

    private void jButtonC4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(34);
    }//GEN-LAST:event_jButtonC4ActionPerformed

    private void jButtonD4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(35);
    }//GEN-LAST:event_jButtonD4ActionPerformed

    private void jButtonE4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(36);
    }//GEN-LAST:event_jButtonE4ActionPerformed

    private void jButtonF4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(37);
    }//GEN-LAST:event_jButtonF4ActionPerformed

    private void jButtonG4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(38);
    }//GEN-LAST:event_jButtonG4ActionPerformed

    private void jButtonH4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH4ActionPerformed
        // TODO add your handling code here:
        buttonPressed(39);
    }//GEN-LAST:event_jButtonH4ActionPerformed

    private void jButtonA3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(40);
    }//GEN-LAST:event_jButtonA3ActionPerformed

    private void jButtonB3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(41);
    }//GEN-LAST:event_jButtonB3ActionPerformed

    private void jButtonC3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(42);
    }//GEN-LAST:event_jButtonC3ActionPerformed

    private void jButtonD3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(43);
    }//GEN-LAST:event_jButtonD3ActionPerformed

    private void jButtonE3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(44);
    }//GEN-LAST:event_jButtonE3ActionPerformed

    private void jButtonF3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(45);
    }//GEN-LAST:event_jButtonF3ActionPerformed

    private void jButtonG3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(46);
    }//GEN-LAST:event_jButtonG3ActionPerformed

    private void jButtonH3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH3ActionPerformed
        // TODO add your handling code here:
        buttonPressed(47);
    }//GEN-LAST:event_jButtonH3ActionPerformed

    private void jButtonA2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(48);
    }//GEN-LAST:event_jButtonA2ActionPerformed

    private void jButtonB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(49);
    }//GEN-LAST:event_jButtonB2ActionPerformed

    private void jButtonC2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(50);
    }//GEN-LAST:event_jButtonC2ActionPerformed

    private void jButtonD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(51);
    }//GEN-LAST:event_jButtonD2ActionPerformed

    private void jButtonE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(52);
    }//GEN-LAST:event_jButtonE2ActionPerformed

    private void jButtonF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(53);
    }//GEN-LAST:event_jButtonF2ActionPerformed

    private void jButtonG2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(54);
    }//GEN-LAST:event_jButtonG2ActionPerformed

    private void jButtonH2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH2ActionPerformed
        // TODO add your handling code here:
        buttonPressed(55);
    }//GEN-LAST:event_jButtonH2ActionPerformed

    private void jButtonA1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonA1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(56);
    }//GEN-LAST:event_jButtonA1ActionPerformed

    private void jButtonB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonB1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(57);
    }//GEN-LAST:event_jButtonB1ActionPerformed

    private void jButtonC1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonC1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(58);
    }//GEN-LAST:event_jButtonC1ActionPerformed

    private void jButtonD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonD1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(59);
    }//GEN-LAST:event_jButtonD1ActionPerformed

    private void jButtonE1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonE1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(60);
    }//GEN-LAST:event_jButtonE1ActionPerformed

    private void jButtonF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonF1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(61);
    }//GEN-LAST:event_jButtonF1ActionPerformed

    private void jButtonG1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonG1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(62);
    }//GEN-LAST:event_jButtonG1ActionPerformed

    private void jButtonH1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonH1ActionPerformed
        // TODO add your handling code here:
        buttonPressed(63);
    }//GEN-LAST:event_jButtonH1ActionPerformed

    private void surrenderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_surrenderMenuItemActionPerformed
        // TODO add your handling code here:
        this.endGame();
    }//GEN-LAST:event_surrenderMenuItemActionPerformed

    private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
        // TODO add your handling code here:
        if (this.board.getCurrentTurn() > 1) { // If two or moves have been made
            this.board.undoMove();
            this.board.undoMove();
            this.updateGUI();
        }
    }//GEN-LAST:event_undoMenuItemActionPerformed

    private void saveGameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGameMenuItemActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();

        // Shows dialog to user asking them to select a file, returns JFileChooser.APPROVE_OPTION if they have selected one:
        boolean userHasSelectedFile = fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION;

        if (userHasSelectedFile) {
            File file = fileChooser.getSelectedFile(); // Gets the file the user selected
            String fen = this.board.getGameString(); // Generates string representing the board
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(fen); // Writes this string to the file
                writer.close(); // Closes the file
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }//GEN-LAST:event_saveGameMenuItemActionPerformed

    private void lightSquareColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightSquareColMenuItemActionPerformed
        // TODO add your handling code here:
        // Change light square colour:
        Color newColour = JColorChooser.showDialog(this, "Select Light Square Colour", new Color(219, 228, 238));
        if (newColour != null) {
            this.lightSquareColour = newColour;
            this.updateGUI();
        }
    }//GEN-LAST:event_lightSquareColMenuItemActionPerformed

    private void darkSquareColMenuUtemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_darkSquareColMenuUtemActionPerformed
        // TODO add your handling code here:
        // Change dark square colour:
        Color newColour = JColorChooser.showDialog(this, "Select Dark Square Colour", new Color(127, 158, 195));
        if (newColour != null) {
            this.darkSquareColour = newColour;
            this.updateGUI();
        }
    }//GEN-LAST:event_darkSquareColMenuUtemActionPerformed

    private void textColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textColMenuItemActionPerformed
        // TODO add your handling code here:
        // Change colour of all JLabels:
        Color newColour = JColorChooser.showDialog(this, "Select Text Colour", Color.BLACK);
        if (newColour != null) {
            this.jLabel1.setForeground(newColour);
            this.jLabel2.setForeground(newColour);
            this.jLabel3.setForeground(newColour);
            this.jLabel4.setForeground(newColour);
            this.jLabel5.setForeground(newColour);
            this.jLabel6.setForeground(newColour);
            this.jLabel7.setForeground(newColour);
            this.jLabel8.setForeground(newColour);
            this.jLabel9.setForeground(newColour);
            this.jLabel10.setForeground(newColour);
            this.jLabel11.setForeground(newColour);
            this.jLabel12.setForeground(newColour);
            this.jLabel13.setForeground(newColour);
            this.jLabel14.setForeground(newColour);
            this.jLabel15.setForeground(newColour);
            this.jLabel16.setForeground(newColour);
            this.jLabel17.setForeground(newColour);
            this.jLabel18.setForeground(newColour);
            this.jLabel19.setForeground(newColour);
            this.jLabel20.setForeground(newColour);
            this.jLabel21.setForeground(newColour);
            this.jLabel22.setForeground(newColour);
            this.jLabel23.setForeground(newColour);
            this.jLabel24.setForeground(newColour);
            this.jLabel25.setForeground(newColour);
            this.jLabel26.setForeground(newColour);
            this.jLabel27.setForeground(newColour);
            this.jLabel28.setForeground(newColour);
            this.jLabel29.setForeground(newColour);
            this.jLabel30.setForeground(newColour);
            this.jLabel31.setForeground(newColour);
            this.jLabel32.setForeground(newColour);
            this.checkLabel.setForeground(newColour);
            this.turnLabel.setForeground(newColour);
        }
    }//GEN-LAST:event_textColMenuItemActionPerformed

    private void selectedPieceColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedPieceColMenuItemActionPerformed
        // TODO add your handling code here:
        // Change piece selection colour:
        Color newColour = JColorChooser.showDialog(this, "Select Piece Selection Colour", Color.CYAN);
        if (newColour != null) {
            this.pieceSelectionColour = newColour;
        }
    }//GEN-LAST:event_selectedPieceColMenuItemActionPerformed

    private void validPosColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validPosColMenuItemActionPerformed
        // TODO add your handling code here:
        // Change valid position colour:
        Color newColour = JColorChooser.showDialog(this, "Select Valid Position Colour", new Color(244, 255, 82));
        if (newColour != null) {
            this.validPositionColour = newColour;
        }
    }//GEN-LAST:event_validPosColMenuItemActionPerformed

    private void noMovesColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noMovesColMenuItemActionPerformed
        // TODO add your handling code here:
        // Change no avaiable moves colour:
        Color newColour = JColorChooser.showDialog(this, "Select No Available Moves Colour", new Color(255, 0, 53));
        if (newColour != null) {
            this.noAvailableMovesColour = newColour;
        }
    }//GEN-LAST:event_noMovesColMenuItemActionPerformed

    private void prevMoveColMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevMoveColMenuItemActionPerformed
        // TODO add your handling code here:
        // Change previous move colour:
        Color newColour = JColorChooser.showDialog(this, "Select Previous Move Colour", Color.ORANGE);
        if (newColour != null) {
            this.previousMoveColour = newColour;
        }
    }//GEN-LAST:event_prevMoveColMenuItemActionPerformed

    private void openGameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openGameMenuItemActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();

        // Shows dialog to user asking them to select a file, returns JFileChooser.APPROVE_OPTION if they have selected one:
        boolean userHasSelectedFile = fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION;

        if (userHasSelectedFile) {
            File file = fileChooser.getSelectedFile(); // Gets the file the user selected
            try {
                Scanner reader = new Scanner(file);
                if (reader.hasNextLine()) { // If the file has a next line
                    String fen = reader.nextLine(); // Get the next line from the file
                    this.board.loadGameString(fen); // Load this string into the board
                }
                this.updateGUI();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }//GEN-LAST:event_openGameMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Chess().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JMenuItem bkgdColMenuItem;
    private javax.swing.JLabel checkLabel;
    private javax.swing.JMenuItem darkSquareColMenuUtem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu gameMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton jButtonA1;
    private javax.swing.JButton jButtonA2;
    private javax.swing.JButton jButtonA3;
    private javax.swing.JButton jButtonA4;
    private javax.swing.JButton jButtonA5;
    private javax.swing.JButton jButtonA6;
    private javax.swing.JButton jButtonA7;
    private javax.swing.JButton jButtonA8;
    private javax.swing.JButton jButtonB1;
    private javax.swing.JButton jButtonB2;
    private javax.swing.JButton jButtonB3;
    private javax.swing.JButton jButtonB4;
    private javax.swing.JButton jButtonB5;
    private javax.swing.JButton jButtonB6;
    private javax.swing.JButton jButtonB7;
    private javax.swing.JButton jButtonB8;
    private javax.swing.JButton jButtonC1;
    private javax.swing.JButton jButtonC2;
    private javax.swing.JButton jButtonC3;
    private javax.swing.JButton jButtonC4;
    private javax.swing.JButton jButtonC5;
    private javax.swing.JButton jButtonC6;
    private javax.swing.JButton jButtonC7;
    private javax.swing.JButton jButtonC8;
    private javax.swing.JButton jButtonD1;
    private javax.swing.JButton jButtonD2;
    private javax.swing.JButton jButtonD3;
    private javax.swing.JButton jButtonD4;
    private javax.swing.JButton jButtonD5;
    private javax.swing.JButton jButtonD6;
    private javax.swing.JButton jButtonD7;
    private javax.swing.JButton jButtonD8;
    private javax.swing.JButton jButtonE1;
    private javax.swing.JButton jButtonE2;
    private javax.swing.JButton jButtonE3;
    private javax.swing.JButton jButtonE4;
    private javax.swing.JButton jButtonE5;
    private javax.swing.JButton jButtonE6;
    private javax.swing.JButton jButtonE7;
    private javax.swing.JButton jButtonE8;
    private javax.swing.JButton jButtonF1;
    private javax.swing.JButton jButtonF2;
    private javax.swing.JButton jButtonF3;
    private javax.swing.JButton jButtonF4;
    private javax.swing.JButton jButtonF5;
    private javax.swing.JButton jButtonF6;
    private javax.swing.JButton jButtonF7;
    private javax.swing.JButton jButtonF8;
    private javax.swing.JButton jButtonG1;
    private javax.swing.JButton jButtonG2;
    private javax.swing.JButton jButtonG3;
    private javax.swing.JButton jButtonG4;
    private javax.swing.JButton jButtonG5;
    private javax.swing.JButton jButtonG6;
    private javax.swing.JButton jButtonG7;
    private javax.swing.JButton jButtonG8;
    private javax.swing.JButton jButtonH1;
    private javax.swing.JButton jButtonH2;
    private javax.swing.JButton jButtonH3;
    private javax.swing.JButton jButtonH4;
    private javax.swing.JButton jButtonH5;
    private javax.swing.JButton jButtonH6;
    private javax.swing.JButton jButtonH7;
    private javax.swing.JButton jButtonH8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JMenuItem lightSquareColMenuItem;
    private javax.swing.JMenuItem noMovesColMenuItem;
    private javax.swing.JMenuItem openGameMenuItem;
    private javax.swing.JMenuItem prevMoveColMenuItem;
    private javax.swing.JMenuItem saveGameMenuItem;
    private javax.swing.JMenuItem selectedPieceColMenuItem;
    private javax.swing.JMenuItem surrenderMenuItem;
    private javax.swing.JMenuItem textColMenuItem;
    private javax.swing.JLabel turnLabel;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JMenuItem validPosColMenuItem;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
