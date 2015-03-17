import java.util.ArrayList;
import java.util.Random;


public class SingleAutomatic extends Algorithm{
	
	public SingleAutomatic(ElevatorSpecs spec)
	{
		this.spec = spec;
		
	}
	
	private int getSomeElevator(ArrayList<Elevator> elev)
	{
		Random r = new Random();
		int number = r.nextInt(elev.size()); 
		return number;
	}
	
	
	//The best shuttle is the one at the same floor. If none are at the same floor, take the one in the list that has fewest allocations
	private int getBestShuttle(ArrayList<Elevator> elev, Passenger p)
	{
		int lowestQueue = elev.get(0).getQueue().size();
		int index = 0;
			for(int i = 0; i < elev.size(); i++) 
			{
				if(elev.get(i).getStatus().floor == p.getOrigin())
				{
					//Closest elevator is the best to take at the moment. Check if not full!
					if(elev.get(i).getStatus().passengers != spec.getCarryCapacity())
					{
						return i;
					}
				}
				if(elev.get(i).getQueue().size() < lowestQueue)
					{
						lowestQueue = elev.get(i).getQueue().size();
						index = i;
				}
			}
			return index;
	}

	private ArrayList<Elevator> assignToElevator(int index, Passenger p, ArrayList<Elevator> elevatorList, CarPosition pos)
	{
		int positionPickup = elevatorList.get(index).getQueue().size();
		if(elevatorList.get(index).getStatus().floor == p.getOrigin() && elevatorList.get(index).getStatus().direction == 0)
		{
			positionPickup = 0; //Assigned elevator is present, board it immediatley.
		}
		//Now have best available shuttle.. //TODO: Check if double decked, passenger need to know top or down in that case
		elevatorList.get(index).addToQueue(p, positionPickup, elevatorList.get(index).getQueue().size() + 1, pos); //True for single automatic
		return elevatorList;
	}
	
	
	public ArrayList<Elevator> manageShuttleCalls(int second, ArrayList<Elevator> shuttles, ArrayList<Passenger> newCalls)
	{
		int counter = 0;
		while(!traffic.isEmpty() && traffic.get(counter).getCallTime() == second)
		{
			//Time to create a passenger
			Passenger p = new Passenger(traffic.get(counter), spec);
			if(p.getDestination() == spec.getSkylobbyfloor() || p.getDestination() == spec.getLobbyFloor())
			{
				//Find best shuttle
				int shuttleIndex = getBestShuttle(shuttles, p);
				shuttles = assignToElevator(shuttleIndex, p, shuttles, CarPosition.NULL); //Modify to support dd
				traffic.remove(counter);
			}
			counter += 1;
		}

		//Now handling newCalls
		for(int i = 0; i < newCalls.size(); i++)
		{
			if(newCalls.get(i).getDestination() == spec.getSkylobbyfloor() || newCalls.get(i).getDestination() == spec.getLobbyFloor())
			{
				int shuttleIndex = getBestShuttle(shuttles, newCalls.get(i));
				shuttles = assignToElevator(shuttleIndex, newCalls.get(i), shuttles, CarPosition.NULL); //Modify to support dd
			}
		}
		return shuttles;
	}

	//Eventually used, should be quite lame
	public ArrayList<Elevator> manageCalls(int second, ArrayList<Elevator> localElevators, ArrayList<Passenger>newCalls)
	{
		int counter = 0;
		while(!traffic.isEmpty() && traffic.get(counter).getCallTime() == second)
		{
			//Time to create a passenger
			Passenger p = new Passenger(traffic.get(counter), spec);
			if(p.getDestination() != spec.getSkylobbyfloor() && p.getDestination() != spec.getLobbyFloor())
			{
				//Find best shuttle
				int localIndex = getSomeElevator(localElevators);
				localElevators = assignToElevator(localIndex, p, localElevators, CarPosition.NULL); //Mod to support dd
				traffic.remove(counter);
			}
			counter += 1;
		}

		//Now handling newCalls
		for(int i = 0; i < newCalls.size(); i++)
		{
			if(newCalls.get(i).getDestination() != spec.getSkylobbyfloor() && newCalls.get(i).getDestination() != spec.getLobbyFloor())
			{
				int localIndex = getSomeElevator(localElevators); 
				localElevators = assignToElevator(localIndex, newCalls.get(i), localElevators, CarPosition.NULL); //Mod to support dd
			}
		}
		return localElevators;
	}
	
	public void setTraffic(ArrayList<Call> traffic)
	{
		this.traffic = traffic;
	}
}
