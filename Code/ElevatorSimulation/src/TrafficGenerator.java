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
	
	
	
	private int getRandomNumber(int low, int high)
	{
		Random r = new Random();
		int destination = r.nextInt(high-low) + low; 
		return destination;
	}
	
	public ArrayList<Call> getTraffic(TrafficType t, ArrayList<Call> oldCalls, int callAmount)
	{
		ArrayList<Call> newCalls = new ArrayList<Call>(); 
		
		switch(t)
		{
			case UPPEAK:

				for(int i = 0; i < callAmount; i++)
				{
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), 0, getRandomNumber(1, specs.getFloors() + 1));
					newCalls.add(c);
				}
				break;
			case LUNCH:
				for(int i = 0; i < callAmount; i++)
				{
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), oldCalls.get(i).getDestination(), 0); //Assuming call reached destination
					newCalls.add(c);
				}
				break;
			case DOWNPEAK:
				for(int i = 0; i < callAmount; i++)
				{
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), oldCalls.get(i).getDestination(), 0); //Assuming call reached destination
					newCalls.add(c);
				}
				break;
			case REGULAR:
				for(int i = 0; i < callAmount; i++)
				{
					int destination = getRandomNumber(1, specs.getFloors() + 1);
					while(destination != oldCalls.get(i).getDestination())
					{
						destination = getRandomNumber(1, specs.getFloors() + 1);
					}
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), oldCalls.get(i).getDestination(), destination);
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
