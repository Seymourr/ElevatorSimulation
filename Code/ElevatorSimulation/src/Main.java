
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 */
public class Main {
	/* Fields */
	private static ArrayList<Passenger> passengers; 
	private static ArrayList<Call> calls;
	private static ArrayList<Elevator> localElevatorsBottom;
	private static ArrayList<Elevator> localElevatorsTop;
	private static ArrayList<Elevator> shuttleElevators;
	private static TrafficGenerator trafficGen;
	private static ElevatorSpecs specs;
	private static int travellingTime;
	private static int waitingTime;
	private static SingleAutomatic algForShuttle;
	
	/**
	 * Read all specifications for this simulation from file, and returns an object carrying all these
	 * in a proper manor. 
	 * NOTE: The specifications written in the file appear in a certain order, and this order is assumed to be the same
	 * when creating an object of ElevatorSpecs. In other words, if the order in the file is changed, or if
	 * stuff gets removed /added, one would have to change in elevatorSpecs as well in order to not get unexpected
	 * results.
	 * @return ElevatorSpecs
	 * @throws FileNotFoundException
	 */
	private static ElevatorSpecs getSpecs() throws FileNotFoundException {
		ArrayList<String> temp = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("Src/ElevatorFiles/specs.txt")); //src m�ste vara med i eclipse
		try {
			String line = br.readLine();
			while(line != null) {
				line = line.split(" ")[0];
				temp.add(line);
				line = br.readLine();
			}
		} catch (Exception e1) {	
			e1.printStackTrace();
		}
		ElevatorSpecs es = new ElevatorSpecs(temp);
		return es;
	}
	
	/**
	 * Method to test the traffic generator
	 */
	private static void testTrafficGen() {
		ArrayList<Call> traffic = trafficGen.getTraffic(TrafficType.LUNCH, 50);
		for (int i = 0; i < traffic.size(); i++) {
			Call tempcall = traffic.get(i);
			System.out.print("Calltime: " + tempcall.getCallTime());
			System.out.print(" | Origin floor: " + tempcall.getOriginFloor());
			System.out.print(" | Destination floor: " + tempcall.getDestination());
			System.out.println();
		}
	}
	
	
	/**
	 * Method to see the values of a specific, pre-generated traffic.
	 */
	private static void testTraffic(ArrayList<Call> traffic){
		for (int i = 0; i < traffic.size(); i++) {
			Call tempcall = traffic.get(i);
			System.out.print("Calltime: " + tempcall.getCallTime());
			System.out.print(" | Origin floor: " + tempcall.getOriginFloor());
			System.out.print(" | Destination floor: " + tempcall.getDestination());
			System.out.println();
	}
	}
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		//Fetch elevator specifications from file
		calls = new ArrayList<Call>();
		try {
			specs = getSpecs();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		algForShuttle = new SingleAutomatic(specs);
		//Create a traffic generator according to the specifications
		trafficGen = new TrafficGenerator(specs);
		
		//Test the traffic generator
	//	testTrafficGen();
		
	
			createElevators();
			simulateDay(new SingleAutomatic(specs), 4); 

		
	}

	/**
	 * Create the elevators used in this simulation. 
	 * Floor parameters from the specs are used here.
	 */
	private static void createElevators()
	{
		localElevatorsBottom = new ArrayList<Elevator>();
		localElevatorsTop = new ArrayList<Elevator>();
		shuttleElevators = new ArrayList<Elevator>();
		int numberOfShuttles = specs.getNumberOfShuttles(); //Edit with proper coding to support different implementations
		
		int[] bottomfloors = new int[specs.getSkylobbyfloor()];
		for(int i = 0; i < specs.getSkylobbyfloor(); i++)
		{
			bottomfloors[i] = i;
		}
		
		int[] shuttleFloors = new int[2];
		shuttleFloors[0] = specs.getLobbyFloor();
		shuttleFloors[1] = specs.getSkylobbyfloor();
		
		int[] topfloors = new int[(specs.getFloors() - bottomfloors.length)];
		
		for(int i = 0; i < topfloors.length; i++)
		{
			topfloors[i] = specs.getSkylobbyfloor() + i;
		}
		
		for(int i = 0; i < specs.getShafts() - numberOfShuttles;i++)
		{
			localElevatorsBottom.add(new Elevator(specs, bottomfloors, 0)); //Begin with start 0 for simplicity
			localElevatorsTop.add(new Elevator(specs, topfloors, specs.getSkylobbyfloor())); 
		}
		
		for(int i = 0; i < numberOfShuttles; i++)
		{
			shuttleElevators.add(new Elevator(specs, shuttleFloors, 0));
		}
		
	}

	/**
	 * Simulate a day of elevator activity. Consists of 5 different periods.
	 * @param alg
	 * @param trafficAmount
	 */
	public static void simulateDay(Algorithm alg, int trafficAmount)
	{
		simulatePeriod(alg, TrafficType.UPPEAK, trafficAmount);
	//	simulatePeriod(alg, TrafficType.REGULAR, trafficAmount);
	//	simulatePeriod(alg, TrafficType.LUNCH, trafficAmount);
	//	simulatePeriod(alg, TrafficType.REGULAR, trafficAmount);
	//	simulatePeriod(alg, TrafficType.DOWNPEAK, trafficAmount);
		
	//	handleRestCalls(alg); // Extra time needed to empty system
	
		//TODO: Something to manage time from this day
	}
	
	/**
	 * Further simulates remaining passengers traveling. Extra time is added to the total time.
	 */
	public static int handleRestCalls(Algorithm alg)
	{
		int secondsElapsed = 0;
		while(!systemEmpty())
		{
			updateElevatorPosition();
			ArrayList<Passenger> newCalls = updateElevatorOnOff();
			localElevatorsBottom = alg.manageNewLocalCalls(localElevatorsBottom, newCalls);
			localElevatorsTop = alg.manageNewLocalCalls(localElevatorsTop, newCalls);
			shuttleElevators = alg.manageNewShuttleCalls(shuttleElevators, newCalls);
			secondsElapsed += 1;
		}
		return secondsElapsed;
	}

	/**
	 * Checks if the system of elevators is free of calls.
	 * @return True or False
	 */
	public static boolean systemEmpty()
	{
		ArrayList<Elevator> localElevators = localElevatorsBottom;
		localElevators.addAll(localElevatorsTop);
		boolean isEmpty = true;
		for(int i = 0; i < localElevators.size(); i++)
		{
			if(localElevators.get(i).getQueue().size() > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
		}
		
		for(int i = 0; i < shuttleElevators.size(); i++)
		{
			if(shuttleElevators.get(i).getQueue().size() > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
		}
		
		return isEmpty;
	}
	
	/**
	 * Simulates a period of elevator traffic.
	 * @param alg
	 * @param t
	 * @param trafficAmount
	 */
	public static void simulatePeriod(Algorithm alg, TrafficType t, int trafficAmount)
	{
		ArrayList<Call> traffic = trafficGen.getTraffic(t, trafficAmount); //Create traffic for this period
		alg.setTraffic(traffic); //Pass the planned traffic to the algorithm
		algForShuttle.setTraffic(traffic); //Pass the planned traffic to shuttleAlgorithm too
		testTraffic(traffic); //Debugging
		
		for(int second_i = 0; second_i < specs.getPeriodTime(); second_i++)
		{
			//Update position of elevators
			updateElevatorPosition();
			
			//People get off elevators
			ArrayList<Passenger> newCalls = updateElevatorOnOff();
			
			if(newCalls.size() > 0)
			{
				System.out.println("Newcalls size: " + newCalls.size());
			}
			//Manage new calls (algorithm call)
			localElevatorsBottom = alg.manageCalls(second_i, localElevatorsBottom, newCalls); //assumes localElevators come first, then shuttles
			localElevatorsTop = alg.manageCalls(second_i, localElevatorsTop, newCalls); //assumes localElevators come first, then shuttles
			shuttleElevators = algForShuttle.manageShuttleCalls(second_i, shuttleElevators, newCalls);
			
			
			//TODO: Update waiting/travel time, calculate by looking through all elevators:
			// calculateNextTime(....); 


		}
	}
	
	/**
	 * Update the position of every elevator by calling its update function.
	 */
	private static void updateElevatorPosition()
	{
		
		for(int i = 0; i < localElevatorsBottom.size(); i++)
		{
			localElevatorsBottom.get(i).updateElevator();
			
		}
		

		for(int i = 0; i < localElevatorsTop.size(); i++)
		{
			localElevatorsTop.get(i).updateElevator();
			
		}
		
		for(int i = 0; i < shuttleElevators.size(); i++)
		{
			shuttleElevators.get(i).updateElevator();
		}
	}
	
	/**
	 * Open elevator doors and allow passengers to enter or exit. For all passengers that exit, either remove
	 * them if they have completed their travel, or return them in a list.
	 * @return An ArrayList containing people who will continue their traveling. 
	 */
	private static ArrayList<Passenger> updateElevatorOnOff()
	{
		ArrayList<Passenger> p = new ArrayList<Passenger>();
		
		for(int i = 0; i < localElevatorsBottom.size(); i++)
		{
			Passenger[] temp = localElevatorsBottom.get(i).openDoors();
			
			for(int j = 0;  j < temp.length; j++)
			{
				p.add(temp[j]);
			}
		}
		
		for(int i = 0; i < localElevatorsTop.size(); i++)
		{
			Passenger[] temp = localElevatorsTop.get(i).openDoors();
			
			for(int j = 0;  j < temp.length; j++)
			{
				p.add(temp[j]);
			}
		}
		
		for(int i = 0; i < shuttleElevators.size(); i++)
		{
			Passenger[] temp = shuttleElevators.get(i).openDoors();
			
			for(int j = 0;  j < temp.length; j++)
			{
				p.add(temp[j]);
			}
		}
		
		for(int i = 0; i < p.size(); i++)
		{
			int destination = p.get(i).nextDestination();
			if(destination == -1)
			{
				//remove passeger
				System.out.println("The following passenger finished traveling (he came from here): " + p.get(i).getOrigin());
			    p.remove(i);
			}
		}

		return p;
	}
	

}
