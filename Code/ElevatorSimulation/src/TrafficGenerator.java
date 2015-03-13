import java.util.ArrayList;
import java.util.Random;
public class TrafficGenerator {
	
	private ElevatorSpecs specs;
	public TrafficGenerator(ElevatorSpecs specs)
	{
		this.specs = specs;
	}
	
	//Is skylobby a legit destination?
	//Call amount is one of the three traffic weights(1000, 5000, 10000) or a random number (5 % or so so)
	
	//TODO: Oldcalls should be removed, skylobby should not be legit destination floor. Random calls.
	//UPDATE: TODO is done.
	
	
	/*
	 * Returns a random number between low (inclusive) and high (exclusive)
	 */
	private int getRandomNumber(int low, int high)
	{
		Random r = new Random();
		int number = r.nextInt(high-low) + low; 
		return number;
	}
	
	/*
	 * Returns a random floor between low(inclusive) and high(exclusive. 
	 * The skylobby floor is not an allowed floor to be returned.
	 */
	private int getRandomFloor(int low, int high)
	{
		Random r = new Random();
		int number = r.nextInt(high-low) + low;
		while(number != specs.getSkylobbyfloor()){
			number = r.nextInt(high-low) + low;
		}
		return number;
	}
	
	public ArrayList<Call> getTraffic(TrafficType t, int callAmount)
	{
		ArrayList<Call> newCalls = new ArrayList<Call>(); 
		
		switch(t)
		{
			case UPPEAK:

				for(int i = 0; i < callAmount; i++)
				{
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), 0, getRandomFloor(1, specs.getFloors() + 1));
					newCalls.add(c);
				}
				break;
			case LUNCH:
				for(int i = 0; i < callAmount; i++)
				{		
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), getRandomFloor(1, specs.getFloors() + 1), 0); //Assuming call reached destination
					newCalls.add(c);
					Call c2 = new Call(c.getCallTime() + 30, 0, c.getOriginFloor()); //30 minutes lunch break assumed, call afterwards
					newCalls.add(c2);
				}
				break;
			case DOWNPEAK:
				for(int i = 0; i < callAmount; i++)
				{
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), getRandomFloor(1, specs.getFloors() + 1), 0);
					newCalls.add(c);
				}
				break;
			case REGULAR:
				for(int i = 0; i < callAmount; i++)
				{
					int destination = getRandomFloor(1, specs.getFloors() + 1);
					int origin = getRandomFloor(1, specs.getFloors() + 1);
					while(destination != origin)
					{
						destination = getRandomFloor(1, specs.getFloors() + 1);
					}
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), origin, destination);
					newCalls.add(c);
				}
				break;
			default: 
				System.out.println("ERROR IN GET TRAFFIC, ABORTING SIMULATION");
				System.exit(0);
				break;
		}
		
		return newCalls;
	}
}
