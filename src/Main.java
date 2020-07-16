import com.sun.rowset.internal.Row;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.zip.ZipFile;

public class Main extends Application{
    private int globalWidth = 1920;
    private int globalHeight = 1080;

    private ArrayList<Bus> buses;
    private ArrayList<Route> routes;
    private ArrayList<Stop> stops;

    private Database db;

    private ArrayList<FuelStation> fuelStations = new ArrayList<>();

    private int zoomLevel = 1;

    private final int ridersArriveHigh = 8;
    private final int ridersArriveLow = 2;
    private final int ridersOffHigh = 8;
    private final int ridersOffLow = 2;
    private final int ridersOnHigh = 8;
    private final int ridersOnLow = 2;
    private final int ridersDepartHigh = 8;
    private final int ridersDepartLow = 2;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(getHomeScene(primaryStage));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public Scene getHomeScene(Stage window) {
        ImageView marta_background = createImage("marta_background.png", 0, 0, globalWidth, globalHeight);

        Label prompt = createLabel("Select a start time", globalWidth / 40, globalHeight / 2 + 50, 35, Color.BLACK, 500);
        prompt.setFont(Font.font("Verdana", FontWeight.BOLD, 35));

        Label dateChoiceDesc = createLabel("Day: ", globalWidth / 50, globalHeight / 2 + 150, 35, Color.BLACK, 100);
        dateChoiceDesc.setPrefHeight(50);
        ChoiceBox<String> dateChoiceBox = new ChoiceBox<>();
        dateChoiceBox.getStyleClass().add("choiceBox");
        dateChoiceBox.getItems().addAll("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        dateChoiceBox.setValue("Sunday");
        dateChoiceBox.setLayoutX(globalWidth / 50 + 100);
        dateChoiceBox.setPrefHeight(50);
        dateChoiceBox.setPrefWidth(110);
        dateChoiceBox.setLayoutY(globalHeight / 2 + 150);

        Label timeChoiceDesc = createLabel("Hour:", globalWidth / 50, globalHeight / 2 + 250, 35, Color.BLACK, 100);
        timeChoiceDesc.setPrefHeight(50);

        ChoiceBox<String> hourChoiceBox = new ChoiceBox<>();
        hourChoiceBox.getStyleClass().add("choiceBox");
        hourChoiceBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        hourChoiceBox.setValue("12");
        hourChoiceBox.setLayoutX(globalWidth / 50 + 100);
        hourChoiceBox.setPrefHeight(50);
        hourChoiceBox.setPrefWidth(110);
        hourChoiceBox.setLayoutY(globalHeight / 2 + 250);

        Label colon = createLabel(":", globalWidth / 50 + 215, globalHeight / 2 + 250, 35, Color.BLACK, 10);
        timeChoiceDesc.setPrefHeight(50);

        ChoiceBox<String> minChoiceBox = new ChoiceBox<>();
        minChoiceBox.getStyleClass().add("choiceBox");
        minChoiceBox.getItems().addAll("00", "30");
        minChoiceBox.setValue("00");
        minChoiceBox.setLayoutX(globalWidth / 50 + 230);
        minChoiceBox.setPrefHeight(50);
        minChoiceBox.setPrefWidth(110);
        minChoiceBox.setLayoutY(globalHeight / 2 + 250);

        ChoiceBox<String> ampm = new ChoiceBox<>();
        ampm.getStyleClass().add("choiceBox");
        ampm.getItems().addAll("AM", "PM");
        ampm.setValue("AM");
        ampm.setLayoutX(globalWidth / 50 + 350);
        ampm.setPrefHeight(50);
        ampm.setPrefWidth(110);
        ampm.setLayoutY(globalHeight / 2 + 250);

        Button beginSim = createButton(globalWidth * 6 / 8, globalHeight * 1/4, 350, 50, Color.ORANGE, "BEGIN", 50);
        beginSim.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        beginSim.setOnMouseEntered(e -> beginSim.setTextFill(Color.RED));
        beginSim.setOnMouseExited(e -> beginSim.setTextFill(Color.ORANGE));

        Group dtGroup = new Group();
        dtGroup.getChildren().addAll(marta_background, prompt, dateChoiceDesc, dateChoiceBox,
                timeChoiceDesc, hourChoiceBox, colon, minChoiceBox, ampm, beginSim);
        Scene scene = new Scene(dtGroup, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        window.setScene(scene);

        beginSim.setOnAction(e -> {
            String day = dateChoiceBox.getValue();
            //ALL CORE SIM LOGIC AND DATABASE RETRIEVAL TEAM WORK HERE - leads to array of all simobjects
            try {
                ZipFile zip = new ZipFile("C:/Users/Nisha/IdeaProjects/MTS-UI/src/gtfs022118.zip");
                db = DatabaseFactory.createDatabaseFromGtfs(zip, day);
                generateData();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            window.setScene(getMainScreen(window));
        });

        return scene;
    }

    public void generateData() throws IOException, SQLException {
        buses = new ArrayList<>();
        routes = new ArrayList<>();
        stops = new ArrayList<>();

        int i = 0;
        for (Route route : db.getAllRoutes()) {
            if (i == 0) {
                route.setColor(Color.BLACK);
            }
            else if (i == 1) {
                route.setColor(Color.BLUE);
            } else if (i == 2) {
                route.setColor(Color.GREEN);
            } else if (i == 3) {
                route.setColor(Color.RED);
            } else if (i == 4) {
                route.setColor(Color.PINK);
            } else if (i == 5) {
                route.setColor(Color.SILVER);
            } else if (i == 6) {
                route.setColor(Color.YELLOW);
            } else {
                route.setColor(Color.ORANGE);
            }
            if (i == 7) {
                i = 0;
            } else {
                i++;
            }
            routes.add(route);
            Stop stop1 = route.getStops().get(0);
            Stop stop2 = route.getStops().get(1);
            double x = (stop1.getLocation().getX() + stop2.getLocation().getX()) / 2;
            double y = (stop1.getLocation().getY() + stop2.getLocation().getY()) / 2;
            Point p = new Point((int) x, (int) y);
            buses.add(new Bus("Bus " +  route.getID(), route.getID(),(int) (Math.random() * 20), 10, route, stop1, stop2, p, 100, 100 ));
        }
        for (Stop stop : db.getAllStops()) {
            stops.add(stop);
        }

        /*
        // GT Bus Routes
        ArrayList<Stop> blueStops = new ArrayList<>();
        Route blueRoute = new Route("Blue Route", 2, blueStops, Color.BLUE);
        Stop blueNav = new Stop("North Avenue (Blue)", 1, 20, new Point(4000, 4000), blueRoute);
        Stop blueBrown = new Stop("Brown Dorm", 2, 5, new Point(3800, 3700), blueRoute);
        Stop blueTechwood3rd = new Stop("Techwood Dr & 3rd St", 3, 5, new Point(3800, 3300), blueRoute);
        Stop blueFourthStreet = new Stop("4th Street Houses", 4, 5, new Point(3800, 3100), blueRoute);
        Stop blueTechwood5th = new Stop("Techwood & 5th SE Corner", 5, 2, new Point(3800, 2200), blueRoute);
        Stop blueRussChandler = new Stop("Russ Chandler Stadium", 6, 0, new Point(3000, 1900), blueRoute);
        Stop blueKlausWB = new Stop("Klaus Building WB", 7, 10, new Point(2500, 1500), blueRoute);
        Stop blueNanotech = new Stop("Nanotechnology", 8, 5, new Point(2000, 1200), blueRoute);
        Stop blueKendeda = new Stop("Kendeda Building", 9, 2, new Point(1700, 1150), blueRoute);
        Stop bluePaper = new Stop("Paper Tricentennial", 40, 1, new Point(300, 600), blueRoute);
        Stop blueWillage = new Stop("West Village", 11, 10, new Point(300, 1600), blueRoute);
        Stop blue8thSt = new Stop("8th St & Hemphill Ave", 12, 2, new Point(900, 1600), blueRoute);
        Stop blueCouchPark = new Stop("Couch Park", 41, 4, new Point(1200, 2200), blueRoute);
        Stop blueCRC = new Stop("CRC & Stamps Health", 42, 5, new Point(1250, 3000), blueRoute);
        Stop blueStuce = new Stop("Ferst Dr & Student Center", 43, 10, new Point(1900, 3300), blueRoute);
        Stop blueHUB = new Stop("HUB/Tech Parkway PATH", 44, 5, new Point(2500, 3550), blueRoute);
        Stop blueCherry = new Stop("Ferst Dr & Cherry St", 45, 3, new Point(2700, 3700), blueRoute);

        ArrayList<Stop> redStops = new ArrayList<>();
        Route redRoute = new Route("Red Route", 1, redStops, Color.RED);
        Stop redFitten = new Stop("Fitten Hall", 10, 5, new Point(350,2100), redRoute);
        Stop redWillage = new Stop("West Village", 11, 15, new Point(300, 1600), redRoute);
        Stop red8thSt = new Stop("8th St & Hemphill Ave", 12, 2, new Point(900, 1600), redRoute);
        Stop redFerstHemphill = new Stop("Ferst Dr & Hemphill Ave", 13, 3, new Point(1400, 1200), redRoute);
        Stop redCherryEmerson = new Stop("Cherry Emerson", 14, 13,  new Point(2200, 1300), redRoute);
        Stop redKlaus = new Stop("Klaus Building EB", 15, 10, new Point(2550, 1750), redRoute);
        Stop redFerstFowler = new Stop("Ferst Dr & Fowler", 16, 1, new Point(3100,2000), redRoute);
        Stop redTechwood5th = new Stop("Techwood & 5th SW Corner", 17, 7, new Point(3900,2200), redRoute);
        Stop redTechwood4th = new Stop("Techwood Dr & 4th St", 18, 2, new Point(3900, 3100), redRoute);
        Stop redTechwoodBobbyDodd = new Stop("Techwood Dr & Bobby Dodd Way", 19, 8, new Point(3900, 3300), redRoute);
        Stop redWardlaw = new Stop("Wardlaw Building", 20, 5, new Point(3900, 3700), redRoute);
        Stop redNav = new Stop("North Avenue (Red)", 21, 10, new Point(4000, 4100), redRoute);
        Stop redTechTower = new Stop("Tech Tower", 22, 4, new Point(2700, 3650), redRoute);
        Stop redHUB = new Stop("HUB/Weber Building", 23, 5, new Point(2500, 3500), redRoute);
        Stop redStuce = new Stop("Student Center", 24, 12, new Point(1900, 3250), redRoute);
        Stop redISYE = new Stop("ISyE & Instruction Center", 25, 3, new Point(1350, 3000), redRoute);

        ArrayList<Stop> trolleyStops = new ArrayList<>();
        Route techTrolley = new Route("Tech Trolley", 3, trolleyStops, Color.GOLD);
        Stop trolleyHUB = new Stop("Transit HUB", 26, 11, new Point(2450, 3300), techTrolley);
        Stop trolleyStuce = new Stop("Student Center", 24, 6, new Point(1900, 3250), techTrolley);
        Stop trolleyISYE = new Stop("ISyE & Instruction Center", 25, 5, new Point(1350, 3000), techTrolley);
        Stop trolleyFerstHemphill = new Stop("Ferst Dr & Hemphill Ave", 13, 5, new Point(1400, 1200), techTrolley);
        Stop trolleyCherryEmerson = new Stop("Cherry Emerson", 14, 13,  new Point(2200, 1300), redRoute);
        Stop trolleyKlausEB = new Stop("Klaus Building EB", 15, 7, new Point(2550, 1750), techTrolley);
        Stop trolleyFerstFowler = new Stop("Ferst Dr & Fowler", 16, 3, new Point(3100,2000), techTrolley);
        Stop trolleyFifthStBridgeEB = new Stop("5th Street Bridge EB", 27, 7, new Point(4000, 2100), techTrolley);
        Stop trolleyTechSquareEB = new Stop ("Technology Square EB", 28, 2, new Point(5500, 2100), techTrolley);
        Stop trolleyScheller = new Stop("College of Business", 29, 3, new Point(6000, 2100), techTrolley);
        Stop trolleyMedicine = new Stop("Academy of Medicine", 30, 1, new Point(6150, 1500), techTrolley);
        Stop trolleyMARTA = new Stop("MARTA Midtown Station", 31, 6, new Point(7000, 500), techTrolley);
        Stop trolleyTechSquareWB = new Stop ("Technology Square WB", 32, 7, new Point(5500, 2000), techTrolley);
        Stop trolleyFifthStBridgeWB = new Stop("5th Street Bridge WB", 33, 7, new Point(4200, 2000), techTrolley);
        Stop trolleyRussChandler = new Stop("Russ Chandler Stadium", 6, 2, new Point(3000, 1900), techTrolley);
        Stop trolleyKlausWB = new Stop("Klaus Building WB", 7, 10, new Point(2500, 1500), techTrolley);
        Stop trolleyNanotech = new Stop("Nanotechnology", 8, 7, new Point(2000, 1200), techTrolley);
        Stop trolleyKendeda = new Stop("Kendeda Building", 9, 3, new Point(1700, 1150), techTrolley);
        Stop trolleyCouchPark = new Stop("Couch Park", 41, 4, new Point(1200, 2200), techTrolley);
        Stop trolleyCRC = new Stop("CRC & Stamps Health", 42, 5, new Point(1250, 3000), techTrolley);
        Stop trolleyFerstStuce = new Stop("Ferst Dr & Student Center", 43, 10, new Point(2700, 3700), techTrolley);

        blueStops.add(blueNav);
        blueStops.add(blueBrown);
        blueStops.add(blueTechwood3rd);
        blueStops.add(blueFourthStreet);
        blueStops.add(blueTechwood5th);
        blueStops.add(blueRussChandler);
        blueStops.add(blueKlausWB);
        blueStops.add(blueNanotech);
        blueStops.add(blueKendeda);
        blueStops.add(bluePaper);
        blueStops.add(blueWillage);
        blueStops.add(blue8thSt);
        blueStops.add(blueCouchPark);
        blueStops.add(blueCRC);
        blueStops.add(blueStuce);
        blueStops.add(blueHUB);
        blueStops.add(blueCherry);

        redStops.add(redFitten);
        redStops.add(redWillage);
        redStops.add(red8thSt);
        redStops.add(redFerstHemphill);
        redStops.add(redCherryEmerson);
        redStops.add(redKlaus);
        redStops.add(redFerstFowler);
        redStops.add(redTechwood5th);
        redStops.add(redTechwood4th);
        redStops.add(redTechwoodBobbyDodd);
        redStops.add(redWardlaw);
        redStops.add(redNav);
        redStops.add(redTechTower);
        redStops.add(redHUB);
        redStops.add(redStuce);
        redStops.add(redISYE);

        trolleyStops.add(trolleyHUB);
        trolleyStops.add(trolleyStuce);
        trolleyStops.add(trolleyISYE);
        trolleyStops.add(trolleyFerstHemphill);
        trolleyStops.add(trolleyCherryEmerson);
        trolleyStops.add(trolleyKlausEB);
        trolleyStops.add(trolleyFerstFowler);
        trolleyStops.add(trolleyFifthStBridgeEB);
        trolleyStops.add(trolleyTechSquareEB);
        trolleyStops.add(trolleyScheller);
        trolleyStops.add(trolleyMedicine);
        trolleyStops.add(trolleyMARTA);
        trolleyStops.add(trolleyTechSquareWB);
        trolleyStops.add(trolleyFifthStBridgeWB);
        trolleyStops.add(trolleyRussChandler);
        trolleyStops.add(trolleyKlausWB);
        trolleyStops.add(trolleyNanotech);
        trolleyStops.add(trolleyKendeda);
        trolleyStops.add(trolleyCouchPark);
        trolleyStops.add(trolleyCRC);
        trolleyStops.add(trolleyFerstStuce);

        stops.add(blueNav);
        stops.add(blueBrown);
        stops.add(blueTechwood3rd);
        stops.add(blueFourthStreet);
        stops.add(blueTechwood5th);
        stops.add(blueRussChandler);
        stops.add(blueKlausWB);
        stops.add(blueNanotech);
        stops.add(blueKendeda);
        stops.add(bluePaper);
        stops.add(blueWillage);
        stops.add(blue8thSt);
        stops.add(blueCouchPark);
        stops.add(blueCRC);
        stops.add(blueStuce);
        stops.add(blueHUB);
        stops.add(blueCherry);
        stops.add(redFitten);
        stops.add(redWillage);
        stops.add(red8thSt);
        stops.add(redFerstHemphill);
        stops.add(redCherryEmerson);
        stops.add(redKlaus);
        stops.add(redFerstFowler);
        stops.add(redTechwood5th);
        stops.add(redTechwood4th);
        stops.add(redTechwoodBobbyDodd);
        stops.add(redWardlaw);
        stops.add(redNav);
        stops.add(redTechTower);
        stops.add(redHUB);
        stops.add(redStuce);
        stops.add(redISYE);
        stops.add(trolleyHUB);
        stops.add(trolleyStuce);
        stops.add(trolleyISYE);
        stops.add(trolleyFerstHemphill);
        stops.add(trolleyCherryEmerson);
        stops.add(trolleyKlausEB);
        stops.add(trolleyFerstFowler);
        stops.add(trolleyFifthStBridgeEB);
        stops.add(trolleyTechSquareEB);
        stops.add(trolleyScheller);
        stops.add(trolleyMedicine);
        stops.add(trolleyMARTA);
        stops.add(trolleyTechSquareWB);
        stops.add(trolleyFifthStBridgeWB);
        stops.add(trolleyRussChandler);
        stops.add(trolleyKlausWB);
        stops.add(trolleyNanotech);
        stops.add(trolleyKendeda);
        stops.add(trolleyCouchPark);
        stops.add(trolleyCRC);
        stops.add(trolleyFerstStuce);

        routes.add(blueRoute);
        routes.add(redRoute);
        routes.add(techTrolley);
        buses.add(new Bus("Blue Bus 1", 1, 10, 20.0, blueRoute, blueRoute.getStops().get(0), blueRoute.getStops().get(1), blueRoute.getStops().get(0).getLocation(), 100, 100));
        buses.add(new Bus("Red Bus 1", 2, 10, 20.0, redRoute, redRoute.getStops().get(0), redRoute.getStops().get(1), redRoute.getStops().get(0).getLocation(), 100, 100));
        buses.add(new Bus("Tech Trolley 1", 2, 10, 20.0, techTrolley, techTrolley.getStops().get(0), techTrolley.getStops().get(1), techTrolley.getStops().get(0).getLocation(), 100, 100));

        */

    }

    public Scene getMainScreen(Stage window) {
        int globalTemp = globalWidth * 3/4;

        ArrayList<ImageView> busImages = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
            //System.out.println(b.getScreenLocation().getX() + " woa " +  b.getScreenLocation().getY());
            ImageView newBus = createImage("bus_icon.PNG", (int) b.getScreenLocation().getX() - 40, (int) b.getScreenLocation().getY() - 25, 80, 50);
            busImages.add(newBus);
        }

        ArrayList<Circle> stopImages = new ArrayList<>();
        ArrayList<Line> routeLines = new ArrayList<>();
        ArrayList<Label> stopLabels = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            Route currRoute = routes.get(i);
            for (int j = 0; j < currRoute.getStops().size(); j++) {
                Stop stop = currRoute.getStops().get(j);
                //System.out.println(stop.getScreenLocation().getX() + " " +  stop.getScreenLocation().getY());
                Circle newStop = new Circle(stop.getScreenLocation().getX(), stop.getScreenLocation().getY(), 20 + zoomLevel);
                newStop.setFill(currRoute.getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getScreenLocation().getX() - 5, (int) stop.getScreenLocation().getY() - 10, 15, Color.BLACK, 50);
                stopLabels.add(stopLabel);

                Line stopLine = null;
                if (j != currRoute.getStops().size() - 1) {
                    Point stop1 = stop.getScreenLocation();
                    Point stop2 = currRoute.getStops().get(j + 1).getScreenLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                } else {
                    Point stop1 = stop.getScreenLocation();
                    Point stop2 = currRoute.getStops().get(0).getScreenLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                }
                routeLines.add(stopLine);
            }
        }

        ArrayList<ImageView> fuelImages = new ArrayList<>();
        for (int i = 0; i < fuelStations.size(); i++) {
            FuelStation station = fuelStations.get(i);
            ImageView newFuel = createImage("fuelStation.PNG", (int) station.getScreenLocation().getX() - 10, (int) station.getScreenLocation().getY() - 20, 20, 40);
            fuelImages.add(newFuel);
        }

        ArrayList<Line> mapLines = new ArrayList<>();
        int spacing = 100;
        int numLinesHor = globalHeight / spacing;
        int numLinesVert = globalTemp / spacing;
        for (int i = 0; i < numLinesHor;i ++) {
            Line stopLine = new Line(0, i * spacing, globalTemp, i * spacing);
            stopLine.setStrokeWidth(5);
            mapLines.add(stopLine);
        }
        for (int i = 0; i < numLinesVert + 1;i ++) {
            Line stopLine = new Line(i * spacing, 0, i * spacing, globalHeight);
            stopLine.setStrokeWidth(5);
            mapLines.add(stopLine);
        }

        Pane msGroup = new Pane();

        for (int i = 0; i < stopImages.size(); i++) {
            msGroup.getChildren().add(stopImages.get(i));
        }
        for (int i = 0; i < routeLines.size(); i++) {
            msGroup.getChildren().add(routeLines.get(i));
        }
        for (int i = 0; i < stopLabels.size(); i++) {
            msGroup.getChildren().add(stopLabels.get(i));
        }
        for (int i = 0; i < busImages.size(); i++) {
            msGroup.getChildren().add(busImages.get(i));
        }
        for (int i = 0; i < fuelImages.size(); i++) {
            msGroup.getChildren().add(fuelImages.get(i));
        }
        /*
        for (int i = 0; i < mapLines.size(); i++) {
            msGroup.getChildren().add(mapLines.get(i));
        }
         */

        Circle navigationCenter = new Circle(globalWidth * 3/4 + 200, (int) globalHeight * 54/64, 20, Color.BLACK);

        Button navigationRight = createButton(globalWidth * 3/4 + 190, (int) globalHeight * 54/64 - 60, 50, 40, Color.BLACK, "", 50);
        navigationRight.setGraphic(createImage("rightArrowEmpty.jpg", (int) navigationRight.getLayoutX(), (int) navigationRight.getLayoutY(), 100, 80));
        navigationRight.setOnMouseEntered(e -> navigationRight.setGraphic(createImage("rightArrowFill.jpg", (int) navigationRight.getLayoutX(), (int) navigationRight.getLayoutY(), 100, 80)));
        navigationRight.setOnMouseExited(e -> navigationRight.setGraphic(createImage("rightArrowEmpty.jpg", (int) navigationRight.getLayoutX(), (int) navigationRight.getLayoutY(), 100, 80)));

        Button navigationLeft = createButton(globalWidth * 3/4 + 40, (int) globalHeight * 54/64 - 60, 50, 40, Color.BLACK, "", 50);
        navigationLeft.setGraphic(createImage("leftArrowEmpty.jpg", (int) navigationLeft.getLayoutX(), (int) navigationLeft.getLayoutY(), 100, 80));
        navigationLeft.setOnMouseEntered(e -> navigationLeft.setGraphic(createImage("leftArrowFill.jpg", (int) navigationLeft.getLayoutX(), (int) navigationRight.getLayoutY(), 100, 80)));
        navigationLeft.setOnMouseExited(e -> navigationLeft.setGraphic(createImage("leftArrowEmpty.jpg", (int) navigationLeft.getLayoutX(), (int) navigationRight.getLayoutY(), 100, 80)));

        Button navigationUp = createButton(globalWidth * 3/4 + 125, (int) globalHeight * 54/64 - 140, 50, 40, Color.BLACK, "", 50);
        navigationUp.setGraphic(createImage("upArrowEmpty.jpg", (int) navigationUp.getLayoutX(), (int) navigationUp.getLayoutY(), 80, 100));
        navigationUp.setOnMouseEntered(e -> navigationUp.setGraphic(createImage("upArrowFill.jpg", (int) navigationUp.getLayoutX(), (int) navigationUp.getLayoutY(), 80, 100)));
        navigationUp.setOnMouseExited(e -> navigationUp.setGraphic(createImage("upArrowEmpty.jpg", (int) navigationUp.getLayoutX(), (int) navigationUp.getLayoutY(), 80, 100)));

        Button navigationDown = createButton(globalWidth * 3/4 + 125, (int) globalHeight * 54/64 + 10, 50, 40, Color.BLACK, "", 50);
        navigationDown.setGraphic(createImage("downArrowEmpty.jpg", (int) navigationDown.getLayoutX(), (int) navigationDown.getLayoutY(), 80, 100));
        navigationDown.setOnMouseEntered(e -> navigationDown.setGraphic(createImage("downArrowFill.jpg", (int) navigationDown.getLayoutX(), (int) navigationDown.getLayoutY(), 80, 100)));
        navigationDown.setOnMouseExited(e -> navigationDown.setGraphic(createImage("downArrowEmpty.jpg", (int) navigationDown.getLayoutX(), (int) navigationDown.getLayoutY(), 80, 100)));

        navigationRight.setOnAction(e -> {
            moveAll(-50, 0);
            window.setScene(getMainScreen(window));
        });

        navigationLeft.setOnAction(e -> {
            moveAll(50, 0);
            window.setScene(getMainScreen(window));
        });

        navigationUp.setOnAction(e -> {
            moveAll(0, 50);
            window.setScene(getMainScreen(window));
        });

        navigationDown.setOnAction(e -> {
            moveAll(0, -50);
            window.setScene(getMainScreen(window));
        });

        //listGroup with lines and buttons
        Line placeHolder = new Line(0, 0, 500, 0);
        placeHolder.setStrokeWidth(8);
        Line listLine1 = new Line(0, 0, 500, 0);
        listLine1.setStrokeWidth(8);
        Line listLine2 = new Line(0, 0, 500, 0);
        listLine2.setStrokeWidth(8);
        Line listLine3 = new Line(0, 0, 500, 0);
        listLine3.setStrokeWidth(8);
        Line listLine4 = new Line(0, 0, 500, 0);
        listLine4.setStrokeWidth(8);

        Line analysisHolder = new Line(0, 0, 0, 0);
        placeHolder.setStrokeWidth(0);

        Button analysisButton = createButton(50, 0, 150, 50, Color.BLACK, "Analysis", 30);
        analysisButton.setAlignment(Pos.CENTER);
        analysisButton.getStyleClass().add("simObjListButton");
        analysisButton.setOnAction(e -> {
            window.setScene(getDataAnalysisScreen(window));
        });

        ImageView modelAnalysis = createImage("analysis.png", 220, 10, 150, 60);

        Group analysisGroup = new Group();
        analysisGroup.getChildren().addAll(analysisHolder, analysisButton, modelAnalysis);

        Line busHolder = new Line(0, 0, 0, 0);
        placeHolder.setStrokeWidth(0);

        Button busList = createButton(50, 0, 150, 50, Color.BLACK, "Bus List", 30 );
        busList.setAlignment(Pos.CENTER);
        busList.getStyleClass().add("simObjListButton");
        busList.setOnAction(e -> {
            window.setScene(getListScene(window, "Bus"));
        });

        ImageView modelBus = createImage("bus_icon.PNG", 220, 10, 125, 50);

        Group modelBusGroup = new Group();
        modelBusGroup.getChildren().addAll(busHolder, busList, modelBus);

        Line routeHolder = new Line(0, 0, 0, 0);
        placeHolder.setStrokeWidth(0);

        Button routeList = createButton(25, 0, 220, 50, Color.BLACK, "Route List", 30);
        routeList.getStyleClass().add("simObjListButton");
        routeList.setOnAction(e -> {
            window.setScene(getListScene(window, "Route"));
        });

        ImageView modelRoute = createImage("modelRoute.png", 220, 10, 150, 60);

        Group modelRouteGroup = new Group();
        modelRouteGroup.getChildren().addAll(routeHolder, routeList, modelRoute);

        Button stopList = createButton(30, 0, 200, 50, Color.BLACK, "Stop List", 30);
        stopList.getStyleClass().add("simObjListButton");
        stopList.setOnAction(e -> {
            window.setScene(getListScene(window, "Stop"));
        });

        Line stopHolder = new Line(0, 0, 0, 0);
        stopHolder.setStrokeWidth(0);

        Circle modelStop = new Circle(290, 30, 20);
        modelStop.setFill(Color.BLUE);

        Group modelStopGroup = new Group();
        modelStopGroup.getChildren().addAll(stopHolder, stopList, modelStop);

        Label placeholder = createLabel("", globalWidth * 3/4, 30, 30, Color.BLACK, 20);
        Label zoom = createLabel("Zoom: ", globalWidth * 3/4 + 40, 30, 30, Color.BLACK, 100);
        Button zoomIn = createButton(globalWidth * 3/4 + 120, 0, 100, 100, Color.BLACK, "", 30);
        zoomIn.setGraphic(createImage("plusEmpty.jpg", globalWidth * 3/4 + 120, 0, 60, 60));
        zoomIn.setOnMouseEntered(e -> zoomIn.setGraphic(createImage("plusFill.jpg", (int) zoomIn.getLayoutX(), (int) zoomIn.getLayoutY(), 60, 60)));
        zoomIn.setOnMouseExited(e -> zoomIn.setGraphic(createImage("plusEmpty.jpg", (int) zoomIn.getLayoutX(), (int) zoomIn.getLayoutY(), 60, 60)));

        Button zoomOut = createButton(globalWidth * 3/4 + 200, 0, 100, 100, Color.BLACK, "", 30);
        zoomOut.setGraphic(createImage("minusEmpty.jpg", globalWidth * 3/4 + 250, 0, 60, 60));
        zoomOut.setOnMouseEntered(e -> zoomOut.setGraphic(createImage("minusFill.jpg", (int) zoomOut.getLayoutX(), (int) zoomOut.getLayoutY(), 60, 60)));
        zoomOut.setOnMouseExited(e -> zoomOut.setGraphic(createImage("minusEmpty.jpg", (int) zoomOut.getLayoutX(), (int) zoomOut.getLayoutY(), 60, 60)));

        zoomIn.setOnAction(e -> {
            scaleAll(1);
            zoomLevel++;
            window.setScene(getMainScreen(window));
        });

        zoomOut.setOnAction(e -> {
            scaleAll(-1);
            zoomLevel--;
            window.setScene(getMainScreen(window));
        });


        Label placeholder1 = createLabel("", globalWidth * 3/4, 30, 30, Color.BLACK, 20);
        Label simStep = createLabel("Step: ", globalWidth * 3/4 + 40, 30, 30, Color.BLACK, 100);

        Button stepBackward = createButton(globalWidth * 3/4 + 120, 0, 100, 100, Color.BLACK, "", 30);
        stepBackward.setGraphic(createImage("stepBackwardEmpty.png", globalWidth * 3/4 + 120, 0, 60, 60));
        stepBackward.setOnMouseEntered(e -> stepBackward.setGraphic(createImage("stepBackwardFill.png", (int) stepBackward.getLayoutX(), (int) stepBackward.getLayoutY(), 60, 60)));
        stepBackward.setOnMouseExited(e -> stepBackward.setGraphic(createImage("stepBackwardEmpty.png", (int) stepBackward.getLayoutX(), (int) stepBackward.getLayoutY(), 60, 60)));

        Button stepForward = createButton(globalWidth * 3/4 + 200, 0, 100, 100, Color.BLACK, "", 30);
        stepForward.setGraphic(createImage("stepForwardEmpty.png", globalWidth * 3/4 + 250, 0, 60, 60));
        stepForward.setOnMouseEntered(e -> stepForward.setGraphic(createImage("stepForwardFill.png", (int) stepForward.getLayoutX(), (int) stepForward.getLayoutY(), 60, 60)));
        stepForward.setOnMouseExited(e -> stepForward.setGraphic(createImage("stepForwardEmpty.png", (int) stepForward.getLayoutX(), (int) stepForward.getLayoutY(), 60, 60)));

        Button reset = createButton(globalWidth * 3/4 + 280, 0, 100, 100, Color.BLACK, "", 30);
        reset.setGraphic(createImage("resetEmpty.png", globalWidth * 3/4 + 250, 0, 70, 70));
        reset.setOnMouseEntered(e -> reset.setGraphic(createImage("resetFill.png", (int) reset.getLayoutX(), (int) reset.getLayoutY(), 70, 70)));
        reset.setOnMouseExited(e -> reset.setGraphic(createImage("resetEmpty.png", (int) reset.getLayoutX(), (int) reset.getLayoutY(), 70, 70)));


        stepForward.setOnAction(e -> {
            for (int i = 0; i < buses.size(); i++) {
                Bus b = buses.get(i);
                moveBus(b, 50);
            }
            window.setScene(getMainScreen(window));
        });

        stepBackward.setOnAction(e -> {
            for (int i = 0; i < buses.size(); i++) {
                Bus b = buses.get(i);
                moveBusBack(b, 50);
            }
            window.setScene(getMainScreen(window));
        });

        reset.setOnAction(e -> {
            zoomLevel = 1;
            try {
                generateData();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            window.setScene(getMainScreen(window));
        });

        VBox lists = new VBox(15);
        lists.getChildren().addAll(placeHolder, analysisGroup, listLine4, modelBusGroup, listLine1,
                modelRouteGroup, listLine2, modelStopGroup, listLine3);
        lists.setPrefSize(globalWidth * 1/4, 500);
        lists.setPadding(new Insets(0, 0, 0, 0));

        ImageView martaLogo = createImage("martaLogo.gif", 0, 0, globalWidth * 1/4, 170);

        Pane logoGroup = new Pane();
        logoGroup.getChildren().add(martaLogo);
        logoGroup.setPrefSize(globalWidth * 1/4, 200);

        Group navGroup = new Group();
        navGroup.getChildren().addAll(navigationCenter, navigationDown, navigationLeft, navigationRight, navigationUp);

        Group zoomGroup = new Group();
        zoomGroup.getChildren().addAll(placeholder, zoom, zoomIn, zoomOut);

        Group stepGroup = new Group();
        stepGroup.getChildren().addAll(placeholder1, simStep, stepForward, stepBackward, reset);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid");

        msGroup.setPrefSize(globalWidth * 3/4, globalHeight);
        msGroup.setClip(new Rectangle(msGroup.getPrefWidth(), msGroup.getPrefHeight()));
        grid.add(msGroup, 0, 0, 1, 1);
        grid.add(logoGroup, 1, 0, 1, 1);
        grid.add(lists, 1, 1, 1, 1);
        grid.add(stepGroup, 1, 2, 1, 1);
        grid.add(zoomGroup, 1, 3, 1, 1);
        grid.add(navGroup, 1, 4, 1, 1);

        ColumnConstraints column1 = new ColumnConstraints(globalWidth * 3 / 4);
        ColumnConstraints column2 = new ColumnConstraints(globalWidth * 1/4);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        Scene scene = new Scene(grid, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public void moveBus(Bus b, int distance) {
        double currentX = b.getScreenLocation().getX();
        double currentY = b.getScreenLocation().getY();
        double stopX = b.getNextStop().getScreenLocation().getX();
        double stopY = b.getNextStop().getScreenLocation().getY();
        double backStopX = b.getCurrStop().getScreenLocation().getX();
        double backStopY = b.getCurrStop().getScreenLocation().getY();
        double diffx, diffy = 0;
        if (distance > 0) {
            diffx = stopX - currentX;
            diffy = stopY - currentY;
        } else {
            diffx = backStopX - currentX;
            diffy = backStopY - currentY;
        }
        double distanceTillStop = Math.sqrt(diffx * diffx + diffy * diffy);
        double angle = Math.atan(diffy / diffx);
        int distancePerStep = (int) (distance * b.getAvgSpeed() / 10);
        if (distanceTillStop < Math.abs(distancePerStep)) {
            double changeScreenY = diffy * Math.pow(1.1, zoomLevel);
            double changeScreenX = diffx * Math.pow(1.1, zoomLevel);
            double currScreenX = b.getScreenLocation().getX();
            double currScreenY = b.getScreenLocation().getY();
            double newScreenX = currScreenX + changeScreenX;
            double newScreenY = currScreenY + changeScreenY;

            //Algorithm to exchange passengers at stops
            Stop currStop = b.getNextStop();

            //Step1
            int waitingGroup = currStop.getNumPassengers();
            int step1Arrive = ridersArriveLow + (int) (Math.random() * (ridersArriveHigh + 1));
            waitingGroup = waitingGroup + step1Arrive;

            //Step2
            int ridersOffBus = ridersOffLow + (int) (Math.random() * (ridersOffHigh + 1));
            b.changeNumPassengers(-ridersOffBus);
            int transferGroup = ridersOffBus;

            //Step3
            int ridersOnBus = ridersOnLow + (int) (Math.random() * (ridersOnHigh + 1));
            b.changeNumPassengers(ridersOnBus);
            waitingGroup = waitingGroup - ridersOnBus;

            //Step4
            int ridersDepart = ridersDepartLow + (int) (Math.random() * (ridersDepartHigh + 1));
            if (ridersDepart <= transferGroup) {
                transferGroup = transferGroup - ridersDepart;
                waitingGroup = waitingGroup + transferGroup;
            } else {
                int unhappyPass = ridersDepart - transferGroup;
                transferGroup = 0;
                waitingGroup = waitingGroup - unhappyPass;

            }
            currStop.setNumPassengers(waitingGroup);

            /*
            int randomPercentageBus = (int) (Math.random() * 50);
            int randomPercentageStop = (int) (Math.random() * 50);
            int amountOffBus = (int) (b.getNumPassengers() * (randomPercentageBus * .01));
            System.out.println(amountOffBus + " passengers getting off bus at stop " + currStop.getName());
            int amountOffStop = (int) (currStop.getNumPassengers() * (randomPercentageStop * .01));
            System.out.println(amountOffStop + " passengers getting on bus at stop " + currStop.getName());
            b.changeNumPassengers(-amountOffBus);
            b.changeNumPassengers(amountOffStop);
            currStop.changeNumPassengers(-amountOffStop);
            currStop.changeNumPassengers(amountOffBus);
             */

            if (distance > 0) {
                b.setScreenLocation(b.getNextStop().getScreenLocation());
                b.setLocation(b.getNextStop().getLocation());
                b.setCurrStop(b.getNextStop());
                b.setNextStop(b.getRoute().getNextStop(b.getNextStop()));
            } else {
                b.setScreenLocation(b.getCurrStop().getScreenLocation());
                b.setLocation(b.getCurrStop().getLocation());
                b.setNextStop(b.getCurrStop());
                b.setCurrStop(b.getRoute().getPrevStop(b.getCurrStop()));
            }
            /*
            System.out.println(b.getCurrStop().getScreenLocation());
            System.out.println(b.getNextStop().getScreenLocation());
            System.out.println(b.getCurrStop());
            System.out.println(b.getNextStop());
             */
        }
        else {
            double changeY = Math.abs(Math.sin(angle)) * distance * b.getAvgSpeed() / 10;
            double changeX = Math.abs(Math.cos(angle)) * distance * b.getAvgSpeed() / 10;
            if (diffx < 0) {
                changeX = -changeX;
            }
            if (diffy < 0) {
                changeY = -changeY;
            }
            double newX = currentX + changeX;
            double newY = currentY + changeY;
            b.setLocation(new Point((int) newX, (int) newY));
            double changeScreenY = changeY * Math.pow(1.1, zoomLevel);
            double changeScreenX = changeX * Math.pow(1.1, zoomLevel);
            double currScreenX = b.getScreenLocation().getX();
            double currScreenY = b.getScreenLocation().getY();
            double newScreenX = currScreenX + changeScreenX;
            double newScreenY = currScreenY + changeScreenY;
            b.setScreenLocation(new Point((int) newScreenX, (int) newScreenY));
        }
        b.setCurrFuel(b.getCurrFuel() - distancePerStep / 20);
    }

    public void moveBusBack(Bus b, int distance) {
        double currentX = b.getScreenLocation().getX();
        double currentY = b.getScreenLocation().getY();
        double stopX = b.getCurrStop().getScreenLocation().getX();
        double stopY = b.getCurrStop().getScreenLocation().getY();
        double diffx = stopX - currentX;
        double diffy = stopY - currentY;
        double distanceTillStop = Math.sqrt(diffx * diffx + diffy * diffy);
        double angle = Math.atan(diffy / diffx);
        int distancePerStep = (int) (distance * b.getAvgSpeed() / 10);
        if (distanceTillStop < Math.abs(distancePerStep)) {
            double changeScreenY = diffy * Math.pow(1.1, zoomLevel);
            double changeScreenX = diffx * Math.pow(1.1, zoomLevel);
            double currScreenX = b.getScreenLocation().getX();
            double currScreenY = b.getScreenLocation().getY();
            double newScreenX = currScreenX + changeScreenX;
            double newScreenY = currScreenY + changeScreenY;
            b.setScreenLocation(b.getCurrStop().getScreenLocation());
            b.setLocation(b.getCurrStop().getLocation());
            b.setNextStop(b.getCurrStop());
            b.setCurrStop(b.getRoute().getPrevStop(b.getCurrStop()));
        }
        else {
            double changeY = Math.abs(Math.sin(angle)) * distance * b.getAvgSpeed() / 10;
            double changeX = Math.abs(Math.cos(angle)) * distance * b.getAvgSpeed() / 10;
            if (diffx < 0) {
                changeX = -changeX;
            }
            if (diffy < 0) {
                changeY = -changeY;
            }
            double newX = currentX + changeX;
            double newY = currentY + changeY;
            b.setLocation(new Point((int) newX, (int) newY));
            double changeScreenY = changeY * Math.pow(1.1, zoomLevel);
            double changeScreenX = changeX * Math.pow(1.1, zoomLevel);
            double currScreenX = b.getScreenLocation().getX();
            double currScreenY = b.getScreenLocation().getY();
            double newScreenX = currScreenX + changeScreenX;
            double newScreenY = currScreenY + changeScreenY;
            b.setScreenLocation(new Point((int) newScreenX, (int) newScreenY));
        }

    }

    public void scaleAll(int direction) {
        double scalingFactor = 1.1;
        ArrayList<Bus> newBusList = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
            double newPointX;
            double newPointY;
            if (direction > 0) {
                newPointX = (b.getScreenLocation().getX() * scalingFactor);
                newPointY = b.getScreenLocation().getY() * scalingFactor;
            } else {
                newPointX = (b.getScreenLocation().getX() / scalingFactor);
                newPointY = b.getScreenLocation().getY() / scalingFactor;
            }
            b.setScreenLocation(new Point((int) newPointX, (int) newPointY));
            newBusList.add(b);
        }
        buses.removeAll(buses);
        for (int i = 0; i < newBusList.size(); i++) {
            buses.add(newBusList.get(i));
        }

        ArrayList<FuelStation> newFuelList = new ArrayList<>();
        for (int i = 0; i < fuelStations.size(); i++) {
            FuelStation station = fuelStations.get(i);
            double newPointX;
            double newPointY;
            if (direction > 0) {
                newPointX = (station.getScreenLocation().getX() * scalingFactor);
                newPointY = station.getScreenLocation().getY() * scalingFactor;
            } else {
                newPointX = (station.getScreenLocation().getX() / scalingFactor);
                newPointY = station.getScreenLocation().getY() / scalingFactor;
            }
            station.setScreenLocation(new Point((int) newPointX, (int) newPointY));
            newFuelList.add(station);
        }
        fuelStations.removeAll(fuelStations);
        for (int i = 0; i < newFuelList.size(); i++) {
            fuelStations.add(newFuelList.get(i));
        }

        ArrayList<Route> newRouteList = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            ArrayList<Stop> newStopList = new ArrayList<>();
            for (int j = 0; j < route.getStops().size(); j++) {
                Stop stop = route.getStops().get(j);
                double newPointX;
                double newPointY;
                if (direction > 0) {
                    newPointX = (stop.getScreenLocation().getX() * scalingFactor);
                    newPointY = stop.getScreenLocation().getY() * scalingFactor;
                } else {
                    newPointX = (stop.getScreenLocation().getX() / scalingFactor);
                    newPointY = stop.getScreenLocation().getY() / scalingFactor;
                }
                stop.setScreenLocation(new Point((int) newPointX, (int) newPointY));
                newStopList.add(stop);
            }
            route.setStops(newStopList);
            newRouteList.add(route);
        }
        routes.removeAll(routes);
        for (int i = 0; i < newRouteList.size(); i++) {
            routes.add(newRouteList.get(i));
        }
    }

    public void moveAll(int x, int y) {
        ArrayList<Bus> newBusList = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
            int newPointX = (int) b.getScreenLocation().getX() + x;
            int newPointY = (int) b.getScreenLocation().getY() + y;
            b.setScreenLocation(new Point(newPointX, newPointY));
            newBusList.add(b);
        }
        buses.removeAll(buses);
        for (int i = 0; i < newBusList.size(); i++) {
            buses.add(newBusList.get(i));
        }

        ArrayList<FuelStation> newFuelList = new ArrayList<>();
        for (int i = 0; i < fuelStations.size(); i++) {
            FuelStation station = fuelStations.get(i);
            int newPointX = (int) station.getScreenLocation().getX() + x;
            int newPointY = (int) station.getScreenLocation().getY() + y;
            station.setScreenLocation(new Point(newPointX, newPointY));
            newFuelList.add(station);
        }
        fuelStations.removeAll(fuelStations);
        for (int i = 0; i < newFuelList.size(); i++) {
            fuelStations.add(newFuelList.get(i));
        }

        ArrayList<Route> newRouteList = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            ArrayList<Stop> newStopList = new ArrayList<>();
            for (int j = 0; j < route.getStops().size(); j++) {
                Stop stop = route.getStops().get(j);
                int newPointX = (int) stop.getScreenLocation().getX() + x;
                int newPointY = (int) stop.getScreenLocation().getY() + y;
                stop.setScreenLocation(new Point(newPointX, newPointY));
                newStopList.add(stop);
            }
            route.setStops(newStopList);
            newRouteList.add(route);
        }
        routes.removeAll(routes);
        for (int i = 0; i < newRouteList.size(); i++) {
            routes.add(newRouteList.get(i));
        }
    }

    public Scene getListScene(Stage window, String type) {
        GridPane gridPane = new GridPane();

        Label title = createLabel(type + " List", 0, 0, 50, Color.BLACK, 400);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));
        gridPane.add(title, 0, 0, 2, 1);

        int i = 0;

        if (type.equals("Bus")) {
            for (int j = 0; j < buses.size(); j++) {
                Bus currBus = buses.get(j);
                Button busButton = createButton(0, 0, 600, 100, Color.BLACK, currBus.getName(), 30);
                busButton.getStyleClass().add("listButton");
                busButton.setOnAction(e -> {
                    window.setScene(getBusScene(currBus, window));
                });
                Button delete = createButton(0, 0,50, 50, Color.BLACK, "X", 30);
                delete.getStyleClass().add("deleteButton");
                delete.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
                delete.setOnAction(e -> {
                    buses.remove(currBus);
                    window.setScene(getListScene(window, "Bus"));
                });
                i++;
                gridPane.add(busButton, 0, i, 1, 1);
                gridPane.add(delete, 1, i, 1, 1);
            }
        } else if (type.equals("Route")) {
            for (int j = 0; j < routes.size(); j++) {
                Route currRoute = routes.get(j);
                Button routeButton = createButton(0, 0, 600,100,currRoute.getColor(), currRoute.getName(), 30);
                routeButton.getStyleClass().add("listButton");
                routeButton.setTextFill(currRoute.getColor());
                routeButton.setOnAction(e -> {
                    window.setScene(getRouteScene(currRoute, window));
                });
                Button delete = createButton(0, 0,50, 50, Color.BLACK, "X", 30);
                delete.getStyleClass().add("deleteButton");
                delete.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
                delete.setOnAction(e -> {
                    routes.remove(currRoute);
                    for (int k = 0; k < buses.size(); k++) {
                        if (buses.get(k).getRoute() == currRoute) {
                            buses.remove(k  );
                        }
                    }
                    window.setScene(getListScene(window, "Route"));
                });
                i++;
                gridPane.add(routeButton, 0, i, 1, 1);
                gridPane.add(delete, 1, i, 1, 1);
            }
        } else {
            for (int k = 0; k < routes.size(); k++) {
                for (int j = 0; j < routes.get(k).getStops().size(); j++) {
                    Stop currStop = routes.get(k).getStops().get(j);
                    Button stopButton = createButton(0, 0, 600, 100, Color.BLACK, currStop.getName(), 30);
                    stopButton.getStyleClass().add("listButton");
                    if (currStop.getRoute() != null) {
                        stopButton.setTextFill(currStop.getRoute().getColor());
                    }
                    stopButton.setOnAction(e -> {
                        window.setScene(getStopScene(currStop, window));
                    });
                    Button delete = createButton(0, 0, 50, 50, Color.BLACK, "X", 30);
                    delete.getStyleClass().add("deleteButton");
                    delete.getStyleClass().add("deleteButton");
                    delete.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
                    delete.setOnAction(e -> {
                        currStop.getRoute().getStops().remove(currStop);
                        stops.remove(currStop);
                        window.setScene(getListScene(window, "Stop"));
                    });
                    i++;
                    gridPane.add(stopButton, 0, i, 1, 1);
                    gridPane.add(delete, 1, i, 1, 1);
                }
            }
        }

        Button exit = createButton(0, 0, 100, 50, Color.WHITE, "Exit", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });
        Button add = createButton(0,0,175,50,Color.BLACK, "Add " + type, 25);
        add.getStyleClass().add("addButton");
        add.setOnAction(e -> {
            if (type.equals("Bus")) {
                window.setScene(addBusScene(window));
            } else if (type.equals("Route")) {
                window.setScene(addRouteScene(window));
            } else {
                window.setScene(addStopScene(window));
            }

        });

        i++;

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(exit, add);
        gridPane.add(hbox, 0, i, 2, 1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);
        gridPane.setHgap(10);
        ScrollPane scroll = new ScrollPane(gridPane);
        Scene scene = new Scene(scroll, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene addBusScene(Stage window) {
        GridPane gridPane = new GridPane();

        Label title = createLabel("Add a New Bus", 0, 0, 50, Color.BLACK, 500);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 150, 50, Color.WHITE, "Cancel", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getListScene(window, "Bus"));
        });

        Label name = createLabel("Name:", 0, 0, 30, Color.BLACK, 400);
        TextField nameTF = new TextField();

        Label id = createLabel("ID:", 0, 0, 30, Color.BLACK, 400);
        TextField idTF = new TextField();

        Label route = createLabel("Route:", 0, 0, 30, Color.BLACK, 400);
        ChoiceBox<Route> routeChoiceBox = new ChoiceBox<>();
        routeChoiceBox.getStyleClass().add("choiceBox");
        ChoiceBox<Stop> stopChoiceBox = new ChoiceBox<>();
        stopChoiceBox.getStyleClass().add("choiceBox");

        for (int i = 0; i < routes.size(); i++) {
            routeChoiceBox.getItems().add(routes.get(i));
        }

        routeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            // if the item of the list is changed
            public void changed(ObservableValue ov, Number value, Number newValue) {
                stopChoiceBox.getItems().clear();
                Iterator<Route> rIter = routes.iterator();
                Route currRoute = rIter.next();
                int i = 0;
                while (rIter.hasNext() && i < newValue.intValue()) {
                    currRoute = rIter.next();
                    i++;
                }
                Collection<Stop> listStops = currRoute.getStops();
                Iterator<Stop> sIter = listStops.iterator();
                while (sIter.hasNext()) {
                    stopChoiceBox.getItems().add(sIter.next());
                }
            }
        });

        Label currStop = createLabel("Starting Stop:", 0, 0, 30, Color.BLACK, 300);

        Label numPassengers = createLabel("Number of Passengers:", 0, 0, 30, Color.BLACK, 400);
        TextField passengerTF = new TextField();

        Label initalFuel = createLabel("Initial Fuel:", 0, 0, 30, Color.BLACK, 400);
        TextField initFuelTF = new TextField();

        Label fuelCapacity = createLabel("Fuel Capacity:", 0, 0, 30, Color.BLACK, 400);
        TextField fuelCapTF = new TextField();

        Label speed = createLabel("Speed:", 0, 0, 30, Color.BLACK, 400);
        TextField speedTF = new TextField();

        Button submit = createButton(0,0, 150, 50, Color.BLACK, "Submit", 25);
        submit.getStyleClass().add("submitButton");

        submit.setOnAction(e -> {
            try {
                String busName = nameTF.getText();
                int busID = Integer.parseInt(idTF.getText());
                Route busCurrRoute = routeChoiceBox.getValue();
                Stop busCurrStop = stopChoiceBox.getValue();
                ArrayList<Stop> stops = busCurrRoute.getStops();
                Iterator<Stop> iter = stops.iterator();
                Stop busNextStop = iter.next();
                Stop curr = busNextStop;
                boolean found = false;
                while (!found && iter.hasNext()) {
                    if (curr.getName().equals(busCurrRoute.getName())) {
                        found = true;
                    } else {
                        curr = iter.next();
                    }
                }
                if (iter.hasNext()) {
                    busNextStop = iter.next();
                } else {

                }
                int busNumPassengers = Integer.parseInt(passengerTF.getText());
                int busInitFuel = Integer.parseInt(initFuelTF.getText());
                int busFuelCap = Integer.parseInt(fuelCapTF.getText());
                double busSpeed = Double.parseDouble(speedTF.getText());


                buses.add(new Bus(busName, busID, busNumPassengers, busSpeed, busCurrRoute, busCurrStop, busNextStop, busCurrStop.getLocation(),
                        busInitFuel, busFuelCap));
                window.setScene(getListScene(window, "Bus"));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid Value(s)" , "Bad Arguments",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gridPane.add(title, 0, 0, 2, 1);
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(nameTF, 1, 1, 1, 1);
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(idTF, 1, 2, 1,1);
        gridPane.add(numPassengers,0, 3, 1, 1);
        gridPane.add(passengerTF,1,3,1, 1);
        gridPane.add(speed,0, 4, 1, 1);
        gridPane.add(speedTF,1,4,1, 1);
        gridPane.add(route, 0, 5, 1, 1);
        gridPane.add(routeChoiceBox, 1, 5, 1, 1);
        gridPane.add(currStop, 0, 6, 1, 1);
        gridPane.add(stopChoiceBox, 1, 6, 1, 1);
        gridPane.add(initalFuel,0,8,1,1);
        gridPane.add(initFuelTF,1,8,1,1);
        gridPane.add(fuelCapacity,0,9,1,1);
        gridPane.add(fuelCapTF,1,9,1,1);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(exit, submit);
        gridPane.add(hbox,0,10,1,1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }


    public Scene addRouteScene(Stage window) {
        GridPane gridPane = new GridPane();

        Label title = createLabel("Add a New Route", 0, 0, 50, Color.BLACK, 500);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 150, 50, Color.WHITE, "Cancel", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getListScene(window, "Route"));
        });

        Label name = createLabel("Name:", 0, 0, 30, Color.BLACK, 200);
        TextField nameTF = new TextField();

        Label id = createLabel("ID:", 0, 0, 30, Color.BLACK, 200);
        TextField idTF = new TextField();

        Label stopLab = createLabel("Stops:", 0, 0, 30, Color.BLACK, 200);
        ListView stopChoices = new ListView();
        stopChoices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Iterator<Stop> iter = stops.iterator();
        while (iter.hasNext()) {
            stopChoices.getItems().add(iter.next().getName());
        }

        Label colorLab = createLabel("Color:", 0, 0, 30, Color.BLACK, 200);
        ColorPicker pickColor = new ColorPicker();

        Button submit = createButton(0,0, 150, 50, Color.BLACK, "Submit", 25);
        submit.getStyleClass().add("submitButton");

        submit.setOnAction(e -> {
            try {
                String routeName = nameTF.getText();
                int routeID = Integer.parseInt(idTF.getText());

                ArrayList<Stop> routeStops = new ArrayList<>();
                ObservableList<Integer> selectedIndices = stopChoices.getSelectionModel().getSelectedIndices();

                int i = 0;
                Iterator<Stop> stopIter = stops.iterator();
                while (stopIter.hasNext()) {
                    Stop currStop = stopIter.next();
                    if (selectedIndices.contains(i)) {
                        routeStops.add(currStop);
                    }
                    i++;
                }

                ArrayList<Stop> theREALstops = new ArrayList<>();
                int size = routeStops.size();
                for (int a = 0; a < size; a++) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Choose!!");
                    alert.setHeaderText("Choose stop #" + (a+1));
                    Collection<ButtonType> buttonsBruh = new ArrayList<>();
                    for (int b = 0; b < routeStops.size(); b++) {
                        ButtonType buttonType = new ButtonType(b + " " + routeStops.get(b).getName());
                        buttonsBruh.add(buttonType);
                    }
                    alert.getButtonTypes().setAll(buttonsBruh);
                    Optional<ButtonType> result = alert.showAndWait();
                    theREALstops.add(routeStops.remove(Integer.parseInt(result.get().getText().substring(0, result.get().getText().indexOf(' ')))));
                }

                Color color = pickColor.getValue();

                routes.add(new Route(routeName, routeID, routeStops, color));
                routes.add(new Route(routeName, routeID, theREALstops, color));
                window.setScene(getListScene(window, "Route"));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid Value(s)" , "Bad Arguments",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gridPane.add(title, 0, 0, 2, 1);
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(nameTF, 1, 1, 1, 1);
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(idTF, 1, 2, 1,1);
        gridPane.add(stopLab,0, 3, 1, 1);
        gridPane.add(stopChoices,1,3,1, 1);
        gridPane.add(colorLab,0, 4, 1, 1);
        gridPane.add(pickColor,1,4,1, 1);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(exit, submit);
        gridPane.add(hbox,0,5,1,1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene addStopScene(Stage window) {
        GridPane gridPane = new GridPane();

        Label title = createLabel("Add a Stop", 0, 0, 50, Color.BLACK, 500);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 150, 50, Color.WHITE, "Cancel", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getListScene(window, "Stop"));
        });

        Label name = createLabel("Name:", 0, 0, 30, Color.BLACK, 400);
        TextField nameTF = new TextField();

        Label id = createLabel("ID:", 0, 0, 30, Color.BLACK, 400);
        TextField idTF = new TextField();

        Label numPassengers = createLabel("Number of Passengers:", 0, 0, 30, Color.BLACK, 400);
        TextField numpsTF = new TextField();

        Label xcoord = createLabel("Point (x):", 0, 0, 30, Color.BLACK, 400);
        TextField xcoordTF = new TextField();

        Label ycoord = createLabel("Point (y):", 0, 0, 30, Color.BLACK, 400);
        TextField ycoordTF = new TextField();

        Label routeLabel = createLabel("On Route:", 0, 0, 30, Color.BLACK, 400);
        TextField routeLabelTF = new TextField();

        Label stopBefore = createLabel("Set Stop After:", 0, 0, 30, Color.BLACK, 400);
        TextField stopBeforeTF = new TextField();

        Button submit = createButton(0,0, 150, 50, Color.BLACK, "Submit", 25);
        submit.getStyleClass().add("submitButton");

        submit.setOnAction(e -> {
            try {
                String stopName = nameTF.getText();
                int stopID = Integer.parseInt(idTF.getText());
                int stopNumPass = Integer.parseInt(numpsTF.getText());
                Point stopLoc = new Point(Integer.parseInt(xcoordTF.getText()), Integer.parseInt(ycoordTF.getText()));
                String routeName = routeLabelTF.getText();
                Route currRoute = null;
                int indexOfRoute = 0;
                for (int i = 0; i < routes.size(); i++) {
                    Route tempRoute = routes.get(i);
                    if (Integer.toString(tempRoute.getID()).equals(routeName)) {
                        currRoute = tempRoute;
                        indexOfRoute = i;
                    }
                }
                Stop newStop = new Stop(stopName, stopID, stopNumPass, stopLoc, currRoute);
                stops.add(newStop);
                Route mainRoute = routes.get(indexOfRoute);
                String stopNameString = stopBeforeTF.getText();
                int indexOfStop = 0;
                for (int i = 0; i < mainRoute.getStops().size(); i++) {
                    if (Integer.toString(mainRoute.getStops().get(i).getID()).equals(stopNameString)) {
                        indexOfStop = i;
                    }
                }
                mainRoute.getStops().add(indexOfStop, newStop);

                window.setScene(getListScene(window, "Stop"));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid Value(s)" , "Bad Arguments",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gridPane.add(title, 0, 0, 2, 1);
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(nameTF, 1, 1, 1, 1);
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(idTF, 1, 2, 1,1);
        gridPane.add(numPassengers,0, 3, 1, 1);
        gridPane.add(numpsTF,1,3,1, 1);
        gridPane.add(xcoord,0, 4, 1, 1);
        gridPane.add(xcoordTF,1,4,1, 1);
        gridPane.add(ycoord, 0, 5, 1, 1);
        gridPane.add(ycoordTF, 1, 5, 1, 1);
        gridPane.add(routeLabel,0, 6, 1, 1);
        gridPane.add(routeLabelTF,1,6,1, 1);
        gridPane.add(stopBefore, 0, 7, 1, 1);
        gridPane.add(stopBeforeTF, 1, 7, 1, 1);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(exit, submit);
        gridPane.add(hbox,0,8,1,1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getBusScene(Bus bus, Stage window) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid");

        Label title = createLabel("Bus Info", 0, 0, 50, Color.BLACK, 400);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 100, 50, Color.WHITE, "Exit", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        Label name = createLabel("Name: " + bus.getName(), 0, 0, 30, Color.BLACK, 400);
        Button editName = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editName.getStyleClass().add("editButton");
        editName.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New Name for the Bus");
            td.showAndWait();
            String newName = td.getEditor().getText();
            bus.setName(newName);
            name.setText("Name: " + bus.getName());
        });

        Label id = createLabel("ID: " + bus.getID(), 0, 0, 30, Color.BLACK, 400);
        Button editId = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editId.getStyleClass().add("editButton");
        editId.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New ID for the Bus");
            td.showAndWait();
            String newIdString = td.getEditor().getText();
            int newId = Integer.parseInt(newIdString);
            bus.setID(newId);
            id.setText("ID: " + bus.getID());
        });

        Label numPassengers = createLabel("Number of Passengers: " + bus.getNumPassengers(), 0, 0, 30, Color.BLACK, 400);
        Button editNumPassengers = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editNumPassengers.getStyleClass().add("editButton");
        editNumPassengers.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New Number of Passengers for the Bus");
            td.showAndWait();
            String newNPString = td.getEditor().getText();
            int newNP = Integer.parseInt(newNPString);
            bus.setNumPassengers(newNP);
            numPassengers.setText("Number of Passengers: " + bus.getNumPassengers());
        });

        Label avgSpeed = createLabel("Average Speed: " + bus.getAvgSpeed(), 0, 0, 30, Color.BLACK, 400);
        Button editAvgSpeed = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editAvgSpeed.getStyleClass().add("editButton");
        editAvgSpeed.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New Average Speed for the Bus");
            td.showAndWait();
            String newAVGString = td.getEditor().getText();
            double newNP = Double.parseDouble(newAVGString);
            bus.setAvgSpeed(newNP);
            avgSpeed.setText("Average Speed: " + bus.getAvgSpeed());
        });

        Label route = createLabel("Route: " + bus.getRoute().getName(), 0, 0, 30, Color.BLACK, 400);

        ContextMenu routeMenu = new ContextMenu();
        Iterator<Route> routeIterator = routes.iterator();
        while (routeIterator.hasNext()) {
            Route tempRoute = routeIterator.next();
            MenuItem item = new MenuItem(tempRoute.getName());
            item.setOnAction(e -> {
                bus.setRoute(tempRoute);
                route.setText("Route: " + bus.getRoute().getName());
            });
            routeMenu.getItems().add(item);
        }

        Button routeEdit = createButton(0,0, 300, 30, Color.BLACK, "Edit (Right Click)", 30);
        routeEdit.getStyleClass().add("editButton");
        routeEdit.setContextMenu(routeMenu);

        Label currStop = createLabel("Current Stop: " + bus.getCurrStop().getName(), 0, 0, 30, Color.BLACK, 300);
        Label nextStop = createLabel("Next Stop: " + bus.getNextStop().getName(), 0, 0, 30, Color.BLACK, 300);
        Label location = createLabel("Location: (" + bus.getLocation().x + ", " + bus.getLocation().y + ")", 0, 0, 30, Color.BLACK, 300);
        Label currFuel = createLabel("Current Fuel: " + bus.getCurrFuel(), 0, 0, 30, Color.BLACK, 400);
        Label fuelCap = createLabel("Fuel Capacity: " + bus.getFuelCapacity(), 0, 0, 30, Color.BLACK, 400);

        gridPane.add(title, 0, 0, 1, 1);
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(editName, 1, 1, 1, 1);
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(editId, 1, 2, 1,1);
        gridPane.add(numPassengers,0, 3, 1, 1);
        gridPane.add(editNumPassengers,1,3,1, 1);
        gridPane.add(avgSpeed,0, 4, 1, 1);
        gridPane.add(editAvgSpeed,1,4,1, 1);
        gridPane.add(route, 0, 5, 1, 1);
        gridPane.add(routeEdit, 1, 5, 1, 1);
        gridPane.add(currFuel,0,6,1,1);
        gridPane.add(fuelCap,0,7,1,1);
        gridPane.add(currStop, 0, 8, 1, 1);
        gridPane.add(nextStop, 0, 9, 1, 1);
        gridPane.add(location,0,10,1,1);
        gridPane.add(exit,0,11,1,1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        ScrollPane sp = new ScrollPane(gridPane);
        Scene scene = new Scene(sp, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getRouteScene(Route route, Stage window) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid");

        Label title = createLabel("Route Info", 0, 0, 50, Color.BLACK, 400);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 100, 50, Color.WHITE, "Exit", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        Label name = createLabel("Name: " + route.getName(), 0, 0, 30, Color.BLACK, 400);
        Button editName = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editName.getStyleClass().add("editButton");
        editName.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New Name for the Bus");
            td.showAndWait();
            String newName = td.getEditor().getText();
            route.setName(newName);
            name.setText("Name: " + route.getName());
        });

        Label id = createLabel("ID: " + route.getID(), 0, 0, 30, Color.BLACK, 400);
        Button editId = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editId.getStyleClass().add("editButton");
        editId.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New ID for the Bus");
            td.showAndWait();
            String newIdString = td.getEditor().getText();
            int newId = Integer.parseInt(newIdString);
            route.setID(newId);
            id.setText("ID: " + route.getID());
        });

        Label color = createLabel("Color: " + toHexString(route.getColor()), 0, 0, 30, Color.BLACK, 400);

        String stops = "";
        for (Stop stop : route.getStops()) {
            stops = stops.concat(stop.getName() + ", ");
        }

        stops = stops.substring(0, stops.length() - 2);
        Label listStops = createLabel("Stops: " + stops, 0, 0, 30, Color.BLACK, 400);
        listStops.setWrapText(true);

        gridPane.add(title, 0, 0, 1, 1);
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(editName, 1, 1, 1, 1 );
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(editId, 1, 2, 1,1 );
        gridPane.add(color, 0, 3, 1, 1);
        gridPane.add(listStops, 0, 4, 1, 1);
        gridPane.add(exit,0,5,1,1 );

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getStopScene(Stop stop, Stage window) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid");

        Label title = createLabel("Stop Info", 0, 0, 50, Color.BLACK, 400);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 100, 50, Color.WHITE, "Exit", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        Label name = createLabel("Name: " + stop.getName(), 0, 0, 30, Color.BLACK, 400);
        Button editName = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editName.getStyleClass().add("editButton");
        editName.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New Name for the Bus");
            td.showAndWait();
            String newName = td.getEditor().getText();
            stop.setName(newName);
            name.setText("Name: " + stop.getName());
        });

        Label id = createLabel("ID: " + stop.getID(), 0, 0, 30, Color.BLACK, 400);
        Button editId = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editId.getStyleClass().add("editButton");
        editId.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New ID for the Bus");
            td.showAndWait();
            String newIdString = td.getEditor().getText();
            int newId = Integer.parseInt(newIdString);
            stop.setID(newId);
            id.setText("ID: " + stop.getID());
        });

        Label numPassengers = createLabel("Number of Passengers: " + stop.getNumPassengers(), 0, 0, 30, Color.BLACK, 400);
        Button editNumPassengers = createButton(0,0, 100, 30, Color.BLACK, "Edit", 30);
        editNumPassengers.getStyleClass().add("editButton");
        editNumPassengers.setOnAction(e -> {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter the New Number of Passengers for the Stop");
            td.showAndWait();
            String newNPString = td.getEditor().getText();
            int newNP = Integer.parseInt(newNPString);
            stop.setNumPassengers(newNP);
            numPassengers.setText("Number of Passengers: " + stop.getNumPassengers());
        });

        Label loc = createLabel("Location: (" + stop.getLocation().getX() + ", " + stop.getLocation().getY() + ")", 0, 0, 30, Color.BLACK, 400);

        gridPane.add(title,0,0,1,1 );
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(editName, 1, 1, 1, 1 );
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(editId, 1, 2, 1,1 );
        gridPane.add(numPassengers,0, 3, 1, 1 );
        gridPane.add(editNumPassengers,1,3,1, 1 );
        gridPane.add(loc,0,4,1,1 );
        gridPane.add(exit,0,5,1,1 );

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getDataAnalysisScreen(Stage window) {
        GridPane gridPane = new GridPane();

        Label title = createLabel("Choose How to Analyze", 0, 0, 50, Color.BLACK, 2000);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button effScore = createButton(0, 0, 600, 100, Color.BLACK, "Generate Effectiveness Score", 30);
        effScore.getStyleClass().add("listButton");
        effScore.setOnAction( e -> {
            window.setScene(getEffectivenessScoreScreen(window));
        });

        Button predModel = createButton(0, 0, 600, 100, Color.BLACK, "Show Predicted Number of Passengers", 30);
        predModel.getStyleClass().add("listButton");
        predModel.setOnAction( e -> {
//            window.setScene(getPredictiveModelScreen(window));
        });

        Button heatMap = createButton(0, 0, 600, 100, Color.BLACK, "Display Heatmap", 30);
        heatMap.getStyleClass().add("listButton");
        heatMap.setOnAction( e -> {
            try {
                toCsv(window);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
                });

        Button exit = createButton(0, 0, 100, 50, Color.WHITE, "Exit", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        gridPane.add(title, 0, 0, 1, 1);
        gridPane.add(effScore, 0, 1, 1, 1);
        gridPane.add(predModel, 0, 2, 1, 1);
        gridPane.add(heatMap, 0, 3, 1, 1);
        gridPane.add(exit, 0, 4, 1, 1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);
        gridPane.setHgap(10);
        ScrollPane scroll = new ScrollPane(gridPane);
        Scene scene = new Scene(scroll, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getHeatmapScreen(Stage window) {
        GridPane gridPane = new GridPane();

        Label title = createLabel("Heat Map", 0, 0, 50, Color.BLACK, 2000);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Image heatmapImage = new Image("heatmap.png");
        ImageView image = new ImageView(heatmapImage);
        image.setFitHeight(700);
        image.setFitWidth(700);
        image.setLayoutX(0);
        image.setLayoutY(0);

        Button exit = createButton(0, 0, 100, 50, Color.WHITE, "Exit", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getDataAnalysisScreen(window));
        });

        gridPane.add(title, 0, 0, 1, 1);
        gridPane.add(image, 0, 1, 1, 1);
        gridPane.add(exit, 0, 2, 1, 1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);
        gridPane.setHgap(10);
        ScrollPane scroll = new ScrollPane(gridPane);
        Scene scene = new Scene(scroll, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public void toCsv(Stage window) throws IOException {
        //csv of passengers
        FileWriter writer = new FileWriter("passengers.csv");
        for (int i = 0; i < stops.size(); i++) {
            writer.append(stops.get(i).getNumPassengers() + ",\n");
        }
        writer.flush();
        writer.close();

        FileWriter writer2 = new FileWriter("stops.csv");
        for (int i = 0; i < stops.size(); i++) {
            writer2.append(stops.get(i).getName() + ',');
            writer2.append(String.valueOf(stops.get(i).getScreenLocation().getX()) + ',');
            writer2.append(String.valueOf(stops.get(i).getScreenLocation().getY()) + ',' + '\n');
        }
        writer2.flush();
        writer2.close();
        Process p = Runtime.getRuntime().exec("python heatmap.py");
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        window.setScene(getHeatmapScreen(window));

    }


    public Scene getEffectivenessScoreScreen(Stage window) {
        GridPane gridPane = new GridPane();

        Label title = createLabel("Generate Effectiveness Score", 0, 0, 50, Color.BLACK, 700);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, 50));

        Button exit = createButton(0, 0, 150, 50, Color.WHITE, "Cancel", 25);
        exit.getStyleClass().add("exitButton");
        exit.setOnAction(e -> {
            window.setScene(getDataAnalysisScreen(window));
        });

        ListView stopChoices = new ListView();
        stopChoices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Iterator<Stop> stopIter = stops.iterator();
        while (stopIter.hasNext()) {
            stopChoices.getItems().add(stopIter.next().getName());
        }

        ListView busChoices = new ListView();
        busChoices.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Iterator<Bus> busIter = buses.iterator();
        while (busIter.hasNext()) {
            busChoices.getItems().add(busIter.next().getName());
        }

        Button submit = createButton(0,0, 150, 50, Color.BLACK, "Submit", 25);
        submit.getStyleClass().add("submitButton");


        submit.setOnAction(e -> {
            try {
                ArrayList<Stop> analysisStops = new ArrayList<>();
                ObservableList<Integer> selectedIndices1 = stopChoices.getSelectionModel().getSelectedIndices();

                ArrayList<Bus> analysisBus = new ArrayList<>();
                ObservableList<Integer> selectedIndices2 = busChoices.getSelectionModel().getSelectedIndices();

                int i = 0;
                Iterator<Stop> stopIterator = stops.iterator();
                while (stopIterator.hasNext()) {
                    Stop currStop = stopIterator.next();
                    if (selectedIndices1.contains(i)) {
                        analysisStops.add(currStop);
                    }
                    i++;
                }

                int j = 0;
                Iterator<Bus> busIterator = buses.iterator();
                while (busIterator.hasNext()) {
                    Bus currBus = busIterator.next();
                    if (selectedIndices2.contains(j)) {
                        analysisBus.add(currBus);
                    }
                    j++;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Data Analysis");
                alert.setHeaderText("Effectiveness Score");
                alert.setContentText("Score: " + effectScore(analysisBus, analysisStops));
                alert.showAndWait();

            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid Value(s)" , "Bad Arguments",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gridPane.add(title, 0, 0, 2, 1);
        gridPane.add(stopChoices, 0, 1, 1, 1);
        gridPane.add(busChoices, 1, 1, 1, 1);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(exit, submit);
        gridPane.add(hbox, 0, 2, 1, 1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public double effectScore(ArrayList<Bus> analysisBuses, ArrayList<Stop> analysisStops) {
        double waitTime = 0;
        double busCost = 0;

        for (int i = 0; i < analysisBuses.size(); i++) {
            //set the following effectiveness scores for each given bus
            busCost += ((double) analysisBuses.get(i).getAvgSpeed())/70 + (1-((double)analysisBuses.get(i).getCurrFuel())/analysisBuses.get(i).getFuelCapacity());
        }
        for (int i = 0; i < analysisStops.size(); i++) {
            waitTime += ((double) analysisStops.get(i).getNumPassengers())/20;
        }
        busCost /= analysisBuses.size();
        waitTime /= analysisStops.size();

        double score = waitTime * (.5*busCost);
        score *= 10;
        score = 10 - score;
        return score;
    }

//    public Scene getPredictiveModelScreen(Stage window) {
//
//    }

    public static void main(String[] args) {
        launch(args);
    }

    public ImageView createImage(String path, int x, int y, int width, int height) {
        Image newImage = new Image(Main.class.getResource("/resources/" + path).toString());
        ImageView image = new ImageView(newImage);
        image.setFitHeight(height);
        image.setFitWidth(width);
        image.setLayoutX(x);
        image.setLayoutY(y);
        return image;
    }

    public Label createLabel(String text, int x, int y, int font, Color c, int width) {
        Label newLabel = new Label(text);
        newLabel.setTextFill(c);
        newLabel.setFont(new Font(font));
        newLabel.setLayoutX(x);
        newLabel.setLayoutY(y);
        newLabel.setPrefWidth(width);
        return newLabel;
    }

    private Button createButton(int x, int y, int width, int height, Color c, String name, int size) {
        Button startButton = new Button(name);
        startButton.setFont(new Font(size));
        startButton.setTextFill(c);
        startButton.getStyleClass().add("buttons");
        startButton.setPrefWidth(width);
        startButton.setPrefHeight(height);
        startButton.setLayoutX(x);
        startButton.setLayoutY(y);
        return startButton;
    }

    public Slider createSlider(int min, int max, int val, int width, boolean ticks, int majorTick,
                               int minorTick) {
        Slider slider = new Slider(min, max, val);
        slider.setPrefWidth(width);
        slider.setShowTickMarks(ticks);
        slider.setMajorTickUnit(majorTick);
        slider.setMinorTickCount(minorTick);
        return slider;
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()))
                .toUpperCase();
    }
}
