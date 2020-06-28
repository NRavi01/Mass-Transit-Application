import javafx.scene.paint.Color;

public class Route extends SimulationObject {

    private Stop[] stops;
    private Color c;

    public Route(String name, int id, Stop[] stops, Color c) {
        super(name, id);
        this.stops = stops;
        this.c = c;
    }

    public Stop[] getStops() {
        return stops;
    }

    public Color getColor() {
        return c;
    }
}
