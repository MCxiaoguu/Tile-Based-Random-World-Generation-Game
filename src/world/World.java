package world;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;


import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


/*
 * This class handles the generation of the world under seed-based randomness
 * The world uses the TETile[][] 2D array as a backup. As per spec mentioned,
 * "The first coordinate is the x coordinate, e.g. world[9][0] refers to the tile 9
 * spaces over to the right from the bottom left tile. The second coordinate is the y
 * coordinate, and the value increases as we move upwards, e.g. world[0][5] is 5 tiles
 * up from the bottom left tile."
 *
 * */
public class World {

    // build your own world!
    private final int WIDTH; //The width of the board, the first coordinate in the array
    private final int HEIGHT; //The height of the board, the first coordinate in the array
    private static final int DEFAULT_WIDTH = 32;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int ROOM_MIN_WIDTH = 5;
    private static final int ROOM_MAX_WIDTH = 12;
    private static final int MAX_GENERATION_TRIAL = 1000;
    private final long seed;
    private TETile[][] world;
    private TETile[][] auxBoard;
    private int area;
    private int size;
    final Randomizer randomizer;
    private final TERenderer ter;
    private boolean[][] isOccupied;
    private List<Rooms> roomsList;
    private Player player;
    private long prevActionTimestamp;
    private final double MOUSE_X = 3.5;

    /*
     * Instantiating a new world under given seed, width, and height
     *
     * */
    public World(long seed, int w, int h) {
        this.ter = new TERenderer();
        this.seed = seed;
        WIDTH = w;
        HEIGHT = h;
        this.randomizer = new Randomizer(seed);
        this.area = 0;
        this.size = WIDTH * HEIGHT;
        world = new TETile[WIDTH][HEIGHT];
        isOccupied = new boolean[WIDTH][HEIGHT];
        roomsList = new ArrayList<>();
        fillTilesWithNothing(world);
    }

    public World(long seed) {
        this(seed, DEFAULT_WIDTH, DEFAULT_HEIGHT); //default width and height
    }

    public World(String seed, int w, int h) {
        this(Long.parseLong(seed), w, h);
    }

    public World(TETile[][] board, Long s) {
        world = board;
        WIDTH = board.length;
        HEIGHT = board[0].length;
        seed = s;
        this.randomizer = new Randomizer(seed);
        ter = new TERenderer();
        auxBoard = new TETile[WIDTH][HEIGHT];
    }

    public World(String seed) {
        this(Long.parseLong(seed), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public int width() {
        return WIDTH;
    }

    public int height() {
        return HEIGHT;
    }

    public boolean[][] isOccupied() {
        return isOccupied;
    }

    public TETile[][] getWorld() {
        return world;
    }

    /*
     * Fill the tiles[][] arr with NOTHING.
     * */
    public static void fillTilesWithNothing(TETile[][] tle) {
        for (int col = 0; col < tle.length; col++) {
            for (int row = 0; row < tle[0].length; row++) {
                tle[col][row] = Tileset.NOTHING;
            }
        }
    }

    public void initialize() {
        randomRooms();
        fillAux();
        spawnPlayer();
        ter.initialize(world.length, world[0].length + 3, 0, 2);
    }

    public void initForAutograder() {
        randomRooms();
        fillAux();
        spawnPlayer();
    }

    public void fillAux() {
        auxBoard = new TETile[world.length][world[0].length];
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                auxBoard[i][j] = world[i][j];
            }
        }
    }

    /*
     * Maybe useful in future lol.
     * */
    public long getSeed() {
        return this.seed;
    }

    public void renderBoard(TETile[][] board) {
        //ter.initialize(board.length, board[0].length);
        //if (player.getLightOnStat()){ //Full-game light on
        //ter.drawTiles(board);
        //   ter.renderFrame(board);
        //}
        renderWithLimitedVision(board);
    }

    public void renderWithLimitedVision(TETile[][] oGBoard) {
        //ter.drawTiles(OGBoard);
        ter.drawTiles(player.getVisibleBoard(oGBoard, player.getLightOnStat(),
                player.getCoord().x(), player.getCoord().y()));
    }

    public void randomRooms() {
        Rooms lastRoom = null;
        int trial = 0;
        while (area < 0.5 * size) {
            if (trial > MAX_GENERATION_TRIAL) {
                return;
            }
            int randWidth = randomizer.getRandInt(ROOM_MIN_WIDTH, ROOM_MAX_WIDTH);
            int randHeight = randomizer.getRandInt(ROOM_MIN_WIDTH, ROOM_MAX_WIDTH);
            int randX = randomizer.getRandInt(0, WIDTH);
            int randY = randomizer.getRandInt(0, HEIGHT);
            Rooms randRoom = new Rooms(randX, randY, randWidth, randHeight);

            if (randRoom.roomCanGenerate(this)) {
                //System.out.printf(String.format("Adding room with center at (%s, %s) "
                //        + "with width of %s and height of %s\n", randX, randY, randWidth, randHeight));
                //System.out.printf(this.toString());
                roomsList.add(randRoom);
                area += randRoom.width() * randRoom.height();
                randRoom.putRoomInWorld(this);
                Rooms.connectRooms(lastRoom, randRoom, this);
                lastRoom = randRoom;
            } else {
                trial++;
            }
        }
    }

    public void spawnPlayer() {
        Point pointSpawnCoord = randomizer.getRandPlayerSpawn(roomsList);
        TETile tileBeforeSpawn = auxBoard[pointSpawnCoord.x()][pointSpawnCoord.y()];
        //put player on the aux board
        player = new Player(pointSpawnCoord.x(), pointSpawnCoord.y(), this, auxBoard,
                Tileset.AVATAR, tileBeforeSpawn, false);
        player.putPlayerInBoard();
    }

    public Player getPlayer() {
        return player;
    }

    public TETile[][] getAux() {
        return auxBoard;
    }

    @Override
    public String toString() {
        String board = "";
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                board = board.concat(String.valueOf(world[x][y].character()));
            }
            board = board.concat("\n");
        }
        return board;
    }

    public void save() {
        int width = world.length;
        int height = world[0].length;
        String s = width + " " + height + "\n";
        String w = toString();
        s += w;
        //store player info
        String p = String.format("%s %s %s %s\n", this.player.getCoord().x(),
                this.player.getCoord().y(), this.player.getPrevTile().character(),
                boolToInt(this.player.getLightOnStat()));
        s += p;
        s += String.format("%s\n", this.getSeed());
        FileUtils.writeFile("world.txt", s);
    }

    private int boolToInt(boolean b) {
        if (b) {
            return 1;
        }
        return 0;
    }

    public static World load(String fileName) {
        String read = FileUtils.readFile(fileName);
        String[] lines = read.split("\n");

        int width = Integer.parseInt(lines[0].split(" ")[0]);
        int height = Integer.parseInt(lines[0].split(" ")[1]);
        TETile[][] tiles = new TETile[width][height];
        int iter = 1;
        for (int x = 0; x < width; x++) {
            String line = lines[iter];
            for (int y = 0; y < height; y++) {
                char c = line.charAt(y);
                tiles[x][y] = charToTile(c);
            }
            iter++;
        }
        String[] player = lines[iter].split(" ");
        long seed = Long.parseLong(lines[iter + 1]);
        World w = new World(tiles, seed);
        System.out.println("Finished board loading");
        //System.out.println(w);
        w.fillAux();
        w.player = new Player(Integer.parseInt(player[0]), Integer.parseInt(player[1]), w,
                w.auxBoard, Tileset.AVATAR, charToTile(player[2].charAt(0)), intToBool(Integer.parseInt(player[3])));
        //System.out.println(w.player);
        System.out.println("Finished player loadings");
        w.player.putPlayerInBoard();
        w.ter.initialize(w.world.length, w.world[0].length + 3, 0, 2);
        return w;
    }

    private static boolean intToBool(int i) {
        return i == 1;
    }

    private static TETile charToTile(char c) {
        if (c == '#') {
            return Tileset.WALL;
        } else if (c == '·') {
            return Tileset.FLOOR;
        } else if (c == '@') {
            return Tileset.AVATAR;
        } else if (c == ' ') {
            return Tileset.NOTHING;
        }
        return Tileset.NOTHING;
    }

    public void quitSave() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'q' || key == 'Q') {
                    save();
                    System.exit(0);
                } else {
                    return;
                }
            }
        }
    }

    public void updateWorld() {

        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            if (key == 'w' || key == 'W') {
                player.tryMove(0, 1);
            } else if (key == 'a' || key == 'A') {
                player.tryMove(-1, 0);
            } else if (key == 's' || key == 'S') {
                player.tryMove(0, -1);
            } else if (key == 'd' || key == 'D') {
                player.tryMove(1, 0);
            } else if (key == 'L' | key == 'l') {
                player.toggleLight();
            } else if (key == ':') {
                quitSave();
            }
        }
    }

    public void updateMouse() {
        TETile t = mouseTile();
        if (t == null) {
            return;
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(MOUSE_X, HEIGHT - 1, "Tile: " + t.description());
    }

    public void updateTime() {
        Date d = new Date();
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH - 5, 1, d.toString());
        StdDraw.show(); //only one show() method among all methods in the main game loop, so no more flickering
    }

    public TETile mouseTile() {
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY() - 2; //considered the yOffSet
        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            return auxBoard[(int) mouseX][(int) mouseY];
        }
        return null;
    }

    public void runGame() {
        StdDraw.enableDoubleBuffering();
        playMainMusic("retro-city-14099.wav");
        while (true) {
            updateMouse();
            updateTime();
            updateWorld();
            renderBoard(getAux());
        }
    }

    public void playMainMusic(String filePath) {

        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }

    public void playMoveSoundEffect(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }


    /* need to implement */
    public boolean gameOver() {
        return true;
    }

}
