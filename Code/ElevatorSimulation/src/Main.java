import java.util.ArrayList;
public class Main {

	private static int simulationDays = 365; //A year
	private static int periodTime = 2*60*60;
	private static int lowTraffic = 1000;
	private static int mediumTraffic = 5000;
	private static int heavyTraffic = 10000;
	private static int floors = 100;
	private static int skylobbyfloor = 45;
	private static int shafts = 50;
	
	private static ArrayList<Elevator> local;
	private static ArrayList<Elevator> shuttle;
	private static ArrayList<Passenger> pass; 
	
	private static int carryCapacity = 12;
	private static int carSpeed = 4;
	private static int distanceBetweenFloors = 5;
	private static int buildingHeight = 500;
	private static int floorDelay = 10; //Passenger boarding time
	
	
	public static void main(String[] args)
	{
		local = new ArrayList<Elevator>(); //Start with simple elevator for now
		shuttle = new ArrayList<Elevator>();
		TrafficGenerator gen = new TrafficGenerator(lowTraffic, floors, skylobbyfloor, shafts);
		
		
		for(int i = 0; i < simulationDays; i++)
		{
			boolean traffic = true; //A new day
			while(traffic) 
			{
				for(int i_up = 0; i_up < periodTime; i_up++)
				{
					
					
				}
				//While there is still traffic (day not over)
				//TODO: Split up day, can passengers in e.g. lunch go over to regular? Can be problematic.
				
			}
		}
	}
}
