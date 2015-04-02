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
			if(elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
				if(elevators.get(i).getStatus().floor == p.getOrigin() || elevators.get(i).getStatus().floor - 1 == p.getOrigin()) {
					chosenElevator = i;
					break;
				}
			}
		}

		int from = 0;

		if(chosenElevator == -1) {
			//No optimal elevator was found, assign a random one
			Random r = new Random();
			chosenElevator = r.nextInt(elevators.size());
			from = elevators.get(chosenElevator).getQueue().size();
			for(int i = 0; i < elevators.get(chosenElevator).getQueue().size(); i++) {
				if(elevators.get(chosenElevator).getQueue().get(i).getPassenger().getOrigin() == p.getOrigin() && elevators.get(chosenElevator).getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
					from = i;
					break;
				}
			}
		
		} 
		int to = elevators.get(chosenElevator).getQueue().size() + 1;

		CarPosition pos = CarPosition.NULL;
		if(specs.getShuttle() == ElevatorType.DOUBLE) {
			pos = getCarPosShuttle(p);

		if(pos == CarPosition.NULL) {
			System.out.println("ERROR IN DECIDING POSITION FOR DD, ABORTING");
			System.exit(0);
		}
		
		}
		elevators.get(chosenElevator).addToQueue(p, from, to, pos);
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
/*
		if(elevator.getStatus().floor == p.getOrigin()) {
				if(elevator.getStatus().upperCarPassengers < specs.getCarryCapacity()) {
					pos = CarPosition.UPPER;
				} else if(elevator.getStatus().lowerCarPassengers < specs.getCarryCapacity()) {
					pos = CarPosition.LOWER;
					p.shift();
				} else {
					pos = CarPosition.UPPER; //No space in elevator, just assign it to upper
				}
		} else if(elevator.getStatus().floor - 1 == p.getOrigin()){
				if(elevator.getStatus().lowerCarPassengers < specs.getCarryCapacity()) {
					pos = CarPosition.LOWER;
				} else if(elevator.getStatus().upperCarPassengers < specs.getCarryCapacity()) {
					pos = CarPosition.UPPER;
					p.shift();
				} else {
					pos = CarPosition.LOWER; //No space in elevator, just assign it to lower
				}
		} else {
			//Not parked at close proximity, decide up or down for incoming elevator
			int pickingUpAtOtherFloor = 0;
			int pickingUpAtPassengerFloor = 0;
			for(int i = 0; i < elevator.getQueue().size(); i++) {
				if(elevator.getQueue().get(i).getPassenger().getOrigin() == p.getOrigin() && elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
					pickingUpAtPassengerFloor +=1;
				} else if((elevator.getQueue().get(i).getPassenger().getOrigin() == p.getOrigin() + 1 || elevator.getQueue().get(i).getPassenger().getOrigin() == p.getOrigin() - 1) && elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP){
					pickingUpAtOtherFloor +=1;
				}
			}

			if(pickingUpAtPassengerFloor < specs.getCarryCapacity()) {
				if(p.getOrigin() == specs.getSkylobbyfloor() || p.getOrigin() == specs.getLobbyFloor()) {
					pos = CarPosition.LOWER;
				} else {
					pos = CarPosition.UPPER;
				}
			} else if(pickingUpAtOtherFloor < specs.getCarryCapacity()) {
				if(p.getOrigin() == specs.getSkylobbyfloor() || p.getOrigin() == specs.getLobbyFloor()) {
					pos = CarPosition.UPPER;
				} else {
					pos = CarPosition.LOWER;
				}
			} else {
				//Not even incoming elevator can afford, assign it to closest cart
				if(p.getOrigin() == specs.getSkylobbyfloor() || p.getOrigin() == specs.getLobbyFloor()) {
					pos = CarPosition.LOWER;
				} else {
					pos = CarPosition.UPPER;
				}
			}

		}
		return pos;

	}
*/



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
	* Returns a semi-random elevator position in a list of elevators
	*/
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

}

