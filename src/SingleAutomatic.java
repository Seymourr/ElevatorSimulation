 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Random;
 import java.util.LinkedList;


 public class SingleAutomatic extends Algorithm {


 	public SingleAutomatic(ElevatorSpecs spec){
 		this.specs = spec;
 	}

 	/**
 	* Goes through the list of calls and assigns elevators to passangers based on those calls.
 	*/
 	public ArrayList<ArrayList<ElevatorInterface>> manageCalls(ArrayList<ArrayList<ElevatorInterface>> allElevators, LinkedList<Passenger> calls)
 	{
 		while(!calls.isEmpty()) {
 			Passenger p = calls.removeFirst();
 			if(containsFloor(allElevators.get(0), p.getOrigin(), p.getDestination())) {
 				//Bot ride
 				allElevators.set(0, assignLocalWithSingleAutomatic(allElevators.get(0), p));
 			} else if(containsFloor(allElevators.get(1), p.getOrigin(), p.getDestination())) {
 				//Shuttle ride
 				allElevators.set(1,  assignShuttleElevator(allElevators.get(1), p));
 			} else if(containsFloor(allElevators.get(2), p.getOrigin(), p.getDestination())) {
 				//Top ride
 				allElevators.set(2, assignLocalWithSingleAutomatic(allElevators.get(2), p));
 			} else {
 				System.out.println("ERROR IN MANAGECALL, SIMULATION ABORTED");
 				System.exit(0);
 			}
 		}

 		return allElevators;
 	}
	

 	/**
 	* Returns a index of a elevator, attempting to provide a empty one. If no such is found, a random one is returned. 
 	*/
	protected int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int emptyCheck = getRandomEmptyElevator(elevators, p);
		if(emptyCheck != -1) return emptyCheck;
		
		//No empty elevator was found. Pick a random one 
		return getRandomElevator(elevators, p);
	}

	/**
	* Assigns calls to local elevators or double decked elevators (depending on the elevator type).
	*/
	private ArrayList<ElevatorInterface> assignLocalWithSingleAutomatic(ArrayList<ElevatorInterface> elevators, Passenger p) {
		if(elevators.get(0).ofType() == ElevatorType.SINGLE) {
			int elevatorIndex = getElevator(elevators, p);
			elevators.get(elevatorIndex).addToQueue(p, elevators.get(elevatorIndex).getQueue().size(), elevators.get(elevatorIndex).getQueue().size() + 1, CarPosition.NULL);
		} else if(elevators.get(0).ofType() == ElevatorType.DOUBLE) {
			int elevatorIndex = getElevator(elevators, p);
			CarPosition pos = CarPosition.NULL;

			if(p.getDestination() == elevators.get(elevatorIndex).getFloors()[elevators.get(elevatorIndex).getFloors().length - 1]) {
				pos = CarPosition.UPPER;
				if(p.getOrigin() == specs.getLobbyFloor() || p.getOrigin() == specs.getSkylobbyfloor()) {
					p.shift();
				}
			} else {
				if(p.getOrigin() < p.getDestination()) {
					pos = CarPosition.UPPER;
				} else {
					pos = CarPosition.LOWER;
				}
			}
			elevators.get(elevatorIndex).addToQueue(p, elevators.get(elevatorIndex).getQueue().size(), elevators.get(elevatorIndex).getQueue().size() + 1, pos);
		} else {
			System.out.println("Something went wrong with assigning elevators, ABORTING SIMULATION");
			System.exit(0);
		}

		return elevators;
	}
}
