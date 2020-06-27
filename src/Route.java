public class Route extends SimulationObject {

    private Stop[] stops;

    public Route(String name, int id, Stop[] stops) {
        super(name, id);
        this.stops = stops;
    }
}
