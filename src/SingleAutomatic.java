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
 				allElevators.set(1,  assignShuttleElevator(allElevators.get(1), p, CarPosition.NULL));
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
 	* Returns a random index of elevators.
 	*/
	private int getRandomElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		ArrayList<Integer> tempNumber = new ArrayList<Integer>();
		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				temp.add(elevators.get(i));
				tempNumber.add(i);
			}
		}
		if(temp.size() == 0) {
			System.out.println("SOMETHING WRONG HAPPENED IN GETRANDOMELEVATOR");
			System.exit(0);
		}

		Random r = new Random();
 		int number = r.nextInt(temp.size()); 
 		int index = tempNumber.get(number);
 		return index;
	}

	/**
	* Assigns calls to local elevators or double decked elevators (depending on the elevator type).
	*/
	private ArrayList<ElevatorInterface> assignLocalWithSingleAutomatic(ArrayList<ElevatorInterface> elevators, Passenger p) {
		if(elevators.get(0).ofType() == ElevatorType.SINGLE) {
			int elevatorIndex = getRandomElevator(elevators, p);
			elevators.get(elevatorIndex).addToQueue(p, elevators.get(elevatorIndex).getQueue().size(), elevators.get(elevatorIndex).getQueue().size() + 1, CarPosition.NULL);
		} else if(elevators.get(0).ofType() == ElevatorType.DOUBLE) {
			int elevatorIndex = getRandomElevator(elevators, p);
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
	 /**
 	 * Checks that a given origin and destination is within the range of given elevators. 
	 * @param temp
 	 * @param origin
 	 * @param destination
 	 * @return True, if within range, false otherwise
 	 */
 	private boolean containsFloor(ArrayList<ElevatorInterface> elevators, int origin, int destination) {
 		boolean inOrigin = false;
 		boolean goingToDestination = false;

 		for(int i = 0; i < elevators.size(); i++) {
 			for(int j = 0; j < elevators.get(i).getFloors().length; j++) {
 				if(elevators.get(i).getFloors()[j] == origin) {
 					inOrigin = true;
 				}

 				if(elevators.get(i).getFloors()[j] == destination) {
 					goingToDestination = true;
 				}

 				if(inOrigin && goingToDestination) {
 					return true; //There is some way to the goal
 				}
 			}
 		}

 		return false;
	}

	/**
	* Confirms whether the given elevator is a possible elevator to take, that is
	* it can take the passanger from its origin to its destination.
	*/
	private boolean elevatorContainsFloor(ElevatorInterface e, int origin, int destination) {
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
}