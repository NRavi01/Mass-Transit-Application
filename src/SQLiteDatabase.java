import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLiteDatabase implements Database {

    Connection connection;

    public SQLiteDatabase() throws SQLException {
        this("MartaSimulation.db");
    }

    public SQLiteDatabase(File file) throws SQLException {
        this(file.getAbsolutePath());
    }

    private SQLiteDatabase(String databasePath) throws SQLException {
        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databasePath));
    }

    public void executeUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        statement.closeOnCompletion();
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        //statement.closeOnCompletion();
        return resultSet;
    }

    public void clear() throws SQLException {
        executeUpdate("DROP TABLE IF EXISTS bus");
        executeUpdate("CREATE TABLE bus (id INTEGER PRIMARY KEY, route STRING, outbound INTEGER, currentStop INTEGER, latitude REAL, longitude REAL, passengers INTEGER, passengerCapacity INTEGER, fuel real, fuelCapacity REAL, speed REAL)");
        executeUpdate("DROP TABLE IF EXISTS route");
        executeUpdate("CREATE TABLE route (id STRING PRIMARY KEY, shortName STRING, name STRING)");
        executeUpdate("DROP TABLE IF EXISTS routeToStop");
        executeUpdate("CREATE TABLE routeToStop (routeId STRING, stopId STRING, stopIndex INTEGER)");
        executeUpdate("DROP TABLE IF EXISTS stop");
        executeUpdate("CREATE TABLE stop (id STRING PRIMARY KEY, name STRING, riders INTEGER, previousRiders INTEGER, latitude REAL, longitude REAL)");
        executeUpdate("DROP TABLE IF EXISTS event");
        executeUpdate("CREATE TABLE event (busId STRING, stopId STRING, arrivalTime INTEGER, departureTime INTEGER)");
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void addBus(Bus bus) throws SQLException {
        executeUpdate("INSERT INTO bus values" + bus);
    }


    @Override
    public void addRoute(Route route) throws SQLException {
        executeUpdate("INSERT INTO route values" + route);
    }

    @Override
    public void addStop(Stop stop) throws SQLException {
        executeUpdate("INSERT INTO stop values" + stop);
    }

    @Override
    public void updateBus(Bus bus) throws SQLException {
        executeUpdate(String.format("UPDATE bus SET route='%s', outbound=%d, currentStop=%d, latitude=%f, longitude=%f, passengers=%d, passengerCapacity=%d, fuel=%f, fuelCapacity=%f, speed=%f WHERE id='%s'",
                bus.getRoute().getID(), bus.getOutboundAsInt(), bus.getCurrentStopIndex(), bus.getLocation().getX(), bus.getLocation().getY(), bus.getNumPassengers(), bus.getPassengerCapacity(), bus.getFuel(), bus.getFuelCapacity(), bus.getSpeed(), bus.getID()
        ));
    }


    @Override
    public void updateRoute(Route route) throws SQLException {
        executeUpdate((String.format("UPDATE route SET shortName='%s', name='%s' WHERE id='%s'",
                route.getName(), route.getName(), route.getID())));
    }

    @Override
    public void extendRoute(Route route, Stop stop) throws SQLException {
        executeUpdate(String.format("INSERT INTO routeToStop values ('%s', '%s', %d)", route.getID(), stop.getID(), route.getStops().size()));
        route.extend(stop);
    }

    @Override
    public void updateStop(Stop stop) throws SQLException {
        executeUpdate((String.format("UPDATE stop SET name='%s', riders=%d, previousRiders=%d, latitude=%f, " +
                        "longitude=%f WHERE id='%s'",
                stop.getName(), stop.getNumPassengers(), stop.getNumPassengers(), stop.getLocation().getX(), stop.getLocation().getY(), stop.getID())));
    }

    @Override
    public Bus getBus(String id) throws SQLException {
        Bus bus = null;
        ResultSet resultSet = executeQuery("SELECT * FROM bus WHERE id='" + id + '\'');
        if (resultSet.next()) {
            bus = getBus(resultSet);
        }
        return bus;
    }

    private Bus getBus(ResultSet resultSet) throws SQLException {
        double x = resultSet.getDouble("latitude");
        double y = resultSet.getDouble("longitude");
        double newx = Math.abs(34 - x) * 50000 - 9000;
        double newy = Math.abs(-84 - y) * 50000 - 16000;
        return new Bus(
                resultSet.getString("id"),
                getRoute(resultSet.getString("route")),
                resultSet.getInt("outbound") == 0,
                resultSet.getInt("currentStop"),
                newx,
                newy,
                (int) (Math.random() * 20),
                resultSet.getInt("passengerCapacity"),
                resultSet.getDouble("fuel"),
                resultSet.getDouble("fuelCapacity"),
                resultSet.getDouble("speed")
        );
    }


    @Override
    public Route getRoute(String id) throws SQLException {
        Route route = null;
        ResultSet resultSet = executeQuery("SELECT * FROM route WHERE id='" + id + '\'');
        if (resultSet.next()) {
            route = getRoute(resultSet);
        }
        return route;
    }

    private Route getRoute(ResultSet resultSet) throws SQLException {
        return new Route(
                resultSet.getString("name"),
                resultSet.getInt("id"),
                (ArrayList<Stop>) getAllStops(Integer.toString(resultSet.getInt("id"))),
                Color.BLACK
        );
    }

    @Override
    public Stop getStop(String id) throws SQLException {
        Stop stop = null;
        ResultSet resultSet = executeQuery("SELECT * FROM stop WHERE id='" + id + '\'');
        if (resultSet.next()) {
            stop = getStop(resultSet);
        }
        return stop;
    }

    private Stop getStop(ResultSet resultSet) throws SQLException {
        //System.out.println(resultSet.getDouble("latitude") + " " + resultSet.getDouble("longitude"));
        double x = resultSet.getDouble("latitude");
        double y = resultSet.getDouble("longitude");
        double newx = Math.abs(34 - x) * 50000 - 9000;
        double newy = Math.abs(-84 - y) * 50000 - 16000;
        return new Stop (
                resultSet.getString("name"),
                resultSet.getInt("id"),
                (int) (Math.random() * 20),
                new Point((int) newx, (int) newy),
                null
        );
    }

    @Override
    public Collection<Bus> getAllBuses() throws SQLException {
        List<Bus> buses = new ArrayList<>();
        ResultSet rs = executeQuery("SELECT * FROM bus");

        int counter = 0;
        while (rs.next() && counter < 10) {
            buses.add(getBus(rs));
            counter++;
        }
        return buses;
    }

    @Override
    public Collection<Bus> getAllBuses(String routeId) throws SQLException {
        List<Bus> buses = new ArrayList<>();
        ResultSet rs = executeQuery("SELECT * FROM bus WHERE route=" + routeId);
        int counter = 0;
        while (rs.next()) {
            buses.add(getBus(rs));
            counter++;
        }
        return buses;
    }


    @Override
    public Collection<Route> getAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM route");
        int counter = 0;
        while (resultSet.next() && counter < 15) {
            routes.add(getRoute(resultSet));
            counter++;
        }
        return routes;
    }

    @Override
    public Collection<Stop> getAllStops() throws SQLException {
        List<Stop> stops = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM stop");
        int counter = 0;
        while (resultSet.next() && counter < 10) {
            stops.add(getStop(resultSet));
            counter++;
        }
        return stops;
    }

    @Override
    public List<Stop> getAllStops(String routeId) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        ResultSet resultSet = executeQuery(String.format(
                "SELECT stopId FROM routeToStop WHERE routeId='%s' ORDER BY stopIndex",
                routeId
        ));
        while (resultSet.next()) {
            stops.add(getStop(resultSet.getString("stopId")));
        }
        return stops;
    }

    @Override
    public void removeBus(Bus bus) throws SQLException {
        executeUpdate("DELETE FROM bus WHERE id='" + bus.getID() + '\'');
    }

    @Override
    public void removeRoute(Route route) throws SQLException {
        executeUpdate("DELETE FROM route WHERE id='" + route.getID() + '\'');
        executeUpdate("DELETE FROM routeToStop WHERE routeId='" + route.getID() + '\'');
    }

    @Override
    public void removeFromRoute(Route route, Stop stop) throws SQLException {
        route.getStops().remove(stop);
        removeFromRoute(Integer.toString(route.getID()), Integer.toString(stop.getID()));
    }

    @Override
    public void removeFromRoute(String routeId, String stopId) throws SQLException {
        ResultSet resultSet = executeQuery(String.format(
                "SELECT stopIndex FROM routeToStop WHERE routeId='%s' AND stopId='%s'",
                routeId,
                stopId
        ));
        resultSet.next();
        int stopIndex = resultSet.getInt("stopIndex");
        executeUpdate(String.format("DELETE FROM routeToStop WHERE routeId='%s' AND stopId='%s'", routeId, stopId));
        executeUpdate(String.format(
                "UPDATE routeToStop SET stopIndex = stopIndex - 1 WHERE routeId='%s' AND stopIndex>%d",
                routeId,
                stopIndex
        ));
    }

    @Override
    public void removeStop(Stop stop) throws SQLException {
        removeStop(Integer.toString(stop.getID()));
    }

    @Override
    public void removeStop(String stopId) throws SQLException {
        ResultSet resultSet = executeQuery(String.format(
                "SELECT routeId FROM routeToStop WHERE stopId='%s'",
                stopId
        ));
        while (resultSet.next()) {
            removeFromRoute(resultSet.getString("routeId"), stopId);
        }
        executeUpdate("DELETE FROM stop WHERE id='" + stopId + '\'');
    }
}
