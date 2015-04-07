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

	//BUGGED FOR DOUBLE DECKED
	protected int getSelectiveElevator(ArrayList<ElevatorInterface> e, int[] z, Passenger p) {
		if(p.getDestination() - p.getOrigin() == 0) {
			System.out.println(p.getDestination());
			System.out.println(p.getOrigin());
			System.out.println("ERROR: Origin and Destination cannot be the same. Traffic generator bugged (Double decked case)");
			System.exit(0);
		}
		int pDir = (p.getDestination() - p.getOrigin()) /Math.abs(p.getDestination() - p.getOrigin());
		ArrayList<Integer> potentialElevators = new ArrayList<Integer>();
		for(Integer i : z) {
			int eDir = e.get(i).getStatus().direction;
			if(eDir == pDir) {
				if(pDir == 1 && e.get(i).getStatus().floor < p.getOrigin()) {
					potentialElevators.add(i);
				} else if(pDir == -1 && e.get(i).getStatus().floor > p.getOrigin()) {
					potentialElevators.add(i);
				}
			}
		}

		if(potentialElevators.isEmpty()) {
			return getRandomElevator(e, z, p);
		}

		//Return the one with shortest queue
		int index = 0;
		int bestSize = e.get(potentialElevators.get(0)).getQueue().size();
		for(int i = 0; i < potentialElevators.size(); i++) {
			if(e.get(potentialElevators.get(i)).getQueue().size() < bestSize) {
				index = i;
				bestSize = e.get(potentialElevators.get(i)).getQueue().size();
			}
		}
		return potentialElevators.get(index);
       
		

	}
		
		
	

	protected ArrayList<ElevatorInterface> assignLocal(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = getElevator(elevators, p);

		CarPosition pos = CarPosition.NULL;
		if(specs.getLocal() == ElevatorType.DOUBLE) {
			pos = getCarPos(elevators.get(elevatorIndex), p);
		}

		int pickUp = getPoint(elevators.get(elevatorIndex), p, 0, p.getOrigin());
		int dropOff = getPoint(elevators.get(elevatorIndex), p, pickUp, p.getDestination());
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


	//TODO: Fix for double decked. Produce worse result than old selective collective..
	public int getPoint(ElevatorInterface e, Passenger p, int startIndex, int floor) {
		int point = -1;
		int pAbs = Math.abs(p.getDestination() - p.getOrigin());
		int pDir = (p.getDestination() - p.getOrigin())/pAbs;
		LinkedList<ElevatorQueueObject> iQ = e.getQueue();

		int preDir = -1;
		int dir = -1;
		for(int i = startIndex; i < iQ.size() - 1; i++) {
			int t1 = getFloor(iQ.get(i + 1));
			int t2 = getFloor(iQ.get(i));
			int t3 = t1 - t2;
			if(t3 == 0) {
				continue;
			}
			preDir = dir;
			dir = t3/Math.abs(t3);
			if(pDir == dir) {
				if(isBetween(t1, t2, floor)) {
					point = i + 1;
				}
			} else if(preDir == pDir){
				int t4 = floor - t2;
				if(t4 != 0) {
					if(t4/Math.abs(t4) == pDir) {
						point = i + 1;
					}
				}
			}
		}

		if(point == -1) {
			point = e.getQueue().size();
		}
		return point;

	}

	private int specialCase(ElevatorInterface e, Passenger p, int floor, int pDir) {
		if(e.getQueue().size() == 0) {
			return 0;
		}
		int index = -1;
		for(int i = 0; i < e.getQueue().size(); i++) {
			int mAbs = Math.abs((int)e.getStatus().floor - getFloor(e.getQueue().get(i)));
			if(mAbs == 0) {
				return 0; //Special case, for now. 
			}
			int dir = ((int)e.getStatus().floor - getFloor(e.getQueue().get(i)))/ mAbs;
			if(dir == pDir && isBetween(getFloor(e.getQueue().get(i)), (int)e.getStatus().floor, floor)) {
				index = i;
			}
		}
		if(index == -1) {
			index = e.getQueue().size();
		}
		return index;
	}
	private int getFloor(ElevatorQueueObject q) {
		int ret = 0;
		if(q.getActionType() == ElevatorAction.PICKUP) {
			ret = q.getPassenger().getOrigin();
		} else {
			ret = q.getPassenger().getDestination();
		}
		return ret;
	}

	//Checkup
	private boolean isBetween(int l, int s, int f) {
		if(l > s && (f < l && f >= s)) return true;
		if(l < s && (f > l && f <= s)) return true;
		if(l == s && f == s) return true; //?
		return false;
	}
}