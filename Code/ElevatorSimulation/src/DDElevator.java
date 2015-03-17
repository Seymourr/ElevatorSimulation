
import java.util.LinkedList;
import java.util.Arrays;

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
    public boolean updateElevator() {
        //TODO
        return true;
    }

    /* See ElevatorInterface for details */
    public LinkedList<ElevatorQueueObject> getQueue() {
        return queue;
    }

    /* See ElevatorInterface for details */
    public boolean addToQueue(Passenger p, int index1, int index2, CarPosition c) {
        if (!Arrays.asList(floors).contains(p.getOrigin())) {
            return false;
        }        
        if (!Arrays.asList(floors).contains(p.getDestination())) {
            return false;
        }
        
        ElevatorQueueObject q1 = new ElevatorQueueObject(
            p, ElevatorAction.PICKUP, CarPosition.NULL
        );
        ElevatorQueueObject q2 = new ElevatorQueueObject(
            p, ElevatorAction.DROPOFF, CarPosition.NULL
        );
        
        queue.add(index1, q1);
        queue.add(index2 + 1, q2);
        
        return true;
    }

        /* See ElevatorInterface for details */
    public ElevatorStatusObject getStatus() {
        //TODO
        return null;
    }
}
