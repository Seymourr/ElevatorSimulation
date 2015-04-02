
import java.util.LinkedList;
import java.util.HashMap;

/**
 * Interface for the elevator implementing classes. The set of zoning floors
 * potentially used by the elevator implementing this interface are inteded 
 * as storage assistance for the algorithm only. No check for the validity
 * for these are performed.
 */
public interface ElevatorInterface {
    /**
     * Returns a semi-deep copy of this Elevator.
     */
    public ElevatorInterface duplicate();
    
    /**
     * Resets the service status counters to 0.
     */
    public void resetSerivceStatus();
    
    /** 
     * Allows passengers to embark and disembark on the current floor
     * of the elevator. All passengers inside the elevator will disembark
     * if the stop is at their destination floor. However, only passengers
     * who are at the beginning of the queue will embark the elevator.
     * Returns a list of the passengers who disembarks at each CarPosition.
     */
    public HashMap<CarPosition, Passenger[]> openDoors();

    /**
     * Update elevator with one second of travel time.
     *
     * @return true if successful.
     */
    public boolean updateElevator();

    /**
     * Returns the current queue of assignments for the elevator. The
     * element at index 0 is handles first and so on.
     */
    public LinkedList<ElevatorQueueObject> getQueue();

    /**
     * Adds Passenger p to the queue. Assigns that p should be picked
     * up on index1 in the queue and dropped off at index2 in the
     * queue. The fact the index1 pushes index2 by one will be 
     * compensated for in the method. For decks with multiple positions
     * the CarPositions can be set to UPPER or LOWER. If the elevator
     * is single decked, the CarPosition is irrelevant.
     *
     * @return true if the additions were successful.
     */
    public boolean addToQueue(Passenger p, int index1, int index2, CarPosition c);
    
    /**
     * Returns the current status of the elevator. See the description
     * of ElevatorStatusObject for details.
     */
    public ElevatorStatusObject getStatus();
    
    /**
     * Returns an ElevatorServiceStatus which shows various service records
     * for the elevator. See documentation for ElevatorServiceStatus for details.
     */
    public ElevatorServiceStatus getRecords();
    
    /**
     * Returns ElevatorType.SINGLE or ElevatorType.DOUBLE depending on the type
     * of elevator this object is.
     */
    public ElevatorType ofType();
    
    /**
     * Returns a list of floors this elevator operates on.
     */
    public int[] getFloors();
    
    /**
     * Returns a list of floors this elevator is zoned for.
     * This will return the same floors as getFloors() if
     * zoning is not used.
     */
    public int[] getZonedFloors();
}
