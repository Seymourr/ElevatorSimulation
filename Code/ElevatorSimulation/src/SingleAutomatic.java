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
 	public ArrayList<ArrayList<Elevator>> manageCalls(ArrayList<ArrayList<Elevator>> elevators, LinkedList<Passenger> calls)
 	{
 		while(!calls.isEmpty()) {
 			Passenger p = calls.removeFirst();
 			if(containsFloor(elevators.get(0).get(0).floors, p.getOrigin(), p.getDestination())) {
 				//Bot ride
 				int elevatorIndex = getRandomElevator(elevators.get(0));
 				elevators.get(0).get(elevatorIndex).addToQueue(p, elevators.get(0).get(elevatorIndex).getQueue().size(), elevators.get(0).get(elevatorIndex).getQueue().size() + 1, CarPosition.NULL);
 			} else if (containsFloor(elevators.get(1).get(0).floors, p.getOrigin(), p.getDestination())) {
 				//Shuttle ride
 				ArrayList<Elevator> temp = assignShuttleElevator(elevators.get(1), p, CarPosition.NULL);
 				elevators.set(1, temp);
 			} else if (containsFloor(elevators.get(2).get(0).floors, p.getOrigin(), p.getDestination())) {
 				//Top ride
 				int elevatorIndex = getRandomElevator(elevators.get(2));
 				elevators.get(2).get(elevatorIndex).addToQueue(p, elevators.get(2).get(elevatorIndex).getQueue().size(), elevators.get(2).get(elevatorIndex).getQueue().size() + 1, CarPosition.NULL);
 			}
 		}

 		return elevators;
 	}
	

 	/**
 	* Returns a random index of elevators.
 	*/
	private int getRandomElevator(ArrayList<Elevator> elevator) {
		Random r = new Random();
 		int number = r.nextInt(elevator.size()); 
 		return number;
	}

	 /**
 	 * Checks that a given origin and destination is within the range of given floors. 
	 * @param temp
 	 * @param origin
 	 * @param destination
 	 * @return True, if within range, false otherwise
 	 */
 	private boolean containsFloor(int[] temp, int origin, int destination) {
 		boolean inOrigin = false;
 		boolean goingToDestination = false;
		for(int i = 0; i < temp.length; i++){
 			if(temp[i] == origin){
				inOrigin = true;
				break;
 			}
 		}
		
		for(int i = 0; i < temp.length; i++){
			if(temp[i] == destination){
 				goingToDestination = true;
				break;
			}
		}
		return (inOrigin && goingToDestination);
	}
}
