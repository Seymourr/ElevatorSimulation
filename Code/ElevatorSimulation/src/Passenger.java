
import java.util.Stack;

/**
 * This class will represent one passenger in the simulation. It is used to
 * store the partial goals for a passenger while traveling from one floors to
 * another. For example, if the passenger if traveling past the sky lobby then
 * a partial goal would be the skylobby.
 *
 * Note that the nextDestination method has to be called before getDestination
 * contains any valid destination.
 */
public class Passenger {
    /* Fields */
	private Stack<Integer> destinations = new Stack<Integer>();
    private int currentDestination = -1;
    
    /** 
	 * Constructor 
     * Calculates the partial goals for the passenger in the given call toward
     * his/her destination and stores them for access via the nextDestination
	 * and getDestination methods.
     */
    public Passenger(Call c, ElevatorSpecs spec) {
        int goal = c.getDestination();
        int origin = c.getOriginFloor();
        int skylobby = spec.getSkylobbyfloor();
		int lobby = spec.getLobbyFloor();
		
		destinations.push(goal);
		
        if (goal > skylobby) {
            if (origin < skylobby) {
				destinations.push(skylobby);
                if (origin != lobby) {
                    destinations.push(lobby);
                }
            }        
        } else {
			if (origin > skylobby) {
				if (goal != lobby) {
					destinations.push(lobby);
				}
				destinations.push(skylobby);
			}
		}
    }

    /**
     * Fetches the next partial destination for this Passenger. This function
	 * also stores this new destination for later access via the getDestination
	 * method.
	 *
	 * @return The next destination for this Passenger or -1 if the final destination has already been reached.
     */
    public int nextDestination() {
		if (!destinations.empty()) {
			currentDestination = destinations.pop();
		} else {
			currentDestination = -1;
		}
		return currentDestination;
    }
	
	/**
	 * Fetches the current destination for this passenger.
	 */
	public int getDestination() {
		return currentDestination;
	}
}
