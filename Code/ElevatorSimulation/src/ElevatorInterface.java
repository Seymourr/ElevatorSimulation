
import java.util.LinkedList;

/**
 * Interface for the elevator implementing classes.
 */
public interface ElevatorInterface {
    /** 
     * Allows passengers to embark and disembark on the current floor
     * of the elevator. All passengers inside the elevator will disembark
     * if the stop is at their destination floor. However, only passengers
     * who are at the beginning of the queue will embark the elevator.
     * Returns a list of the passengers who disembarks.
     */
    public Passenger[] openDoors();

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
}
