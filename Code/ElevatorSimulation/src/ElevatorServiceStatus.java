
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
}