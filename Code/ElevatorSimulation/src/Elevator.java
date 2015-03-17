
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
    private LinkedList<Passenger> currentPassengers;
    private int waitingTime;
    private float currentFloor;
    private final float distancePerFloor;
    
    /**
     * Constructor 
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     */
    public Elevator(ElevatorSpecs spec, int[] floors, float currentFloor) {
        specs = spec;
        this.floors = floors;
        currentPassengers = new LinkedList<Passenger>();
        queue = new LinkedList<ElevatorQueueObject>();
        waitingTime = 0;
        this.currentFloor = currentFloor;
        distancePerFloor = (float)specs.getBuildingHeight() / (float)specs.getFloors();
    }
    
    /* See ElevatorInterface for details */
    public Passenger[] openDoors() {
        //Check if currently at a floor
        int floor = Math.round(currentFloor);
        if (floor != currentFloor) {
            return new Passenger[0];
        }
        
        //TODO Flytta kod om väntesekunder hit
        
        //Disembarking
        LinkedList<Passenger> retPas = new LinkedList<Passenger>();
        Passenger[] temp = new Passenger[currentPassengers.size()];
        for (int i = 0; i < currentPassengers.size(); i++) {
            temp[i] = currentPassengers.get(i);
        }
        
        //Update queue, elevator and fill return list
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getDestination() == currentFloor) {
                currentPassengers.remove(temp[i]); //Remove from elevator
                retPas.add(temp[i]);
                for (int j = 0; j < queue.size(); j++) {
                    ElevatorQueueObject q = queue.get(j);
                    if (q.getPassenger() == temp[i] && q.getActionType() == ElevatorAction.DROPOFF) {
                        queue.remove(q); //Remove from queue
                        break;
                    }
                }
            }
        }
        
        //Embarking
        while (currentPassengers.size() < specs.getCarryCapacity() && queue.size() > 0) {
            ElevatorQueueObject q = queue.getFirst();
            if (q.getActionType() == ElevatorAction.PICKUP) {
                if(q.getPassenger().getOrigin() == floor) {
                    currentPassengers.add(q.getPassenger());
                    queue.removeFirst();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        //Return disembarking passengers as an array
        Passenger[] retArr = new Passenger[retPas.size()];
        for (int i = 0; i < retPas.size(); i++) {
            retArr[i] = retPas.get(i);
        }
        
        return retArr;
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
        //TODO om hissen är full, ta endast DROPOFF typ
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
        
        //TODO Make sure last index work
        
        return true;
    }
    
    /* See ElevatorInterface for details */
    public ElevatorStatusObject getStatus() {
        //Fetch destination
        ElevatorQueueObject q = queue.getFirst();
        int dest = 0;
        if (q.getActionType() == ElevatorAction.PICKUP) {
            dest = q.getPassenger().getOrigin();
        } else {
            dest = q.getPassenger().getDestination();
        }
        
        //Calculate direction
        int dir = 0;
        if (dest > currentFloor) {
            dir = 1;
        } else if (dest < currentFloor) {
            dir = -1;
        }
        
        return new ElevatorStatusObject(currentFloor, dir, dest, currentPassengers.size());
    }
}
