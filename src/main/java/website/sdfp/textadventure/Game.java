package website.sdfp.textadventure;

import website.sdfp.textadventure.library.GameLoop;
import website.sdfp.textadventure.library.GameState;

import java.util.Random;

import static website.sdfp.textadventure.library.GameStateUtils.end;
import static website.sdfp.textadventure.library.GameStateUtils.next;


class Game {
    static Random rng = new Random();
    static class Player {
        final String name;
        final boolean rolled;
        final int diceRoll;
        final int money;

        public Player(String name, int money) {
            this(name, money, false, -1);
        }

        public Player(String name, int money, boolean rolled, int diceRoll) {
            this.name = name;
            this.rolled = rolled;
            this.diceRoll = diceRoll;
            this.money = money;
        }

        @Override
        public String toString() {
            String status = rolled ? "rolled a " + diceRoll : "hasn't rolled";
            return name + " ($" + money + ") - " + status;
        }
    }
    static class GameData {
        private int rounds;
        private Player player1;
        private Player player2;

        public GameData(int rounds, Player player1, Player player2) {
            this.rounds = rounds;
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public String toString() {
            String p1Status = player1 == null ? "No player 1" : player1.toString();
            String p2Status = player2 == null ? "No player 2" : player2.toString();

            return "Round: " + rounds + "\n" + p1Status + "\n" + p2Status + "\n";
        }
    }


    public static Player rollDice(Player p) {
        return new Player(p.name, p.money, true, (rng.nextInt(6) + 1));
    }


    static GameState handleInput(GameData currentData, String userInput) {
        GameData updated;


        switch(userInput) {
            case "p1setup":
                Player p1 = new Player("Maiia", 10);
                updated = new GameData(0, p1, currentData.player2);

                return next(updated + "Now what?", updated);
            case "p2setup":
                Player p2 = new Player("David", 5);
                updated = new GameData(0, currentData.player1, p2);
                return next(updated + "Now what?", updated);
            case "r":
                updated = new GameData(currentData.rounds + 1, rollDice(currentData.player1), rollDice(currentData.player2));
                return compare(updated);

            default:
                return next("You just hit enter...what do you want?", currentData);
        }

    }

    private static GameState compare(GameData currentData) {
        if(currentData.player1 != null && currentData.player1.rolled && currentData.player2 != null && currentData.player2.rolled) {
            String announceWinner;
            Player updatedPlayer1, updatedPlayer2;
            boolean player1Wins = (currentData.player1.diceRoll > currentData.player2.diceRoll);
            if(player1Wins) {
                announceWinner = currentData.player1.name + " wins!";
                updatedPlayer1 = new Player(currentData.player1.name, currentData.player1.money + 1);
                updatedPlayer2 = new Player(currentData.player2.name, currentData.player2.money - 1);
            } else {
                announceWinner = currentData.player2.name + " wins!";
                updatedPlayer1 = new Player(currentData.player1.name, currentData.player1.money - 1);
                updatedPlayer2 = new Player(currentData.player2.name, currentData.player2.money + 1);
            }

            GameData reset = new GameData(currentData.rounds, updatedPlayer1, updatedPlayer2);
            if(reset.player1.money == 0 || reset.player2.money == 0) {
                return end(announceWinner + "The game is over!");

            }
            return next(announceWinner + "\n" + currentData, reset);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        GameData gameData = new GameData(0,new Player("Maiia", 10), new Player("David", 5));
        GameLoop.run(gameData, "Hi!", Game::handleInput);
    }
}
