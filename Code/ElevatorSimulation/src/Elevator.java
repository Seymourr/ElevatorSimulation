import java.util.ArrayList;
public class Elevator {

	protected int currentFloor;
	protected int destinationFloor; //Simplified for first protoype, could be a list with sequential destinations
	protected ArrayList<Passenger> currentPassengers;
	protected int capacity;
	protected int velocity;
	
	public Elevator(int capacity, int velocity, int currentFloor)
	{
		currentPassengers = new ArrayList<Passenger>();
		this.capacity = capacity;
		this.velocity = velocity;
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
}
