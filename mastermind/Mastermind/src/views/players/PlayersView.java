package views.players;

import model.Sequence;
import model.SequenceInfoGuess;

public interface PlayersView {
    /**
     * Add a new registered player to the view.
     * Show the name and the generated sequence.
     *
     * @param player Registered player.
     */
    void addPlayer(String player, Sequence sequence);

    /**
     * Show the guess made by a player to another.
     *
     * @param from     Player that tried the guess.
     * @param to       Player that received the guess.
     * @param sequence Sequence info tried.
     */
    void inputSolution(String from, String to, SequenceInfoGuess sequence);

    /**
     * Show a message in a label.
     * @param message Message to show.
     */
    void showMessage(String message);

    /**
     * Show that player from have solved player to.
     * @param from Player solver.
     * @param to Player solved.
     */
    void playerSolved(String from, String to);

    /**
     * Update view for a new game.
     */
    void refresh();
}