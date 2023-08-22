
import java.util.ArrayList;

public class MoveHistory {

    private ArrayList<String> moves;

    public MoveHistory() {
        this.moves = new ArrayList<>();
    }

    // Adds move to end of list
    public void add(String move) {
        this.moves.add(move);
    }

    // Removes and returns move at end of list
    public String pop() {
        if(this.moves.size() > 0) {
            String move = this.moves.get(this.moves.size() - 1);
            this.moves.remove(this.moves.size() - 1);
            return move;
        }
        return "";
    }

    // Returns move at end of list
    public String peek() {
        if(this.moves.size() > 0) {
            return this.moves.get(this.moves.size() - 1);
        }
        return "";
    }

    // Returns number of moves in list
    public int getSize() {
        return this.moves.size();
    }

}

