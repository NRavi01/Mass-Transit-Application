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

    private void executeUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        statement.closeOnCompletion();
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        statement.closeOnCompletion();
        return resultSet;
    }

    public void clear() throws SQLException {
        executeUpdate("DROP TABLE IF EXISTS bus");
        executeUpdate("CREATE TABLE bus (id INTEGER PRIMARY KEY, route INTEGER, currentStop INTEGER, latitude REAL, longitude REAL, passengers INTEGER, passengerCapacity INTEGER, fuel real, fuelCapacity REAL, speed REAL)");
        executeUpdate("DROP TABLE IF EXISTS route");
        executeUpdate("CREATE TABLE route (id INTEGER PRIMARY KEY, number INTEGER, name STRING)");
        executeUpdate("DROP TABLE IF EXISTS routeToStop");
        executeUpdate("CREATE TABLE routeToStop (routeId INTEGER, stopId INTEGER, stopIndex INTEGER)");
        executeUpdate("DROP TABLE IF EXISTS stop");
        executeUpdate("CREATE TABLE stop (id INTEGER PRIMARY KEY, name STRING, riders INTEGER, previousRiders INTEGER, latitude REAL, longitude REAL)");
        executeUpdate("DROP TABLE IF EXISTS event");
        executeUpdate("CREATE TABLE event (id INTEGER, time INTEGER, type STRING NOT NULL)");
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
    public Bus getBus(int id) throws SQLException {
        Bus bus = null;
        ResultSet resultSet = executeQuery("SELECT * FROM bus WHERE id=" + id);
        if (resultSet.next()) {
            bus = getBus(resultSet);
        }
        return bus;
    }

    private Bus getBus(ResultSet resultSet) throws SQLException {
        return new Bus(
                Integer.toString(resultSet.getInt("id")),
                resultSet.getInt("id"),
                resultSet.getInt("passengers"),
                resultSet.getDouble("speed"),
                getRoute(resultSet.getInt("route")),
                getStop(resultSet.getInt("currentStop")),
                getRoute(resultSet.getInt("route")).getNextStop(getStop(resultSet.getInt("currentStop"))),
                new Point((int) resultSet.getDouble("latitude"), (int) resultSet.getDouble("longitude")),
                (int) resultSet.getDouble("fuel"),
                (int) resultSet.getDouble("fuelCapacity")
        );
    }


    @Override
    public Route getRoute(int id) throws SQLException {
        Route route = null;
        ResultSet resultSet = executeQuery("SELECT * FROM route WHERE id=" + id);
        if (resultSet.next()) {
            route = getRoute(resultSet);
        }
        return route;
    }

    private Route getRoute(ResultSet resultSet) throws SQLException {
        return new Route(
                resultSet.getString("name"),
                resultSet.getInt("id"),
                (ArrayList<Stop>) getAllStops(resultSet.getInt("id")),
                Color.BLACK
        );
    }

    @Override
    public Stop getStop(int id) throws SQLException {
        Stop stop = null;
        ResultSet resultSet = executeQuery("SELECT * FROM stop WHERE id=" + id);
        if (resultSet.next()) {
            stop = getStop(resultSet);
        }
        return stop;
    }

    private Stop getStop(ResultSet resultSet) throws SQLException {
        return new Stop (
                resultSet.getString("name"),
                resultSet.getInt("id"),
                resultSet.getInt("riders"),
                new Point((int) resultSet.getDouble("latitude"), (int) resultSet.getDouble("longitude")),
                null
        );
    }

    @Override
    public Collection<Bus> getAllBuses() throws SQLException {
        List<Bus> buses = new ArrayList<>();
        ResultSet rs = executeQuery("SELECT * FROM bus");
        while (rs.next()) {
            buses.add(getBus(rs));
        }
        return buses;
    }

    @Override
    public Collection<Bus> getAllBuses(int routeId) throws SQLException {
        List<Bus> buses = new ArrayList<>();
        ResultSet rs = executeQuery("SELECT * FROM bus WHERE route=" + routeId);
        while (rs.next()) {
            buses.add(getBus(rs));
        }
        return buses;
    }


    @Override
    public Collection<Route> getAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM route");
        while (resultSet.next()) {
            routes.add(getRoute(resultSet));
        }
        return routes;
    }

    @Override
    public Collection<Stop> getAllStops() throws SQLException {
        List<Stop> stops = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM stop");
        while (resultSet.next()) {
            stops.add(getStop(resultSet));
        }
        return stops;
    }

    @Override
    public List<Stop> getAllStops(int routeId) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        ResultSet resultSet = executeQuery(
                "SELECT stopId FROM routeToStop WHERE routeId=" + routeId + " ORDER BY stopIndex"
        );
        while (resultSet.next()) {
            stops.add(getStop(resultSet.getInt("stopId")));
        }
        return stops;
    }

    @Override
    public void removeBus(Bus bus) throws SQLException {
        executeUpdate("DELETE FROM bus WHERE id=" + bus.getID());
    }


    @Override
    public void removeRoute(Route route) throws SQLException {
        executeUpdate("DELETE FROM route WHERE id=" + route.getID());
        executeUpdate("DELETE FROM routeToStop WHERE routeId=" + route.getID());
    }

    @Override
    public void removeFromRoute(Route route, Stop stop) throws SQLException {
        route.getStops().remove(stop);
        removeFromRoute(route.getID(), stop.getID());
    }

    @Override
    public void removeFromRoute(int routeId, int stopId) throws SQLException {
        executeUpdate(String.format("DELETE FROM routeToStop WHERE routeId=%d AND stopId=%d", routeId, stopId));
        executeUpdate(String.format(
                "UPDATE routeToStop SET stopIndex = stopIndex + 1 WHERE routeId=%d AND stopId>%d",
                routeId,
                stopId
        ));
    }

    @Override
    public void removeStop(Stop stop) throws SQLException {
        removeStop(stop.getID());
    }

    @Override
    public void removeStop(int stopId) throws SQLException {
        executeUpdate(String.format("DELETE FROM routeToStop WHERE stopId=%d", stopId));
        executeUpdate(String.format("UPDATE routeToStop SET stopIndex = stopIndex + 1 WHERE stopId>%d", stopId));
        executeUpdate("DELETE FROM stop WHERE id=" + stopId);
    }
}
