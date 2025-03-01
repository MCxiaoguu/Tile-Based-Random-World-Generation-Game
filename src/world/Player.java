package world;


import tileengine.TETile;
import tileengine.Tileset;


/*
 * This class handles the player in the game, '@'. It includes method that, for example,
 * parses keyboard input, player movement, and world interaction.
 * */
public class Player {
    private TETile symbol;
    private Point coord;
    private TETile[][] board; //the board on which the player will try to move.
    private TETile prevTile; //The tile that supposed should be at the position of the player.
    private World world;
    private final int visionRadius = 3; //the diamond shape radius that the player can see when the light is off
    private boolean lightOn;

    public Player(int x, int y, World world, TETile[][] board, TETile symbol,
                  TETile tileBeforeSpawn, boolean lightOnStat) {
        coord = new Point(x, y);
        this.symbol = symbol;
        this.board = board;
        this.prevTile = tileBeforeSpawn;
        this.world = world;
        this.lightOn = lightOnStat;
    }

    public void putPlayerInBoard() {
        System.out.println("put player in board");
        board[coord.x()][coord.y()] = symbol;
    }

    public boolean canMove(int dx, int dy) {
        int newCoordX = coord.x() + dx;
        int newCoordY = coord.y() + dy;
        if (newCoordX > board.length || newCoordX < 0
                || newCoordY > board[0].length || newCoordY < 0) { //don't think it's necessary
            return false;
        }
        return board[newCoordX][newCoordY] == Tileset.FLOOR
                || board[newCoordX][newCoordY] == Tileset.UNLOCKED_DOOR;
    }

    public void tryMove(int dx, int dy) {
        if (canMove(dx, dy)) {
            int newCoordX = coord.x() + dx;
            int newCoordY = coord.y() + dy;
            board[coord.x()][coord.y()] = prevTile;
            prevTile = board[newCoordX][newCoordY];
            board[newCoordX][newCoordY] = symbol;
            coord = new Point(newCoordX, newCoordY);
            world.playMoveSoundEffect("cartoon-bouncing-sound-effect_vVvuDhiQ.wav");
        }
    }

    public TETile[][] getVisibleBoard(TETile[][] originalBoard, boolean isLightOn, int centerX, int centerY) {
        if (isLightOn) {
            return originalBoard;
        } else {
            int boardBound = visionRadius * 2;
            TETile[][] visiblePart = new TETile[originalBoard.length][originalBoard[0].length];
            World.fillTilesWithNothing(visiblePart);
            for (int yExpand = -visionRadius; yExpand <= visionRadius; yExpand++) {
                int xLim = visionRadius - Math.abs(yExpand);
                for (int xDis = -xLim; xDis <= xLim; xDis++) {
                    int realXInBoardR = centerX + xDis;
                    int realXInBoardL = centerX - xDis;
                    int realYInBoardR = centerY + yExpand;
                    int realYInBoardL = centerY - yExpand;
                    if (isXYInBoardInBound(board, realXInBoardR, realYInBoardR)
                            && isXYInBoardInBound(board, realXInBoardL, realYInBoardL)) {
                        visiblePart[realXInBoardR][realYInBoardR] = originalBoard[realXInBoardR][realYInBoardR];
                        visiblePart[realXInBoardR][realYInBoardL] = originalBoard[realXInBoardR][realYInBoardL];
                        visiblePart[realXInBoardL][realYInBoardR] = originalBoard[realXInBoardL][realYInBoardR];
                        visiblePart[realXInBoardL][realYInBoardL] = originalBoard[realXInBoardL][realYInBoardL];
                    }
                }
            }
            return visiblePart;
        }
    }

    private boolean isXYInBoardInBound(TETile[][] b, int x, int y) {
        return x >= 0 && x < b.length && y >= 0 && y < b[0].length;
    }

    public TETile getSymbol() {
        return symbol;
    }

    public Point getCoord() {
        return coord;
    }

    public TETile getPrevTile() {
        return prevTile;
    }

    public void toggleLight() {
        this.lightOn = !this.lightOn;
        System.out.printf("Light On: %s%n", lightOn);
    }

    public boolean getLightOnStat() {
        return lightOn;
    }

    public int getVisibleRange() {
        return visionRadius;
    }

    @Override
    public String toString() {
        return String.format("Player with symbol %s is currently at (%s, %s)",
                this.symbol, this.getCoord().x(), this.getCoord().y());
    }
}
