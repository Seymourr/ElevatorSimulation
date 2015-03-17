
/**
 * Class used to represent the status of an elevator with three
 * public but unchangable fields.
 *
 * Floor: Indicates which floor the elevator currently is at
 *  or ex 5.4 if it is between floors 5 and 6.
 * Direction: -1 for down, 0 for idle, 1 for up
 * Destination: The current destination floor of the elevator,
 *  -1 if idle.
 */
public class ElevatorStatusObject {
    /* Fields */
    public final float floor; //Current floor
    public final int direction; //Up or down
    public final int destination; //Headed to which floor
    public final int passengers; //Passengers inside

    /* Constructor */
    public ElevatorStatusObject(float f, int dir, int dest, int cont) {
       floor = f;
       direction = dir;
       destination = dest;
       passengers = cont;
    }
}