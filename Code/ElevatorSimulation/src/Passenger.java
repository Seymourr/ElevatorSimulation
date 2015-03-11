
public class Passenger {
	
	private int currentFloor;
	private int previousFloor;
	private int destinationFloor;
	private double waitTime;
	private double travelingTime;
	
	public Passenger()
	{
		//?
	}
	
	public void setFloor(int floor)
	{
		currentFloor = floor;
	}
	
	public void setDestination(int floor)
	{
		destinationFloor = floor;
	}
	
	public void setPreviousFloor(int floor)
	{
		previousFloor = floor;
	}
	
	public void setWaitTime(double newTime)
	{
		waitTime = newTime;
	}
	
	public void setTravelTime(double newTime)
	{
		travelingTime = newTime;
	}
	public int getFloor()
	{
		return currentFloor;
	}
	
	public int getDestination()
	{
		return destinationFloor;
	}
	
	public int getPreviousFloor()
	{
		return previousFloor;
	}
	
	
	public double getWaitTime()
	{
		return waitTime;
	}
	
	public double getTravelTime()
	{
		return travelingTime;
	}
}
