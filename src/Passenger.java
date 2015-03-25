
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
        
        reviseDoubleDeckedTrip();
    }
    
    /**
     * Used to check whether the lobby or skylobby of the current trip should
     * be switched to the other lobby or skylobby floor with a double decked
     * trip (since there are two of these when the shuttle is double decked).
     */
    private void reviseDoubleDeckedTrip() {
        if (spec.getShuttle() != ElevatorType.DOUBLE) {
            return;
        }
        
        if (currentDestination == -1 || origin == -1) {
            return;
        }
        
        if (currentDestination == spec.getLobbyFloor()) {
            if (origin == spec.getSkylobbyfloor() + 1) {
                //Shuttle ride from upper car
                currentDestination = spec.getLobbyFloor() + 1;
            } else if (origin < spec.getSkylobbyfloor()) {
                //Local ride in bot locals downwards
                if ((origin - spec.getLobbyFloor()) % 2 == 1) {
                    //Upper floor is best
                    currentDestination = spec.getLobbyFloor() + 1;
                } 
            }
        } else if (currentDestination == spec.getSkylobbyfloor()) {
            if (origin == spec.getLobbyFloor() + 1) {
                //Shuttle ride from upper car
                currentDestination = spec.getSkylobbyfloor() + 1;
            } else if (origin > spec.getSkylobbyfloor()) {
                //Local ride in top locals downwards
                if ((origin - spec.getSkylobbyfloor()) % 2 == 1) {
                    //Upper floor is best
                    currentDestination = spec.getSkylobbyfloor() + 1;
                } 
            }
        } else if (origin == spec.getLobbyFloor() || origin == spec.getLobbyFloor() + 1) {
            //Local ride in bot locals upwards
            if ((currentDestination - spec.getLobbyFloor()) % 2 == 1) {
                //Upper floor is best
                origin = spec.getLobbyFloor() + 1;
            } else {
                //Lower floor is best
                origin = spec.getLobbyFloor();
            }
        } else if (origin == spec.getSkylobbyfloor() || origin == spec.getSkylobbyfloor() + 1) {
            //Local ride in top locals upwards
            if ((currentDestination - spec.getSkylobbyfloor()) % 2 == 1) {
                //Upper floor is best
                origin = spec.getSkylobbyfloor() + 1;
            } else {
                //Lower floor is best
                origin = spec.getSkylobbyfloor();
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
			origin = currentDestination;
			currentDestination = destinations.pop();
		} else {
			origin = -1;
			currentDestination = -1;
		}
        
        reviseDoubleDeckedTrip();
        
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
		} else if(origin == spec.getLobbyFloor() + 1 || origin == spec.getSkylobbyfloor() + 1) {
            origin-=1;
        }
	}
}
