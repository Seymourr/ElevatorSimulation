import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class SelectiveCollective extends Algorithm {

	public SelectiveCollective(ElevatorSpecs spec) {
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
 				allElevators.set(1,  assignShuttleElevator(allElevators.get(1), p)); //TODO: Fix for dd
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
	private int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		return getRandomElevator(elevators, p);
	}



	private ArrayList<ElevatorInterface> assignLocalWithSelectiveCollective(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = getElevator(elevators, p);

		//Check double decked possibilty, addto where?
		CarPosition p = CarPosition.NULL;
		if(specs.getLocal() == ElevatorType.DOUBLE) {

		}


		int passengerDirection = 1;
		if(p.getOrigin() > p.getDestination()) {
			passengerDirection = -1;
		}

		if(elevators.get(elevatorIndex).getStatus().direction == 0) {
			elevators.get(elevatorIndex).addToQueue(p, elevators.get(elevatorIndex).getQueue().size(), elevators.get(elevatorIndex).getQueue().size() + 1, carPos);
		} else if(elevators.get(elevatorIndex).getStatus().direction == passengerDirection) {
			if(passengerDirection == 1) {
				if(elevators.get(elevatorIndex).getStatus().floor > p.getOrigin()) {
					//assignmethod 1
				} else {
					//assignmethod 2
				}
			} else {
				if(elevators.get(elevatorIndex).getStatus().floor < p.getOrigin()) {
					//assignmethod 1
				} else {
					//assignmethod 2
				}
			}
		} else {
			//Elevator is going the other way, bummer.
			//assignmethod 1
		}

		//assign to proper position in queue (its position in double decked case is already known)
		return elevators;
	}



}