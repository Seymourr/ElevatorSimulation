import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
public class Main {

	private static ArrayList<Passenger> passengers; 
	private static ArrayList<Call> calls;
	private static ArrayList<Elevator> localElevators;
	private static ArrayList<Elevator> shuttleElevators;
	private static TrafficGenerator trafficGen;
	private static ElevatorSpecs specs;
	
	
	public static void main(String[] args)
	{
		localElevators = new ArrayList<Elevator>();
		shuttleElevators = new ArrayList<Elevator>();
		calls = new ArrayList<Call>();
		try {
			specs = getSpecs();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		trafficGen = new TrafficGenerator(specs);
	//	makeElevators();
	//	startSimulation();
	//	fancyPrint();  //TODO print results
		
	}
	
	
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
	private static ElevatorSpecs getSpecs() throws FileNotFoundException
	{
		
		ArrayList<String> temp = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("Src/ElevatorFiles/specs.txt"));
		try {
		
			String line = br.readLine();
			
			while(line != null) {
				line = line.split(" ")[0];
				temp.add(line);
				line = br.readLine();
			}
		  }	catch (Exception e1) {
				
				e1.printStackTrace();
			}
		ElevatorSpecs es = new ElevatorSpecs(temp);
		return es;
		
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
