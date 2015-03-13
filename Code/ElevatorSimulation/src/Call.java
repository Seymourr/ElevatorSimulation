
/**
 * An object of this class represent the a passenger call in the simulation.
 * One call represents one passenger with the parameters:
 *	- callTime: When the call is made
 *	- originFloor: The floor from where the call is made
 * 	- destination: The destination floor for this passenger
 */
public class Call {
	/* Fields */
	private int callTime;
	private int originFloor;
	private int destination;
	//private boolean hasBeenServed;
	
	/* Creates a call object with the parameters according to the input */
	public Call(int callTime, int originFloor, int destination) {
		this.callTime = callTime;
		this.originFloor = originFloor;
		this.destination = destination;
	}
	
	public int getCallTime() {
		return callTime;
	}
	
	public int getOriginFloor() {
		return originFloor;
	}
	
	public int getDestination() {
		return destination;
	}
	
	/*
	public void flipHasBeenServed()
	{
		hasBeenServed = !hasBeenServed;
	}
	
	public boolean hasBeenServed()
	{
		return hasBeenServed;
	}
	*/
}
