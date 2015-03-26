 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.LinkedList;

/**
 * Implementation of a Collective algorithm using a search based assignment
 * strategy.
 */
public class SearchBasedCollective extends Algorithm {
    /* Class representing parameters for addToQueue for an elevator */
    private class QueueDetails {
        /* Fields */
        public final int i1;
        public final int i2;
        public final CarPosition c;
        public final Passenger p;
        
        /* Constructor */
        public QueueDetails(int i1, int i2, CarPosition c, Passenger p) {
            this.i1 = i1;
            this.i2 = i2;
            this.c = c;
            this.p = p;
        }
    }
    
    /* Fields */
    private ElevatorSpecs specs;
    
    /* Constructor */
    public SearchBasedCollective(ElevatorSpecs spec) {
        specs = spec;
    }
    
    /* Returns an index of a suitable elevator for the given passenger */
	protected int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
        //TODO
        
        //Order by distance to current
        
        //Go through list, if no has space and wrong direction, remove it
        
        //Calculate waiting and travel times based on the queue
        
        //Chose min of above
        
        return 0;
    }
    
    /* Returns a QueueDetails object with values for when the passenger should be picked up */
    private QueueDetails getAddQueueParameters(ElevatorInterface elevator, Passenger p) {
        //TODO
        return null;
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
            
            //Fetch a suitable spot in this elevator to add the new passenger
            QueueDetails qd = getAddQueueParameters(elevators.get(elIndex), p);
            
            //Assign the passenger to an elevator
            allElevators.get(rideType).get(elIndex).addToQueue(p, qd.i1, qd.i2, qd.c);  
        }
        return allElevators;
    }
}