import java.util.ArrayList;


public abstract class Algorithm {

	
	ElevatorSpecs spec;
	ArrayList<Call> traffic;
	
	public abstract ArrayList<Elevator> manageCalls(int second, ArrayList<Elevator>localElevators, ArrayList<Passenger>newCalls);
	public abstract void setTraffic(ArrayList<Call> traffic);
	public abstract ArrayList<Elevator> manageNewLocalCalls(ArrayList<Elevator> localElevators, ArrayList<Passenger> newCalls);
	public abstract ArrayList<Elevator> manageNewShuttleCalls(ArrayList<Elevator> shuttles, ArrayList<Passenger> newCalls);

}

