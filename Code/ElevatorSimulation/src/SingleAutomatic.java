import java.util.ArrayList;


public class SingleAutomatic extends Algorithm{
	
	public SingleAutomatic(ElevatorSpecs spec)
	{
		this.spec = spec;
		
	}
	
	//Best elevator is either the idle one, or the one on the same floor. No other exceptions.
	private int getBestElevator(ArrayList<Elevator> elev)
	{
		int lowestQueue = elev.get(0).getQueue().size();
		Elevator temp = elev.get(0);
		int index = 0;
			for(int i = 0; i < elev.size(); i++) 
			{
				if(elev.get(i).getQueue().size() < lowestQueue)
					{
						lowestQueue = elev.get(i).getQueue().size();
						temp = elev.get(i);
						index = i;
				}
			}
			return index;
	}
	
	
	//The best shuttle is the one at the same floor. If none are at the same floor, take the next one in the list that has fewest allocations
	private int getBestShuttle(ArrayList<Elevator> elev)
	{
		int lowestQueue = elev.get(0).getQueue().size();
		Elevator temp = elev.get(0); //EV REMOVE
		int index = 0;
			for(int i = 0; i < elev.size(); i++) 
			{
				if(elev.get(i).getQueue().size() < lowestQueue)
					{
						lowestQueue = elev.get(i).getQueue().size();
						temp = elev.get(i);
						index = i;
				}
			}
			return index;
	}
//TODO, Imply algorithm correctly (if not on floor, dont do shit)
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
				int shuttleIndex = getBestShuttle(shuttles);

				//Now have best available shuttle.. //TODO: Check if double decked, passenger need to know top or down in that case
				shuttles.get(shuttleIndex).addToQueue(p, p.getOrigin(), p.getDestination(), null);
				traffic.remove(counter);
			}
			counter += 1;
		}

		//Now handling newCalls
		for(int i = 0; i < newCalls.size(); i++)
		{
			if(newCalls.get(i).getDestination() == spec.getSkylobbyfloor() || newCalls.get(i).getDestination() == spec.getLobbyFloor())
			{
				int shuttleIndex = getBestShuttle(shuttles);
				shuttles.get(shuttleIndex).addToQueue(newCalls.get(i), newCalls.get(i).getOrigin(), newCalls.get(i).getDestination(), null); //TODO: SEE ABOVE
			}
		}
		return shuttles;
	}


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
				int localIndex = getBestElevator(localElevators);

				//Now have best available shuttle.. //TODO: Check if double decked, passenger need to know top or down in that case
				localElevators.get(localIndex).addToQueue(p, p.getOrigin(), p.getDestination(), null);
				traffic.remove(counter);
			}
			counter += 1;
		}

		//Now handling newCalls
		for(int i = 0; i < newCalls.size(); i++)
		{
			if(newCalls.get(i).getDestination() == spec.getSkylobbyfloor() || newCalls.get(i).getDestination() == spec.getLobbyFloor())
			{
				int localIndex = getBestElevator(localElevators);
				localElevators.get(localIndex).addToQueue(newCalls.get(i), newCalls.get(i).getOrigin(), newCalls.get(i).getDestination(), null); //TODO: SEE ABOVE
			}
		}
		return localElevators;
	}
	
	public void setTraffic(ArrayList<Call> traffic)
	{
		this.traffic = traffic;
	}
}
