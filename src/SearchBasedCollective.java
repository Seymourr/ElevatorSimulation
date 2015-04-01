 
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
            e =  sc.pickUpOnTheWay(e, p, pDir, c);
        } else if (p.getOrigin() <= ePos && eDir == -1) {
            //Elevator going down, passenger on the way
             e =   sc.pickUpOnTheWay(e, p, pDir, c);
        } else if (eDir == 0) {
            //Elevator idle
             e =   sc.pickUpOnTheWay(e, p, pDir, c);
        } else {
            //Surely not on the way
             e =   sc.pickUpOnReverse(e, p, pDir, c);
        }

        return e;
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
        int bestTime = Integer.MAX_VALUE;
        
        for (int i = 0; i < elevators.size(); i++) {
            //Check elevator is valid
            if (!elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
                continue;
            }

            //Reset elevator service status
            ElevatorInterface e1 = elevators.get(i);
            e1.resetSerivceStatus();

            //Run elevator until empty
            emptyElevator(e1);
            
            //TODO Need to clone before emptying
            
            //Calculate total time
            int e1time = e1.getRecords().waitingTime.add(e1.getRecords().travelingTime).intValue();
            
            //Add the new passenger to the elevator
            e1.resetSerivceStatus();
            ElevatorInterface e2 = addToElevator(e1, p);

            //Run the elevator with the passenger until empty 
            emptyElevator(e2);
            
            //Calculate total time
            int e2time = e2.getRecords().waitingTime.add(e2.getRecords().travelingTime).intValue();
            
            //Calculate time caused by adding the new passenger
            int totTime = e2time - e1time;
            
            if (totTime < 0) {
                System.out.println("\nERROR\n");
                System.out.println("E1 time: " + e1time + ", E2 time: " + e2time);
                System.out.println(e1.getRecords().getStringRepresentation());
                System.out.println(e2.getRecords().getStringRepresentation());
                System.exit(1);
            }

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
        while(!calls.isEmpty()) {
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
            
            //DEBUG UTSKRIFT
            System.out.print("Chosen index: " + elIndex + ", ");
            
            //Assign the passenger to an elevator
            ElevatorInterface e = addToElevator(elevators.get(elIndex), p);

            //Update allElevators
            allElevators.get(rideType).set(elIndex, e);
        }
        return allElevators;
    }
}