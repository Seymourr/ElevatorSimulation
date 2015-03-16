import java.util.ArrayList;


public abstract class Algorithm {

	
	ElevatorSpecs spec;
	
	public abstract ArrayList<Elevator> manageCalls(ArrayList<Call> traffic, ArrayList<Elevator>localElevators, ArrayList<Elevator>shuttleElevators, ArrayList<Passenger>newCalls);
}
