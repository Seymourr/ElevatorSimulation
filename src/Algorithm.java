import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public abstract class Algorithm {
    ElevatorSpecs specs;	
	public abstract ArrayList<ArrayList<ElevatorInterface>> manageCalls(ArrayList<ArrayList<ElevatorInterface>> elevators, LinkedList<Passenger> calls);
	protected abstract int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p);

    
    /* Help function to assignShuttleElevator */
    private CarPosition getCarPosShuttle(Passenger p) {
		CarPosition pos = CarPosition.NULL;
		if(p.getOrigin() == specs.getSkylobbyfloor() || p.getOrigin() == specs.getLobbyFloor()) {
			pos = CarPosition.LOWER;
		} else {
			pos = CarPosition.UPPER;
		}
		return pos;
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

    /* Fetch car position for next ride for the given passenger and elevator */
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

    /* Fetches a random elevator and returns its index */
    //TODO This function is the issue
    protected int getRandomElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
        /* THIS IS TOTALY RANDOM */   
        //Random r = new Random();
		//return r.nextInt(elevators.size());
        
        /* THIS IS THE OLD GETRANDOM */
        ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		ArrayList<Integer> temp2 = new ArrayList<Integer>();

        //Try to find a non-full idle elevator
		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				if(elevators.get(i).getStatus().direction == 0 && elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
                    //DEBUG Sanity check
                    if (elevators.get(i).getQueue().size() != 0) {
                        throw new RuntimeException("Indicated idle but was not.");
                    }
					temp.add(elevators.get(i));
					temp2.add(i);
				}
			}
		}

		Random r = new Random();
		if(!temp.isEmpty()){
			int elevatorIndex = 0;
			for(int i = 0; i < temp.size(); i++) {
				if(Math.abs(temp.get(i).getStatus().floor - p.getOrigin()) < Math.abs(temp.get(elevatorIndex).getStatus().floor - p.getOrigin())) {
					elevatorIndex = i;
				}
			}
            System.out.print("I: " + elevatorIndex + "\t");
			return elevatorIndex;	
		}

		//No empty or idle elevators exist. Attempt to get the closest one, which is not full.
		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				if(elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
					temp.add(elevators.get(i));
					temp2.add(i);
				}
			}
		}
		
		if(!temp.isEmpty()) {
			int elevatorIndex = 0;
			for(int i = 0; i < temp.size(); i++) {
				if(Math.abs(temp.get(i).getStatus().floor - p.getOrigin()) < Math.abs(temp.get(elevatorIndex).getStatus().floor - p.getOrigin())) {
					elevatorIndex = i;
				}
			}
            System.out.println("\tGot long");
			return elevatorIndex;	
		}

		//If we come here, all elevators are full.

		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				temp.add(elevators.get(i));
				temp2.add(i);
			}
		}
        
		int number = r.nextInt(temp.size());
        System.out.println("Got here");
		return temp2.get(number);

        /* THIS IS THE NEW GETRANDOM */
        // //Fill the parameter z by itself for now 
        // int[] z = new int[e.size()];
        // for (int i = 0; i < e.size(); i++) {
            // z[i] = i;
        // }
        
        // /* Sanity check */
        // if (e.size() != z.length) {
            // throw new RuntimeException ("Z has incorrect size");
        // }
        
		// int index = -1;
		// Random r = new Random();

        // //Validity check
        // if (e.size() != z.length && !specs.zoningUsed()) {
            // throw new RuntimeException("getZoned did not find all elevators.");
        // }

        // //Create a list of elevator indexes that will be filled with potential candidates
        // ArrayList<Integer> potentialElevatorIndexes = new ArrayList<Integer>();
        
        // //Try to find idle (and non-full) elevators
		// for (int i : z) {
            // ElevatorStatusObject esq = e.get(i).getStatus();
			// if (esq.direction == 0 && esq.passengers < specs.getCarryCapacity()) {
				// potentialElevatorIndexes.add(i);
			// }
		// }
        
        // //DEBUG
        // for (int i = 0; i < potentialElevatorIndexes.size(); i++) {
            // ElevatorInterface el = e.get(potentialElevatorIndexes.get(i));
            // if (el.getStatus().direction != 0) {
                // throw new RuntimeException("potentialElevatorIndexes contains invalid indexes.");
            // }
        // }

        // //Try to find elevators that are not full
		// if (potentialElevatorIndexes.isEmpty()) {
			// for (Integer i : z) {
                // ElevatorStatusObject esq = e.get(i).getStatus();
				// if (esq.passengers < specs.getCarryCapacity()) {
					// potentialElevatorIndexes.add(i);
				// }
			// }
		// }

        // //If we get here, all elevators are full, just return a random elevator
		// if (potentialElevatorIndexes.isEmpty()) {
			// int indexOfElevatorIndex = r.nextInt(z.length);
			// return z[indexOfElevatorIndex];
		// }

		// //Of the potential elevators fetched, find the closest one
		// float bestDistance = Integer.MAX_VALUE;
        // int bestIndex = 0;
		// for(Integer i : potentialElevatorIndexes) {
            // ElevatorStatusObject esq = e.get(i).getStatus();
            // float distance = Math.abs(esq.floor - (float)p.getOrigin());
			// if (distance < bestDistance) {
				// bestDistance = distance;
                // bestIndex = i;
			// }
		// }

		// return bestIndex;       
	}
    
	/**
	* THIS IS THE OLD GETRANDOM
	*/
    /*
	protected int getRandomElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		ArrayList<Integer> temp2 = new ArrayList<Integer>();

		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				if(elevators.get(i).getStatus().direction == 0 && elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
					temp.add(elevators.get(i));
					temp2.add(i);
				}
			}
		}

		Random r = new Random();
		if(!temp.isEmpty()){
			int elevatorIndex = 0;
			for(int i = 0; i < temp.size(); i++) {
				if(Math.abs(temp.get(i).getStatus().floor - p.getOrigin()) < Math.abs(temp.get(elevatorIndex).getStatus().floor - p.getOrigin())) {
					elevatorIndex = i;
				}
			}
			return elevatorIndex;	
		}

		//No empty or idle elevators exist. Attempt to get the closest one, which is not full.

		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				if(elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
					temp.add(elevators.get(i));
					temp2.add(i);
				}
			}
		}
		
		if(!temp.isEmpty()) {
			int elevatorIndex = 0;
			for(int i = 0; i < temp.size(); i++) {
				if(Math.abs(temp.get(i).getStatus().floor - p.getOrigin()) < Math.abs(temp.get(elevatorIndex).getStatus().floor - p.getOrigin())) {
					elevatorIndex = i;
				}
			}
			return elevatorIndex;	
		}

		//If we come here, all elevators are full.

		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				temp.add(elevators.get(i));
				temp2.add(i);
			}
		}
		int number = r.nextInt(temp.size());
		return temp2.get(number);
		
	}
    */

}

