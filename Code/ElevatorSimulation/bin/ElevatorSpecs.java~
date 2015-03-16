
import java.util.ArrayList;

/**
 * Objects of this class contains elevator specifications that will be used in the simulation.
 */
public class ElevatorSpecs {
	/* Fields */
	private int simulationDays; 
	private int periodTime;
	private int lowTraffic;
	private int mediumTraffic;
	private int heavyTraffic;
	private int floors;
	private int skylobbyfloor;
	private int distanceBetweenFloors;
	private int shafts;
	private int buildingHeight;
	private int carryCapacity;
	private int carSpeed;
	private int floorDelay; //Passenger boarding time
	private ElevatorType local;
	private ElevatorType shuttle;
	private int numberOfShuttles;
	
	private int lobbyFloor;
	
	//TODO Number of shuttles
	//TODO Research low, medium, heavy traffic params
	
	/**
	 * Creates an object which puts data from the input ArrayList
	 * into specific field according to the following specification.
	 * TODO Complete the table below
	 *
	 * Index	Field Name		Data
	 * 0		simulationDays	Amount of days that will be simulated
	 * 1		periodTime		The amount of seconds that one period of simulation will include
	 * 2
	 */
	public ElevatorSpecs(ArrayList<String> indata) {
		lobbyFloor = 0;

		this.simulationDays = Integer.parseInt(indata.get(0));
		this.periodTime = Integer.parseInt(indata.get(1));
		this.lowTraffic = Integer.parseInt(indata.get(2));
		this.mediumTraffic = Integer.parseInt(indata.get(3));
		this.heavyTraffic = Integer.parseInt(indata.get(4));
		this.floors = Integer.parseInt(indata.get(5));
		this.skylobbyfloor = Integer.parseInt(indata.get(6));
		this.distanceBetweenFloors = Integer.parseInt(indata.get(7));
		this.shafts = Integer.parseInt(indata.get(8));
		this.buildingHeight = Integer.parseInt(indata.get(9));
		this.carryCapacity = Integer.parseInt(indata.get(10));
		this.carSpeed = Integer.parseInt(indata.get(11));
		this.floorDelay = Integer.parseInt(indata.get(12));
		
		if(indata.get(13).equals("single"))
		{
			local = ElevatorType.SINGLE;
		} else {
			local = ElevatorType.DOUBLE;
		}
		
		if(indata.get(14).equals("single"))
		{
			shuttle = ElevatorType.SINGLE;
		} else {
			shuttle = ElevatorType.DOUBLE;
		}
		
		this.numberOfShuttles = Integer.parseInt(indata.get(15));
		
		//no consideration to error in reading
	}
	
	/****************************** GET METHODS FOR THE FIELDS *******************/

	public int getLobbyFloor() {
		return lobbyFloor;
	}
	
	public int getSimulationDays() {
		return simulationDays;
	}

	public int getPeriodTime() {
		return periodTime;
	}

	public int getLowTraffic() {
		return lowTraffic;
	}

	public int getMediumTraffic() {
		return mediumTraffic;
	}

	public int getHeavyTraffic() {
		return heavyTraffic;
	}

	public int getFloors() {
		return floors;
	}

	public int getSkylobbyfloor() {
		return skylobbyfloor;
	}

	public int getDistanceBetweenFloors() {
		return distanceBetweenFloors;
	}

	public int getShafts() {
		return shafts;
	}

	public int getBuildingHeight() {
		return buildingHeight;
	}

	public int getCarryCapacity() {
		return carryCapacity;
	}

	public int getCarSpeed() {
		return carSpeed;
	}

	public int getFloorDelay() {
		return floorDelay;
	}

	public ElevatorType getLocal() {
		return local;
	}



	public ElevatorType getShuttle() {
		return shuttle;
	}

		
	public int getNumberOfShuttles() {
		return numberOfShuttles;
	}
}
