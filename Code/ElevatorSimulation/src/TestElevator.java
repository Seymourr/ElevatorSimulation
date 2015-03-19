
import java.util.Random;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Test class for Elevator.java
 */
public class TestElevator {
    private static ElevatorSpecs specs;
    private static Elevator[] shuttles;
    private static Elevator[] topLocals;
    private static Elevator[] botLocals;
    private static Random r = new Random();
    private static int[] shuttleFloors;
    private static int[] botFloors;
    private static int[] topFloors;
    
    /* ElevatorSpecs */
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
    
    /* Contains */
    private static boolean containsFloor(int[] arr, int floor) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == floor) {
                return true;
            }
        }
        return false;
    }
    
    /* System empty */
    public static boolean systemEmpty() {
		for(int i = 0; i < botLocals.length; i++){
			if(botLocals[i].getQueue().size() > 0) {
				return false;
			}
		}
		for(int i = 0; i < topLocals.length; i++){
			if(topLocals[i].getQueue().size() > 0){
				return false;
			}
		}
		for(int i = 0; i < shuttles.length; i++){
			if(shuttles[i].getQueue().size() > 0){
				return false;
			}
		}
		return true;
    }
    
    /* Update Elevators */ 
    private static void updateElevators() {
        for(int j = 0; j < shuttles.length; j++){
            shuttles[j].updateElevator();
        }
        
        for(int j = 0; j < botLocals.length; j++){
            botLocals[j].updateElevator();
        }
        
        for(int j = 0; j < topLocals.length; j++){
            topLocals[j].updateElevator();
        }
    }
    
    /* Open Doors */
    private static LinkedList<Passenger> openDoors() {
        LinkedList<Passenger> disembarked = new LinkedList<Passenger>();
        
        //Open shuttle doors
        for(int j = 0; j < shuttles.length; j++) {      
            Passenger[] temp;
            HashMap<CarPosition, Passenger[]> map = shuttles[j].openDoors();
            if (map.containsKey(CarPosition.NULL)) {
                temp = map.get(CarPosition.NULL);
            } else {
                temp = new Passenger[0];
            }
                   
            //Update Passenger Destinations
            for(Passenger p : temp) {
                p.nextDestination();
                disembarked.add(p);
            }
        }
        
        //Open bot local doors
        for(int j = 0; j < botLocals.length; j++) {
            Passenger[] temp;
            HashMap<CarPosition, Passenger[]> map = botLocals[j].openDoors();
            if (map.containsKey(CarPosition.NULL)) {
                temp = map.get(CarPosition.NULL);
            } else {
                temp = new Passenger[0];
            }
                    
            //Update Passenger Destinations
            for(Passenger p : temp) {
                p.nextDestination();
                disembarked.add(p);
            }
        }
        
        //Open top local doors
        for(int j = 0; j < topLocals.length; j++) {
            Passenger[] temp;
            HashMap<CarPosition, Passenger[]> map = topLocals[j].openDoors();
            if (map.containsKey(CarPosition.NULL)) {
                temp = map.get(CarPosition.NULL);
            } else {
                temp = new Passenger[0];
            }
            
            //Update Passenger Destinations
            for(Passenger p : temp) {
                p.nextDestination();
                disembarked.add(p);
            }            
        }
        
        return disembarked;
    }
    
    /* Assign passengers in the parameter to elevators by random */
    private static void assignPassengers(LinkedList<Passenger> disembarked) {
        while(!disembarked.isEmpty()) {
            Passenger p = disembarked.removeFirst();
            if (containsFloor(shuttleFloors, p.getOrigin()) && 
                containsFloor(shuttleFloors, p.getDestination())) {
                //Shuttle ride
                int rand = r.nextInt(shuttles.length);
                Elevator el = shuttles[rand];
                el.addToQueue(p, el.getQueue().size(), el.getQueue().size() + 1, CarPosition.NULL);
            } else if (containsFloor(botFloors, p.getOrigin()) && 
                containsFloor(botFloors, p.getDestination())) {
                //Bot local ride
                int rand = r.nextInt(botLocals.length);
                Elevator el = botLocals[rand];
                el.addToQueue(p, el.getQueue().size(), el.getQueue().size() + 1, CarPosition.NULL);
            } else if (containsFloor(topFloors, p.getOrigin()) && 
                containsFloor(topFloors, p.getDestination())) {
                //Top local ride
                int rand = r.nextInt(topLocals.length);
                Elevator el = topLocals[rand];
                el.addToQueue(p, el.getQueue().size(), el.getQueue().size() + 1, CarPosition.NULL);
            }
        }
    }
    
    /* Main */
    public static void main(String[] args) {
        try {
			specs = getSpecs();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        //Generate traffic
        TrafficGenerator trafficGen = new TrafficGenerator(specs);
        LinkedList<Call> traffic = new LinkedList<Call>(trafficGen.getTraffic(TrafficType.LUNCH, 500));
        
        /******* CREATE ELEVATORS *********/
        
        //Fetch specs
        int noShuttles = specs.getNumberOfShuttles();
        int noLocals = specs.getShafts() - noShuttles;
        int skylobby = specs.getSkylobbyfloor();
        int lobby = specs.getLobbyFloor();
        int floors = specs.getFloors();
        
        //Calculate allowed floros
        shuttleFloors = new int[2];
        shuttleFloors[0] = lobby;
        shuttleFloors[1] = skylobby;
        
        botFloors = new int[skylobby - lobby];
        for (int i = 0; i < botFloors.length; i++) {
            botFloors[i] = lobby + i;
        }
        
        topFloors = new int[floors - skylobby + 1];
        for (int i = 0; i < topFloors.length; i++) {
            topFloors[i] = skylobby + i;
        }
        
        //Create the elevators
        shuttles = new Elevator[noShuttles];
        for (int i = 0; i < noShuttles; i++) {
            shuttles[i] = new Elevator(specs, shuttleFloors, shuttleFloors[0]);
        }
        
        botLocals = new Elevator[noLocals];
        for (int i = 0; i < noLocals; i++) {
            botLocals[i] = new Elevator(specs, botFloors, botFloors[0]);
        }
        
        topLocals = new Elevator[noLocals];
        for (int i = 0; i < noLocals; i++) {
            topLocals[i] = new Elevator(specs, topFloors, topFloors[0]);
        }
        
        /***** RUN THE ALGORITHM ****/
        
        int time = specs.getPeriodTime();
        for (int i = 0; i < time; i++) {
            //Update Elevators           
            updateElevators();
            
            //Open Doors
            LinkedList<Passenger> disembarked = openDoors();
            
            //Fetch new calls from traffic
            while(!traffic.isEmpty() && traffic.getFirst().getCallTime() == i) {
                disembarked.add(new Passenger(traffic.removeFirst(), specs));
            }
            
            //Put passengers in new elevators if needed
            assignPassengers(disembarked);
        }
        
        //Empty the system
        while(!systemEmpty()) {
            //Update Elevators           
            updateElevators();
            
            //Open Doors
            LinkedList<Passenger> disembarked = openDoors();
            
            //Put passengers in new elevators if needed
            assignPassengers(disembarked);
        }
            
        //Print results
        System.out.println("************** LOCAL BOT ************");
        for(int i = 0; i < botLocals.length; i++) {
            System.out.println(botLocals[i].getRecords().getStringRepresentation());
        }
       
        System.out.println("************** LOCAL TOP ************");
        for(int i = 0; i < topLocals.length; i++) {
            System.out.println(topLocals[i].getRecords().getStringRepresentation());
        }
        
        System.out.println("************** SHUTTLES ************");
        for(int i = 0; i < shuttles.length; i++) {
            System.out.println(shuttles[i].getRecords().getStringRepresentation());
        }
    }
}