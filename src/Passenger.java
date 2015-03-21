
import java.util.Stack;

/**
 * This class will represent one passenger in the simulation. It is used to
 * store the partial goals for a passenger while traveling from one floors to
 * another. For example, if the passenger if traveling past the sky lobby then
 * a partial goal would be the skylobby.
 *
 * Note that the constructor automaticly sets the origin and destination to
 * the first travel.
 */
public class Passenger {
    /* Fields */
	private Stack<Integer> destinations = new Stack<Integer>();
    private int currentDestination = -1;
	private int origin = -1;
	private ElevatorSpecs spec;
    
    /** 
	 * Constructor 
     * Calculates the partial goals for the passenger in the given call toward
     * his/her destination and stores them for access via the nextDestination
	 * and getDestination methods.
     */
    public Passenger(Call c, ElevatorSpecs spec) {
        
        //TODO Calculate route depending on the type of elevators in usage
        
        int goal = c.getDestination();
        origin = c.getOriginFloor();
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
		
		currentDestination = destinations.pop();
		this.spec = spec;
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
			origin = currentDestination;
			currentDestination = destinations.pop();
		} else {
			origin = -1;
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
	
	/**
	 * Return the origin floor for the current travel.
	 */
	public int getOrigin() {
		return origin;
	}

	/**
	* Shift the position of the origin to a different one (above)
	*/
	public void shift() {
		if(origin == spec.getLobbyFloor() || origin == spec.getSkylobbyfloor()) {
			origin+=1;
		}
	}
}
