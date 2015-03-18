import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class SingleAutomatic extends Algorithm{
	
	public ArrayList<Elevator> manageCalls(int second, ArrayList<Elevator> localElevators, ArrayList<Passenger>newCalls) {
		for(int i = 0; i < traffic.size(); i++){
			if(traffic.get(i).getCallTime() == second) {
				Passenger p = new Passenger(traffic.get(i), spec);
				if(checker(localElevators.get(0).floors, p.getOrigin(), p.getDestination())){
					int localIndex = getSomeElevator(localElevators);
					localElevators = assignToElevator(localIndex, p, localElevators, CarPosition.NULL); //Edit to support DD
					System.out.println("The passenger from " + traffic.get(i).getOriginFloor() + " to " + traffic.get(i).getDestination() + " was assigned");
				}
			} else {
				break; 
			}
		}
		ArrayList<Call> temp = new ArrayList<Call>();
		for(Call c : traffic){
			if(c.getCallTime() > second){
				temp.add(c);
			}
		}
		traffic = temp;
		
		return manageNewLocalCalls(localElevators, newCalls);
	}
	
	public ArrayList<Elevator> manageShuttleCalls(int second, ArrayList<Elevator> shuttles, ArrayList<Passenger> newCalls){
		for(int i = 0; i < traffic.size(); i++){
			if(traffic.get(i).getCallTime() == second){
				Passenger p = new Passenger(traffic.get(i), spec);
				if(checker(shuttles.get(0).floors, p.getOrigin(), p.getDestination())){
					int shuttleIndex = getBestShuttle(shuttles, p);
					shuttles = assignToElevator(shuttleIndex, p, shuttles, CarPosition.NULL); //Modify to support dd	
					System.out.println("The passenger from " + traffic.get(i).getOriginFloor() + " to " + traffic.get(i).getDestination() + " was assigned to a shuttle");
				} 
			}
		}
		ArrayList<Call> temp = new ArrayList<Call>();
		for(Call c : traffic){
			if(c.getCallTime() > second){
				temp.add(c);
			}
		}
		traffic = temp;

		return manageNewShuttleCalls(shuttles, newCalls);
	}

	public SingleAutomatic(ElevatorSpecs spec) {
		this.spec = spec;
	}
	
	private int getSomeElevator(ArrayList<Elevator> elev){
		Random r = new Random();
		int number = r.nextInt(elev.size()); 
		return number;
	}
	
	//The best shuttle is the one at the same floor. If none are at the same floor, take the one in the list that has fewest allocations
	private int getBestShuttle(ArrayList<Elevator> elev, Passenger p){
		for(int i = 0; i < elev.size(); i++){
			if(elev.get(i).getStatus().floor == p.getOrigin()){
				if(elev.get(i).getStatus().direction == 0 && elev.get(i).getStatus().passengers < spec.getCarryCapacity()) {
					return i;
				}
			}
		}
		
		//An optimal elevator was not found.. pick a random one
		return getSomeElevator(elev);
	}

	private ArrayList<Elevator> assignToElevator(int index, Passenger p, ArrayList<Elevator> elevatorList, CarPosition pos){
		int positionPickup = elevatorList.get(index).getQueue().size();
		if(elevatorList.get(index).getStatus().floor == p.getOrigin() && elevatorList.get(index).getStatus().direction == 0){
			positionPickup = 0; //Assigned elevator is present, board it immediatley.
		}
		//Now have best available shuttle.. //TODO: Check if double decked, passenger need to know top or down in that case
		elevatorList.get(index).addToQueue(p, positionPickup, elevatorList.get(index).getQueue().size() + 1, pos); //True for single automatic

		return elevatorList;
	}
		
	
	private boolean checker(int[] temp, int origin, int destination){
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
	
	
	public ArrayList<Elevator> manageNewLocalCalls(ArrayList<Elevator> localElevators, ArrayList<Passenger> newCalls){
		for(int i = 0; i < newCalls.size(); i++){
			if(checker(localElevators.get(0).floors, newCalls.get(i).getOrigin(), newCalls.get(i).getDestination())){
				int localIndex = getSomeElevator(localElevators);
				localElevators = assignToElevator(localIndex, newCalls.get(i), localElevators, CarPosition.NULL);
				System.out.println("The newcall passenger from " + newCalls.get(i).getOrigin() + " to " +  newCalls.get(i).getDestination()  + " was assigned");
			}
		}
		return localElevators;	
	}
	
	public ArrayList<Elevator> manageNewShuttleCalls(ArrayList<Elevator> shuttles, ArrayList<Passenger> newCalls){
		for(int i = 0; i < newCalls.size(); i++){
			if(checker(shuttles.get(0).floors, newCalls.get(i).getOrigin(), newCalls.get(i).getDestination())){
				int shuttleIndex = getBestShuttle(shuttles, newCalls.get(i));
				shuttles = assignToElevator(shuttleIndex, newCalls.get(i), shuttles, CarPosition.NULL); //Modify to support dd
				System.out.println("The newcall shuttle passenger from " + newCalls.get(i).getOrigin() + " to " +  newCalls.get(i).getDestination()  + " was assigned");
			}
		}
		return shuttles;
	}
	
	public void setTraffic(ArrayList<Call> traffic){
		this.traffic = traffic;
	}
}
