import javafx.scene.paint.Color;

import java.util.Collection;

public class Route extends SimulationObject {

    private Collection<Stop> stops;
    private Color c;

    public Route(String name, int id, Collection<Stop> stops, Color c) {
        super(name, id);
        this.stops = stops;
        this.c = c;
    }

    public void setStops(Collection<Stop> stops) { this.stops = stops; }

    public Collection<Stop> getStops() {
        return stops;
    }

    public Color getColor() {
        return c;
    }
}
