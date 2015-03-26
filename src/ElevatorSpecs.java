
import java.util.ArrayList;

/**
 * Objects of this class contains elevator specifications that will be used in the simulation.
 * Note that if a double decked elevator is used the skyLobbyFloor will represent the lower
 * of the two lobby floors in used, the same goes for lobbyfloor.
 */
public class ElevatorSpecs {
	/* Fields */
	private final int simulationDays; 
	private final int periodTime;
	private final int lowTraffic;
	private final int mediumTraffic;
	private final int heavyTraffic;
	private final int floors;
	private final int skylobbyfloor;
	private final int distanceBetweenFloors;
	private final int shafts;
	private final int buildingHeight;
	private final int carryCapacity;
	private final int carSpeed;
	private final int floorDelay; //Passenger boarding time
	private final ElevatorType local;
	private final ElevatorType shuttle;
	private final int numberOfShuttles;
	private final int lobbyFloor;
	private final boolean zoning;
	
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
        
		if(indata.get(13).equals("single")) {
			local = ElevatorType.SINGLE;
		} else {
			local = ElevatorType.DOUBLE;
		}
        
		if(indata.get(14).equals("single")) {
			shuttle = ElevatorType.SINGLE;
		} else {
			shuttle = ElevatorType.DOUBLE;
		}
		
		this.numberOfShuttles = Integer.parseInt(indata.get(15));
		if(indata.get(16).equals("true")) {
			zoning = true;
		} else {
			zoning = false;
		}
		
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

	public boolean zoningUsed() {
		return zoning;
	}
}
