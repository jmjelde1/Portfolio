package teacher;

import game.Game;
import utils.DatabaseStaticHandler;

public class Teacher {

    public final int teacherId;
    public final String firstName;
    public final String lastName;
    private final Game game;

    public Teacher(int teacherId, String firstName, String lastName, final Game game) {
        this.teacherId = teacherId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }

}
