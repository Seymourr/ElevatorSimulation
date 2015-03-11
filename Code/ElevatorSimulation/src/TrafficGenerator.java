import java.util.ArrayList;
import java.util.Random;
public class TrafficGenerator {

	private int secondsPerPeriod = 2*60*60; //Passengers are allocated over this timespan
	
	
	private int passengerNumber;
	private int floors;
	private int skylobbyfloor;
	private int shafts;
	
	public TrafficGenerator(int passengerNumber, int floors, int skylobbyfloor, int shafts)
	{
		this.passengerNumber = passengerNumber;
		this.floors = floors;
		this.skylobbyfloor = skylobbyfloor;
		this.shafts = shafts;
	}
	
	//Is skylobby a legit destination?
	public ArrayList<Passenger> getTraffic(TrafficType t, ArrayList<Passenger> pass) throws Exception
	{
		Random r = new Random();
		
		switch(t)
		{
			case UPPEAK:

				pass = new ArrayList<Passenger>();
				for(int i = 0; i < passengerNumber; i++)
				{
					Passenger p = new Passenger();
					int Low = 1; //Inclusive
					int High = floors + 1; //Exclusive
					int destination = r.nextInt(High-Low) + Low; //check that it behaves correctly
					p.setFloor(0); //Lobby
					p.setDestination(destination);
					pass.add(p);
				}
				break;
			case LUNCH:
				for(int i = 0; i < pass.size(); i++)
				{
					pass.get(i).setPreviousFloor(pass.get(i).getFloor());
					pass.get(i).setDestination(0); //Lobby
				}
				break;
			case DOWNPEAK:
				for(int i = 0; i < pass.size(); i++)
				{
					pass.get(i).setDestination(0); //Lobby, final destination for the day
				}
				break;
			case REGULAR:
				for(int i = 0; i < pass.size(); i++)
				{
					int Low = 0; //Inclusive
					int High = floors + 1;
					int destination = r.nextInt(High-Low)+Low;
					while(destination != pass.get(i).getFloor())
					{
						destination = r.nextInt(High-Low)+Low;
					}
					pass.get(i).setDestination(destination); 		
				}
				break;
			default: throw new Exception("Wrong traffictype");
		}
		
		return pass;
	}
}
