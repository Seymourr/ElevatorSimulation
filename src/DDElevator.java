
import java.util.LinkedList;
import java.util.Arrays;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * This class represents a double decked elevator.
 */
public class DDElevator implements ElevatorInterface{   
    /* Fields */
    private final ElevatorSpecs specs;
    private final int[] floors; 
    private final int[] zonedFloors;
    private final LinkedList<ElevatorQueueObject> queue;
    private LinkedList<Passenger> lowerCarPassengers;
    private LinkedList<Passenger> upperCarPassengers;
    private int waitingTime;
    private float currentUpperFloor;
    private final float distancePerFloor;
    
    private BigInteger totalWaitTime;
    private BigInteger totalTravelTime;
    private BigDecimal totalTravelDistance;
    private BigInteger passengersServed;
    
    /**
     * Constructor 
     * Used for cloning
     */
    public DDElevator(ElevatorSpecs s, int[] f, int[] z, LinkedList<ElevatorQueueObject> q, 
		    LinkedList<Passenger> lc, LinkedList<Passenger> uc, int w, float cf, BigInteger twt, 
		    BigInteger ttt, BigDecimal ttd, BigInteger ps) {
        specs = s;
        floors = f;
        queue = q;
        lowerCarPassengers = lc;
        upperCarPassengers = uc;
        waitingTime = w;
        currentUpperFloor = cf;
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
     * @param zonedFloors The set of floors used for zoning.
     * $param currentFloor The current position of the upper car.
     */
    public DDElevator(ElevatorSpecs spec, int[] floors, int zonedFloors[], float currentFloor) {
        specs = spec;
        this.floors = floors;
        queue = new LinkedList<ElevatorQueueObject>();
        lowerCarPassengers = new LinkedList<Passenger>();
        upperCarPassengers = new LinkedList<Passenger>();
        waitingTime = 0;
        currentUpperFloor = currentFloor;
        distancePerFloor = (float)specs.getBuildingHeight() / (float)specs.getFloors();
        this.zonedFloors = zonedFloors;
        
        totalWaitTime = new BigInteger("0");
        totalTravelTime = new BigInteger("0");
        totalTravelDistance = new BigDecimal("0");
        passengersServed = new BigInteger("0");
    }
    
    /**
     * Constructor used without zoning
     *
     * @param spec The specifications for this elevator.
     * @param floors The set of floors to operate on.
     * $param currentFloor The current position of the upper car.
     */
    public DDElevator(ElevatorSpecs spec, int[] floors, float currentFloor) {
        specs = spec;
        this.floors = floors;
        queue = new LinkedList<ElevatorQueueObject>();
        lowerCarPassengers = new LinkedList<Passenger>();
        upperCarPassengers = new LinkedList<Passenger>();
        waitingTime = 0;
        currentUpperFloor = currentFloor;
        distancePerFloor = (float)specs.getBuildingHeight() / (float)specs.getFloors();
        zonedFloors = floors;
        
        totalWaitTime = new BigInteger("0");
        totalTravelTime = new BigInteger("0");
        totalTravelDistance = new BigDecimal("0");
        passengersServed = new BigInteger("0");
    }
    
    /**
      * Help method to openDoors that disembarking passengers from the specified
      * car and returns a list of these.
      */
    private Passenger[] disembarkFromCar(CarPosition cp) {
        //Specify values depending on car positions
        LinkedList<Passenger> carPassengers;
        float floor = 0;
        if (cp == CarPosition.UPPER) {
            floor = currentUpperFloor;
            carPassengers = upperCarPassengers;
        } else if (cp == CarPosition.LOWER) {
            floor = currentUpperFloor - 1;
            carPassengers = lowerCarPassengers;
        } else {
            throw new IllegalArgumentException("Only Upper or Lower allowed in disembark.");
        }
        
        //Disembarking
        LinkedList<Passenger> retPas = new LinkedList<Passenger>();
        Passenger[] temp = new Passenger[carPassengers.size()];
        for (int i = 0; i < carPassengers.size(); i++) {
            temp[i] = carPassengers.get(i);
        }
        
        //(Disembarking) Update queue, elevator and fill return list
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getDestination() == floor) {
                carPassengers.remove(temp[i]); //Remove from elevator
                retPas.add(temp[i]); //Add to return list
                passengersServed = passengersServed.add(new BigInteger("1")); //Update service counter
                
                //Remove from queue
                boolean removed = false;
                for (int j = 0; j < queue.size(); j++) {
                    ElevatorQueueObject q = queue.get(j);
                    if (q.getPassenger() == temp[i] && q.getActionType() == ElevatorAction.DROPOFF) {
                    	removed = true;
                        queue.remove(q);
                        break;
                    }
                }
                if (!removed) { //Could not find queue object
                    throw new RuntimeException("Could not find dismebarking passenger in queue.");
                }
            }
        }
        
        //Update Elevator Contents
        if (cp == CarPosition.UPPER) {
            upperCarPassengers = carPassengers;
        } else if (cp == CarPosition.LOWER) {
            lowerCarPassengers = carPassengers;
        }
        
        //Return disembarking passengers as an array
        Passenger[] retArr = new Passenger[retPas.size()];
        for (int i = 0; i < retPas.size(); i++) {
            retArr[i] = retPas.get(i);
        }
        
        return retArr;
    }
    
    /* See ElevatorInterface for details */
    public HashMap<CarPosition, Passenger[]> openDoors() {
        //Disembarking
        HashMap<CarPosition, Passenger[]> retMap = new HashMap<CarPosition, Passenger[]>(2);
        retMap.put(CarPosition.UPPER, disembarkFromCar(CarPosition.UPPER));
        retMap.put(CarPosition.LOWER, disembarkFromCar(CarPosition.LOWER));
        
        //Embarking
        while (queue.size() > 0) {
            ElevatorQueueObject q = queue.getFirst();
            if (q.getActionType() == ElevatorAction.PICKUP) {
                if (q.getPassenger().getOrigin() == currentUpperFloor &&
                upperCarPassengers.size() < specs.getCarryCapacity() &&
                q.getCarPosition() == CarPosition.UPPER) {
                    //Passenger entering upper car
                    upperCarPassengers.add(q.getPassenger());
                    queue.removeFirst();
                } else if (q.getPassenger().getOrigin() == currentUpperFloor - 1 &&
                lowerCarPassengers.size() < specs.getCarryCapacity() &&
                q.getCarPosition() == CarPosition.LOWER) {
                    //Passenger entering lower car
                    lowerCarPassengers.add(q.getPassenger());
                    queue.removeFirst();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        //Return disembarking passengers
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
        totalTravelTime = totalTravelTime.add(BigInteger.valueOf(lowerCarPassengers.size()));
        totalTravelTime = totalTravelTime.add(BigInteger.valueOf(upperCarPassengers.size()));
        
        //Passengers boarding, no movement
        if (waitingTime > 0) {
            waitingTime -= 1;
            return true;
        }
        
        //If the elevator is full, fetch the next passenger who can disembark successfully
        ElevatorQueueObject q = queue.getFirst();
        int index = 1;
        while (true) {
            if (q.getCarPosition() == CarPosition.UPPER) {
                if (upperCarPassengers.size() == specs.getCarryCapacity() 
                    && (q.getActionType() == ElevatorAction.PICKUP ||
                    !upperCarPassengers.contains(q.getPassenger()))) {
                    q = queue.get(index);
                    index += 1;
                } else {
                    break;
                }
            } else if (q.getCarPosition() == CarPosition.LOWER) {
                if (lowerCarPassengers.size() == specs.getCarryCapacity() 
                    && (q.getActionType() == ElevatorAction.PICKUP ||
                    !lowerCarPassengers.contains(q.getPassenger()))) {
                    q = queue.get(index);
                    index += 1;
                } else {
                    break;
                }
            } else {
                throw new RuntimeException("CarPosition.NULL in double decked.");
            }
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
        
        //Compensate destination if lower car
        if (q.getCarPosition() == CarPosition.LOWER) {
            dest += 1;
        }
        
        //Check destination is valid
        if (!containsFloor(floors, dest)) {
            return false;
        }
        
        //Update Elevator Position
        float tempFloor = currentUpperFloor;
        float newFloor = currentUpperFloor;
 
        if (dest > currentUpperFloor) { //Going up
            newFloor += (specs.getCarSpeed() / distancePerFloor);
            if (dest <= newFloor) { //Reached destination
                currentUpperFloor = dest;
                //Set waiting time for embarking/disembarking
                waitingTime = specs.getFloorDelay();
            } else {
                currentUpperFloor = newFloor;
            }
        } else if (dest < currentUpperFloor) { //Going down
            newFloor -= (specs.getCarSpeed() / distancePerFloor);
            if (dest >= newFloor) { //Reached destination?
                currentUpperFloor = dest;
                //Set waiting time for embarking/disembarking
                waitingTime = specs.getFloorDelay();
            } else {
                currentUpperFloor = newFloor;
            }
        }
        
        //Update travel distance 
        totalTravelDistance = totalTravelDistance.add(
            BigDecimal.valueOf(Math.abs(tempFloor - currentUpperFloor) * distancePerFloor)
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
            return false;
        }        
        if (!containsFloor(floors, p.getDestination())) {
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
        if (c == CarPosition.NULL) {
            return false;
        }
        
        ElevatorQueueObject q1 = new ElevatorQueueObject(
            p, ElevatorAction.PICKUP, c
        );
        ElevatorQueueObject q2 = new ElevatorQueueObject(
            p, ElevatorAction.DROPOFF, c
        );
        if(queue.size() == 0) {
            
        }
        queue.add(index1, q1);
        queue.add(index2, q2);
        
        return true;
    }

    /* See ElevatorInterface for details */
    public ElevatorStatusObject getStatus() {
        //Fetch destination
    	if (queue.isEmpty()) {
    		return new ElevatorStatusObject(
                currentUpperFloor, 0, -1, upperCarPassengers.size(), lowerCarPassengers.size()
            );
    	}
    	
        //Fetch the next destination
        ElevatorQueueObject q = queue.getFirst();
        int index = 1;
        while (true) {
            if (q.getCarPosition() == CarPosition.UPPER) {
                if (upperCarPassengers.size() == specs.getCarryCapacity() 
                    && (q.getActionType() == ElevatorAction.PICKUP ||
                    !upperCarPassengers.contains(q.getPassenger()))) {
                    q = queue.get(index);
                    index += 1;
                } else {
                    break;
                }
            } else if (q.getCarPosition() == CarPosition.LOWER) {
                if (lowerCarPassengers.size() == specs.getCarryCapacity() 
                    && (q.getActionType() == ElevatorAction.PICKUP ||
                    !lowerCarPassengers.contains(q.getPassenger()))) {
                    q = queue.get(index);
                    index += 1;
                } else {
                    break;
                }
            } else {
                throw new RuntimeException("CarPosition.NULL in double decked.");
            }
            q = queue.get(index);
            index += 1;
        }
        
        //Fetch current destination
        int dest = 0;
        if (q.getActionType() == ElevatorAction.PICKUP) {
            dest = q.getPassenger().getOrigin();
        } else {
            dest = q.getPassenger().getDestination();
        }
        
        //Compensate if lower car
        if (q.getCarPosition() == CarPosition.LOWER) {
            dest += 1;
        }
        
        //Calculate direction
        int dir = 0;
        if (dest > currentUpperFloor) {
            dir = 1;
        } else if (dest < currentUpperFloor) {
            dir = -1;
        }
        
        return new ElevatorStatusObject(
            currentUpperFloor, dir, dest, upperCarPassengers.size(), lowerCarPassengers.size()
        );
    }
    
    /* See ElevatorInterface for details */
    public ElevatorServiceStatus getRecords() {
        return new ElevatorServiceStatus(totalWaitTime, totalTravelTime, 
            totalTravelDistance.toBigInteger(), passengersServed);
    }
    
    /* See ElevatorInterface for details */
    public ElevatorType ofType() {
        return ElevatorType.DOUBLE;
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
    public DDElevator duplicate() {
        //Duplicate queue
        LinkedList<ElevatorQueueObject> newQueue = new LinkedList<ElevatorQueueObject>();
        for (int i = 0; i < queue.size(); i++) {
            newQueue.add(i, queue.get(i));
        }
    
        //Duplicate lowerCarPassengers
        LinkedList<Passenger> lcp = new LinkedList<Passenger>();
        for (int i = 0; i < lowerCarPassengers.size(); i++) {
            lcp.add(i, lowerCarPassengers.get(i));
        }
        //Duplicate upperCarPassengers
        LinkedList<Passenger> ucp = new LinkedList<Passenger>();
        for (int i = 0; i < upperCarPassengers.size(); i++) {
            ucp.add(i, upperCarPassengers.get(i));
        }
        
        return new DDElevator(specs, floors, zonedFloors, newQueue, lcp, ucp, waitingTime, 
        currentUpperFloor, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ZERO, BigInteger.ZERO);
    }
}
