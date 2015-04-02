import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.HashMap;

public class SelectiveCollective extends Algorithm {

	public SelectiveCollective(ElevatorSpecs spec) {
		this.specs = spec;
	}

	private class pointHolder {
		public int pickUpPoint;
		public boolean willReverse;

		public pointHolder(int p, boolean r) {
			this.pickUpPoint = p;
			this.willReverse = r;
		}
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

		int passengerDirection = 1;
		if(p.getOrigin() > p.getDestination()) {
			passengerDirection = -1;
		}

		if(passengerDirection == elevators.get(elevatorIndex).getStatus().direction) {
			if((passengerDirection == 1 && elevators.get(elevatorIndex).getStatus().floor <= p.getOrigin()) || (passengerDirection == -1 && elevators.get(elevatorIndex).getStatus().floor >= p.getOrigin())) {
				//assign so that it picks up on the way
				elevators.set(elevatorIndex, pickUpOnTheWay(elevators.get(elevatorIndex), p, passengerDirection, pos));
			} else {
				//assign so that it picks up after turning twice
				elevators.set(elevatorIndex, pickUpOnReverse(elevators.get(elevatorIndex), p, passengerDirection, pos));
			}
		} else if(elevators.get(elevatorIndex).isIdle()) { //Empty, idle elevator
			int from = elevators.get(elevatorIndex).getQueue().size();
			int to = from + 1;
			if(!elevators.get(elevatorIndex).addToQueue(p, from, to, pos)) {
			System.out.println("Error picking up passenger going from " + p.getOrigin() + " to " +p.getDestination());
			System.out.println("Pickupposition: " + from + " Dropposition: " + to + " Pos in elevator: " + pos);
			System.out.println(elevators.get(elevatorIndex).getStatus().getStringRepresentation());
			System.out.println("Queue size: " + elevators.get(elevatorIndex).getQueue().size());
			}
		} else {
			elevators.set(elevatorIndex, pickUpOnReverse(elevators.get(elevatorIndex), p, passengerDirection, pos)); //Elevator going in opposite direction
		}

		//DEBUGG STUFF UP NEXT
		int inQueue = 0;
		for(int i = 0; i < elevators.get(elevatorIndex).getQueue().size(); i++) {
				if(elevators.get(elevatorIndex).getQueue().get(i).getPassenger() == p) {
				 inQueue += 1;
				}

			}
		
		if(inQueue != 2) {
			System.out.println("ERROR, PASSENGER NOT IN QUEUE TWICE!");
			System.out.println(p.getOrigin() + " " + p.getDestination() + " " + inQueue);
			System.out.println("E direction: " + elevators.get(elevatorIndex).getStatus().direction);
			System.out.println("E floor: " + elevators.get(elevatorIndex).getStatus().floor);
			for(int i = 0; i < elevators.get(elevatorIndex).getFloors().length; i++) {
				System.out.print(" " + elevators.get(elevatorIndex).getFloors()[i]);
			}
			for(int i = 0; i < elevators.get(elevatorIndex).getQueue().size(); i++) {
				if(elevators.get(elevatorIndex).getQueue().get(i).getPassenger() == p) {
					System.out.println(elevators.get(elevatorIndex).getQueue().get(i).getActionType());
				}
			}
			System.exit(0);
		}
			return elevators;
	}

	/*
	* Pick up passenger with an elevator heading the same direction as the passenger,
	* available to pick up the passenger in the same direction.
	*/
	public ElevatorInterface pickUpOnTheWay(ElevatorInterface elevator, Passenger p, int passDir, CarPosition pos) {
		int pickUpPosition = -1;
		int dropPosition = -1;

		int callDest = -1;
		int preCall = -1;
			
		pointHolder res = getPickUpPoint(elevator, p, passDir, 0, elevator.getStatus().floor);
		pickUpPosition = res.pickUpPoint;
		dropPosition = getDropOffPoint(elevator, res, p, passDir);

		if(pickUpPosition == -1 ||dropPosition == -1) {
			System.out.println("ERROR IN PICKUPONTHEWAY, FIX");
			System.exit(0);
		}

		if(!elevator.addToQueue(p, pickUpPosition, dropPosition, pos)) {
			System.out.println("Error picking up passenger going from " + p.getOrigin() + " to " +p.getDestination());
			System.out.println("Pickupposition: " + pickUpPosition + " Dropposition: " + dropPosition + " Pos in elevator: " + pos);
			System.out.println(elevator.getStatus().getStringRepresentation());
			System.out.println("Queue size: " + elevator.getQueue().size());
		}
		return elevator;
	}

	/*
	* Pick up the passenger with an elevator when the elevator is heading the same direction as the passenger,
	* and has the possibility to pick up the passenger.
	*/
	public ElevatorInterface pickUpOnReverse(ElevatorInterface elevator, Passenger p, int passDir, CarPosition pos) {
		int pickUpPosition = -1;
		int dropPosition = -1;

		int callDest = -1;
		int preCall = -1;
		int reversePoint = -1;

		//Find out where the elevator will or can reverse
		reversePoint = getReversePoint(elevator, p, 0);

		//Are we having one more reverse if the elevator is going the same direction as the passengers travel? (Has passed passenger floor)
		if(passDir == elevator.getStatus().direction && reversePoint != -1) {
			reversePoint = getReversePoint(elevator, p, reversePoint);
		}

		if(reversePoint == -1) { //No reverse, just add to end 
			pickUpPosition = elevator.getQueue().size();
			dropPosition = pickUpPosition +1;
			if(!elevator.addToQueue(p, pickUpPosition, dropPosition, pos)) {
				System.out.println("Error picking up passenger going from " + p.getOrigin() + " to " +p.getDestination());
				System.out.println("Pickupposition: " + pickUpPosition + " Dropposition: " + dropPosition + " Pos in elevator: " + pos);
				System.out.println(elevator.getStatus().getStringRepresentation());
				System.out.println("Queue size: " + elevator.getQueue().size());
			}
			return elevator;
		}

		//Elevator is going in opposite direction of passenger after reverse

		//Assign pickup
		int reverseFloor = -1;
		if(elevator.getQueue().get(reversePoint).getActionType() == ElevatorAction.PICKUP) {
			reverseFloor = elevator.getQueue().get(reversePoint).getPassenger().getOrigin();
		} else {
			reverseFloor = elevator.getQueue().get(reversePoint).getPassenger().getDestination();
		}

		if(passDir == 1) {
			if(reverseFloor > p.getOrigin()) {
				pickUpPosition = reversePoint;
			}
		} else {
			if(reverseFloor < p.getOrigin()) {
				pickUpPosition = reversePoint;
			}
		}

		pointHolder pointInfo;
		if(pickUpPosition == -1) {
			pointInfo = getPickUpPoint(elevator, p, passDir, reversePoint, reverseFloor);
			pickUpPosition = pointInfo.pickUpPoint;
		} else {
			pointInfo = new pointHolder(pickUpPosition, false);
		}
		
		dropPosition = getDropOffPoint(elevator, pointInfo, p, passDir);
				
		if(pickUpPosition == -1 ||dropPosition == -1) {
			System.out.println("ERROR IN REVERSE, FIX");
			System.exit(0);
		}

		if(!elevator.addToQueue(p, pickUpPosition, dropPosition, pos)) {
			System.out.println("Error picking up passenger going from " + p.getOrigin() + " to " +p.getDestination());
			System.out.println("Pickupposition: " + pickUpPosition + " Dropposition: " + dropPosition + " Pos in elevator: " + pos);
			System.out.println(elevator.getStatus().getStringRepresentation());
			System.out.println("Queue size: " + elevator.getQueue().size());
		}
			return elevator;
	}

	/*
	* Calculates in which position in the given elevators queue the passenger will be picked up.
	*/
	private pointHolder getPickUpPoint(ElevatorInterface elevator, Passenger p, int passDir, int indexPoint, float floor) {
		int pickUpPosition = -1;

		int preCall = -1;
		int callDest =-1;
		boolean willReverse = false;
		pointHolder result;


		//NÅNTING I DENNA LOOP GÖR SÅ ATT RESULTATET TAR LÄNGRE/MINDRE TID. FIX OR IGNORE
		//Det går att ignorera o få bättre resultat (dock inte collective då..)
/*
		for(int i = indexPoint; i < elevator.getQueue().size(); i++) {
			preCall = callDest;
			if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
				callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
			} else {
				callDest = elevator.getQueue().get(i).getPassenger().getDestination();
			}

			if(passDir == 1) {
				if(preCall > callDest) {
					willReverse = true;
					pickUpPosition = i;
					break;
				} else if(callDest >= p.getOrigin()) {
					pickUpPosition = i;
					break;
				}
			} else {
				if(preCall < callDest && preCall != -1) {
					willReverse = true;
					pickUpPosition = i;
					break;
				} else if(callDest <= p.getOrigin()) {
					pickUpPosition = i;
					break;
				}
			}
		}
	*/	
	//Is all calls in the queue done prior to passenger floor without a reverse?
		if(pickUpPosition == -1) {
			pickUpPosition = elevator.getQueue().size();
		}

	//	if((int)floor == p.getOrigin()) {
	//		pickUpPosition = 0; //On same floor
	//	} 

		


	//
		
//		pickUpPosition = elevator.getQueue().size();
	//	System.out.println(indexPoint);
		result = new pointHolder(pickUpPosition, willReverse);
		return result;
	}

	/*
	* Calculates in which position in the given elevators queue the passenger should be dropped off at.
	*/
	private int getDropOffPoint(ElevatorInterface elevator, pointHolder pickUpData, Passenger p, int passDir) {
		boolean willReverse = pickUpData.willReverse;
		int pickUpPosition = pickUpData.pickUpPoint;
		int dropPosition = -1;
		int preCall = -1;
		int callDest = -1;
		
		if(willReverse) {
			dropPosition = pickUpPosition + 1; //Drop off before reverse
		} else {
			for(int i = 0; i < elevator.getQueue().size(); i++) {
				if(i > pickUpPosition) { //Skip ahead to relevant queue positions
					preCall = callDest;
					if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
						callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
					} else {
						callDest = elevator.getQueue().get(i).getPassenger().getDestination();
					}
					if(passDir == 1) {
						if(preCall > callDest) {
							dropPosition = i; 	//Reverse, add to reverse position
							break;
						} else if(callDest >= p.getDestination()) {
							dropPosition = i;  //Drop off before next destination
							break;
						}
					} else {
						if(preCall < callDest && preCall != -1) {
							dropPosition = i; 	//Reverse, add to reverse position
							break;
						} else if(callDest <= p.getDestination()) {
							dropPosition = i;  //Drop off before next destination
							break;
						}
					}
				}
			}
			
			//Is all calls in the queue done prior to passengers destination without a reverse?
			if(dropPosition == -1) {
				dropPosition = elevator.getQueue().size(); //Just put to end of list (final thing to do)
				if(dropPosition == pickUpPosition) {
					dropPosition += 1;
				}
			}
		}
	
		return dropPosition;
	}


	/*
	* Returns the queue point where the given elevator will reverse, if it does that.
	*/
	private int getReversePoint(ElevatorInterface elevator, Passenger p, int start) {
		int reversePoint = -1;

		int callDest = -1;
		int preCall = -1;

		for(int i = start; i < elevator.getQueue().size(); i++) {
			preCall = callDest;
			if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
				callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
			} else {
				callDest = elevator.getQueue().get(i).getPassenger().getDestination();
			}

			if(elevator.getStatus().direction == 1) {
				if(preCall > callDest) {
					reversePoint = i;
					break;
				}
			} else {
				if(preCall < callDest && preCall != -1) {
					reversePoint = i;
					break;
				}
			}
		}
		return reversePoint;
	}

}