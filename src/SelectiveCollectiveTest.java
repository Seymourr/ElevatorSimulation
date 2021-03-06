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
		int pickUp = getPickUpPoint(elevators.get(elevatorIndex), p, p.getOrigin(), pos);
        
        //fetch drop-off point
		int dropOff = getDropOffPoint(elevators.get(elevatorIndex), p, p.getDestination(), pos, pickUp);

		if(pickUp >= dropOff) {
            throw new RuntimeException("Should not get here.");
			//dropOff = pickUp + 1;
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
    
    /* Fetch drop off point */
    public int getDropOffPoint(ElevatorInterface e, Passenger p, int floor, CarPosition pos, 
    int pickUpIndex) {
        //fetch pick up floor
        int pickUpFloor = p.getOrigin();
        
        //convert floor to upper floor is neccesary
        if (pos == CarPosition.LOWER) {
            floor += 1;
        }
        
        //calculate passenger direction
        if (p.getDestination() == p.getOrigin()) {
            //Error printout
            System.out.println("Dest was: " + p.getDestination() + ", Origin was: " + p.getOrigin());
        }
		int pDir = (p.getDestination() - p.getOrigin()) / Math.abs(p.getDestination() - p.getOrigin());
        
        //initiate return value
		int point = -1;
        
        //fetch elevator queue
		LinkedList<ElevatorQueueObject> iQ = e.getQueue();
        
        //if q is empty, return first index
        if (iQ.isEmpty()) {
            if (pickUpIndex != 0) {
                throw new RuntimeException("Wierd argument in getDropOffPoint.");
            }
         //   System.out.println("Returned 1");
            return 1;
        }
        
        //obvious
        if (pickUpIndex == iQ.size()) {
       //     System.out.println("Returned 2");
            return pickUpIndex + 1;
        }
        
        //calculate previous direction
        int preDir = 0;
        int tempDir = getUpperFloor(iQ.get(pickUpIndex)) - pickUpFloor;
        if (tempDir > 0) {
            preDir = 1;
        } else if (tempDir < 0) {
            preDir = -1;
        } 
        
        //check if should pick up before loop
        if (isBetween(pickUpFloor, getUpperFloor(iQ.get(pickUpIndex)), floor)) {
    //        System.out.println("Returned 3");
            return pickUpIndex + 1;
        }
        
        //Calculate next direction
        int nextDir = 0;
        int index;
        for (index = pickUpIndex; index < iQ.size(); index++) {
            if (getUpperFloor(iQ.get(index)) > p.getOrigin()) {
                nextDir = 1;
                break;
            } else if (getUpperFloor(iQ.get(index)) < p.getOrigin()) {
                nextDir = -1;
                break;
            }
        }
        
        //If the elevator is about to travel in the wrong direction, add dropoff immediatly
        if (nextDir != pDir) {
            return index + 1;
        }
        
        
        //main loop, find suitable index
		for(int i = pickUpIndex; i < iQ.size() - 1; i++) {
			int t1 = getUpperFloor(iQ.get(i + 1)); //Queue object i+1
			int t2 = getUpperFloor(iQ.get(i)); //Queue object i
			int t3 = t1 - t2; //Floor difference
            
            //If elevator is not moving between these queue objects, not interesting
            if (t3 == 0) {
                continue;
            }
			
            //Calculate current elevator direction
			int dir = t3 / Math.abs(t3);

            //between these queue objects, perfect
            if (isBetween(t1, t2, floor)) {
    //            System.out.println("Returned 5");
                return i + 2;
            }

			if (preDir != dir && preDir != 0) {
               //elevator turning but passenger further in previous direction
                if ((floor - t2) / Math.abs(floor - t2) == preDir) {
           //         System.out.println("Returned 6");
                    if (iQ.get(i).getActionType() == ElevatorAction.PICKUP) {
                        //Append first
                        return i + 1;
                    } else if (iQ.get(i).getActionType() == ElevatorAction.DROPOFF) {
                        //Fetch next object of type pickup
                        return fetchQueueIndex(iQ, floor, i + 2);
                    }
                } else {
                    //We dont wanna be here
                    System.out.println("Pickupindex was: " + pickUpIndex);
                    System.out.println("pickUpFloor was: " + pickUpFloor);
                    System.out.println("Drop off floor should be: " + floor);
                    System.out.println("Predir: " + preDir);
                    System.out.println("T2: " + t2);
                    System.out.println("i is: " + i);
                    System.out.println("Type: " + iQ.get(i).getActionType());
                    System.out.println();
                    System.out.print("Queue is: ");
                    for (int j = 0; j < iQ.size(); j++) {
                        System.out.print(" " + getUpperFloor(iQ.get(j)));
                        if(iQ.get(j).getActionType() == ElevatorAction.PICKUP) {
                            System.out.print("P");
                        } else {
                            System.out.print("D");
                        }
                    }
                    System.out.println();
                    System.out.println("Elevator position: " + e.getStatus().floor);
                    throw new RuntimeException("Illegal pickupindex cause issue in getdropoff");
                }              
			}
            
            //update previous dir
            preDir = dir;
		}
     //   System.out.println("Returned 7");
        return e.getQueue().size() + 1;
    }
    
    /* Fetch next queue index of type pickup */
    public int fetchQueueIndex(LinkedList<ElevatorQueueObject> iQ, int floor, int startIndex) {
        for (int i = startIndex; i < iQ.size(); i++) {
            if (getUpperFloor(iQ.get(i)) != floor ||
            iQ.get(i).getActionType() == ElevatorAction.PICKUP) {
                return i;
            }
        }
        return iQ.size();
    }
    
    /* Fetch pick up point, should work for double decked as well */
    public int getPickUpPoint(ElevatorInterface e, Passenger p, int floor, CarPosition pos) {
        //convert floor to upper floor is neccesary
        if (pos == CarPosition.LOWER) {
            floor += 1;
        }
        
        //initiate return value
		int point = -1;
        
        //calculate passenger direction
        if (p.getDestination() == p.getOrigin()) {
            //Error printout
            System.out.println("Dest was: " + p.getDestination() + ", Origin was: " + p.getOrigin());
        }
		int pDir = (p.getDestination() - p.getOrigin()) / Math.abs(p.getDestination() - p.getOrigin());
        
        //fetch elevator queue
		LinkedList<ElevatorQueueObject> iQ = e.getQueue();
        
        //if q is empty, return first index
        if (iQ.isEmpty()) {
            return 0; //DONE
        }
        
        //initiate previous direction as the direct between current elevator floor and first queue object
        int preDir = 0;
        float tempDir = (float)getUpperFloor(iQ.get(0)) - e.getStatus().floor;
        if (tempDir > 0) {
            preDir = 1;
        } else if (tempDir < 0) {
            preDir = -1;
        }
        
        //check if pick up should take place before first queue object
        if (preDir == pDir) {
            int q1 = getUpperFloor(iQ.get(0));
            float elev = e.getStatus().floor;

            if (elev < q1 && floor >= elev) {
                if (floor < q1) {
                    //Free to add
                    return 0;
                } else if (floor == q1) {
                    //Find suitable index for pickup
                    if (iQ.get(0).getActionType() == ElevatorAction.PICKUP) {
                        //Append first
                        return 0;
                    } else if (iQ.get(0).getActionType() == ElevatorAction.DROPOFF) {
                        //Fetch next object of type pickup
                        return fetchQueueIndex(iQ, floor, 1);
                    }
                }
            } else if (elev > q1 && floor <= elev) {
                if (floor > q1) {
                    //Free to add
                    return 0;
                } else if (floor == q1) {
                    //Find suitable index for pickup
                    if (iQ.get(0).getActionType() == ElevatorAction.PICKUP) {
                        //Append first
                        return 0;
                    } else if (iQ.get(0).getActionType() == ElevatorAction.DROPOFF) {
                        //Fetch next object of type pickup
                        return fetchQueueIndex(iQ, floor, 1);
                    }
                }
            } else if (elev == q1 && floor == elev) {
                if (iQ.get(0).getActionType() == ElevatorAction.PICKUP) {
                    //Append first
                    return 0;
                } else if (iQ.get(0).getActionType() == ElevatorAction.DROPOFF) {
                    //Fetch next object of type pickup
                    return fetchQueueIndex(iQ, floor, 1);
                }
            }
        }
        
        //main loop, find suitable index
		for (int i = 0; i < iQ.size() - 1; i++) {
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
                    if (t1 == floor) {
                        //Add pickups after dropoffs are completed
                        if (iQ.get(i + 1).getActionType() == ElevatorAction.PICKUP) {
                            //Append first
                            return i + 1;
                        } else if (iQ.get(i + 1).getActionType() == ElevatorAction.DROPOFF) {
                            //Fetch next object of type pickup
                            return fetchQueueIndex(iQ, floor, i + 2);
                        }
                    } else {
                        return i + 1;
                    }
				}
			} 

			if (preDir != dir && preDir != 0) {
                //elevator turning but passenger further in previous direction
                if ((floor - t2) / Math.abs(floor - t2) == preDir) {
                    //Add pickups after dropoffs are completed
                    if (iQ.get(i).getActionType() == ElevatorAction.PICKUP) {
                        //Append first
                        return i;
                    } else if (iQ.get(i).getActionType() == ElevatorAction.DROPOFF) {
                        //Fetch next object of type pickup
                        return fetchQueueIndex(iQ, floor, i + 1);
                    }
                }               
			}
            
            //update previous dir
            preDir = dir;
		}

        return e.getQueue().size();
    }
    
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