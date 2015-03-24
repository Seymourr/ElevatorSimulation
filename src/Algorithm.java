import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public abstract class Algorithm {

	
	ElevatorSpecs specs;	
	public abstract ArrayList<ArrayList<ElevatorInterface>> manageCalls(ArrayList<ArrayList<ElevatorInterface>> elevators, LinkedList<Passenger> calls);
	protected abstract int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p);


	/**
	* Attempts to pick an optimal shuttle for the given passenger. The optimal shuttle is the idle one at 
	* the same floor as the passenger. If no optimal shuttle is found, the passenger is assinged to a random
	* shuttle.
	*/
	public ArrayList<ElevatorInterface> assignShuttleElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int chosenElevator = -1;
		for(int i = 0; i < elevators.size(); i++) {
			if(elevators.get(i).getStatus().direction == 0 && elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
				if(elevators.get(i).getStatus().floor == p.getOrigin()) {
					//Optimal elevator found
					chosenElevator = i;
				}
			}
		}
		CarPosition pos = CarPosition.NULL;
		if(specs.getShuttle() == ElevatorType.DOUBLE) {
			if(p.getOrigin() == specs.getLobbyFloor() || p.getOrigin() == specs.getSkylobbyfloor()) {
				
				pos = CarPosition.LOWER;
			} else {
				pos = CarPosition.UPPER;
			}
		}
		if(chosenElevator == -1) {
			//No optimal elevator was found, assign a random one
			Random r = new Random();
			chosenElevator = r.nextInt(elevators.size());
			elevators.get(chosenElevator).addToQueue(p, elevators.get(chosenElevator).getQueue().size(), elevators.get(chosenElevator).getQueue().size() + 1, pos);
		} else {
			elevators.get(chosenElevator).addToQueue(p, 0, elevators.get(chosenElevator).getQueue().size() + 1, pos);
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
	* Returns a random elevator position in a list of elevators
	*/
	protected int getRandomElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		ArrayList<Integer> temp2 = new ArrayList<Integer>();
		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				temp.add(elevators.get(i));
				temp2.add(i);
			}
		}
		Random r = new Random();
		int number = r.nextInt(temp.size());
		return temp2.get(number);
	}

	protected int getRandomEmptyElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = -1;
		ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		ArrayList<Integer> temp2 = new ArrayList<Integer>();
		for(int i = 0; i < elevators.size(); i++) {
			if(elevators.get(i).getStatus().direction == 0 && elevators.get(i).getStatus().passengers == 0) {
				temp.add(elevators.get(i));
				temp2.add(i);
			}
		}
		if(temp.isEmpty()) {
			return -1;
		}
		Random r = new Random();
		int number = r.nextInt(temp.size());
		return temp2.get(number);
	}
}

