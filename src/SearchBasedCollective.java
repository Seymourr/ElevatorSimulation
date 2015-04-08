 
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
    private SelectiveCollectiveTest sc;
    
    /* Constructor */
    public SearchBasedCollective(ElevatorSpecs spec) {
        specs = spec;
        sc = new SelectiveCollectiveTest(spec);
    }
    
    /* Adds the passenger to the elevator using SelectiveCollective, returns the elevator again */
    private ElevatorInterface addToElevator(ElevatorInterface e, Passenger p) {
      
        //Calculate car position to use
        CarPosition c = getCarPos(e, p);
  
           //fetch pick-up point
        int pickUp = sc.getPickUpPoint(e, p, p.getOrigin(), c);
        
        //fetch drop-off point
        int dropOff = sc.getDropOffPoint(e, p, p.getDestination(), c, pickUp);
        
        if(pickUp >= dropOff) {
            throw new RuntimeException("Should not get here.");
            //dropOff = pickUp + 1;
        }
        boolean b = e.addToQueue(p, pickUp, dropOff, c);
        if(!b) {
            System.out.println("FATAL ERROR in selective collective");
            System.out.println(pickUp);
            System.out.println(dropOff);
            System.exit(0);
        }
        return e;
        /*
        
        //Fetch elevator direction
        int eDir = e.getStatus().direction;
        
        //Fetch elevator position
        float ePos = e.getStatus().floor;

        if(pDir == eDir) {
            if((pDir == 1 && ePos <= p.getOrigin()) || (pDir == -1 && ePos >= p.getOrigin())) {
                return sc.pickUpOnTheWay(e, p, pDir, c);
            } else {
                return sc.pickUpOnReverse(e, p, pDir, c);
            }
        } else if(e.isIdle()) {
            int from = e.getQueue().size();
            int to = from + 1;
            e.addToQueue(p, from, to, c);
            return e;
        } else {
            return sc.pickUpOnReverse(e, p, pDir, c);
        }
        
    //    e.addToQueue(p, e.getQueue().size(), e.getQueue().size() + 1, c);
//        return e;
        */


    }
    
    /* Run the elevators until it is empty, return the amount of time it took */
    private int emptyElevator(ElevatorInterface e) {
        e.resetSerivceStatus();
        while (!e.getQueue().isEmpty()) {
            e.openDoors();
            e.updateElevator();
        }
        int etime = e.getRecords().waitingTime.add(e.getRecords().travelingTime).intValue();
        return etime;
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
            ElevatorInterface e = elevators.get(i);
            
            //Duplicate
            ElevatorInterface e1 = e.duplicate();
 
            //Run elevator until empty
            int e1time = emptyElevator(e1);

            //Duplicate a new elevator
            ElevatorInterface e2 = e.duplicate();

            //add the new passenger
            e2 = addToElevator(e2, p);

            //Run e2 until empty
            int e2time = emptyElevator(e2);

            //Calculate extra time caused by adding the new passenger
            int totTime = e2time - e1time;
            
            //Error check
            if (totTime < -1) {
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
            
            //Assign the passenger to an elevator
            ElevatorInterface e = addToElevator(elevators.get(elIndex), p);

            //Update allElevators
            allElevators.get(rideType).set(elIndex, e);
            
        }
        return allElevators;
    }
}