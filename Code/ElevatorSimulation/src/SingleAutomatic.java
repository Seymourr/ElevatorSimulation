import java.util.ArrayList;
import java.util.Arrays;
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
				if(!Arrays.asList(elev.get(i).floors).contains(p.getOrigin())) {
					continue;
				}
				{
					if(elev.get(i).getStatus().floor == p.getOrigin())
					{
						//Closest elevator is the best to take at the moment. Check if not full!
						if(elev.get(i).getStatus().passengers != spec.getCarryCapacity())
						{
							return i;
						}
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

		for(int i = 0; i < traffic.size(); i++)
		{
			if(traffic.get(i).getCallTime() == second)
			{
				Passenger p = new Passenger(traffic.get(i), spec);
			
				if(p.getDestination() == spec.getSkylobbyfloor() || p.getDestination() == spec.getLobbyFloor())
				{
					//Find best shuttle
					int shuttleIndex = getBestShuttle(shuttles, p);
					shuttles = assignToElevator(shuttleIndex, p, shuttles, CarPosition.NULL); //Modify to support dd
				
				} 
			}
		}
	
		
		ArrayList<Call> temp = new ArrayList<Call>();
		
		for(Call c : traffic)
		{
			if(c.getCallTime() > second)
			{
				temp.add(c);
			}
		}
		traffic = temp;

		//Now handling newCalls
		return manageNewShuttleCalls(shuttles, newCalls);
	}

	private boolean checker(int[] temp, int origin)
	{
		boolean exists = false;
		for(int i = 0; i < temp.length; i++){
			if(temp[i] == origin)
			{
				exists = true;
				break;
			}
		}
		return exists;
	}
	//Eventually used, should be quite lame
	public ArrayList<Elevator> manageCalls(int second, ArrayList<Elevator> localElevators, ArrayList<Passenger>newCalls)
	{
		for(int i = 0; i < traffic.size(); i++)
		{
			if(traffic.get(i).getCallTime() == second && checker(localElevators.get(0).floors, traffic.get(i).getOriginFloor()))
			{
				//Time to create a passenger
				Passenger p = new Passenger(traffic.get(i), spec);
			
			//	System.out.println(p.getOrigin() + " " + p.getDestination());
				if(p.getDestination() != spec.getSkylobbyfloor() && p.getDestination() != spec.getLobbyFloor())
				{
				
					//Find best shuttle
					int localIndex = getSomeElevator(localElevators);
					localElevators = assignToElevator(localIndex, p, localElevators, CarPosition.NULL); //Mod to support dd

				}
			}
		}
		
		ArrayList<Call> temp = new ArrayList<Call>();
		
		for(Call c : traffic)
		{
			if(c.getCallTime() > second)
			{
				temp.add(c);
			}
		}
		traffic = temp;


		return manageNewLocalCalls(localElevators, newCalls);
	}
	
	public ArrayList<Elevator> manageNewLocalCalls(ArrayList<Elevator> localElevators, ArrayList<Passenger> newCalls)
	{
		for(int i = 0; i < newCalls.size(); i++)
		{
		//	System.out.println(newCalls.get(i).getDestination());
			if(newCalls.get(i).getDestination() != spec.getSkylobbyfloor() && newCalls.get(i).getDestination() != spec.getLobbyFloor() && checker(localElevators.get(0).floors, newCalls.get(i).getOrigin()))
			{
				System.out.println(newCalls.get(i).getDestination());
				int localIndex = getSomeElevator(localElevators); 
				localElevators = assignToElevator(localIndex, newCalls.get(i), localElevators, CarPosition.NULL); //Mod to support dd
			}
		}
		return localElevators;	
	}
	
	public ArrayList<Elevator> manageNewShuttleCalls(ArrayList<Elevator> shuttles, ArrayList<Passenger> newCalls)
	{
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
	
	public void setTraffic(ArrayList<Call> traffic)
	{
		this.traffic = traffic;
	}
}
