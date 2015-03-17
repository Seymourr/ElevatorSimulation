
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
	private static ArrayList<Elevator> localElevators;
	private static ArrayList<Elevator> shuttleElevators;
	private static TrafficGenerator trafficGen;
	private static ElevatorSpecs specs;
	
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
		BufferedReader br = new BufferedReader(new FileReader("ElevatorFiles/specs.txt"));
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
		
		//Create a traffic generator according to the specifications
		trafficGen = new TrafficGenerator(specs);
		
		//Test the traffic generator
		testTrafficGen();
		
		//	makeElevators();
		//localElevators = new ArrayList<Elevator>();
		//shuttleElevators = new ArrayList<Elevator>();
		//	startSimulation();
		//	fancyPrint();  //TODO print results
	}
	

	public static void simulateDay()
	{
		int trafficAmount = specs.getLowTraffic();  //CHANGE OR DO SOOMETHING FANCY TO SUPPORT ALL THREE TRAFFIC TYPES
		ArrayList<Call> traffic = trafficGen.getTraffic(TrafficType.UPPEAK, trafficAmount);
		ArrayList<Call> traffic2 = trafficGen.getTraffic(TrafficType.REGULAR, trafficAmount);
		ArrayList<Call> traffic3 = trafficGen.getTraffic(TrafficType.LUNCH, trafficAmount);
		ArrayList<Call> traffic4 = trafficGen.getTraffic(TrafficType.REGULAR, trafficAmount);
		ArrayList<Call> traffic5 = trafficGen.getTraffic(TrafficType.DOWNPEAK, trafficAmount);

		//Call


	}

	public static void simulatePeriod(Algorithm alg, TrafficType t, int trafficAmount)
	{
		ArrayList<Call> traffic = trafficGen.getTraffic(t, trafficAmount);
		alg.setTraffic(traffic);
		SingleAutomatic algForShuttle = new SingleAutomatic(specs);
		algForShuttle.setTraffic(traffic);
		for(int second_i = 0; second_i < specs.getPeriodTime(); second_i++) //i seconds 
		{
			//Update position of elevators
			updateElevatorPosition();
			
			//People get off elevators
			ArrayList<Passenger> newCalls = updateElevatorOnOff();
			
			//Manage new calls (algorithm call)
			localElevators = alg.manageCalls(second_i, localElevators, newCalls); //assumes localElevators come first, then shuttles
			shuttleElevators = algForShuttle.manageShuttleCalls(second_i, shuttleElevators, newCalls);
			
			
			//TODO: Update waiting/travel time


		}
	}
	
	private static void updateElevatorPosition()
	{
		for(int i = 0; i < localElevators.size(); i++)
		{
			localElevators.get(i).updateElevator();
		}
		
		for(int i = 0; i < shuttleElevators.size(); i++)
		{
			shuttleElevators.get(i).updateElevator();
		}
	}
	
	//TODO update travel time (sum)
	private static ArrayList<Passenger> updateElevatorOnOff()
	{
		ArrayList<Passenger> p = new ArrayList<Passenger>();
		for(int i = 0; i < localElevators.size(); i++)
		{
			Passenger[] temp = localElevators.get(i).openDoors();
			
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
			    p.remove(i);
			}
		}

		return p;
	}
	
	//TODO: Adapt for different elevator types
	/*
	private static void makeElevators()
	{
		int numberOfShuttles = 8; //Edit with proper coding to support different implementations
		for(int i = 0; i < specs.getShafts() - numberOfShuttles;i++)
		{
			localElevators.add(new Elevator(0));
		}
		
		for(int i = 0; i < numberOfShuttles; i++)
		{
			shuttleElevators.add(new Elevator(0));
		}
		
		for(int i = 0; i < specs.getShafts() - numberOfShuttles; i++)
		{
			localElevators.add(new Elevator(specs.getSkylobbyfloor())); //Begin in skylobby? Discuss!
		}
	}

	private static void startSimulation()
	{
		for(int i = 0; i < specs.getSimulationDays(); i++)
		{
			calls = trafficGen.getTraffic(TrafficType.UPPEAK, null, specs.getLowTraffic());
			
			//What should we do next? Pre assign an elevator to a passenger? 
			// In real life, without the system knowing where passengers wants aforehand, a passenger walk to the nearby elevators. (60 elevators is a lot to choose from)
			//Best solution: System answer calls by assigning elevators to floors via own method. Maybe cut down amount of elevators.
			//After selection is made, let all passengers board elevators, and move it up each iteration step(which probably is a loop over each second)
			
		}
	}
		*/
}
