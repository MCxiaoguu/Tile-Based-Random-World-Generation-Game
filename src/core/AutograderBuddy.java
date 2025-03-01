package core;


import tileengine.TETile;
import tileengine.Tileset;
import world.World;

import java.util.List;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        if (input.charAt(0) == 'N' || input.charAt(0) == 'n') {
            String[] seedInputCombo = getSeedFromStringInput(input);
            long seed = Long.parseLong(seedInputCombo[0]);
            System.out.println(seed);
            World w = new World(seed);
            w.initForAutograder();
            for (int i = 0; i < seedInputCombo[1].length(); i++) {
                System.out.println(seedInputCombo[1].charAt(i));
                if (seedInputCombo[1].charAt(i) == ':'
                        && (seedInputCombo[1].charAt(i + 1) == 'q' || seedInputCombo[1].charAt(i + 1) == 'Q')) {
                    System.out.println("detected :");
                    System.out.println(String.format("char at i+1: %s", seedInputCombo[1].charAt(i + 1)));
                    System.out.println("saving");
                    w.save();
                    return w.getAux();
                }
                updateWorld(seedInputCombo[1].charAt(i), w);
            }
            return w.getAux();
        }
        if (input.charAt(0) == 'L' || input.charAt(0) == 'l') {
            World w = World.load("world.txt");
            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == ':') {
                    if (input.charAt(i + 1) == 'q' || input.charAt(i + 1) == 'Q') {
                        w.save();
                        return w.getAux();
                    }
                }
                updateWorld(input.charAt(i), w);
            }
            return w.getAux();
        }
        return null;
    }

    private static void updateWorld(char key, World w) {

        if (key == 'w' || key == 'W') {
            w.getPlayer().tryMove(0, 1);
        } else if (key == 'a' || key == 'A') {
            w.getPlayer().tryMove(-1, 0);
        } else if (key == 's' || key == 'S') {
            w.getPlayer().tryMove(0, -1);
        } else if (key == 'd' || key == 'D') {
            w.getPlayer().tryMove(1, 0);
        }
    }

    private static String[] getSeedFromStringInput(String input) {
        String[] toReturn = new String[2];
        toReturn[0] = "";
        List<Character> numbers = List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == 's' || c == 'S') {
                toReturn[1] = input.substring(i + 1);
                System.out.printf("toReturn %s:\n", toReturn[1]);
                break;
            }
            if (numbers.contains(c)) {
                toReturn[0] += c;
            }
        }
        return toReturn;
    }

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
