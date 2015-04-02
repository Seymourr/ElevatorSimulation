 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.LinkedList;

/**
 * Implementation of a Collective algorithm using a search based assignment
 * strategy.
 */
public class SearchBasedCollective extends Algorithm {
    /* Fields */
    private ElevatorSpecs specs;
    private SelectiveCollective sc;
    
    /* Constructor */
    public SearchBasedCollective(ElevatorSpecs spec) {
        specs = spec;
        sc = new SelectiveCollective(spec);
    }
    
    /* Adds the passenger to the elevator using SelectiveCollective, returns the elevator again */
    private ElevatorInterface addToElevator(ElevatorInterface e, Passenger p) {
        //Calculate passenger direction
        int pDir = 1;
		if (p.getOrigin() > p.getDestination()) {
			pDir = -1;
		}
        
        //Calculate car position to use
        CarPosition c = getCarPos(e, p);
        
        //Fetch elevator direction
        int eDir = e.getStatus().direction;
        
        //Fetch elevator position
        float ePos = e.getStatus().floor;
        
        //Do the assignment
        if (p.getOrigin() >= ePos && eDir == 1) {
            //Elevator going up, passenger on the way
            return sc.pickUpOnTheWay(e, p, pDir, c);
        } else if (p.getOrigin() <= ePos && eDir == -1) {
            //Elevator going down, passenger on the way
            return sc.pickUpOnTheWay(e, p, pDir, c);
        } else if (eDir == 0) {
            //Elevator idle
            return sc.pickUpOnTheWay(e, p, pDir, c);
        } else {
            //Surely not on the way
            return sc.pickUpOnReverse(e, p, pDir, c);
        }
    }
    
    /* Run the elevators until it is empty */
    private ElevatorInterface emptyElevator(ElevatorInterface e) {
        while (!e.getQueue().isEmpty()) {
            e.openDoors();
            e.updateElevator();
        }
        return e;
    }
    
    /* Returns an index of a suitable elevator for the given passenger */
	protected int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
        int bestIndex = 0;
        int bestTime = 0;
        
        for (int i = 0; i < elevators.size(); i++) {
            //Check elevator is valid
            if (!elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
                continue;
            }
            
            //Clone the elevators list
            ElevatorInterface e1 = elevators.get(i).clone();
            ElevatorInterface tmp = e1.clone();
            
            //Assign the new passenger using Selective Collective
            ElevatorInterface e2 = addToElevator(tmp, p);
            
            //Lets the two elevators run until empty
            e1 = emptyElevator(e1);
            e2 = emptyElevator(e2);
            
            //Calculate total wait + travel time for both elevators
            int e1time = e1.getRecords().waitingTime.add(e1.getRecords().travelingTime).intValue();
            int e2time = e2.getRecords().waitingTime.add(e2.getRecords().travelingTime).intValue();
            int totTime = e2time - e1time;
            
            //Check if better than previous best
            if (totTime < bestTime) {
                bestTime = totTime;
                bestIndex = i;
            }
        }
        
        return bestIndex;
    }
    
    /* Assigns all new calls in the call list to an elevator */
    public ArrayList<ArrayList<ElevatorInterface>> manageCalls(
        ArrayList<ArrayList<ElevatorInterface>> allElevators, LinkedList<Passenger> calls) {
        //Iterate through all new calls
        while(calls.peekFirst() != null) {
            Passenger p = calls.removeFirst();
            
            //Check which type of ride this call belongs to
            int rideType = 0;
            if (containsFloor(allElevators.get(0), p.getOrigin(), p.getDestination())) {
 				//Bot ride
                rideType = 0;
 			} else if(containsFloor(allElevators.get(1), p.getOrigin(), p.getDestination())) {
 				//Shuttle ride
                rideType = 1;
 			} else if(containsFloor(allElevators.get(2), p.getOrigin(), p.getDestination())) {
 				//Top ride
                rideType = 2;
 			} else {
 				System.out.println("ERROR IN MANAGECALL, SIMULATION ABORTED");
 				System.exit(0);
 			}
            
            //Fetch the elevator list for the specified ride type
            ArrayList<ElevatorInterface> elevators = allElevators.get(rideType);
            
            //Fetch a suitable elevator in the elevators list
            int elIndex = getElevator(elevators, p);
            
            //Assign the passenger to an elevator
            ElevatorInterface e = addToElevator(elevators.get(elIndex), p);
            
            //Update allElevators
            allElevators.get(rideType).set(elIndex, e);
        }
        return allElevators;
    }
}