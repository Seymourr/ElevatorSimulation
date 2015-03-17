
import java.util.LinkedList;

/**
 * Interface for the elevator implementing classes.
 */
public interface ElevatorInterface {
    /** 
     * Return a list of passengers that disembark on the current floor.
     * Null will be returned if no passengers are leaving on this floor
     * or if the elevator is not currently at any floor.
     */
    public Passenger[] disembarkElevator();

    /**
     * Return a list of passengers that embarks on the current floor,
     * if any. Returns null otherwise.
     */
    public Passenger[] embarkElevator();

    /**
     * Update elevator with one second of travel time. Return the floor
     * reached, if any. If the elevator did not reach a floor, -1 will
     * be returned. For elevators or more than one deck, return the floor
     * the upper car reaches, if any.
     */
    public int updateElevator();

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
     */
    public void addToQueue(Passenger p, int index1, int index2, CarPosition c);
}
