import java.awt.*;

public class Stop extends SimulationObject {

    private int numPassengers;
    private Point location;
    private Point screenLocation;

    public Stop(String name, int id, int numPassengers, Point location) {
        super(name, id);
        this.numPassengers = numPassengers;
        this.location = location;
        this.screenLocation = location;
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
}
