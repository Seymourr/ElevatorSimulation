
import java.math.BigInteger;

/**
 * Status objects for elevators representing various service records.
 */
public class ElevatorServiceStatus {
    /* Fields */
    public final BigInteger waitingTime; //Total passenger waiting time
    public final BigInteger travelingTime; //Total passenger traveling time
    public final BigInteger travelingDistance; //Total passenger traveling distance
    public final BigInteger ridesServed; //Number of rides served

    /* Constructor */
    public ElevatorServiceStatus(BigInteger w, BigInteger t, BigInteger td, BigInteger rs) {
        waitingTime = w;
        travelingTime = t;
        travelingDistance = td;
        ridesServed = rs;
    }
    
    /* Converts this ElevatorServiceStatus to a nice little String */
    public String getStringRepresentation() {
        String s = "*********** SERVICE RECORDS ********** \n" +
            "Total waiting time: " + waitingTime.toString() + "\n" +
            "Total traveling time: " + travelingTime.toString() + "\n" +
            "Total distance traveled: " + travelingDistance.toString() + "\n" +
            "Number of Rides completed: " + ridesServed.toString();
        return s;
    }
}