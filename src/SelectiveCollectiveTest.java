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
 				allElevators.set(0, assignLocalWithSelectiveCollective(allElevators.get(0), p));
 			} else if(containsFloor(allElevators.get(1), p.getOrigin(), p.getDestination())) {
 				//Shuttle ride
 				allElevators.set(1,  assignShuttleElevator(allElevators.get(1), p)); 
 			} else if(containsFloor(allElevators.get(2), p.getOrigin(), p.getDestination())) {
 				//Top ride
 				allElevators.set(2, assignLocalWithSelectiveCollective(allElevators.get(2), p));
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

	protected ArrayList<ElevatorInterface> assignLocalWithSelectiveCollective(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = getElevator(elevators, p);

		CarPosition pos = CarPosition.NULL;
		if(specs.getLocal() == ElevatorType.DOUBLE) {
			pos = getCarPos(elevators.get(elevatorIndex), p);
		}

		int pickUp = getPoint(elevators.get(elevatorIndex), p, index start, p.getOrigin());
		int dropOff = getPoint(elevators.get(elevatorIndex), p, pickUp. p.getDestination());
		return elevators;
	}

	public int getPoint(ElevatorInterface e, Passenger p, int startIndex, int floor) {
		int point = -1;
		int pDir = (p.getDestination() - p.getOrigin())/Math.abs(p.getDestination() - p.getOrigin());
		ArrayList<ElevatorQueueObject> iQ = e.getQueue();
		int t1 = getFloor(iQ.get(startIndex + 1));
		int t2 = getFloor(iQ.get(startIndex));
		int preDir = (t1-t2)/Math.abs(t1-t2);
		for(int i = startIndex + 1; i < e.getQueue().size() - 1; i++) {
			int t1 = getFloor(e.getQueue(i + 1));
			int t2 = getFloor(e.getQueue(i));
			int dir = (t1 - t2) / Math.abs(t1-t2);
			if(pDir == dir) {
				if(isBetween(t1, t2, floor)) {
					point = i;
				}
			} else if(//Somecondition) {
				point = i; //I + 1? Why do this?
			}
		}
		if(point = -1) {
			point = e.getQueue().size();
		}
		return point;

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