
/**
 * Class used to represent the status of an elevator with three
 * public but unchangable fields.
 *
 * Floor: Indicates which floor the elevator currently is at
 *  or ex 5.4 if it is between floors 5 and 6.
 * Direction: -1 for down, 0 for idle, 1 for up. 0 is only given if the queue is empty.
 * Destination: The current destination floor of the elevator,
 *  -1 if idle.
 * Passengers: Total no of passengers (in all cars)
 * upperCarPassengers: Passengers in upper car for double deckers, -1 for single deckers
 * lowerCarpassengers: Passengers in lower car for double deckers, -1 for single deckers
 */
public class ElevatorStatusObject {
    /* Fields */
    public final float floor; //Current floor (upper floor for double decked)
    public final int direction; //Up or down
    public final int destination; //Headed to which floor

    /* Constructor for single deckers */
    public ElevatorStatusObject(float f, int dir, int dest) {
       floor = f;
       direction = dir;
       destination = dest;
    }
    
    /* Converts this ElevatorStatusObject to a nice little String */
    public String getStringRepresentation() {
        String s = "*********** ELEVATOR STATUS ********** \n" +
            "Current Floor: " + floor + "\n" +
            "Current Direction: " + direction + "\n" +
            "Current Destination: " + destination + "\n" +
            "************************************";
        return s;
    }
}