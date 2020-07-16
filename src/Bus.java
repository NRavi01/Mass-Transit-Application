import java.awt.*;

public class Bus extends SimulationObject {

    private int numPassengers;
    private double avgSpeed;
    private Route route;
    private int currentStop;
    private Stop currStop;
    private Stop nextStop;
    private Point location;
    private Point screenLocation;
    private int currFuel;
    private int fuelCapacity;
    private int passengerCapacity;
    boolean outbound;

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
    public Bus(String id, Route route, boolean outbound, int currentStop, double latitude, double longitude, int passengers,
               int passengerCapacity, double fuel, double fuelCapacity, double speed) {
        super("Bus", Integer.valueOf(id));
        this.numPassengers = numPassengers;
        this.avgSpeed = avgSpeed;
        this.outbound = outbound;
        this.route = route;
        this.currentStop = currentStop;
        this.nextStop = route.getStops().get(currentStop + 1);
        this.location = new Point((int) latitude, (int) longitude);
        this.screenLocation = location;
        this.currFuel = (int) fuel;
        this.fuelCapacity = (int) fuelCapacity;
        this.passengerCapacity = passengerCapacity;
    }

    public boolean isOutbound() {
        return outbound;
    }

    public int getOutboundAsInt() {
        return outbound ? 0 : 1;
    }

    public void setOutbound(boolean outbound) {
        this.outbound = outbound;
    }

    public void setOutbound(int outbound) {
        this.outbound = outbound == 0;
    }

    public int getCurrentStopIndex() {
        return currentStop;
    }

    public void setCurrentStopIndex(int currentStop) {
        if (currentStop < -1) {
            throw new RuntimeException("can't set current stop to any value less than -1");
        } else if (currentStop != -1) {
            if (route == null) {
                throw new RuntimeException("can't set current stop to value other than -1 when the bus's route is null");
            } else if (currentStop >= route.getStops().size()) {
                throw new IndexOutOfBoundsException("can't set current stop to value greater than or equal to length of stops on bus's route");
            }
        }
        this.currentStop = currentStop;
    }

    /**
     * @return the stop the bus is at currently or the last stop the bus visited
     */
    public Stop getCurrentStop() {
        if (currentStop == -1 || route == null) {
            return null;
        }
        return route.getStops().get(currentStop);
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

    public double getFuel() {
        return currFuel;
    }

    public double getSpeed() {
        return avgSpeed;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
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

    @Override
    public String toString() {
        return String.format(
                "('%s', '%s', %d, %d, %f, %f, %d, %d, %d, %d, %f)",
                getID(),
                (route == null ? "null" : route.getID()),
                outbound ? 0 : 1,
                currentStop,
                location.getX(),
                location.getY(),
                numPassengers,
                passengerCapacity,
                currFuel,
                fuelCapacity,
                avgSpeed
        );
    }

}
