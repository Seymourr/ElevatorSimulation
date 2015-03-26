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
	protected int getElevator(ArrayList<ElevatorInterface> elevators, Passenger p) {
		return getRandomElevator(elevators, p);
	}



	protected ArrayList<ElevatorInterface> assignLocalWithSelectiveCollective(ArrayList<ElevatorInterface> elevators, Passenger p) {
		int elevatorIndex = getElevator(elevators, p);

		//Check double decked possibilty, addto where?
		CarPosition pos = getCarPos(elevators.get(elevatorIndex), p);

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
		} else if(elevators.get(elevatorIndex).getStatus().direction == 0) { //Empty, idle elevator
			int from = elevators.get(elevatorIndex).getQueue().size();
			int to = from + 1;
			elevators.get(elevatorIndex).addToQueue(p, from, to, pos);
		} else {
			elevators.set(elevatorIndex, pickUpOnReverse(elevators.get(elevatorIndex), p, passengerDirection, pos)); //Elevator going in opposite direction
		}
			return elevators;
	}

	public ElevatorInterface pickUpOnTheWay(ElevatorInterface elevator, Passenger p, int passDir, CarPosition pos) {
		int pickUpPosition = -1;
		int dropPosition = -1;

		int callDest = -1;
		int preCall = -1;
		boolean willReverse = false;


		//set pickup-position
		if(elevator.getStatus().floor == p.getOrigin()) {
			pickUpPosition = 0;
		} else {
			for(int i = 0; i < elevator.getQueue().size(); i++) {
				preCall = callDest;
				if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
					callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
				} else {
					callDest = elevator.getQueue().get(i).getPassenger().getDestination();
				}
				if(passDir == 1) {
					if(preCall > callDest) {
						//Reverse, add to reverse position
						willReverse = true;
						pickUpPosition = i;
						break;
					} else if(callDest >= p.getOrigin()) {
						pickUpPosition = i;
						break;
					}
				} else {
					if(preCall < callDest && preCall != -1) {
						//Reverse, add to reverse position
						willReverse = true;
						pickUpPosition = i;
						break;
					} else if(callDest <= p.getOrigin()) {
						pickUpPosition = i;
						break;
					}
				}
			}

			//Is all calls in the queue done prior to passengers floor without a reverse?
			if(pickUpPosition == -1) {
				pickUpPosition = elevator.getQueue().size(); 
			}

		}

		//set drop-position
		if(willReverse) {
		dropPosition = pickUpPosition + 1;
		} else {
			for(int i = 0; i < elevator.getQueue().size(); i++) {
				if(i > pickUpPosition) {
					preCall = callDest;
					if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
					callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
					} else {
					callDest = elevator.getQueue().get(i).getPassenger().getDestination();
					}
					if(passDir == 1) {
						if(preCall > callDest) {
							//Reverse, add to reverse position
							dropPosition = i;
							break;
						} else if(callDest >= p.getDestination()) {
							dropPosition = i;
							break;
						}
					} else {
						if(preCall < callDest && preCall != -1) {
							//Reverse, add to reverse position
							dropPosition = i;
							break;
						} else if(callDest <= p.getDestination()) {
							dropPosition = i;
							break;
						}
					}
			}

			//Is all calls in the queue done prior to passengers destination without a reverse?
			if(dropPosition == -1) {
				dropPosition = elevator.getQueue().size(); 
				if(dropPosition == pickUpPosition) {
					dropPosition += 1;
				}
			}
			}
		}
		if(pickUpPosition == -1 ||dropPosition == -1) {
			System.out.println("ERROR IN SAME, FIX");
			System.exit(0);
		}
		elevator.addToQueue(p, pickUpPosition, dropPosition, pos);
		return elevator;
	}

public ElevatorInterface pickUpOnReverse(ElevatorInterface elevator, Passenger p, int passDir, CarPosition pos) {
	int pickUpPosition = -1;
	int dropPosition = -1;

	int callDest = -1;
	int preCall = -1;
	int reverseFloor = -1;
	int reversePoint = -1;


	//Find out where the elevator will or can reverse
	for(int i = 0; i < elevator.getQueue().size(); i++) {
		preCall = callDest;
		if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
			callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
		} else {
			callDest = elevator.getQueue().get(i).getPassenger().getDestination();
		}

		if(elevator.getStatus().direction == 1) {
			if(preCall > callDest) {
				reverseFloor = callDest;
				reversePoint = i;
				break;
			}
		} else {
			if(preCall < callDest && preCall != -1) {
				reverseFloor = callDest;
				reversePoint = i;
				break;
			}
		}
	}



	//Okay, so there will be one reverse at least

	//Are we having one more reverse if the elevator is going the same direction as the passengers travel? (Has passed passenger floor)
	if(passDir == elevator.getStatus().direction && reversePoint != -1) {
		int temp = reversePoint;
		reversePoint = -1;
		preCall = -1;
		callDest = -1;
		for(int i = temp; i < elevator.getQueue().size(); i++) {
			preCall = callDest;
		if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
			callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
		} else {
			callDest = elevator.getQueue().get(i).getPassenger().getDestination();
		}

		if(elevator.getStatus().direction == 1) {
			if(preCall > callDest) {
				reverseFloor = callDest;
				reversePoint = i;
				break;
			}
		} else {
			if(preCall < callDest && preCall != -1) {
				reverseFloor = callDest;
				reversePoint = i;
				break;
			}
		}

		}
	}

		if(reversePoint == -1) {
			//No reverse, just add to end 
			pickUpPosition = elevator.getQueue().size();
			dropPosition = pickUpPosition +1;
			elevator.addToQueue(p, pickUpPosition, dropPosition, pos);
			return elevator;
		}

	//Elevator is going in opposite direction of passengers travel after reverse

		//Assign pickup
		if(passDir == 1) {
			if(reverseFloor > p.getOrigin()) {
				pickUpPosition = reversePoint;
			}
		} else {
			if(reverseFloor < p.getOrigin()) {
				pickUpPosition = reversePoint;
			}
		}
		boolean willReverse = false;
		if(pickUpPosition == -1) {
			callDest = -1;
			//find pickUp position
			if(reverseFloor == p.getOrigin()) {
				pickUpPosition = 0;
			} else {
				for(int i = reversePoint; i < elevator.getQueue().size(); i++) {
					preCall = callDest;
					if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
						callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
					} else {
						callDest = elevator.getQueue().get(i).getPassenger().getDestination();
					}
					if(passDir == 1) {
						if(preCall > callDest) {
							//Reverse, add to reverse position
							willReverse = true;
							pickUpPosition = i;
							break;
						} else if(callDest >= p.getOrigin()) {
							pickUpPosition = i;
							break;
						}
					} else {
						if(preCall < callDest && preCall != -1) {
							//Reverse, add to reverse position
							willReverse = true;
							pickUpPosition = i;
							break;
						} else if(callDest <= p.getOrigin()) {
							pickUpPosition = i;
							break;
						}
					}
				}
			}

			if(pickUpPosition == -1) {
				pickUpPosition = elevator.getQueue().size(); //Will stop before passenger floor
			}
		}

		//Is all calls in the queue done prior to passengers floor without a reverse?
		
		

		//set drop-position
		if(willReverse) {
			dropPosition = pickUpPosition + 1;
		} else {
			for(int i = 0; i < elevator.getQueue().size(); i++) {
				if(i > pickUpPosition) {
					preCall = callDest;
					if(elevator.getQueue().get(i).getActionType() == ElevatorAction.PICKUP) {
						callDest = elevator.getQueue().get(i).getPassenger().getOrigin();
					} else {
						callDest = elevator.getQueue().get(i).getPassenger().getDestination();
					}
					if(passDir == 1) {
						if(preCall > callDest) {
							//Reverse, add to reverse position
							dropPosition = i;
							break;
						} else if(callDest >= p.getDestination()) {
							dropPosition = i;
							break;
						}
					} else {
						if(preCall < callDest && preCall != -1) {
							//Reverse, add to reverse position
							dropPosition = i;
							break;
						} else if(callDest <= p.getDestination()) {
							dropPosition = i;
							break;
						}
					}
				}
			}
			//Is all calls in the queue done prior to passengers destination without a reverse?
			if(dropPosition == -1) {
				dropPosition = elevator.getQueue().size(); 
				if(dropPosition == pickUpPosition) {
					dropPosition += 1;
				}
			}
			
		}
		if(pickUpPosition == -1 ||dropPosition == -1) {
			System.out.println("ERROR IN REVERSE, FIX");
			System.exit(0);
		}
		elevator.addToQueue(p, pickUpPosition, dropPosition, pos);
		return elevator;
	}


}