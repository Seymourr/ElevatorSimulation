
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.LinkedList;

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
	private static ArrayList<ArrayList<Elevator>> allElevators;
	private static TrafficGenerator trafficGen;
	private static ElevatorSpecs specs;
	private static int travellingTime;
	private static int waitingTime;
	
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
		BufferedReader br = new BufferedReader(new FileReader("ElevatorFiles/specs.txt")); //src måste vara med i eclipse
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
		ArrayList<Call> traffic = trafficGen.getTraffic(TrafficType.LUNCH, 1000);
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
	private static void testTraffic(LinkedList<Call> traffic){
		for (Call c : traffic) {
			System.out.print("Calltime: " + c.getCallTime());
			System.out.print(" | Origin floor: " + c.getOriginFloor());
			System.out.print(" | Destination floor: " + c.getDestination());
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
	//	testTrafficGen();
		createElevators();
		simulateDay(new SingleAutomatic(specs), 1000); 
	}

	/**
	 * Creates bot and top local elevators, as well as shuttle elevators, and put their respective collection
	 * into the common Arraylist 'allElevators'. Index 0 of allElevators always contain the bottom local elevators, 
	 * index 1 contain all shuttle elevators and index 2 the top local elevators.
	 */
	private static void createElevators(){
		localElevatorsBottom = new ArrayList<Elevator>();
		localElevatorsTop = new ArrayList<Elevator>();
		shuttleElevators = new ArrayList<Elevator>();
		int numberOfShuttles = specs.getNumberOfShuttles(); //Edit with proper coding to support different implementations
		
		int[] bottomfloors = new int[specs.getSkylobbyfloor()];
		for(int i = 0; i < specs.getSkylobbyfloor(); i++){
			bottomfloors[i] = i;
		}
		
		int[] shuttleFloors = new int[2];
		shuttleFloors[0] = specs.getLobbyFloor();
		shuttleFloors[1] = specs.getSkylobbyfloor();
		
		int[] topfloors = new int[(specs.getFloors() - bottomfloors.length)];
		
		for(int i = 0; i < topfloors.length; i++){
			topfloors[i] = specs.getSkylobbyfloor() + i;
		}
		
		for(int i = 0; i < specs.getShafts() - numberOfShuttles;i++){
			localElevatorsBottom.add(new Elevator(specs, bottomfloors, 0)); //Begin with start 0 for simplicity
			localElevatorsTop.add(new Elevator(specs, topfloors, specs.getSkylobbyfloor())); 
		}
		
		for(int i = 0; i < numberOfShuttles; i++){
			shuttleElevators.add(new Elevator(specs, shuttleFloors, 0));
		}
		
		allElevators = new ArrayList<ArrayList<Elevator>>();
		allElevators.add(localElevatorsBottom);
		allElevators.add(shuttleElevators);
		allElevators.add(localElevatorsTop);
	}

	/**
	 * Simulate a day of elevator activity. Consists of 5 different periods.
	 * @param alg
	 * @param trafficAmount
	 */
	public static void simulateDay(Algorithm alg, int trafficAmount){
		simulatePeriod(alg, TrafficType.UPPEAK, trafficAmount);
	//	simulatePeriod(alg, TrafficType.REGULAR, trafficAmount);
	//	simulatePeriod(alg, TrafficType.LUNCH, trafficAmount);
	//	simulatePeriod(alg, TrafficType.REGULAR, trafficAmount);
	//	simulatePeriod(alg, TrafficType.DOWNPEAK, trafficAmount);
		System.out.println("Now going into rest calls");
		handleRestCalls(alg); // Extra time needed to empty system
		System.out.println("Simulation finished, system empty");
		printDayResults(new BigInteger("" + trafficAmount)); 
		//TODO: Something to manage time from this day (UPDATE: NOT FINISHED?)
	}
	
	/**
	 * Print out the total waiting and average time of the system, including the average passenger case.
	 * @param passengerAmount
	 */
	private static void printDayResults(BigInteger passengerAmount){
		BigInteger totalWaitingTime = new BigInteger("0");
		BigInteger totalTravelingTime = new BigInteger("0");
        
        System.out.println("************** LOCAL BOT ************");
        
		for(int i = 0; i < localElevatorsBottom.size(); i++){
            System.out.println(localElevatorsBottom.get(i).getRecords().getStringRepresentation()); // DEBUG UTSKRIFTER HÄR
			totalWaitingTime = totalWaitingTime.add(localElevatorsBottom.get(i).getRecords().waitingTime);
			totalTravelingTime = totalTravelingTime.add(localElevatorsBottom.get(i).getRecords().travelingTime);
		}
        
        System.out.println("************** LOCAL TOP ************");
		
		for(int i = 0; i < localElevatorsTop.size(); i++){
            System.out.println(localElevatorsTop.get(i).getRecords().getStringRepresentation()); // DEBUG UTSKRIFTER HÄR
			totalWaitingTime = totalWaitingTime.add(localElevatorsTop.get(i).getRecords().waitingTime);
			totalTravelingTime = totalTravelingTime.add(localElevatorsTop.get(i).getRecords().travelingTime);
		}
        
        System.out.println("************** SHUTTLES ************");
		
		for(int i = 0; i < shuttleElevators.size(); i++){
            System.out.println(shuttleElevators.get(i).getRecords().getStringRepresentation()); // DEBUG UTSKRIFTER HÄR
			totalWaitingTime = totalWaitingTime.add(shuttleElevators.get(i).getRecords().waitingTime);
			totalTravelingTime = totalTravelingTime.add(shuttleElevators.get(i).getRecords().travelingTime);
		}
		
		System.out.println("***RESULTS ARE THE FOLLOWING***");
		System.out.println("Total waiting time: " + totalWaitingTime.toString() + " virtual seconds");
		System.out.println("Total traveling time: " + totalTravelingTime.toString() + " virtual seconds");
		System.out.println("Average Total waiting time: " + totalWaitingTime.divide(passengerAmount).toString() + " virtual seconds");
		System.out.println("Average Total traveling time: " + totalTravelingTime.divide(passengerAmount).toString() + " virtual seconds");
	}
	
	/**
	 * Further simulates remaining passengers traveling. Extra time is added to the total time.
	 */
	public static void handleRestCalls(Algorithm alg){
		while(!systemEmpty()){

			updateElevatorPosition();

			LinkedList<Passenger> calls = updateElevatorOnOff();
			allElevators = alg.manageCalls(allElevators, calls);
		}
	}

	/**
	 * Checks if the system of elevators is free of calls.
	 * @return True or False
	 */
	public static boolean systemEmpty(){
		boolean isEmpty = true;
		
		for(int i = 0; i < allElevators.get(0).size(); i++){
			if(allElevators.get(0).get(i).getQueue().size() > 0){
				isEmpty = false;
				return isEmpty;
			}
		}
		for(int i = 0; i < allElevators.get(1).size(); i++){
			if(allElevators.get(1).get(i).getQueue().size() > 0){
				isEmpty = false;
				return isEmpty;
			}
		}
		for(int i = 0; i < allElevators.get(2).size(); i++){
			if(allElevators.get(2).get(i).getQueue().size() > 0){
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
		LinkedList<Call> traffic = new LinkedList<Call>(trafficGen.getTraffic(t, trafficAmount));
	//	testTraffic(traffic); //Debugging
		
		for(int second_i = 0; second_i < specs.getPeriodTime(); second_i++){
			//Update position of elevators
			updateElevatorPosition();
			
			//People get off elevators
			LinkedList<Passenger> calls = updateElevatorOnOff();
			
			while(!traffic.isEmpty() && traffic.getFirst().getCallTime() == second_i) {
                calls.add(new Passenger(traffic.removeFirst(), specs));
            }
			
			//Manage new calls (algorithm call)
			allElevators = alg.manageCalls(allElevators, calls);
			
		}
	}
	
	/**
	 * Update the position of every elevator by calling its update function.
	 */
	private static void updateElevatorPosition(){
		for(int i = 0; i < allElevators.get(0).size(); i++){
			allElevators.get(0).get(i).updateElevator();
		}
		
		for(int i = 0; i < allElevators.get(1).size(); i++){
			allElevators.get(1).get(i).updateElevator();	
		}
		
		for(int i = 0; i < allElevators.get(2).size(); i++){
			allElevators.get(2).get(i).updateElevator();
		}
	}
	
	/**
	 * Open elevator doors and allow passengers to enter or exit. For all passengers that exit, either remove
	 * them if they have completed their travel, or return them in a list.
	 * @return An ArrayList containing people who will continue their traveling. 
	 */
	private static LinkedList<Passenger> updateElevatorOnOff(){
	 LinkedList<Passenger> disembarked = new LinkedList<Passenger>();
		
		for(int i = 0; i < allElevators.get(0).size(); i++){
			Passenger[] temp = allElevators.get(0).get(i).openDoors();
			for(Passenger p : temp) {
                p.nextDestination();
                disembarked.add(p);
            }
		}

		for(int i = 0; i < allElevators.get(1).size(); i++){
			Passenger[] temp = allElevators.get(1).get(i).openDoors();
			for(Passenger p : temp) {
                p.nextDestination();
                disembarked.add(p);
            }
		}

		for(int i = 0; i < allElevators.get(2).size(); i++){
			Passenger[] temp = allElevators.get(2).get(i).openDoors();
			for(Passenger p : temp) {
                p.nextDestination();
                disembarked.add(p);
            }
		}

		return disembarked;
	}
	

}
