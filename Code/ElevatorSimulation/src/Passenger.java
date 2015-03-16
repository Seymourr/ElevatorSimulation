
import java.util.Stack;

/**
 * This class will represent one passenger in the simulation. It is used to
 * store the partial goals for a passenger while traveling from one floors to
 * another. For example, if the passenger if traveling past the sky lobby then
 * a partial goal would be the skylobby.
 */
public class Passenger {
    /* Fields */
	private Stack<Integer> destinations = new Stack<Integer>();
    private int currentDestination;
    
    /** Constructor 
     * Calculates the partial goals for the passenger in the given goal toward
     * his destination and stores them.   
     */
    public Passenger(Call c, ElevatorSpecs spec) {
        goal = c.getDestination();
        int origin = c.getOriginFloor();
        int skylobby = spec.getSkylobbyfloor();
        if (goal > skylobby) {
            if (origin < skylobby) {
                if (origin = spec.getLobbyFloor()) {
                    destinations = {skylobby};                
                } else {
                    destinations = {spec.getLobbyFloor(), skylobby};
                }
            }        
        }
    }

    /**
     *
     */
    public int nextDestination() {
        
    }
}
