
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
 * Test class for Selective Collective
 */
public class TestSelCol {
    private static SelectiveCollectiveTest alg;
    
    /* Get specs */
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
    
    /* Print the queue */
    private static void printQueue(ElevatorInterface e) {
        LinkedList<ElevatorQueueObject> iQ = e.getQueue();
        System.out.println("Queue is: ");
        for (int j = 0; j < iQ.size(); j++) {
            System.out.print(" " + alg.getUpperFloor(iQ.get(j)));
            if(iQ.get(j).getActionType() == ElevatorAction.PICKUP) {
                System.out.print("P");
            } else {
                System.out.print("D");
            }
        }
    }
    
    /* Main */
    public static void main (String[] args) {
        //Generate some calls 
     /* 
        Call c1 = new Call(0, 0, 5);
        Call c2 = new Call(0, 44, 3);
        Call c3 = new Call(0, 7, 1);
        Call c4 = new Call(0, 30, 3);
		Call c5 = new Call(0, 3, 8);
        Call c6 = new Call(0, 2, 1);
		Call c7 = new Call(0, 0, 11);
        Call c8 = new Call(0, 8, 9);
        Call c9 = new Call(0, 48, 49);
        Call c10 = new Call(0, 0, 11);
        Call c11 = new Call(0, 0, 11);
        Call c12 = new Call(1, 6, 3);
        Call c13 = new Call(1,33,7);
        Call c14 = new Call(0, 0, 11);
        Call c15 = new Call(0, 11, 22);
	  /*
        Call c1 = new Call(0, 7, 1);
        Call c2 = new Call(0, 3, 4);
        Call c3 = new Call(0, 5, 3);    */
        //Load specs
        Call c2 = new Call(0, 80, 81);
        Call c3 = new Call (0, 81, 77);
        Call c4 = new Call(0, 87, 50);
        ElevatorSpecs specs;
		try {
			specs = getSpecs();
		} catch (Exception e) {
			specs = null;
		}
        
        Call[] calls = {c2, c3, c4};
        
        //Convert calls to passengers
        LinkedList<Passenger> passengers = new LinkedList<Passenger>();
        for(Call c : calls) {
            passengers.add(new Passenger(c, specs));
        }
        
        //Specify elevator range
        int[] floors = new int[specs.getFloors() -specs.getSkylobbyfloor()];
        for(int i = 0; i < floors.length; i++) {
        	floors[i] = i + specs.getSkylobbyfloor();
        }
        
        //Create the elevator
        ElevatorInterface e = new Elevator(specs, floors, 50);
        //Create the algorithm object
        alg = new SelectiveCollectiveTest(specs);
        
        //Do the testing
        for (Passenger p : passengers) {
            int pick = alg.getPickUpPoint(e, p, p.getOrigin(), CarPosition.NULL);
            int drop = alg.getDropOffPoint(e, p, p.getDestination(), CarPosition.NULL, pick);
            if (!e.addToQueue(p, pick, drop, CarPosition.NULL)) {
                System.out.println("ERROR");
            }
            printQueue(e);
            System.out.print("\nPick was: " + pick + ", Drop was: " + drop + "\n");  
        }
    }
}