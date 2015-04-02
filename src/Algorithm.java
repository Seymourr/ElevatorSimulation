
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public abstract class Algorithm {
	ElevatorSpecs specs;	
	public abstract ArrayList<ArrayList<ElevatorInterface>> manageCalls(ArrayList<ArrayList<ElevatorInterface>> elevators, LinkedList<Passenger> calls);
	protected abstract int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p);

    /**
     * Help function to getZonedElevators
     * Return true if the given floor is a lobby floor.
     */
    private boolean isLobbyFloor(ElevatorInterface e, int floor) {
        //If the actual lobby or skylobby floor ok
        if (floor == specs.getLobbyFloor() || floor == specs.getSkylobbyfloor()) {
            return true;
        }
        
        floor -= 1;
        
        //If double decked floor + 1 is also ok
        if (e.ofType() == ElevatorType.DOUBLE) {
            if (floor == specs.getLobbyFloor() || floor == specs.getSkylobbyfloor()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Help function to getZonedElevators
     * Return true if the given floor is within the specified elevator's
     * zone.
     */
    private boolean checkContainsZonedFloor(ElevatorInterface e, int floor) {
        for (int i : e.getZonedFloors()) {
            if (i == floor) {
                return true;
            }
        }
        return false;
    }
     
    /**
     * Returns a list of the indexes of the elevators in the given list of elevators that
     * can be used to serve the given passenger when zoning is used.
     */
    protected int[] getZonedElevators (ArrayList<ElevatorInterface> eles, Passenger p) {
        //If zoning is not used all elevators can be used, return a list of all indexes
        if (!specs.zoningUsed()) {
            int[] retArr = new int[eles.size()];
            for (int i = 0; i < eles.size(); i++) {
                retArr[i] = i;
            }
            return retArr;
        } 
        
        //Create the return list
        ArrayList<Integer> retEles = new ArrayList<Integer>();
        
        //Iterate through the elevators and check which are in the correct zone
        for (int i = 0; i < eles.size(); i++) {
            ElevatorInterface el = eles.get(i);
            
            //Check elevator operates over the required floors
            if (!elevatorContainsFloor(el, p.getOrigin(), p.getDestination())) {
                continue;
            }
            
            //Check passenger ride is within the zone
            if (checkContainsZonedFloor(el, p.getOrigin())) {
                if (checkContainsZonedFloor(el, p.getDestination())) {
                    //Origin and destination both within range, ok!
                    retEles.add(i);
                } else if (!isLobbyFloor(el, p.getOrigin())) {
                    //Interfloor traffic originating from the zone of this elevator, ok!
                    retEles.add(i); 
                }
            }
        }
        
        //Throw exception if empty list
        if (retEles.isEmpty()) {
            throw new RuntimeException("Empty list in getZonedElevators");
        }
        
        //Convert ArrayList to array
        int[] retArr = new int[retEles.size()];
        for (int i = 0; i < retEles.size(); i++) {
            retArr[i] = retEles.get(i);
        }
        
        return retArr;
    }
    
	/**
	 * Attempts to pick an optimal shuttle for the given passenger. The optimal shuttle is the idle one at 
	 * the same floor as the passenger. If no optimal shuttle is found, the passenger is assinged 
     * to a random
	 * shuttle.
	 */
	public ArrayList<ElevatorInterface> assignShuttleElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int index = getElevator(elevators, p);
		int from = elevators.get(index).getQueue().size();
		int to = from+1;
		CarPosition pos = getCarPosShuttle(p);
		/*
		int from = 0;
		if(elevators.get(index).getStatus().floor != p.getOrigin()) {
			from = elevators.get(index).getQueue().size();
		}
		int to = elevators.get(index).getQueue().size() + 1;

		CarPosition pos = CarPosition.NULL;
		if(specs.getShuttle() == ElevatorType.DOUBLE) {
			pos = getCarPosShuttle(p);

		if(pos == CarPosition.NULL) {
			System.out.println("ERROR IN DECIDING POSITION FOR DD, ABORTING");
			System.exit(0);
		}
		
		}*/
	
		elevators.get(index).addToQueue(p, from, to, pos);
		return elevators;
	}


	private CarPosition getCarPosShuttle(Passenger p) {
		CarPosition pos = CarPosition.NULL;
		if(p.getOrigin() == specs.getSkylobbyfloor() || p.getOrigin() == specs.getLobbyFloor()) {
			pos = CarPosition.LOWER;
		} else {
			pos = CarPosition.UPPER;
		}
		return pos;
	 }


	protected CarPosition getCarPos(ElevatorInterface elevator, Passenger p) {
        CarPosition pos = CarPosition.NULL;
        
        //If single decked always return NULL
        if (elevator.ofType() == ElevatorType.SINGLE) {
            return pos;
        }
		
		if(p.getDestination() == elevator.getFloors()[elevator.getFloors().length - 1] || p.getOrigin() == elevator.getFloors()[elevator.getFloors().length-1]) {
			pos = CarPosition.UPPER;
		} else {
			if(p.getOrigin() % 2 == 0) {
				pos = CarPosition.LOWER;
			} else {
				pos = CarPosition.UPPER;
			}
		}
		return pos;
	}


	/**
 	 * Checks that a given origin and destination is within the range of given elevators. 
	 * @param temp
 	 * @param origin
 	 * @param destination
 	 * @return True, if within range, false otherwise
 	 */
 	protected boolean containsFloor(ArrayList<ElevatorInterface> elevators, int origin, int destination) {
 		boolean inOrigin = false;
 		boolean goingToDestination = false;

 		for(int i = 0; i < elevators.size(); i++) {
 			if(elevatorContainsFloor(elevators.get(i), origin, destination)) return true;
 		}
 		return false;
	}

	/**
	* Confirms whether the given elevator is a possible elevator to take, that is
	* it can take the passanger from its origin to its destination.
	*/
	protected boolean elevatorContainsFloor(ElevatorInterface e, int origin, int destination) {
		boolean inOrigin = false;
		boolean goingToDestination = false;
		for(int i = 0; i < e.getFloors().length; i++) {
			if(e.getFloors()[i] == origin) {
				inOrigin = true;
			}

			if(e.getFloors()[i] == destination) {
				goingToDestination = true;
			}
		}

		return (inOrigin && goingToDestination);
	}

	protected int getRandomElevator(ArrayList<ElevatorInterface> e, int[] z, Passenger p) {
		int index = -1;
		Random r = new Random();


        //Create a list of elevator indexes that will be filled with potential candidates
        ArrayList<Integer> potentialElevatorIndexes = new ArrayList<Integer>();
        
        //Try to find idle elevators
		for (Integer i : z) {
            ElevatorInterface esq = e.get(i);
			if (esq.isIdle()) { 
				potentialElevatorIndexes.add(i);
			}
		}

        //Try to find elevators that are not full
		if (potentialElevatorIndexes.isEmpty()) {
			for (Integer i : z) {
               ElevatorInterface esq = e.get(i);
               CarPosition potPos = getCarPos(esq, p);
				if (esq.currentPassengers(potPos) < specs.getCarryCapacity()) {
					potentialElevatorIndexes.add(i);
				}
			}
		}

        //If we get here, all elevators are full, just return a random elevator
		if (potentialElevatorIndexes.isEmpty()) {
			int indexOfElevatorIndex = r.nextInt(z.length);
			return z[indexOfElevatorIndex];
		}

		//Of the potential elevators fetched, find the closest one
		float bestDistance = Integer.MAX_VALUE;
        int bestIndex = 0;
		for(Integer i : potentialElevatorIndexes) {
            ElevatorStatusObject esq = e.get(i).getStatus();
            float distance = Math.abs(esq.floor - (float)p.getOrigin());
			if (distance < bestDistance) {
				bestDistance = distance;
                bestIndex = i;
			}
		}

		return bestIndex; 
	}
}

