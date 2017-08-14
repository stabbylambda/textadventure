package website.sdfp.textadventure;

import org.immutables.value.Value;
import website.sdfp.textadventure.library.GameLoop;
import website.sdfp.textadventure.library.GameState;

import java.util.Random;

import static website.sdfp.textadventure.library.GameStateUtils.*;

class Game {
    @Value.Immutable
    public interface GameData {
        int dice();
    }

    private static Random random = new Random();

    static GameState logic(ImmutableGameData currentData, String userInput) {
        switch(userInput) {
            case "":
                return next("you just hit enter...what do you want?", currentData);
            case "roll":
                int newDice = random.nextInt(20);
                ImmutableGameData newGameData = currentData.withDice(newDice);
                return next("You rolled a " + newGameData.dice() + ". Now what?", newGameData);
        }

        return end("that's not a valid option");
    }

    public static void main(String[] args) throws Exception {
        ImmutableGameData gameData = ImmutableGameData
                .builder()
                .dice(10)
                .build();

        GameLoop.run(gameData, "Hi!", Game::logic);
    }
}
