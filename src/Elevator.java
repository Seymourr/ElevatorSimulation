
import java.util.LinkedList;
import java.util.Arrays;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * This class represents an single decked elevator.
 */
public class Elevator implements ElevatorInterface {
    /* Fields */
    private ElevatorSpecs specs;
    private int[] floors;
    private int[] zonedFloors;
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
     * Private constructor, used for cloning
     */
    private Elevator(ElevatorSpecs s, int[] f, int[] z, LinkedList<ElevatorQueueObject> q, 
            LinkedList<Passenger> c, int w, float cf, BigInteger twt, BigInteger ttt,
            BigDecimal ttd, BigInteger ps) {
        specs = s;
        floors = f;
        queue = q;
        currentPassengers = c;
        waitingTime = w;
        currentFloor = cf;
        distancePerFloor = (float)specs.getBuildingHeight() / (float)specs.getFloors();
        totalWaitTime = twt;
        totalTravelTime = ttt;
        totalTravelDistance = ttd;
        passengersServed = ps;
        zonedFloors = z;
    }
    
    /**
     * Constructor used for zoning
     *
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     * @param zonedFloors The set of floors used in zoning.
     * @param currentFloor The current floor.
     */
    public Elevator(ElevatorSpecs spec, int[] floors, int zonedFloors[], float currentFloor) {
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
        this.zonedFloors = zonedFloors;
    }
    
    /**
     * Constructor used without zoning
     *
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     * @param currentFloor The current floor.
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
        zonedFloors = floors;       
    }
    
    /* See ElevatorInterface for details */
    public HashMap<CarPosition, Passenger[]> openDoors() {        
        //Disembarking
        LinkedList<Passenger> retPas = new LinkedList<Passenger>();
        Passenger[] temp = new Passenger[currentPassengers.size()];
        for (int i = 0; i < currentPassengers.size(); i++) {
            temp[i] = currentPassengers.get(i);
        }
        
        //(Disembarking) Update queue, elevator and fill return list
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getDestination() == currentFloor) {
                if (!currentPassengers.remove(temp[i])) { //Remove from elevator
                    throw new RuntimeException("Could not find dismebarking passenger in elevator.");
                }
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
                if(q.getPassenger().getOrigin() == currentFloor) {
                    currentPassengers.add(q.getPassenger());
                    queue.removeFirst();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        //Format disembarking passengers as an array
        Passenger[] retArr = new Passenger[retPas.size()];
        for (int i = 0; i < retPas.size(); i++) {
            retArr[i] = retPas.get(i);
        }
        
        //Return disembarking passengers as a HashMap
        HashMap<CarPosition, Passenger[]> retMap = new HashMap<CarPosition, Passenger[]>(1);
        retMap.put(CarPosition.NULL, retArr);
        
        return retMap;
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
        
        //If the elevator is full, fetch the next passenger who can disembark successfully
        int index = 1;
        while (currentPassengers.size() == specs.getCarryCapacity() 
            && (q.getActionType() == ElevatorAction.PICKUP ||
            !currentPassengers.contains(q.getPassenger()))) {
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
            System.out.println("1!"); 
            return false;
        }        
        if (!containsFloor(floors, p.getDestination())) {
            System.out.println("2!");
            for(int i = 0; i < floors.length; i++) {
                System.out.print(" " + floors[i]);
            }
            return false;
        }
        if (index2 <= index1) {
            return false;
        }
        if (index1 < 0 || index1 > queue.size()) {
            return false;
        }
        if (index2 < 0 || index2 > queue.size() + 1) {
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
    	if (queue.isEmpty()) {
    		return new ElevatorStatusObject(currentFloor, 0, -1);
    	}
    	
        //Fetch next call
        ElevatorQueueObject q = queue.getFirst();
        int index = 1;
        while (currentPassengers.size() == specs.getCarryCapacity() 
            && (q.getActionType() == ElevatorAction.PICKUP ||
            !currentPassengers.contains(q.getPassenger()))) {
            q = queue.get(index);
            index += 1;
        }
        
        //Fetch destination
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
        
        return new ElevatorStatusObject(currentFloor, dir, dest);
    }
    
    /* See ElevatorInterface for details */
    public ElevatorServiceStatus getRecords() {
        return new ElevatorServiceStatus(totalWaitTime, totalTravelTime, 
            totalTravelDistance.toBigInteger(), passengersServed);
    }
    
    /* See ElevatorInterface for details */
    public ElevatorType ofType() {
        return ElevatorType.SINGLE;
    }
    
    /* See ElevatorInterface for details */
    public int[] getFloors() {
        return floors;
    }
    
    /* See ElevatorInterface for details */
    public int[] getZonedFloors() {
        return zonedFloors;
    }
    
    /* See ElevatorInterface for details */
    public void resetSerivceStatus() {
        totalWaitTime = BigInteger.ZERO;
        totalTravelTime = BigInteger.ZERO;
        totalTravelDistance = BigDecimal.ZERO;
        passengersServed = BigInteger.ZERO;
    }
    
    /* See ElevatorInterface for details */
    public Elevator duplicate() {
        //Duplicate queue
        LinkedList<ElevatorQueueObject> newQueue = new LinkedList<ElevatorQueueObject>();
        for (int i = 0; i < queue.size(); i++) {
            newQueue.add(i, queue.get(i));
        }
    
        //Duplicate currentPassengers
        LinkedList<Passenger> lcp = new LinkedList<Passenger>();
        for (int i = 0; i < currentPassengers.size(); i++) {
            lcp.add(i, currentPassengers.get(i));
        }
        
        return new Elevator(specs, floors, zonedFloors, newQueue, lcp, waitingTime, 
        currentFloor, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ZERO, BigInteger.ZERO);
    }
    
    /* See ElevatorInterface for details */
    public boolean isIdle() {
        return queue.isEmpty();
    }
    
    /* See ElevatorInterface for details */
    public int currentPassengers(CarPosition pos) {
        if (pos != CarPosition.NULL) {
            throw new IllegalArgumentException("Invalid CarPosition.");
        }
        return currentPassengers.size();
    }
}
