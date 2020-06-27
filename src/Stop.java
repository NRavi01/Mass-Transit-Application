public class Stop extends SimulationObject {

    private int numPassengers;
    private Route route;

    public Stop(String name, int id, int numPassengers, Route route) {
        super(name, id);
        this.numPassengers = numPassengers;
        this.route = route;
    }
}
