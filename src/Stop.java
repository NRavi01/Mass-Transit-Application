import java.awt.*;

public class Stop extends SimulationObject {

    private int numPassengers;
    private Point location;
    private Point screenLocation;
    private Route route;

    public Stop(String name, int id, int numPassengers, Point location, Route route) {
        super(name, id);
        this.numPassengers = numPassengers;
        this.location = location;
        this.screenLocation = location;
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }
    public void setLocation(Point p) {
        location = p;
    }

    public Point getLocation() {
        return location;
    }

    public void setScreenLocation(Point p) {
        screenLocation = p;
    }

    public Point getScreenLocation() {
        return screenLocation;
    }

    public int getNumPassengers() {
        return numPassengers;
    }

    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }
}
