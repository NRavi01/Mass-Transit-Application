import java.awt.*;

public class Bus extends SimulationObject {

    private int numPassengers;
    private double avgSpeed;
    private Route route;
    private Stop currStop;
    private Stop nextStop;
    private Point location;
    private Point screenLocation;
    private int currFuel;
    private int fuelCapacity;

    public Bus(String name, int id, int numPassengers, double avgSpeed,
               Route route, Stop currStop, Stop nextStop, Point location, int currFuel, int fuelCapacity) {
        super(name, id);
        this.numPassengers = numPassengers;
        this.avgSpeed = avgSpeed;
        this.route = route;
        this.currStop = currStop;
        this.nextStop = nextStop;
        this.location = location;
        this.screenLocation = location;
        this.currFuel = currFuel;
        this.fuelCapacity = fuelCapacity;
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

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Stop getCurrStop() {
        return currStop;
    }

    public void setCurrStop(Stop currStop) {
        this.currStop = currStop;
    }

    public Stop getNextStop() {
        return nextStop;
    }

    public void setNextStop(Stop nextStop) {
        this.nextStop = nextStop;
    }

    public int getCurrFuel() {
        return currFuel;
    }

    public void setCurrFuel(int currFuel) {
        this.currFuel = currFuel;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public void changeNumPassengers(int amount) {
        numPassengers = numPassengers + amount;
        if (numPassengers < 0) {
            numPassengers = 0;
        }
    }

}
