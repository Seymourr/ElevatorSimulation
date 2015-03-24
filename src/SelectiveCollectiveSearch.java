import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class SelectiveCollectiveSearch extends Algorithm {

	public SelectiveCollectiveSearch(ElevatorSpecs spec) {
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
 				allElevators.set(0, assignLocalWithSelectiveCollectiveSearch(allElevators.get(0), p));
 			} else if(containsFloor(allElevators.get(1), p.getOrigin(), p.getDestination())) {
 				//Shuttle ride
 				allElevators.set(1,  assignShuttleElevator(allElevators.get(1), p)); //TODO: Fix for dd
 			} else if(containsFloor(allElevators.get(2), p.getOrigin(), p.getDestination())) {
 				//Top ride
 				allElevators.set(2, assignLocalWithSelectiveCollectiveSearch(allElevators.get(2), p));
 			} else {
 				System.out.println("ERROR IN MANAGECALL, SIMULATION ABORTED");
 				System.exit(0);
 			}
 		}

 		return allElevators;
 	}
	

 	private boolean isPotentialElevator(ElevatorInterface e, Passenger p) {
 		boolean available = false;
 		int passengerAmount = e.getStatus().passengers;
 		if(e.getStatus().direction == 0) {
 			//idle elevators are always okay to take if they are not full
 			return true;
 		} 

 		//This elevator is moving
 		int passengerDirection = 0;
 		if(p.getOrigin() < p.getDestination()) {
 			passengerDirection = 1;
 		} else {
 			passengerDirection = -1;
 		}

 		if(passengerDirection == e.getStatus().direction) {
 			//The elevator is moving in the same direction as the passengers route, but is it too far ahead/behind? 
		 	if(passengerDirection == 1) {
		 		if(e.getStatus().floor > p.getOrigin()){
		 			return false;
		 		}
		 		for(int i = 0; i < e.getQueue().size(); i++) {
		 			if(e.getQueue().get(i).getActionType() == ElevatorType.PICKUP) {
 						if(e.getQueue().get(i).getPassenger().getOrigin() < p.getOrigin()) {
 							passengerAmount += 1;
 						}
 					} else if(e.getQueue().get(i).getPassenger().getDestination() < p.getOrigin()) {
 							passengerAmount -= 1;
 					}
 				}
		 	}

		 	if(passengerDirection == -1) {
		 		if(e.getStatus().floor < p.getOrigin()){
		 			return false;
		 		}
		 		for(int i = 0; i < e.getQueue().size(); i++) {
		 			if(e.getQueue().get(i).getActionType() == ElevatorType.PICKUP) {
 						if(e.getQueue().get(i).getPassenger().getOrigin() > p.getOrigin()) {
 							passengerAmount += 1;
 						}
 					} else if(e.getQueue().get(i).getPassenger().getDestination() > p.getOrigin()) {
 							passengerAmount -= 1;
 					}
 				}
		 	}

		 	//Is the elevator full?
		 	if(passengerAmount < specs.getCarryCapacity() || (e.ofType() == ElevatorType.DOUBLE && passengerAmount < specs.getCarryCapacity() * 2)) {
		 		return true; //Elevator moving in same direction, and has capability of picking up passenger!
		 	}
		 }

		 return false;
 	}
 	

 	//No elevators are idle, no elevators are going in the right direction and can pick up
 	private int getSemiPotentialElevator(ElevatorInterface elevator, Passenger p) {
 		int elevatorIndex = -1;

 		int passengerDirection = 0;
 		if(p.getOrigin() < p.getDestination()) {
 			passengerDirection = 1;
 		} else {
 			passengerDirection = -1;
 		}
 		if(elevator.getStatus().direction == passengerDirection) {
 			return false; //We know from isPotentialElevator that elevators in the same direction are no good to take (since elevators in the opposing direction will (hopefully) turn faster)
 		} else {
 			for(int i = 0; i < elevator.getQueue().size(); i++) {

 			}
 		}
 		
 		return elevatorIndex;
 	}
 	/**
 	* Returns a index of a elevator
 	*/
	private int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = -1;
			//Best elevator is the one at the same floor, idle
			//Second to best is the closest elevator that goes in the same direction (right direction too)
			//Third to best is the closest elevator that will change direction to a floor most close to the passengers
			//If no elevators are operational, the only alternative is a random elevator closest to the passenger
		//Of course, the elevators would have to not be full, and perhaps there should be a diversity.. 
		//Strategy: If there are multiple candidate fulfilling the choice above, select the one (if any) that in addition to the passenger, wont be full.
		ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		for(int i = 0; i < elevators.size(); i++) {
			if(isPotentialElevator(elevators.get(i), p) {
				temp.add(i);
			}
		}

		if(temp.isEmpty()) {
			for(int i = 0; i < elevators.size(); i++) {
				if(isSemiPotentialElevator(elevators.get(i), p)) {
					temp.add(i);
				}
			}
		}




		ArrayList<ElevatorInterface> temp = new ArrayList<ElevatorInterface>();
		ArrayList<Integer> tempNumber = new ArrayList<Integer>();
		for(int i = 0; i < elevators.size(); i++) {
			if(elevatorContainsFloor(elevators.get(i), p.getOrigin(), p.getDestination())) {
				temp.add(elevators.get(i));
				tempNumber.add(i);
			}
		}
		if(temp.size() == 0) {
			System.out.println("SOMETHING WRONG HAPPENED IN GETELEVATOR");
			System.exit(0);
		}

		Random r = new Random();
 		int number = r.nextInt(temp.size()); 
 		int index = tempNumber.get(number);
 		return index;
	}



	private ArrayList<ElevatorInterface> assignLocalWithSelectiveCollectiveSearch(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = getElevator(elevators, p);
		//assign to proper position in queue (its position in double decked case is already known)
		return elevators;
	}
	/**
	* Assigns calls to local elevators or double decked elevators (depending on the elevator type).
	*/
	private ArrayList<ElevatorInterface> assignLocalWithSelectiveCollectivePREVIOUS(ArrayList<ElevatorInterface> elevators, Passenger p) {
		if(specs.getLocal() == ElevatorType.SINGLE) {
			int elevatorIndex = getElevator(elevators, p);
			//Assign to elevator queue properly (call pickup should be in order, and call dropout as well (before highers))

		} else if(specs.getLocal() == ElevatorType.DOUBLE){
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

			//Assign to elevator

		} else {
			System.out.println("Something went wrong with assigning elevators, ABORTING SIMULATION");
			System.exit(0);
		}
	}	


}