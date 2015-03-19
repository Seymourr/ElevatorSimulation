
import java.util.LinkedList;
import java.util.Arrays;
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * This class represents an single decked elevator.
 */
public class Elevator implements ElevatorInterface {
    /* Fields */
    private ElevatorSpecs specs;
    public int[] floors; //EDIT TEMPORARY
    private LinkedList<ElevatorQueueObject> queue;
    private LinkedList<Passenger> currentPassengers;
    private int waitingTime;
    private float currentFloor;
    private final float distancePerFloor;
    private BigInteger totalWaitTime;
    private BigInteger totalTravelTime;
    private BigDecimal totalTravelDistance;
    private BigInteger passengersServed;

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
        totalWaitTime = new BigInteger("0");
        totalTravelTime = new BigInteger("0");
        totalTravelDistance = new BigDecimal("0");
        passengersServed = new BigInteger("0");   
    }
    
    /* See ElevatorInterface for details */
    public Passenger[] openDoors() {
        //Check if currently at a floor
        int floor = Math.round(currentFloor);
        if (floor != currentFloor) {
            return new Passenger[0];
        }
        
        //Disembarking
        LinkedList<Passenger> retPas = new LinkedList<Passenger>();
        Passenger[] temp = new Passenger[currentPassengers.size()];
        for (int i = 0; i < currentPassengers.size(); i++) {
            temp[i] = currentPassengers.get(i);
        }
        
        //(Disembarking) Update queue, elevator and fill return list
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getDestination() == currentFloor) {
                currentPassengers.remove(temp[i]); //Remove from elevator
                retPas.add(temp[i]); //Add to return list
                passengersServed = passengersServed.add(new BigInteger("1")); //Update service counter
                
                boolean removed = false;
                for (int j = 0; j < queue.size(); j++) {
                    ElevatorQueueObject q = queue.get(j);

                    if (q.getPassenger() == temp[i] && q.getActionType() == ElevatorAction.DROPOFF) {
                    	removed = true;
                        queue.remove(q); //Remove from queue
                        break;
                    }
                }
                if (!removed) { //Could not find queue object
                    throw new RuntimeException("Could not find dismebarking passenger in queue.");
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
    
    /**
     * Checks if the elevator currently contains the given passenger
     */
    private boolean containsPassenger(Passenger p) {
        for(int i = 0; i < currentPassengers.size(); i++) {
            if (currentPassengers.get(i) == p) {
                return true;
            }
        }
        return false;
    }

    /* See ElevatorInterface for details */
    public boolean updateElevator() {    
    	//If no people are waiting, do nothing
        if (queue.isEmpty()){
    		return true;
    	}
        
        //Update total wait time
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getActionType() == ElevatorAction.PICKUP) {
               totalWaitTime = totalWaitTime.add(BigInteger.ONE);
            }
        }
        
        //Update total travel time
        totalTravelTime = totalTravelTime.add(BigInteger.valueOf(currentPassengers.size()));
        
        //Passengers boarding, no movement
        if (waitingTime > 0) {
            waitingTime -= 1;
            return true;
        }
        
        ElevatorQueueObject q = queue.getFirst();
        
        //If the elevator is full, fetch the next passenger DROPOFF
        int index = 1;
        while (currentPassengers.size() == specs.getCarryCapacity() 
            && q.getActionType() == ElevatorAction.PICKUP
            && containsPassenger(q.getPassenger())) {
            q = queue.get(index);
            index += 1;
        }
        
        //Fetch next destination
        int dest = 0;
        if (q.getActionType() == ElevatorAction.PICKUP) {
            dest = q.getPassenger().getOrigin();
        } else {
            dest = q.getPassenger().getDestination();
        }
        
        //Check destination is valid
        if (!containsFloor(floors, dest)) {
            return false;
        }
 
        //Update Elevator Position
        float tempFloor = currentFloor;
        float newFloor = currentFloor;
 
        if (dest > currentFloor) { //Going up
            newFloor += (specs.getCarSpeed() / distancePerFloor);
            if (dest <= newFloor) { //Reached destination
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

        //Update travel distance 
        totalTravelDistance = totalTravelDistance.add(
            BigDecimal.valueOf(Math.abs(tempFloor - currentFloor) * distancePerFloor)
        );
        
        //Everything okay
        return true;
    }

    /* See ElevatorInterface for details */
    public LinkedList<ElevatorQueueObject> getQueue() {
        return queue;
    }

    /**
     * Checks that the given origin is within the floor range.
     */
    private boolean containsFloor(int[] temp, int origin) {
    	for(int i = 0; i < temp.length; i++) {
    		if (temp[i] == origin) {
    			return true;
    		}
    	}
    	return false;
    }
    /* See ElevatorInterface for details */
    public boolean addToQueue(Passenger p, int index1, int index2, CarPosition c) {
        if (!containsFloor(floors, p.getOrigin())) { 
        	System.out.println("1!!");
            return false;
        }        
        if (!containsFloor(floors, p.getDestination())) {
        	System.out.println("2!!");
            return false;
        }
        if (index2 <= index1) {
        	System.out.println("3!!");
            return false;
        }
        if (index1 < 0 || index1 > queue.size()) {
        	System.out.println("4!!");
            return false;
        }
        if (index2 < 0 || index2 > queue.size() + 1) {
        	System.out.println("5!!");
            return false;
        }
        
        ElevatorQueueObject q1 = new ElevatorQueueObject(
            p, ElevatorAction.PICKUP, CarPosition.NULL
        );
        ElevatorQueueObject q2 = new ElevatorQueueObject(
            p, ElevatorAction.DROPOFF, CarPosition.NULL
        );
        
        queue.add(index1, q1);
        queue.add(index2, q2);
        return true;
    }
    
    /* See ElevatorInterface for details */
    public ElevatorStatusObject getStatus() {
        //Fetch destination
    	if(queue.isEmpty()) {
    		return new ElevatorStatusObject(currentFloor, 0, -1, currentPassengers.size());
    	}
    	
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
    
    /* See ElevatorInterface for details */
    public ElevatorServiceStatus getRecords() {
        return new ElevatorServiceStatus(totalWaitTime, totalTravelTime, 
            totalTravelDistance.toBigInteger(), passengersServed);
    }
}
