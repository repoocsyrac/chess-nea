
public abstract class Piece {
    
    private int type; // 1 = pawn, 2 = rook, 3 = knight, 4 = bishop, 5 = queen, 6 = king
    private int colour; // 0 = white, 1 = black
    private String img; // String containing location of image
    
    public Piece(int type, int colour, String img) {
        this.type = type;
        this.colour = colour;
        this.img = img;
    }
    
    public abstract boolean isValidMove(String move, Piece[][] board);
    
    @Override
    public abstract String toString();
    
    public int getColour() {
        return this.colour;
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getImage() {
        return this.img;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public void setColour(int colour) {
        this.colour = colour;
    }
    
    public void setImage(String img) {
        this.img = img;
    }
    
}



