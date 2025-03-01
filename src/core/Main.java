package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import world.World;

import java.awt.*;
import java.util.List;

public class Main {
    private static final int MAIN_MENU_W = 40;
    private static final int MAIN_MENU_H = 40;
    private static boolean isGameStarted = false;
    private static World world;

    public static void main(String[] args) {
        MainMenu.showMainMenu();
        while (!isGameStarted) {
            if (StdDraw.hasNextKeyTyped()) {
                world = parseKeyInput(StdDraw.nextKeyTyped());
            }
        }
        world.runGame();
    }

    private static World parseKeyInput(char key) {
        if (key == 'N' || key == 'n') {
            isGameStarted = true;
            world = new World(getSeedFromGUI());
            world.initialize();
            world.renderBoard(world.getAux());
            return world;
        }
        if (key == 'L' || key == 'l') {
            isGameStarted = true;
            return World.load("world.txt");
        } else if (key == 'q' || key == 'Q') {
            System.exit(0);
        }
        return null;
    }

    private static long getSeedFromGUI() {
        String seed = "";
        List<Character> numbers = List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
        TERenderer ter = new TERenderer();
        ter.initialize(MAIN_MENU_W, MAIN_MENU_H, 0, 0);
        while (true) {
            MainMenu.seedGUI(seed);
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.show();
                char c = StdDraw.nextKeyTyped();
                if (c == 's' || c == 'S') {
                    break;
                }
                if (numbers.contains(c)) {
                    seed += c;
                }
            }
        }
        return Long.parseLong(seed);
    }
}
