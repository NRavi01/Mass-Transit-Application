import java.awt.*;
import java.util.ArrayList;

public class Map {
    private ArrayList<Bus> buses = new ArrayList<>();
    private ArrayList<Route> routes = new ArrayList<>();
    private ArrayList<Stop> stops = new ArrayList<>();
    private int width;
    private int height;
    private Color c;

    public Map(int width, int height, Color c, ArrayList<Bus> buses, ArrayList<Route> routes, ArrayList<Stop> stops) {
        this.width = width;
        this.height = height;
        this.c = c;
        this.buses = buses;
        this.routes = routes;
        this.stops = stops;
    }

    public ArrayList<Bus> getBuses() {
        return buses;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }

    public void addRoute(Route route) {
        routes.add(route);
    }

    public void addStop(Stop stop) {
        stops.add(stop);
    }
}
