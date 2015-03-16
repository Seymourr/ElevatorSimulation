
import java.util.ArrayList;
import java.util.Random;

/**
 * Class responsible for creating the passenger traffic the will be used in the simulation.
 * The traffic is represented by objects of the Call class. Thus this class creates call 
 * objects depending on what type of simulation is to be done. The specs in the constructor
 * are the parameters that specify the elevator specs. The getTraffic method can then be 
 * used on this object to generate traffic for a specified simulation type.
 */
public class TrafficGenerator {
	/* Fields */
	private ElevatorSpecs specs;
	
	/* Constructor */
	public TrafficGenerator(ElevatorSpecs specs) {
		this.specs = specs;
	}
	
	//Call amount is one of the three traffic weights(1000, 5000, 10000) or a random number (5 % or so so
	
	/*
	 * Returns a random number between low (inclusive) and high (exclusive)
	 */
	private int getRandomNumber(int low, int high) {
		Random r = new Random();
		int number = r.nextInt(high-low) + low; 
		return number;
	}
	
	/*
	 * Returns a random floor between low(inclusive) and high(exclusive). 
	 * The skylobby floor is not an allowed floor to be returned.
	 */
	private int getRandomFloor(int low, int high) {
		Random r = new Random();
		int number = r.nextInt(high-low) + low;
		while(number == specs.getSkylobbyfloor()) {
			number = r.nextInt(high-low) + low;
		}
		return number;
	}
	
	/**
	 * Orders the call objects in the given array from lowest call time to highest.
	 */
	private ArrayList<Call> orderCalls(ArrayList<Call> traffic) {
		for (int i = 1; i < traffic.size(); i++) {
			Call temp = traffic.get(i);
			int j;
			for (j = i - 1; j >= 0 && temp.getCallTime() < traffic.get(j).getCallTime(); j--) {
				traffic.set(j + 1, traffic.get(j));
			}
			traffic.set(j + 1, temp);
		}
		return traffic;
	}
	
	/**
	 * Generates traffic according to the specified TrafficType t.
	 * The amount of traffic to be generated is specified with the callAmount parameter.
	 * Traffic will be generated assuming 0 is the lobby floor. This means a building
	 * with 10 floors would be numbered 0 through 9.
	 * @return An ArrayList of Call objects, representing the traffic requested.
	 */
	public ArrayList<Call> getTraffic(TrafficType t, int callAmount) {
		ArrayList<Call> newCalls = new ArrayList<Call>(callAmount); //List of calls representing the traffic
		
		switch(t) {
			case UPPEAK:
				//Creates calls from the lobby floor (0) to random valid destination floor
				for(int i = 0; i < callAmount; i++) {
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), 0, getRandomFloor(1, specs.getFloors()));
					newCalls.add(c);
				}
				break;
			case LUNCH:
				//Creates calls from random origin floors to the lobby...
				//...and call from the lobby floor to the same floor 30 minutes later
				for(int i = 0; i < callAmount; i++) {
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime() - (30 * 60)), getRandomFloor(1, specs.getFloors()), 0);
					newCalls.add(c);
					
					Call c2 = new Call(c.getCallTime() + (30 * 60), 0, c.getOriginFloor());
					newCalls.add(c2);
				}
				break;
			case DOWNPEAK:
				//Creates calls from random origin floors to the lobby
				for(int i = 0; i < callAmount; i++) {
					Call c = new Call(getRandomNumber(0, specs.getPeriodTime()), getRandomFloor(1, specs.getFloors()), 0);
					newCalls.add(c);
				}
				break;
			case REGULAR:
				//Creates call from random origin floors to random destination floors
				for(int i = 0; i < callAmount; i++) {
					int destination = getRandomFloor(0, specs.getFloors());
					int origin = getRandomFloor(0, specs.getFloors());
					while(destination == origin) {
						destination = getRandomFloor(1, specs.getFloors());
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
		return orderCalls(newCalls);
	}
}
