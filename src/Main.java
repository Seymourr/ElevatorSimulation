
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.HashMap;


/**
 *
 */
public class Main {
	/* Fields */
	private static ArrayList<Passenger> passengers; 
	private static ArrayList<Call> calls;
	private static ArrayList<ElevatorInterface> localElevatorsBottom;
	private static ArrayList<ElevatorInterface> localElevatorsTop;
	private static ArrayList<ElevatorInterface> shuttleElevators;
	private static ArrayList<ArrayList<ElevatorInterface>> allElevators;
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
        
		return new ElevatorSpecs(temp);
	}
	
	/**
	 * Method to test the traffic generator
	 */
	private static void testTrafficGen() {
		ArrayList<Call> traffic = trafficGen.getTraffic(TrafficType.REGULAR, 50);
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
       // testTrafficGen();
    
        //Create the elevators
		createElevators();
        
        //Perform the simulation
		simulateDay(new SelectiveCollective(specs), 5000); 
		 //simulateDay(new SingleAutomatic(specs), 5000);

	}

	/**
	 * Creates bot and top local elevators, as well as shuttle elevators, and put their respective collection
	 * into the common Arraylist 'allElevators'. Index 0 of allElevators always contain the bottom local elevators, 
	 * index 1 contain all shuttle elevators and index 2 the top local elevators.
	 */
	private static void createElevators() {
        int skylobby = specs.getSkylobbyfloor();
        
        //Initiate the lists
        localElevatorsBottom = new ArrayList<ElevatorInterface>();
        localElevatorsTop = new ArrayList<ElevatorInterface>();
        shuttleElevators = new ArrayList<ElevatorInterface>();
        
        //Calculate local floor ranges
    
       
     
       	int[] bottomfloors = new int[specs.getSkylobbyfloor()];
       	for(int i = 0; i < specs.getSkylobbyfloor(); i++){
       	   	bottomfloors[i] = i;
       		}
      	int[] topfloors = new int[(specs.getFloors() - bottomfloors.length)];
      	for (int i = 0; i < topfloors.length; i++) {
        	topfloors[i] = skylobby + i;
       	}
       	
        int[] shuttleFloors;
        
        //Calculate shuttle floor ranges
        if (specs.getShuttle() == ElevatorType.SINGLE) {
            shuttleFloors = new int[2];
            shuttleFloors[0] = specs.getLobbyFloor();
            shuttleFloors[1] = specs.getSkylobbyfloor();
        } else { //Double decked shuttle and thus two lobby floors
            shuttleFloors = new int[4];
            shuttleFloors[0] = specs.getLobbyFloor();
            shuttleFloors[1] = shuttleFloors[0] + 1;
            shuttleFloors[2] = specs.getSkylobbyfloor();
            shuttleFloors[3] = shuttleFloors[2] + 1;
        }

        //Fill the shuttle lists with elevators depending on type
        int numberOfShuttles = specs.getNumberOfShuttles();
        if (specs.getShuttle() == ElevatorType.SINGLE) {
            for(int i = 0; i < numberOfShuttles; i++){
                shuttleElevators.add(new Elevator(specs, shuttleFloors, 0));
            }
        } else if (specs.getShuttle() == ElevatorType.DOUBLE) {
            for(int i = 0; i < numberOfShuttles; i++){
                shuttleElevators.add(new DDElevator(specs, shuttleFloors, 0));
            }
        } else {
            System.out.println("Elevator shuttle type not specified correctly.");
            System.exit(1);
        }
        
        int noLocals = specs.getShafts() - numberOfShuttles;
              
        //Fill the local lists with elevators depending on type
        if (specs.getLocal() == ElevatorType.SINGLE) {
            for (int i = 0; i < noLocals; i++) {
                localElevatorsBottom.add(new Elevator(specs, bottomfloors, 0));
                localElevatorsTop.add(new Elevator(specs, topfloors, 0));
            }
        } else if (specs.getLocal() == ElevatorType.DOUBLE) {
            for (int i = 0; i < noLocals; i++) {
                localElevatorsBottom.add(new DDElevator(specs, bottomfloors, 0));
                localElevatorsTop.add(new DDElevator(specs, topfloors, 0));
            }
        } else {
            System.out.println("Elevator local type not specified correctly.");
            System.exit(1);
        }

        //Fill the AllElevators list
		allElevators = new ArrayList<ArrayList<ElevatorInterface>>();
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
		System.out.println("Period complete");
		simulatePeriod(alg, TrafficType.REGULAR, trafficAmount);
			System.out.println("Period complete");
		simulatePeriod(alg, TrafficType.LUNCH, trafficAmount);
			System.out.println("Period complete");
		simulatePeriod(alg, TrafficType.REGULAR, trafficAmount);
			System.out.println("Period complete");
		simulatePeriod(alg, TrafficType.DOWNPEAK, trafficAmount);
			System.out.println("Period complete");
		System.out.println("Now going into rest calls");
		handleRestCalls(alg); // Extra time needed to empty system
		System.out.println("Simulation finished, system empty");
		printDayResults(new BigInteger("" + (trafficAmount*5))); //Maybe consider dividing by amount of calls instead?
		//TODO: Something to manage time from this day (UPDATE: NOT FINISHED?)
	}
    
    	
	/**
	 * Simulates a period of elevator traffic.
	 * @param alg
	 * @param t
	 * @param trafficAmount
	 */
	public static void simulatePeriod(Algorithm alg, TrafficType t, int trafficAmount) {
		LinkedList<Call> traffic = new LinkedList<Call>(trafficGen.getTraffic(t, trafficAmount));
        	//testTraffic(traffic); //Debugging
		

		for(int second_i = 0; second_i < specs.getPeriodTime(); second_i++) {
			//People get off elevators
			LinkedList<Passenger> calls = updateElevatorOnOff();
			
			//Update position of elevators
			updateElevatorPosition();

            //New calls at this second?
			while(!traffic.isEmpty() && traffic.getFirst().getCallTime() == second_i) {
                calls.add(new Passenger(traffic.removeFirst(), specs));
            }
			
			//Manage new calls (algorithm call)
			allElevators = alg.manageCalls(allElevators, calls);
		}
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
	 * Update the position of every elevator by calling its update function.
	 */
	private static void updateElevatorPosition(){
		for(int i = 0; i < allElevators.size(); i++) {
			for(int j = 0; j < allElevators.get(i).size(); j++) {
				allElevators.get(i).get(j).updateElevator();
			}
		}
	}
	
	/**
	 * Open elevator doors and allow passengers to enter or exit. For all passengers that exit, either remove
	 * them if they have completed their travel, or return them in a list.
	 * @return An ArrayList containing people who will continue their traveling. 
	 */
	private static LinkedList<Passenger> updateElevatorOnOff(){
	 LinkedList<Passenger> disembarked = new LinkedList<Passenger>();
		
	 for(int i = 0; i < allElevators.size(); i++) {
	 	for(int j = 0; j < allElevators.get(i).size(); j++) {
	 		 HashMap<CarPosition, Passenger[]> temp = allElevators.get(i).get(j).openDoors();
			for(Passenger[] pList : temp.values()) {
				for(Passenger k : pList) {
					int dest = k.nextDestination();
					if(dest != -1) 
						{
							disembarked.add(k);
						} 
				}
                
            }
	 	}
	 }
		return disembarked;
	}
	

}
