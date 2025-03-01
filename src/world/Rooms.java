package world;

import tileengine.TETile;
import tileengine.Tileset;


/*
 * Rooms in the world.
 *
 * */
public class Rooms {
    private int width;
    private int height;
    private final Point centerCoord, bottomLeftCorner, bottomRightCorner,
            topRightCorner, topLeftCorner;

    public Rooms(int centerX, int centerY, int w, int h) {
        this.width = w;
        this.height = h;
        this.centerCoord = new Point(centerX, centerY);
        this.bottomLeftCorner = new Point(centerX - w / 2, centerY - h / 2);
        this.bottomRightCorner = new Point(centerX + w / 2, centerY - h / 2);
        this.topLeftCorner = new Point(centerX - w / 2, centerY + h / 2);
        this.topRightCorner = new Point(centerX + w / 2, centerY + h / 2);
    }

    public Point getCenter() {
        return centerCoord;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public static double getDis(Rooms r1, Rooms r2) {
        return Math.sqrt(Math.pow((r1.getCenter().x() - r2.getCenter().x()), 2)
                + Math.pow((r1.getCenter().y() - r2.getCenter().y()), 2));
    }

    public boolean isValid(int w, int h) {
        //check if the room surpasses the edge of the world.
        if (topRightCorner.x() >= w || topRightCorner.y() >= h) {
            return false;
        } else if (topLeftCorner.x() <= 0 || topLeftCorner.y() >= h) {
            return false;
        } else if (bottomLeftCorner.x() <= 0 || bottomLeftCorner.y() <= 0) {
            return false;
        } else {
            return bottomRightCorner.x() <= w && bottomRightCorner.y() >= 0;
        }
    }

    public boolean roomCanGenerate(World w) {
        if (!isValid(w.width(), w.height())) {
            return false;
        }
        for (int x = topLeftCorner.x(); x < topRightCorner.x(); x++) {
            for (int y = bottomLeftCorner.y(); y < topRightCorner.y(); y++) {
                if (w.isOccupied()[x][y]) {
                    //cannot generate according to the isOccupied arr
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * Put rooms in the world tile[][];
     * */
    public void putRoomInWorld(World world) {
        //put four edges of the room in the world
        TETile[][] board = world.getWorld();
        if (isValid(world.width(), world.height())) {
            for (int x = topLeftCorner.x(); x < topRightCorner.x() + 1; x++) {
                board[x][topRightCorner.y()] = Tileset.WALL;
            }
            for (int x = bottomLeftCorner.x(); x < bottomRightCorner.x() + 1; x++) {
                board[x][bottomLeftCorner.y()] = Tileset.WALL;
            }
            for (int y = bottomRightCorner.y(); y < topRightCorner.y() + 1; y++) {
                board[topRightCorner.x()][y] = Tileset.WALL;
            }
            for (int y = bottomLeftCorner.y(); y < topLeftCorner.y() + 1; y++) {
                board[bottomLeftCorner.x()][y] = Tileset.WALL;
            }
            for (int x = topLeftCorner.x() + 1; x < topRightCorner.x(); x++) {
                for (int y = bottomLeftCorner.y() + 1; y < topLeftCorner.y(); y++) {
                    board[x][y] = Tileset.FLOOR;
                }
            }
            //modify the isOccupied arr
            for (int x = topLeftCorner.x(); x < topRightCorner.x(); x++) {
                for (int y = bottomLeftCorner.y(); y < topLeftCorner.y(); y++) {
                    world.isOccupied()[x][y] = true;
                }
            }
        }
    }

    private enum Direction { //direction of two rooms relative to the origin in the center of the board.
        NW, SW,
        VER, HOR;
    }

    //connect r2 with r1 and build hallways
    public static void connectRooms(Rooms r1, Rooms r2, World world) {
        if (r1 != null && r2 != null) {
            int dx = r2.getXDisFrom(r1); //r2-r1
            int dy = r2.getYDisFrom(r1);
            if (dx > 0 && dy > 0) {
                /*      r2
                 *       |
                 * r1 ---O   -> NW
                 * */
                verticalHallway(r1, r2, world, Direction.NW);
                horizontalHallway(r1, r2, world, Direction.NW);
            } else if (dx < 0 && dy > 0) {
                /*
                 * r2 ---O -> SW
                 *       |
                 *       r1
                 * */
                verticalHallway(r1, r2, world, Direction.SW);
                horizontalHallway(r1, r2, world, Direction.SW);
            } else if (dx < 0 && dy < 0) { //r2 is on the top-left dir of r1
                /*
                 *      r1
                 *      |
                 *r2 ---O -> NW, but r1 and r2 are switched
                 * */
                verticalHallway(r2, r1, world, Direction.NW);
                horizontalHallway(r2, r1, world, Direction.NW);
            } else if (dx > 0 && dy < 0) { //r2 is on the bottom-left dir of r1
                /*
                 * r1 ---O -> SW, but r2 r1 switched.
                 *       |
                 *       r2
                 * */
                verticalHallway(r2, r1, world, Direction.SW);
                horizontalHallway(r2, r1, world, Direction.SW);
            } else if (dy == 0) {
                if (dx < 0) { //r2 -- r1
                    horizontalHallway(r1, r2, world, Direction.HOR);
                } else { //dx > 0, r1 -- r2
                    horizontalHallway(r2, r1, world, Direction.HOR);
                }
            } else { //dx == 0
                if (dy < 0) {
                    /*
                     * r2
                     *  |
                     * r1
                     *
                     * */
                    verticalHallway(r2, r1, world, Direction.VER);
                } else {
                    verticalHallway(r1, r2, world, Direction.VER);
                }
            }
        }
    }

    private static boolean isSomething(int x, int y, TETile[][] board, TETile t) {
        return board[x][y] == t;
    }

    private static void verticalHallway(Rooms r1, Rooms r2, World world, Direction dir) {
        TETile[][] board = world.getWorld();
        int xL, xR, startY, endYL, endYR;
        switch (dir) {
            default:
                break;
            /*      r2
             *       |
             * r1 ---O   -> NW
             * */
            case NW:
                xL = r2.getCenter().x() - 1;
                xR = xL + 2;
                startY = r2.getCenter().y() - r2.height / 2;
                endYL = r1.getCenter().y() + 1;
                endYR = endYL - 2;
                for (int y = startY; y > endYL - 1; y--) {
                    if (!world.isOccupied()[xL][y] && y > endYL && !isSomething(xL, y, board, Tileset.FLOOR)) {
                        board[xL][y] = Tileset.WALL;
                        world.isOccupied()[xL][y] = true;
                    }
                    board[xL + 1][y] = Tileset.FLOOR;
                }
                for (int y = startY; y > endYR - 1; y--) {
                    if (!world.isOccupied()[xR][y] && y > endYR && !isSomething(xR, y, board, Tileset.FLOOR)) {
                        board[xR][y] = Tileset.WALL;
                        world.isOccupied()[xL][y] = true;
                    }
                }
                break;
            case SW:
                /*
                 * r2 ---O -> SW
                 *       |
                 *       r1
                 * */
                xL = r1.getCenter().x() - 1;
                xR = xL + 2;
                startY = r1.getCenter().y() + r1.height / 2;
                endYL = r2.getCenter().y() - 1;
                endYR = endYL + 2;
                for (int y = startY; y < endYL + 1; y++) {
                    if (!world.isOccupied()[xL][y] && y < endYL && !isSomething(xL, y, board, Tileset.FLOOR)) {
                        board[xL][y] = Tileset.WALL;
                        world.isOccupied()[xL][y] = true;
                    }
                    board[xL + 1][y] = Tileset.FLOOR;
                }
                for (int y = startY; y < endYR + 1; y++) {
                    if (!world.isOccupied()[xR][y] && y < endYR && !isSomething(xR, y, board, Tileset.FLOOR)) {
                        board[xR][y] = Tileset.WALL;
                        world.isOccupied()[xR][y] = true;
                    }
                }
                break;
            case VER:
                /*
                 * r2
                 * |
                 * r1
                 * */
                xL = r2.getCenter().x() - 1;
                xR = xL + 2;
                startY = r2.getCenter().y() - r2.height / 2;
                endYL = r1.getCenter().y() + r1.height / 2;
                for (int y = startY; y > endYL - 1; y--) {
                    if (!world.isOccupied()[xL][y] && !isSomething(xL, y, board, Tileset.FLOOR)) {
                        board[xL][y] = Tileset.WALL;
                        world.isOccupied()[xL][y] = true;
                        board[xR][y] = Tileset.WALL;
                    }
                    board[xL + 1][y] = Tileset.FLOOR;
                }
                break;
        }
    }

    private static void horizontalHallway(Rooms r1, Rooms r2, World world, Direction dir) {
        TETile[][] board = world.getWorld();
        int yTop, yBot, startX, endXTop, endXBot;
        switch (dir) {
            case NW:
                /*      r2
                 *       |
                 * r1 ---O   -> NW
                 * because we first build vertically, then horizontally
                 * */
                yTop = r1.getCenter().y() + 1;
                yBot = yTop - 2;
                startX = r1.getCenter().x() + r1.width / 2;
                endXTop = r2.getCenter().x() - 1;
                endXBot = endXTop + 2;
                for (int x = startX; x < endXTop + 1; x++) { //builds to the right
                    if (!isSomething(x, yTop, board, Tileset.FLOOR)) {
                        board[x][yTop] = Tileset.WALL;
                        world.isOccupied()[x][yTop] = true;
                    }
                    board[x - 1][yTop - 1] = Tileset.FLOOR;
                }
                for (int x = startX; x < endXBot + 1; x++) { //builds to the right
                    if (!isSomething(x, yBot, board, Tileset.FLOOR)) {
                        board[x][yBot] = Tileset.WALL;
                        world.isOccupied()[x][yBot] = true;
                    }
                    board[x - 1][yBot + 1] = Tileset.FLOOR;
                }
                break;
            case SW:
                /*
                 * r2 ---O -> SW
                 *       |
                 *       r1
                 * */
                yTop = r2.getCenter().y() + 1;
                yBot = yTop - 2;
                startX = r2.getCenter().x() + r2.width / 2;
                endXTop = r1.getCenter().x() + 1;
                endXBot = endXTop - 2;
                for (int x = startX; x < endXTop + 1; x++) { //builds to the right
                    if (!isSomething(x, yTop, board, Tileset.FLOOR)) {
                        board[x][yTop] = Tileset.WALL;
                        world.isOccupied()[x][yTop] = true;
                    }
                    board[x - 1][yTop - 1] = Tileset.FLOOR;
                }
                for (int x = startX; x < endXBot + 1; x++) { //builds to the right
                    if (!isSomething(x, yBot, board, Tileset.FLOOR)) {
                        board[x][yBot] = Tileset.WALL;
                        world.isOccupied()[x][yBot] = true;
                    }
                    board[x - 1][yBot + 1] = Tileset.FLOOR;
                }
                break;
            case VER:
                break;
            case HOR: /* r2 ---- r1 */
                startX = r2.getCenter().x() + r2.width / 2;
                endXTop = r1.getCenter().x() - r1.width / 2;
                yTop = r2.getCenter().y() + 1;
                yBot = yTop - 2;
                for (int x = startX; x < endXTop + 1; x++) { //builds to the right
                    if (!world.isOccupied()[x][yTop] && !isSomething(x, yTop, board, Tileset.FLOOR)) {
                        board[x][yTop] = Tileset.WALL;
                        world.isOccupied()[x][yTop] = true;
                        board[x][yBot] = Tileset.WALL;
                    }
                    board[x][yTop - 1] = Tileset.FLOOR;
                }
                break;
            default:
                break;
        }
    }

    private int getXDisFrom(Rooms r) {
        return this.getCenter().x() - r.getCenter().x();
    }

    private int getYDisFrom(Rooms r) {
        return this.getCenter().y() - r.getCenter().y();
    }


}
