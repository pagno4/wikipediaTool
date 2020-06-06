package model;

import actors.messages.NumberAnswer;
import akka.actor.ActorRef;
import info.PlayerInfo;

import java.util.LinkedList;
import java.util.List;

public class OtherPlayersStore {
    private List<PlayerInfo> others;

    public OtherPlayersStore(){
        this.others = new LinkedList<>();
    }

    public void addPlayer(PlayerInfo info) {
        this.others.add(info);
    }

    /**
     * Select a player to guess.
     * @return A player info. Return null if all other players are solved.
     */
    public PlayerInfo getNextUnSolvedPlayer() {
        for (PlayerInfo info: others) {
            if (!info.isSolved())
                return info;
        }
        return null;
    }

    /**
     * Save a guess made to another player.
     * @param name Another player name.
     * @param guess Guess tried.
     */
    public void saveGuess(String name, SequenceInfoGuess guess) {
        others.stream()
                .filter(f ->
                        f.getName().equals(name))
                .findFirst()
                .ifPresent(player -> {
            player.setTry(guess);
        });
    }

    /**
     * Send the number response to all players except the sender.
     * @param response Response to send.
     * @param self Actor ref to self.
     */
    public void notifyOtherPlayersAboutResponse(SequenceInfoGuess response, ActorRef self){
        others.forEach(playerInfo ->
                playerInfo
                        .getReference()
                        .tell(
                                new NumberAnswer(
                                        response.getRightNumbers(),
                                        response.getRightPlaceNumbers()
                                ),
                                self));
    }
}
