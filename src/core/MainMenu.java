package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;

import java.awt.*;

public class MainMenu {
    private static final int MAIN_MENU_W = 40;
    private static final int MAIN_MENU_H = 40;
    private static final int TEXT_X = 20;
    private static final int TEXT1_Y = 30;
    private static final int TEXT2_Y = 28;
    private static final int TEXT3_Y = 26;
    private static final int TEXT4_Y = 24;

    public static void showMainMenu() {
        TERenderer ter = new TERenderer();
        ter.initialize(MAIN_MENU_W, MAIN_MENU_H, 0, 0);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(TEXT_X, TEXT1_Y, "CS61B: My own Game :)");
        StdDraw.text(TEXT_X, TEXT2_Y, "New World(N)");
        StdDraw.text(TEXT_X, TEXT3_Y, "Load from Save(L)");
        StdDraw.text(TEXT_X, TEXT4_Y, "Quit(Q)");
        StdDraw.show();
    }

    public static void seedGUI(String s) {

        StdDraw.enableDoubleBuffering();
        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(TEXT_X, TEXT1_Y, "Please enter a Seed:");
        StdDraw.text(TEXT_X, TEXT2_Y, s);

    }

}
