
public class Call {

	private int callTime;
	private int originFloor;
	private int destination;
	private boolean hasBeenServed;
	
	public Call(int callTime, int originFloor, int destination) 
	{
		this.callTime = callTime;
		this.originFloor = originFloor;
		this.destination = destination;
	}
	
	public int getCallTime()
	{
		return callTime;
	}
	
	public int getOriginFloor()
	{
		return originFloor;
	}
	
	public int getDestination()
	{
		return destination;
	}
	
	public void flipHasBeenServed()
	{
		hasBeenServed = !hasBeenServed;
	}
	
	public boolean hasBeenServed()
	{
		return hasBeenServed;
	}
}
