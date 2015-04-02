
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
     * Returns a list of indexes of the elevators in the given list of elevators that
     * can be used to serve the given passenger when zoning is used.
     */
    protected int[] getZonedElevators (
    ArrayList<ElevatorInterface> eles, Passenger p) {
        //If zoning is not used all elevators can be used
        if (!specs.zoningUsed()) {
            //DEBUG
            //System.out.println("Not using zoning.");
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
        
        //Convert ArrayList to int[]
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
		int elevatorIndex = getElevator(elevators, p);
		int from = 0;
		if(elevators.get(elevatorIndex).getStatus().floor != p.getOrigin()) {
			from = elevators.get(elevatorIndex).getQueue().size();
		}
		int to = elevators.get(elevatorIndex).getQueue().size() + 1;

		CarPosition pos = CarPosition.NULL;
		if(specs.getShuttle() == ElevatorType.DOUBLE) {
			pos = getCarPosShuttle(p);

			if(pos == CarPosition.NULL) {
				System.out.println("ERROR IN DECIDING POSITION FOR DD, ABORTING");
				System.exit(0);
			}
		
		}
		elevators.get(elevatorIndex).addToQueue(p, from, to, pos);
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

    //DEBUG
	protected int getRandomTest(ArrayList<ElevatorInterface> e) {
		Random r = new Random();
		return r.nextInt(e.size());
	}

	protected int getRandomElevator(ArrayList<ElevatorInterface> e, int[] z, Passenger p) {
		int index = -1;
		Random r = new Random();

        //Validity check
        if (e.size() != z.length && !specs.zoningUsed()) {
            throw new RuntimeException("getZoned did not find all elevators.");
        }

        //Create a list of elevator indexes that will be filled with potential candidates
        ArrayList<Integer> potentialElevatorIndexes = new ArrayList<Integer>();
        
        //Try to find idle (and non-full) elevators
		for (int i : z) {
            ElevatorStatusObject esq = e.get(i).getStatus();
			if (esq.direction == 0 && esq.passengers < specs.getCarryCapacity()) {
				potentialElevatorIndexes.add(i);
			}
		}
        
        //DEBUG
        for (int i = 0; i < potentialElevatorIndexes.size(); i++) {
            ElevatorInterface el = e.get(potentialElevatorIndexes.get(i));
            if (el.getStatus().direction != 0) {
                throw new RuntimeException("potentialElevatorIndexes contains invalid indexes.");
            }
        }

        //Try to find elevators that are not full
		if (potentialElevatorIndexes.isEmpty()) {
			for (Integer i : z) {
                ElevatorStatusObject esq = e.get(i).getStatus();
				if (esq.passengers < specs.getCarryCapacity()) {
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
/*
	/**
	* Returns a semi-random elevator position in a list of elevators
	
	protected int getRandomElevator(ArrayList<ElevatorInterface> elevatorsOriginal, ArrayList<ElevatorInterface> zonedElevators, Passenger p) {
		int index = -1;
		Random r = new Random();
 	
 	ArrayList<Integer> legitElevators = new ArrayList<Integer>();

		for(int i = 0; i < zonedElevators.size(); i++) {
			if(elevatorContainsFloor(zonedElevators.get(i), p.getOrigin(), p.getDestination())) {
				legitElevators.add(i);
			}
		}
        
        //DEBUG UTSKRIFT
        // System.out.println("Amount of legit elevators: " + legitElevators.size());

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
        
        //DEBUG UTSKRIFT
      
        
		if(specs.zoningUsed()) {
			for(int i = 0; i < elevatorsOriginal.size(); i++) {
				if(similarElevator(zonedElevators.get(temp.get(index)).getZonedFloors(), elevatorsOriginal.get(i).getZonedFloors())) {
				index = i;
				break;
				}
			}
		}
		
        //DEBUG UTSKRIFT
       
        
		return index;

	}
	*/	
	private boolean similarElevator(int[] a, int[] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(a[i] != b[i]) return false;
		}
		return true;
	}
}

