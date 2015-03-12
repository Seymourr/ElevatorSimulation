import java.util.ArrayList;
public class Elevator {

	protected int currentFloor;
	protected int destinationFloor; //Simplified for first protoype, could be a list with sequential destinations
	protected ArrayList<Passenger> currentPassengers;
	protected ArrayList<Call> currentCalls;
	
	public Elevator(int currentFloor)
	{
		currentPassengers = new ArrayList<Passenger>();
		currentCalls = new ArrayList<Call>();
		this.currentFloor = currentFloor;
	}
	
	public void setFloor(int floor)
	{
		currentFloor = floor;
	}
	
	//Ev remove
	public void setDestination(int floor)
	{
		destinationFloor = floor;
	}
	
	public void addPassenger(Passenger p)
	{
		currentPassengers.add(p);
	}
	
	public int getFloor()
	{
		return currentFloor;
	}
	
	//Ev remove
	public int getDestination()
	{
		return destinationFloor;
	}
	
	public void addCall(Call c)
	{
		currentCalls.add(c);
	}
	
	public boolean removeCall(Call c) //Why not just remove first call in sequence?
	{
		boolean removed = false;
		for(int i = 0; i < currentCalls.size(); i++)
		{
			if(currentCalls.get(i) == c)
			{
				currentCalls.remove(i);
				removed = true;
				break;
			}
		}
		return removed;
	}
}
