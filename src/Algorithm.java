
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

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
     * Returns a list of the elevators in the given list of elevators that
     * can be used to serve the given passenger when zoning is used.
     */
    protected ArrayList<ElevatorInterface> getZonedElevators (
    ArrayList<ElevatorInterface> eles, Passenger p) {
        //If zoning is not used all elevators can be used
        if (!specs.zoningUsed()) {
            return eles;
        } 
        //Create the return list
        ArrayList<ElevatorInterface> retEles = new ArrayList<ElevatorInterface>();
        
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
                    //Origin and destination both within range
                    retEles.add(el);
                } else if (!isLobbyFloor(el, p.getOrigin())) {
                    //Interfloor traffic 
                    retEles.add(el); 
                }
                retEles.add(el); 
            }
        }
        
        //Throw exception if empty list
        if (retEles.isEmpty()) {
            throw new RuntimeException("Empty list in getZonedElevators");
        }
        
        return retEles;
    }
    
	/**
	 * Attempts to pick an optimal shuttle for the given passenger. The optimal shuttle is the idle one at 
	 * the same floor as the passenger. If no optimal shuttle is found, the passenger is assinged 
     * to a random
	 * shuttle.
	 */
	public ArrayList<ElevatorInterface> assignShuttleElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int index = getElevator(elevators, p);
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
		
		}
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

	/**
	* Returns a semi-random elevator position in a list of elevators
	*/
	protected int getRandomElevator(ArrayList<ElevatorInterface> elevatorsOriginal, ArrayList<ElevatorInterface> zonedElevators, Passenger p) {
		ArrayList<Integer> legitElevators = new ArrayList<Integer>();
		int index = -1;
		Random r = new Random();

		for(int i = 0; i < zonedElevators.size(); i++) {
			if(elevatorContainsFloor(zonedElevators.get(i), p.getOrigin(), p.getDestination())) {
				legitElevators.add(i);
			}
		}

		if(legitElevators.isEmpty()) {
			throw new RuntimeException("No eligable elevator found in getRandomElevator");
		}

		ArrayList<Integer> bestElevators = new ArrayList<Integer>();
		for(int i = 0; i < legitElevators.size(); i++) {
			if(zonedElevators.get(legitElevators.get(i)).getStatus().direction == 0 && zonedElevators.get(legitElevators.get(i)).getStatus().passengers < specs.getCarryCapacity()){
				bestElevators.add(legitElevators.get(i));
			}
		}

		if(bestElevators.isEmpty()) {
			for(int i = 0; i < legitElevators.size(); i++) {
				if(zonedElevators.get(legitElevators.get(i)).getStatus().passengers < specs.getCarryCapacity()) {
					bestElevators.add(legitElevators.get(i));
				}
			}
		}

		if(bestElevators.isEmpty()) {
			//All elevators are full, return a random one
			int tempIndex = 0;
			tempIndex = r.nextInt(legitElevators.size());
			for(int i = 0; i < elevatorsOriginal.size(); i++) {
				if(similarElevator(zonedElevators.get(legitElevators.get(tempIndex)).getZonedFloors(), elevatorsOriginal.get(i).getZonedFloors())) {
					return i;
				}
			}
		}

		//Get closest elevator
		index = 0;
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = 0; i < bestElevators.size(); i++) {
			if(Math.abs(zonedElevators.get(bestElevators.get(i)).getStatus().floor - p.getOrigin()) < Math.abs(zonedElevators.get(bestElevators.get(index)).getStatus().floor - p.getOrigin())) {
				index = i;
			}
		}


		for(int i = 0; i < bestElevators.size(); i++) {
			if(Math.abs(zonedElevators.get(bestElevators.get(i)).getStatus().floor - p.getOrigin()) == Math.abs(zonedElevators.get(bestElevators.get(index)).getStatus().floor - p.getOrigin())) {
				temp.add(bestElevators.get(i));
			}
		}

		index = r.nextInt(temp.size());
		if(specs.zoningUsed()) {
			for(int i = 0; i < elevatorsOriginal.size(); i++) {
				if(similarElevator(zonedElevators.get(temp.get(index)).getZonedFloors(), elevatorsOriginal.get(i).getZonedFloors())) {
				index = i;
				break;
				}
			}
		}
		
		return index;

	}
		
	private boolean similarElevator(int[] a, int[] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(a[i] != b[i]) return false;
		}
		return true;
	}
}

