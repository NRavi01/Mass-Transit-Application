import java.awt.*;

public class FuelStation {
    private Point location;
    private Point screenLocation;

    public FuelStation(Point location) {
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
