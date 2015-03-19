import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public abstract class Algorithm {

	
	ElevatorSpecs specs;	
	public abstract ArrayList<ArrayList<Elevator>> manageCalls(ArrayList<ArrayList<Elevator>> elevators, LinkedList<Passenger> calls);

	/**
	* Attempts to pick an optimal shuttle for the given passenger. The optimal shuttle is the idle one at 
	* the same floor as the passenger. If no optimal shuttle is found, the passenger is assinged to a random
	* shuttle.
	*/
	public ArrayList<Elevator> assignShuttleElevator(ArrayList<Elevator> elevators, Passenger p, CarPosition pos) {
		int chosenElevator = -1;
		for(int i = 0; i < elevators.size(); i++) {
			if(elevators.get(i).getStatus().direction == 0 && elevators.get(i).getStatus().passengers < specs.getCarryCapacity()) {
				if(elevators.get(i).getStatus().floor == p.getOrigin()) {
					//Optimal elevator found
					chosenElevator = i;
				}
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
}

