
import java.util.ArrayList;
import java.io.*;

/**
 * Test class for Passenger.java
 */
public class TestPassenger {
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
	
	public static void main (String[] args) {
		Call c1 = new Call(25, 0, 8);
		Call c2 = new Call(25, 8, 3);
		Call c3 = new Call(25, 7, 0);
		Call c4 = new Call(25, 4, 8);
		Call c5 = new Call(25, 0, 3);
		Call c6 = new Call(25, 8, 7);
		
		ElevatorSpecs specs;
		try {
			specs = getSpecs();
		} catch (Exception e) {
			specs = null;
		}
		
		Call[] calls = {c1, c2, c3, c4, c5, c6};
		
		for(Call c : calls) {
			Passenger p = new Passenger(c, specs);
			System.out.println(p.getOrigin());
			System.out.println(p.getDestination());
			System.out.println("Next: " + p.nextDestination());
			System.out.println(p.getOrigin());
			System.out.println(p.getDestination());
			System.out.println("Next: " + p.nextDestination());
			System.out.println(p.getOrigin());
			System.out.println(p.getDestination());
			System.out.println("Next: " + p.nextDestination());
			System.out.println(p.getOrigin());
			System.out.println(p.getDestination());
			System.out.println("Next: " + p.nextDestination());
			System.out.println();
		}
	}
}