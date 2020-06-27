public class Bus extends SimulationObject {

    private int numPassengers;
    private double avgSpeed;
    private Route route;
    private Stop currStop;
    private Stop nextStop;

    public Bus(String name, int id, int numPassengers, double avgSpeed,
               Route route, Stop currStop, Stop nextStop) {
        super(name, id);
        this.numPassengers = numPassengers;
        this.avgSpeed = avgSpeed;
        this.route = route;
        this.currStop = currStop;
        this.nextStop = nextStop;
    }
}
