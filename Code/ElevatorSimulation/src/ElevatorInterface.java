
/**
 * Possible future interfece for the elevator classes.
 */
public interface ElevatorInterface {
	/** 
	 * Rreturn a list of passengers that disemabrks on the current floor.
	 */
	public Passenger[] disembarkElevator();

	/**
	 *
	 */
	public Passenger[] embarkElevator();
}
