import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.HashMap;

public class SelectiveCollectiveTest extends Algorithm {

	public SelectiveCollectiveTest(ElevatorSpecs spec) {
		this.specs = spec;
	}


 	/**
 	* Goes through the list of calls and assigns elevators to passangers based on those calls.
 	*/
 	public ArrayList<ArrayList<ElevatorInterface>> manageCalls(ArrayList<ArrayList<ElevatorInterface>> allElevators, LinkedList<Passenger> calls) {
 		while(!calls.isEmpty()) {
 			Passenger p = calls.removeFirst();
 			if(containsFloor(allElevators.get(0), p.getOrigin(), p.getDestination())) {
 				//Bot ride
 				allElevators.set(0, assignLocal(allElevators.get(0), p));
 			} else if(containsFloor(allElevators.get(1), p.getOrigin(), p.getDestination())) {
 				//Shuttle ride
 				allElevators.set(1,  assignShuttleElevator(allElevators.get(1), p)); 
 			} else if(containsFloor(allElevators.get(2), p.getOrigin(), p.getDestination())) {
 				//Top ride
 				allElevators.set(2, assignLocal(allElevators.get(2), p));
 			} else {
 				System.out.println("ERROR IN MANAGECALL, SIMULATION ABORTED");
 				System.exit(0);
 			}
 		}

 		return allElevators;
 	}

 	/**
 	* Returns a index of a elevator
 	*/
	protected int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		 int[] zonedIndexes = getZonedElevators(elevators, p);
		 return getRandomElevator(elevators, zonedIndexes, p);
	}

	// //BUGGED FOR DOUBLE DECKED
	// protected int getSelectiveElevator(ArrayList<ElevatorInterface> e, int[] z, Passenger p) {
		// if(p.getDestination() - p.getOrigin() == 0) {
			// System.out.println(p.getDestination());
			// System.out.println(p.getOrigin());
			// System.out.println("ERROR: Origin and Destination cannot be the same. Traffic generator bugged (Double decked case)");
			// System.exit(0);
		// }
		// int pDir = (p.getDestination() - p.getOrigin()) /Math.abs(p.getDestination() - p.getOrigin());
		// ArrayList<Integer> potentialElevators = new ArrayList<Integer>();
		// for(Integer i : z) {
			// int eDir = e.get(i).getStatus().direction;
			// if(eDir == pDir) {
				// if(pDir == 1 && e.get(i).getStatus().floor < p.getOrigin()) {
					// potentialElevators.add(i);
				// } else if(pDir == -1 && e.get(i).getStatus().floor > p.getOrigin()) {
					// potentialElevators.add(i);
				// }
			// }
		// }

		// if(potentialElevators.isEmpty()) {
			// return getRandomElevator(e, z, p);
		// }

		// //Return the one with shortest queue
		// int index = 0;
		// int bestSize = e.get(potentialElevators.get(0)).getQueue().size();
		// for(int i = 0; i < potentialElevators.size(); i++) {
			// if(e.get(potentialElevators.get(i)).getQueue().size() < bestSize) {
				// index = i;
				// bestSize = e.get(potentialElevators.get(i)).getQueue().size();
			// }
		// }
		// return potentialElevators.get(index);
	//}
		
	protected ArrayList<ElevatorInterface> assignLocal(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = getElevator(elevators, p);

		CarPosition pos = CarPosition.NULL;
		if(specs.getLocal() == ElevatorType.DOUBLE) {
			pos = getCarPos(elevators.get(elevatorIndex), p);
		}

        //fetch pick-up point
		int pickUp = getPoint(elevators.get(elevatorIndex), p, p.getOrigin(), pos, 0);
        
        //fetch drop-off point
		int dropOff = getPoint(elevators.get(elevatorIndex), p, p.getDestination(), pos, pickUp) + 1;
        
		if(pickUp >= dropOff) {
			dropOff = pickUp + 1;
		}
		boolean b = elevators.get(elevatorIndex).addToQueue(p, pickUp, dropOff, pos);
		if(!b) {
			System.out.println("FATAL ERROR in selective collective");
			System.out.println(pickUp);
			System.out.println(dropOff);
			System.exit(0);
		}
		return elevators;
	}
    
    /* Fetch drop off point, should work for double decked as well */
    public int getPoint(ElevatorInterface e, Passenger p, int floor, CarPosition pos, int pickUpIndex) {
        //convert floor to upper floor is neccesary
        if (pos == CarPosition.LOWER) {
            floor += 1;
        }
        
        //initiate return value
		int point = -1;
        
        //calculate passenger direction
        if (p.getDestination() == p.getOrigin()) {
            System.out.println("Dest was: " + p.getDestination() + ", Origin was: " + p.getOrigin());
        }
		int pDir = (p.getDestination() - p.getOrigin()) / Math.abs(p.getDestination() - p.getOrigin());
        
        //fetch elevator queue
		LinkedList<ElevatorQueueObject> iQ = e.getQueue();
        
        //if q is empty, return first index
        if (iQ.isEmpty()) {
            return 0;
        }
        
        //if pickUpIndex is size, then only the next index is suitable
        if (pickUpIndex == iQ.size()) {
            return pickUpIndex + 1;
        }
        
        //initiate previous direction as the direct between current elevator floor and first queue object
        int preDir = 0;
        if (pickUpIndex == 0) {
            float tempDir = (float)getUpperFloor(iQ.get(0)) - e.getStatus().floor;
            if (tempDir > 0) {
                preDir = 1;
            } else if (tempDir < 0) {
                preDir = -1;
            }
        } else {
            int tempDir = getUpperFloor(iQ.get(pickUpIndex))
                - getUpperFloor(iQ.get(pickUpIndex-1));
            if (tempDir > 0) {
                preDir = 1;
            } else if (tempDir < 0) {
                preDir = -1;
            }
        }
        
        //check if pick up should take place before first queue object
        if (pickUpIndex == 0) {
            if (preDir == pDir) {
                int f1 = getUpperFloor(iQ.get(0));
                float f2 = e.getStatus().floor;
                if(f1 > f2 && (floor <= f1 && floor >= f2)) return 0;
                if(f1 < f2 && (floor >= f1 && floor <= f2)) return 0;
                if(f1 == f2 && floor == f1) return 0;
            }
            pickUpIndex += 1;
        }
        
        //main loop, find suitable index
		for(int i = pickUpIndex - 1 ; i < iQ.size() - 1; i++) {
			int t1 = getUpperFloor(iQ.get(i + 1)); //Queue object i+1
			int t2 = getUpperFloor(iQ.get(i)); //Queue object i
			int t3 = t1 - t2; //Floor difference
            
            //If elevator is not moving between these queue objects, not interesting
            if (t3 == 0) {
                continue;
            }
			
            //Calculate current elevator direction
			int dir = t3 / Math.abs(t3);

            //Check if should add the pick up point here
			if (pDir == dir) {
                //correct direction and between these queue objects, perfect
				if (isBetween(t1, t2, floor)) {
					return i + 1;
				}
			} 
			if (preDir != dir && preDir != 0) {
               //elevator turning but passenger further in previous direction
                if (floor - t2 / Math.abs(floor - t2) == preDir) {
                	return i + 1;
                }               
				// int t4 = floor - t2;
				// if(t4 != 0) {
					// if(t4/Math.abs(t4) == pDir) {
						// point = i + 1;
					// }
				// }
			}
            
            //update previous dir
            preDir = dir;
		}

        return e.getQueue().size();
    }

	// private int specialCase(ElevatorInterface e, Passenger p, int floor, int pDir) {
		// if(e.getQueue().size() == 0) {
			// return 0;
		// }
		// int index = -1;
		// for(int i = 0; i < e.getQueue().size(); i++) {
			// int mAbs = Math.abs((int)e.getStatus().floor - getUpperFloor(e.getQueue().get(i)));
			// if(mAbs == 0) {
				// return 0; //Special case, for now. 
			// }
			// int dir = ((int)e.getStatus().floor - getUpperFloor(e.getQueue().get(i)))/ mAbs;
			// if(dir == pDir && isBetween(getUpperFloor(e.getQueue().get(i)), (int)e.getStatus().floor, floor)) {
				// index = i;
			// }
		// }
		// if(index == -1) {
			// index = e.getQueue().size();
		// }
		// return index;
	// }
    
    /* Return the relevant upper floor for this elevator queue object */
	public int getUpperFloor(ElevatorQueueObject q) {
		int ret = 0;
		if(q.getActionType() == ElevatorAction.PICKUP) {
			ret = q.getPassenger().getOrigin();
		} else {
			ret = q.getPassenger().getDestination();
		}
        
        //if double decked and lower car, convert to upper floor
        if (q.getCarPosition() == CarPosition.LOWER) {
            ret += 1;
        }
        
		return ret;
	}

	/* Checks if f is between l and s */
	private boolean isBetween(int l, int s, int f) {
		if(l > s && (f <= l && f >= s)) return true;
		if(l < s && (f >= l && f <= s)) return true;
		if(l == s && f == s) return true;
		return false;
	}
}