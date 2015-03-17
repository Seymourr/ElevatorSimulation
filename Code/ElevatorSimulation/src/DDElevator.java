
import java.util.LinkedList;

/**
 * This class represents a double decked elevator.
 */
public class DDElevator {
    /* Fields */
    private ElevatorSpecs specs;
    private int[] floors;
    private LinkedList<ElevatorQueueObject> queue;
    private int currentPassengers;
    
    /**
     * Constructor 
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     */
    public DDElevator(ElevatorSpecs spec, int[] floors) {
        specs = spec;
        this.floors = floors;
        currentPassengers = 0;
        queue = new LinkedList<ElevatorQueueObject>();
    }
    
    /* See ElevatorInterface for details */
    public Passenger[] disembarkElevator() {
        //TODO
        return null;
    }

    /* See ElevatorInterface for details */
    public Passenger[] embarkElevator() {
        //TODO
        return null;
    }

    /* See ElevatorInterface for details */
    public int updateElevator() {
        //TODO
        return 0;
    }

    /* See ElevatorInterface for details */
    public LinkedList<ElevatorQueueObject> getQueue() {
        return queue;
    }

    /* See ElevatorInterface for details */
    public void addToQueue(Passenger p, int index1, int index2, CarPosition c) {
        //TODO
    }

        /* See ElevatorInterface for details */
    public ElevatorStatusObject getStatus() {
        //TODO
        return null;
    }
}
