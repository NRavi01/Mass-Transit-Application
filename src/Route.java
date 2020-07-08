import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class Route extends SimulationObject {

    private ArrayList<Stop> stops;
    private Color c;

    public Route(String name, int id, ArrayList<Stop> stops, Color c) {
        super(name, id);
        this.stops = stops;
        this.c = c;
    }

    public void setStops(ArrayList<Stop> stops) { this.stops = stops; }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public Color getColor() {
        return c;
    }

    public Stop getNextStop(Stop stop) {
        int index = stops.indexOf(stop);
        if (index == stops.size() - 1) {
            return stops.get(0);
        }
        else {
            return stops.get(index + 1);
        }
    }

    public Stop getPrevStop(Stop stop) {
        int index = stops.indexOf(stop);
        if (index == 0) {
            return stops.get(stops.size() - 1);
        }
        else {
            return stops.get(index - 1);
        }
    }
}
