
/**
 * Class used to represent the priority queue for elevators to
 * indicate in which order the elevator should pick up and drop
 * off specific passengers.
 */
public class ElevatorQueueObject {
    /* Fields */
    private Passenger p;
    private ElevatorAction action;
    private CarPosition car;
    
    /* Constructor */
    public ElevatorQueueObject(Passenger p, ElevatorAction a, CarPosition c) {
        this.p = p;
        action = a;
        car = c;
    }
    
    /**********  Getters  ********/
    
    public Passenger getPassenger() {
        return p;
    }
    
    public ElevatorAction getActionType() {
        return action;
    }
    
    public CarPosition getCarPosition() {
        return car;
    }
    
    /*********** Setters *********/
    
    public void setCarPosition(CarPosition c) {
        car = c;
    }
    
    public void setElevatorAction(ElevatorAction a) {
        action = a;
    }
}