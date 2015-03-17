
import java.util.LinkedList;
import java.util.Arrays;

/**
 * This class represents an single decked elevator.
 */
public class Elevator implements ElevatorInterface {
    /* Fields */
    private ElevatorSpecs specs;
    private int[] floors;
    private LinkedList<ElevatorQueueObject> queue;
    private int currentPassengers;
    private int waitingTime;
    private float currentFloor;
    private int distancePerFloor;
    
    /**
     * Constructor 
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     */
    public Elevator(ElevatorSpecs spec, int[] floors, float currentFloor) {
        specs = spec;
        this.floors = floors;
        currentPassengers = 0;
        queue = new LinkedList<ElevatorQueueObject>();
        waitingTime = 0;
        this.currentFloor = currentFloor;
        distancePerFloor = specs.getBuildingHeight() / specs.getFloors();
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
        if (waitingTime > 0) {
            waitingTime -= 1;
            return true;
        }
        
        //Fetch next destination
        ElevatorQueueObject q = queue.getFirst();
        int dest = 0;
        if (q.getActionType() == ElevatorAction.PICKUP) {
            dest = q.getPassenger().getOrigin();
        } else {
            dest = q.getPassenger().getDestination();
        }
        
        //Check destination is valid
        if (!Arrays.asList(floors).contains(dest)) {
            return false;
        }
            
        //Update Elevator Position 
        float newFloor = currentFloor;
        if (dest > currentFloor) { //Going up
            newFloor += (specs.getCarSpeed() / distancePerFloor);
            if (dest <= newFloor) { //Reached destination?
                currentFloor = dest;
                //Set waiting time for embarking/disembarking
                waitingTime = specs.getFloorDelay();
            } else {
                currentFloor = newFloor;
            }
        } else if (dest < currentFloor) { //Going down
            newFloor -= (specs.getCarSpeed() / distancePerFloor);
            if (dest >= newFloor) { //Reached destination?
                currentFloor = dest;
                //Set waiting time for embarking/disembarking
                waitingTime = specs.getFloorDelay();
            } else {
                currentFloor = newFloor;
            }
        } else {
            //Set waiting time for embarking/disembarking
            waitingTime = specs.getFloorDelay();
        }
        
        //Everything okay
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
