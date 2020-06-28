import java.awt.*;

public class Bus extends SimulationObject {

    private int numPassengers;
    private double avgSpeed;
    private Route route;
    private Stop currStop;
    private Stop nextStop;
    private Point location;

    public Bus(String name, int id, int numPassengers, double avgSpeed,
               Route route, Stop currStop, Stop nextStop, Point location) {
        super(name, id);
        this.numPassengers = numPassengers;
        this.avgSpeed = avgSpeed;
        this.route = route;
        this.currStop = currStop;
        this.nextStop = nextStop;
        this.location = location;
    }

    public void setLocation(Point p) {
        location = p;
    }

    public Point getLocation() {
        return location;
    }
}
