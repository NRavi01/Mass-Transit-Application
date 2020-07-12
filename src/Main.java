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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Main extends Application{
    private int globalWidth = 1920;
    private int globalHeight = 1080;

    private ArrayList<Bus> buses;
    private ArrayList<Route> routes;
    private ArrayList<Stop> stops;

    private ArrayList<Bus> initialBuses = new ArrayList<>();
    private ArrayList<Route> initialRoutes = new ArrayList<>();
    private ArrayList<Stop> initialStops = new ArrayList<>();

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
            //ALL CORE SIM LOGIC AND DATABASE RETRIEVAL TEAM WORK HERE - leads to array of all simobjects
            buses = new ArrayList<>();
            routes = new ArrayList<>();
            stops = new ArrayList<>();
            //For now, fill with random values
            for (int i = 0; i < 6; i ++) {
                ArrayList<Stop> tempStops = new ArrayList<>();
                ArrayList<Stop> tempStopsInitial = new ArrayList<>();
                Color routeColor;
                if (i == 0) {
                    routeColor = Color.BLUE;
                }
                else if (i == 1) {
                    routeColor = Color.GREEN;
                }
                else if (i == 2){
                    routeColor = Color.RED;
                }
                else if (i == 3){
                    routeColor = Color.PINK;
                }
                else if (i == 4){
                    routeColor = Color.ORANGE;
                }
                else if (i == 5){
                    routeColor = Color.SILVER;
                }
                else {
                    routeColor = Color.YELLOW;
                }
                int randomRoute = ((int) (Math.random() * 100));
                Route route = new Route("Route " + randomRoute, randomRoute, tempStops, routeColor);
                Route initialRoute = new Route("Route " + randomRoute, randomRoute, tempStops, routeColor);
                for (int j = 0; j < 5; j++) {
                    int stopId = (int) (Math.random() * 100);
                    int passengerNum = (int) (Math.random() * 20);
                    Point stopPoint = new Point((int) (Math.random() * (5000)), (int)(Math.random() * (5000)));
                    Stop tempStop = new Stop("Stop " + stopId, stopId, passengerNum, stopPoint, route);
                    Stop tempStopInitial = new Stop("Stop " + stopId, stopId, passengerNum, stopPoint, route);
                    tempStops.add(tempStop);
                    tempStopsInitial.add(tempStopInitial);
                    stops.add(tempStop);
                    initialStops.add(tempStopInitial);
                }
                route.setStops(tempStops);
                initialRoute.setStops(tempStopsInitial);
                routes.add(route);
                initialRoutes.add(initialRoute);
                Stop stop1 = tempStops.get(0);
                Stop stop2 = tempStops.get(1);
                int x = (int) (stop1.getLocation().getX() + stop2.getLocation().getX()) / 2;
                int y = (int) (stop1.getLocation().getY() + stop2.getLocation().getY()) / 2;
                Point startingLoc = new Point(x - 30, y - 20);
                int rand1 = ((int) (Math.random() * 100));
                int rand2 = (int) (Math.random() * 100);
                Bus newBus = new Bus("Bus " + rand1,  rand2, 10, 10, route, stop1, stop2, startingLoc, 100, 100);
                Bus newBusInitial = new Bus("Bus " + rand1,  rand2, 10, 10, route, stop1, stop2, startingLoc, 100, 100);
                initialBuses.add(newBusInitial);
                buses.add(newBus);
            }
            //Fuel Station
            for (int i = 0; i < 1; i++) {
                Point newFuel = new Point((int) (Math.random() * (5000)), (int)(Math.random() * (5000)));
                FuelStation newFuelStation = new FuelStation(newFuel);
                fuelStations.add(newFuelStation);
            }

            window.setScene(getMainScreen(window));
        });

        return scene;
    }

    public Scene getMainScreen(Stage window) {
        int globalTemp = globalWidth * 3/4;

        ArrayList<ImageView> busImages = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
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
                Circle newStop = new Circle(stop.getScreenLocation().getX(), stop.getScreenLocation().getY(), 20);
                newStop.setFill(currRoute.getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getScreenLocation().getX() - 5, (int) stop.getScreenLocation().getY() - 10, 15, Color.BLACK, 20);
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
        //Line listLine4 = new Line(0,    0, 500, -220);

        Line busHolder = new Line(0, 0, 0, 0);
        placeHolder.setStrokeWidth(0);

        Button busList = createButton(50, 0, 150, 50, Color.BLACK, "Bus List", 30  );
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
            buses = initialBuses;
            routes = initialRoutes;
            stops = initialStops;
            window.setScene(getMainScreen(window));
        });

        VBox lists = new VBox(15);
        lists.getChildren().addAll(placeHolder, modelBusGroup, listLine1,
                modelRouteGroup, listLine2, modelStopGroup, listLine3);
        lists.setPrefSize(globalWidth * 1/4, 500);
        lists.setPadding(new Insets(0, 0, 0, 0));

        ImageView martaLogo = createImage("martaLogo.gif", 0, 0, globalWidth * 1/4 - 50, 200);

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
                Button busButton = createButton(0, 0, 300,100,Color.BLACK, currBus.getName(), 30);
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
                Button routeButton = createButton(0, 0, 300,100,currRoute.getColor(), currRoute.getName(), 30);
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
                    window.setScene(getListScene(window, "Route"));
                });
                i++;
                gridPane.add(routeButton, 0, i, 1, 1);
                gridPane.add(delete, 1, i, 1, 1);
            }
        } else {
            for (int j = 0; j < stops.size(); j++) {
                Stop currStop = stops.get(j);
                Button stopButton = createButton(0, 0, 300,100,Color.BLACK, currStop.getName(), 30);
                stopButton.getStyleClass().add("listButton");
                stopButton.setTextFill(currStop.getRoute().getColor());
                stopButton.setOnAction(e -> {
                    window.setScene(getStopScene(currStop, window));
                });
                Button delete = createButton(0, 0,50, 50, Color.BLACK, "X", 30);
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

                Color color = pickColor.getValue();

                routes.add(new Route(routeName, routeID, routeStops, color));
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
            window.setScene(getListScene(window, "Route"));
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
                System.out.println("hi");
                String stopName = nameTF.getText();
                int stopID = Integer.parseInt(idTF.getText());
                int stopNumPass = Integer.parseInt(numpsTF.getText());
                Point stopLoc = new Point(Integer.parseInt(xcoordTF.getText()), Integer.parseInt(ycoordTF.getText()));
                String routeName = routeLabelTF.getText();
                Route currRoute = null;
                int indexOfRoute = 0;
                for (int i = 0; i < routes.size(); i++) {
                    Route tempRoute = routes.get(i);
                    System.out.println("hi");
                    System.out.println(tempRoute.getID());
                    System.out.println(tempRoute.getName());
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
