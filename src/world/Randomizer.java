package world;


import java.util.List;
import java.util.Random;

/*
* Handle the random generating world
*
* */
public class Randomizer {
    private final Random random;

    public Randomizer(long seed) {
        this.random = new Random(seed);
    }
    public int getRandInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }
    public Point getRandPlayerSpawn(List<Rooms> roomList) {
        int x, y;
        Rooms randRoom = roomList.get(getRandInt(0, roomList.size()));
        x = randRoom.getCenter().x() + getRandInt(-randRoom.width() / 2 + 1, randRoom.width() / 2 - 1);
        y = randRoom.getCenter().y() + getRandInt(-randRoom.height() / 2 + 1, randRoom.height() / 2 - 1);
        return new Point(x, y);
    }
    /*
    * return the dimension of the structure. First element is width,
    * the second is the height.
    * */

}
