
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	private static int numberOfCalls;
	private static int debug;
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
 	      if(specs.zoningUsed()) {
        	createZonedElevators();
        	printZoning();
       } else {
			createElevators();
		}
       
        //Perform the simulation
		int trafficAmount = 5; //specs.getHeavyTraffic();
		int cnt = 0;
		System.out.println("Now starting simulation with " + trafficAmount + " passengers per period");
		for(int i = 0; i < specs.getSimulationDays(); i++) {
			cnt += simulateDay(new SearchBasedCollective(specs), trafficAmount); 
			System.out.println("Day " + i + " complete" + " Served: " + cnt + " so far finished: " + debug);
	}
		printResults(new BigInteger("" + cnt));
	
//		for(int i = 0; i < specs.getSimulationDays(); i++) {}
//		simulatePeriod(alg, TrafficType.DOWNPEAK, trafficAmount);
//		printCallAmount();

//		System.out.println("Now going into rest calls");
//		printCallAmount();
	//	handleRestCalls(alg); // Extra time needed to empty system
	//	simulateDay(new SingleAutomatic(specs), 1000);

	}

	private static void printZoning() {
		for(int i = 0; i < allElevators.size(); i++) {
			System.out.println("***************************************");
			System.out.println("Now printing new set of elevators");
			System.out.println("***************************************");
			for(int j = 0; j < allElevators.get(i).size(); j++) {
				System.out.print("Served floors: ");
				for(int k = 0; k < allElevators.get(i).get(j).getZonedFloors().length; k++) {
					System.out.print(allElevators.get(i).get(j).getZonedFloors()[k] + " , ");
				}
				System.out.println();
			}
		}
	}
	/**
	* Creates bot and top local elevators as well as shuttle elevators much like the method createElevators, with the
	* exception that floors are evenly distributed to local elevators. Assumed there are at least 50 floors, with reasonable number of shafts.
	*/
	private static void createZonedElevators() {
		  //Initiate the lists
        localElevatorsBottom = new ArrayList<ElevatorInterface>();
        localElevatorsTop = new ArrayList<ElevatorInterface>();
        shuttleElevators = new ArrayList<ElevatorInterface>();

        int botFloors = specs.getSkylobbyfloor();
        int topFloors = specs.getFloors() - specs.getSkylobbyfloor();

        int[] bF = new int[specs.getSkylobbyfloor()];
        for(int i = 0; i < bF.length; i++) {
        	bF[i] = i;
        }
         int[] tF = new int[specs.getFloors() - specs.getSkylobbyfloor()];
        for(int i = 0; i < tF.length; i++) {
        	tF[i] = i + specs.getSkylobbyfloor();
        }
        localElevatorsBottom = getZonedElevators(botFloors, specs.getLobbyFloor(), specs.getSkylobbyfloor(), bF);
        localElevatorsTop = getZonedElevators(topFloors, specs.getSkylobbyfloor(), specs.getSkylobbyfloor(), tF);

        //Create shuttles
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
        } else {
            for(int i = 0; i < numberOfShuttles; i++){
                shuttleElevators.add(new DDElevator(specs, shuttleFloors, 0));
            }
        } 

		allElevators = new ArrayList<ArrayList<ElevatorInterface>>();
		allElevators.add(localElevatorsBottom);
		allElevators.add(shuttleElevators);
		allElevators.add(localElevatorsTop);

   }

    private static ArrayList<ElevatorInterface> getZonedElevators(int amntFloors, int lobby, int limit, int[] standardFloors) {

    	ArrayList<ElevatorInterface> elevators = new ArrayList<ElevatorInterface>();
    	int minzone = 1;
        if(specs.getShuttle() == ElevatorType.DOUBLE) {
        	minzone = 2;
        }

        int zonesize = minzone;
        int zones = amntFloors / zonesize;
        int eles = specs.getShafts() - specs.getNumberOfShuttles();
        while(eles/zones < 1) {
        	//zoneize needs to get bigger
        	zonesize += minzone;
        	zones = amntFloors/zonesize;
        }

        int elevatorsPerZone = eles / zones;

        //Create elevators
        int remainingElevators = eles;
        int i = 0;
        for(i = minzone; i < amntFloors; i+=zonesize) {
        	for(int j = 0; j < elevatorsPerZone; j++) {
        		ArrayList<Integer> temp = new ArrayList<Integer>();
        		temp.add(lobby);
        		if(specs.getShuttle() == ElevatorType.DOUBLE) {
        			temp.add(lobby + 1);
        		}
        		for(int k = i; k < i + zonesize; k++) {
        			if(k >= limit) {
        				break;
        			}
        			temp.add(k + lobby);
        		}
        		if(temp.size() == 2) {
        			System.out.println("Error first loop");
        		}
        		int[] floors = new int[temp.size()];
        			for(int k = 0; k < temp.size(); k++) {
        				floors[k] = temp.get(k);
        			}

        		if(specs.getLocal() == ElevatorType.SINGLE) {
        			
        			elevators.add(new Elevator(specs, standardFloors, floors, floors[0]));
        		} else {
        			elevators.add(new DDElevator(specs, standardFloors, floors, floors[0]));
        		}
        		remainingElevators -= 1;
        	}
        }
        i = i - zonesize;

        for(int k = i; k > 0; k-= zonesize) {
        	if(remainingElevators == 0) {
        		break;
        	}
        	ArrayList<Integer> temp = new ArrayList<Integer>();
        		temp.add(lobby);
        		if(specs.getShuttle() == ElevatorType.DOUBLE) {
        			temp.add(lobby + 1);
        		}
        		for(int j = k; j < k + zonesize; j++) {
        			if(j >= limit) {
        				break;
        			}
        			temp.add(j + lobby);
        		}
        		if(temp.size() == 2) {
        			System.out.println("Error sec loop");
        		}
        		int[] floors = new int[temp.size()];
        			for(int l = 0; l < temp.size(); l++) {
        				floors[l] = temp.get(l);
        			}

        		if(specs.getLocal() == ElevatorType.SINGLE) {
        			
        			elevators.add(new Elevator(specs, standardFloors, floors, floors[0]));
        		} else {
        			elevators.add(new DDElevator(specs, standardFloors, floors, floors[0]));
        		}
        		remainingElevators -= 1;
        }

        return elevators;
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
                localElevatorsTop.add(new Elevator(specs, topfloors, topfloors[0]));
            }
        } else if (specs.getLocal() == ElevatorType.DOUBLE) {
            for (int i = 0; i < noLocals; i++) {
                localElevatorsBottom.add(new DDElevator(specs, bottomfloors, 0));
                localElevatorsTop.add(new DDElevator(specs, topfloors, topfloors[0]));
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
	public static int simulateDay(Algorithm alg, int trafficAmount){
		int cnta = 0;
		System.out.println("Entered day");
		simulatePeriod(alg, TrafficType.UPPEAK, trafficAmount);
		printCallAmount();
		cnta += trafficAmount;
/*
			
		simulatePeriod(alg, TrafficType.REGULAR, trafficAmount/3);
		printCallAmount();
		cnt += trafficAmount/3;
			
		simulatePeriod(alg, TrafficType.LUNCH, trafficAmount/2);
		debug -= trafficAmount / 2; //Removing extra calls made by lunch
		printCallAmount();
		cnt += trafficAmount/2;
			
		simulatePeriod(alg, TrafficType.REGULAR, trafficAmount/3);
		printCallAmount();
		cnt += trafficAmount/3;
		
		simulatePeriod(alg, TrafficType.DOWNPEAK, trafficAmount);
		printCallAmount();
		cnt += trafficAmount;	
		*/	
			
		System.out.println("Now going into rest calls");
		handleRestCalls(alg); // Extra time needed to empty system

		System.out.println("Simulation finished, system empty");
		return cnta;
	}
    
    	
	/**
	 * Simulates a period of elevator traffic.
	 * @param alg
	 * @param t
	 * @param trafficAmount
	 */
	public static void simulatePeriod(Algorithm alg, TrafficType t, int trafficAmount) {
		LinkedList<Call> traffic = new LinkedList<Call>(trafficGen.getTraffic(t, trafficAmount));
     //   	testTraffic(traffic); //Debugging
		int temps = 0;
		numberOfCalls += traffic.size();
		for(int second_i = 0; second_i < specs.getPeriodTime(); second_i++) {
			//People get off elevators
			LinkedList<Passenger> calls = updateElevatorOnOff();
			numberOfCalls += calls.size();
			//Update position of elevators
			updateElevatorPosition();

            //New calls at this second?
			while(!traffic.isEmpty() && traffic.getFirst().getCallTime() == second_i) {
                calls.add(new Passenger(traffic.removeFirst(), specs));
                temps += 1;
            }
			
			//Manage new calls (algorithm call)
			allElevators = alg.manageCalls(allElevators, calls);
		//	debugPrint();
		}
	}
	

	private static void debugPrint() {
		if(systemEmpty()) {
			return;
		}
		for(int i = 0; i < allElevators.size(); i++) {
			for(int j = 0; j < allElevators.get(i).size(); j++) {
				if(allElevators.get(i).get(j).getStatus().direction == 0 && allElevators.get(i).get(j).getStatus().passengers == 0) {
					continue;
				}
				System.out.println(allElevators.get(i).get(j).getQueue().getFirst().getCarPosition());
				System.out.println(allElevators.get(i).get(j).getStatus().getStringRepresentation());
			}
		}
	}
	/**
	 * Print out the total waiting and average time of the system, including the average passenger case.
	 * @param passengerAmount
	 */
	private static void printResults(BigInteger passengerAmount){
		BigInteger totalWaitingTime = new BigInteger("0");
		BigInteger totalTravelingTime = new BigInteger("0");
        
        int bt = 0;
        int tt = 0;
        int st = 0;
        System.out.println("************** LOCAL BOT ************");
        
		for(int i = 0; i < localElevatorsBottom.size(); i++){
			if(localElevatorsBottom.get(i).getRecords().ridesServed.intValue() > 0){
            System.out.println(localElevatorsBottom.get(i).getRecords().getStringRepresentation()); // DEBUG UTSKRIFTER HÄR
            }
			totalWaitingTime = totalWaitingTime.add(localElevatorsBottom.get(i).getRecords().waitingTime);
			totalTravelingTime = totalTravelingTime.add(localElevatorsBottom.get(i).getRecords().travelingTime);
			bt += Integer.parseInt(localElevatorsBottom.get(i).getRecords().ridesServed.toString());
		}
        
        System.out.println("************** LOCAL TOP ************");
		
		for(int i = 0; i < localElevatorsTop.size(); i++){
				if(localElevatorsTop.get(i).getRecords().ridesServed.intValue()  > 0){
           System.out.println(localElevatorsTop.get(i).getRecords().getStringRepresentation()); // DEBUG UTSKRIFTER HÄR
       }
			totalWaitingTime = totalWaitingTime.add(localElevatorsTop.get(i).getRecords().waitingTime);
			totalTravelingTime = totalTravelingTime.add(localElevatorsTop.get(i).getRecords().travelingTime);
			tt += Integer.parseInt(localElevatorsTop.get(i).getRecords().ridesServed.toString());
		}
        
        System.out.println("************** SHUTTLES ************");
		
		for(int i = 0; i < shuttleElevators.size(); i++){
			if(shuttleElevators.get(i).getRecords().ridesServed.intValue()  > 0){
            System.out.println(shuttleElevators.get(i).getRecords().getStringRepresentation()); // DEBUG UTSKRIFTER HÄR
        }	
			totalWaitingTime = totalWaitingTime.add(shuttleElevators.get(i).getRecords().waitingTime);
			totalTravelingTime = totalTravelingTime.add(shuttleElevators.get(i).getRecords().travelingTime);
			st +=  Integer.parseInt(shuttleElevators.get(i).getRecords().ridesServed.toString());
		}
		
		System.out.println("***RESULTS ARE THE FOLLOWING***");
		System.out.println("Total waiting time: " + totalWaitingTime.toString() + " virtual seconds");
		System.out.println("Total traveling time: " + totalTravelingTime.toString() + " virtual seconds");
		System.out.println("Average waiting time: " + (new BigDecimal(totalWaitingTime)).divide(new BigDecimal(passengerAmount), 2, RoundingMode.HALF_UP).toString() + " virtual seconds");
		System.out.println("Average squared waiting time: " + ((new BigDecimal(totalWaitingTime)).pow(2)).divide((new BigDecimal(passengerAmount)).pow(2), 2, RoundingMode.HALF_UP).toString() + " virtual seconds");
		System.out.println("Average traveling time: " + (new BigDecimal(totalTravelingTime)).divide(new BigDecimal(passengerAmount), 2, RoundingMode.HALF_UP).toString() + " virtual seconds");
		System.out.println("Average squared traveling time: " +  ((new BigDecimal(totalTravelingTime)).pow(2)).divide((new BigDecimal(passengerAmount)).pow(2), 2, RoundingMode.HALF_UP).toString() + " virtual seconds");
		System.out.println("Bot calls served: " + bt);
		System.out.println("Top calls served: " + tt);
		System.out.println("Shuttle calls served: " + st);
		System.out.println("Out of " + passengerAmount.toString() + " passengers, this many finished their journey:" + debug);
	}
	
	/**
	 * Further simulates remaining passengers traveling. Extra time is added to the total time.
	 */
	public static void handleRestCalls(Algorithm alg){
		int cnt = 1;
		int previous = 0;
		while(!systemEmpty()){
			LinkedList<Passenger> calls = updateElevatorOnOff();
			updateElevatorPosition();
			allElevators = alg.manageCalls(allElevators, calls);
		/*	int current = printCallAmount();
			if(previous == current) {
				cnt +=1;
			} else {
				cnt = 1;
			}
			previous = current;
			if(cnt == 2000) {
			for(int i = 0; i < allElevators.size(); i++) {
				for(int j = 0; j < allElevators.get(i).size(); j++) {
					if(!allElevators.get(i).get(j).getQueue().isEmpty()) {
						System.out.println("Elevator type: " + i);
						System.out.println("Queue size: " + allElevators.get(i).get(j).getQueue().size());
						System.out.println(allElevators.get(i).get(j).getQueue().get(0).getActionType());
						System.out.println(allElevators.get(i).get(j).getStatus().getStringRepresentation());

					}
				}	
			}
				
				System.exit(0);
			}
			*/
		}
	}

	private static int printCallAmount() {
		int temp = 0;
		for(int i = 0; i < allElevators.size(); i++) {
			for(int j = 0; j < allElevators.get(i).size(); j++) {
				temp += allElevators.get(i).get(j).getQueue().size();
			}
		}
		System.out.println(temp);
		return temp;
	}

	/**
	 * Checks if the system of elevators is free of calls.
	 * @return True or False
	 */
	public static boolean systemEmpty(){
		boolean isEmpty = true;
		
		for(int i = 0; i < allElevators.get(0).size(); i++){
			if(allElevators.get(0).get(i).getQueue().size() > 0 ||allElevators.get(0).get(i).getStatus().passengers > 0){
				isEmpty = false;
				return isEmpty;
			}
		}
		for(int i = 0; i < allElevators.get(1).size(); i++){
			if(allElevators.get(1).get(i).getQueue().size() > 0 ||allElevators.get(1).get(i).getStatus().passengers > 0){
				isEmpty = false;
				return isEmpty;
			}
		}
		for(int i = 0; i < allElevators.get(2).size(); i++){
			if(allElevators.get(2).get(i).getQueue().size() > 0 ||allElevators.get(2).get(i).getStatus().passengers > 0){
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
				if(!allElevators.get(i).get(j).updateElevator()){
					System.out.println("Something went wrong with updating the elevator ");
					System.exit(0);
				}
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

						} else {
							debug+=1;
						} 

				}
                
            }
	 	}
	 }
		return disembarked;
	}
	

}
