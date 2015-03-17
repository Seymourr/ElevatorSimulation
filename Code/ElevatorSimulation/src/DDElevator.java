
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
    private int lowerCarPassengers;
    private int upperCarPassengers;
    private int waitingTime;
    private float currentFloor; //Upper Car
    private final float distancePerFloor;
    
    /**
     * Constructor 
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     * $param currentFloor The current position of the upper car.
     */
    public DDElevator(ElevatorSpecs spec, int[] floors, float currentFloor) {
        specs = spec;
        this.floors = floors;
        lowerCarPassengers = 0;
        upperCarPassengers = 0;
        queue = new LinkedList<ElevatorQueueObject>();
        waitingTime = 0;
        this.currentFloor = currentFloor;
        distancePerFloor = (float)specs.getBuildingHeight() / (float)specs.getFloors();
    }
    
    /* See ElevatorInterface for details */
    public Passenger[] openDoors() {
        return new Passenger[0];
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
        
        //Compensate if the first call is for the lower car
        float newFloor = currentFloor;
        if (q.getCarPosition() == CarPosition.LOWER) {
            newFloor += 1;
        } else if (q.getCarPosition() == CarPosition.NULL) {
            return false; //Invalid option for double deckers
        }
        
        //Update Elevator Position
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
        }
        
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
