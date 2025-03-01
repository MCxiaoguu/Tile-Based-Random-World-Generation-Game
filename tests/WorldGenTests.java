import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;
import world.World;

import static com.google.common.truth.Truth.assertThat;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        // TODO: write a test that uses an input like "n123swasdwasd"
    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }

    @Test
    public void testGetWorldFromInput() {
        TETile[][] world1 = AutograderBuddy.getWorldFromInput("N1234SWWAADD:Q");
        TETile[][] world2 = AutograderBuddy.getWorldFromInput("N1234SWW:Q");
        world2 = AutograderBuddy.getWorldFromInput("LAADD");
        //System.out.printf("world2: %s\n", world2);
        System.out.println(String.format("world1: %s", new World(world1, 1234L)));
        System.out.println(String.format("world2: %s", new World(world2, 1234L)));
        assertThat(world1).isEqualTo(world2);
    }
}
