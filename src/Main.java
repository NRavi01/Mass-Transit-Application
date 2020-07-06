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

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Main extends Application{
    private int globalWidth = 1920;
    private int globalHeight = 1080;

    private Collection<Bus> buses;
    private Collection<Route> routes;
    private Collection<Stop> stops;

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
            //For now, fill with random values
            for (int i = 0; i < 6; i ++) {
                Collection<Stop> tempStops = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    Stop tempStop = new Stop("Stop " + ((int) (Math.random() * 100)), (int) (Math.random() * 20), 1, new Point((int) (Math.random() * (5000)), (int)(Math.random() * (5000))));
                    tempStops.add(tempStop);
                    System.out.println("stop" + tempStop.getLocation());
                }
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
                Route route = new Route("Route " + ((int) (Math.random() * 100)), 1, tempStops, routeColor);
                routes.add(route);
                Iterator<Stop> iter = route.getStops().iterator();
                Stop stop1 = iter.next();
                Stop stop2 = iter.next();
                int x = (int) (stop1.getLocation().getX() + stop2.getLocation().getX()) / 2;
                int y = (int) (stop1.getLocation().getY() + stop2.getLocation().getY()) / 2;
                Point startingLoc = new Point(x - 30, y - 20);
                Bus newBus = new Bus("Bus " + ((int) (Math.random() * 100)), (int) (Math.random() * 100) , 10, 10, route, stop1, stop2, startingLoc);
                buses.add(newBus);
                System.out.println("bus" + newBus.getLocation());
                stops = tempStops;
            }

            window.setScene(getMainScreen(window));
        });

        return scene;
    }

    public Scene getMainScreen(Stage window) {
        int globalTemp = globalWidth * 3/4;

        ArrayList<ImageView> busImages = new ArrayList<>();
        Iterator<Bus> iterator = buses.iterator();
        while(iterator.hasNext()) {
            Bus b = iterator.next();
            ImageView newBus = createImage("bus_icon.PNG", (int) b.getScreenLocation().getX(), (int) b.getScreenLocation().getY(), 80, 50);
            busImages.add(newBus);
        }

        ArrayList<Circle> stopImages = new ArrayList<>();
        ArrayList<Line> routeLines = new ArrayList<>();
        ArrayList<Label> stopLabels = new ArrayList<>();
        Iterator<Route> routeIterator = routes.iterator();

        while (routeIterator.hasNext()){
            Route currRoute = routeIterator.next();
            for (int j = 0; j < currRoute.getStops().size(); j++) {
                Iterator<Stop> iter = currRoute.getStops().iterator();
                int i = 0;
                while (iter.hasNext() && i < j) {
                    iter.next();
                    i++;
                }
                Stop stop = iter.next();
                Circle newStop = new Circle(stop.getScreenLocation().getX(), stop.getScreenLocation().getY(), 20);
                newStop.setFill(currRoute.getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getScreenLocation().getX() - 5, (int) stop.getScreenLocation().getY() - 10, 15, Color.BLACK, 20);
                stopLabels.add(stopLabel);

                Line stopLine = null;
                if (j != currRoute.getStops().size() - 1) {
                    Point stop1 = stop.getScreenLocation();
                    Point stop2 = iter.next().getScreenLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                } else {
                    Point stop1 = stop.getScreenLocation();
                    Point stop2 = currRoute.getStops().iterator().next().getScreenLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                }
                routeLines.add(stopLine);
            }
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
        for (int i = 0; i < mapLines.size(); i++) {
            msGroup.getChildren().add(mapLines.get(i));
        }

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

        ImageView modelRoute = createImage("modelRoute.PNG", 220, 10, 150, 60);

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
            window.setScene(getMainScreen(window));
        });

        zoomOut.setOnAction(e -> {
            scaleAll(-1);
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

        stepForward.setOnAction(e -> {
            //Code to change simulation states here
            window.setScene(getMainScreen(window));
        });

        stepBackward.setOnAction(e -> {

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
        stepGroup.getChildren().addAll(placeholder1, simStep, stepForward, stepBackward);

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

    public void scaleAll(int direction) {
        double scalingFactor = 1.1;
        Iterator<Bus> busIterator = buses.iterator();
        ArrayList<Bus> newBusList = new ArrayList<>();
        while(busIterator.hasNext()){
            Bus b = busIterator.next();
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

        Iterator<Route> routeIterator = routes.iterator();
        ArrayList<Route> newRouteList = new ArrayList<>();
        while(routeIterator.hasNext()){
            Route route = routeIterator.next();
            Collection<Stop> newStopList = new ArrayList<>();
            Iterator<Stop> iter = route.getStops().iterator();
            for (int i = 0; i < route.getStops().size(); i++) {
                Stop stop = iter.next();
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
        Iterator<Bus> busIterator = buses.iterator();
        ArrayList<Bus> newBusList = new ArrayList<>();
        while(busIterator.hasNext()){
            Bus b = busIterator.next();
            int newPointX = (int) b.getScreenLocation().getX() + x;
            int newPointY = (int) b.getScreenLocation().getY() + y;
            b.setScreenLocation(new Point(newPointX, newPointY));
            newBusList.add(b);
        }
        buses.removeAll(buses);
        for (int i = 0; i < newBusList.size(); i++) {
            buses.add(newBusList.get(i));
        }

        Iterator<Route> routeIterator = routes.iterator();
        ArrayList<Route> newRouteList = new ArrayList<>();
        while(routeIterator.hasNext()){
            Route route = routeIterator.next();
            Collection<Stop> newStopList = new ArrayList<>();
            Iterator<Stop> iter = route.getStops().iterator();
            for (int i = 0; i < route.getStops().size(); i++) {
                Stop stop = iter.next();
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
        title.getStyleClass().add("title");
        gridPane.add(title, 0, 0, 1, 1);

        int i = 0;

        if (type.equals("Bus")) {
            Iterator<Bus> iter = buses.iterator();
            while (iter.hasNext()) {
                Bus currBus = iter.next();
                Button busButton = createButton(0, 0, 400,100,Color.BLACK, currBus.getName(), 30);
                busButton.getStyleClass().add("listButton");
                busButton.setOnAction(e -> {
                    window.setScene(getBusScene(currBus, window));
                });
                Button delete = createButton(0, 0,50, 50, Color.BLACK, "X", 30);
                delete.setOnAction(e -> {
                    buses.remove(currBus);
                    window.setScene(getListScene(window, "Bus"));
                });
                i++;
                gridPane.add(busButton, 0, i, 1, 1);
                gridPane.add(delete, 1, i, 1, 1);
            }
        } else if (type.equals("Route")) {
            Iterator<Route> iter = routes.iterator();
            while (iter.hasNext()) {
                Route currRoute = iter.next();
                Button routeButton = createButton(0, 0, 400,100,Color.BLACK, currRoute.getName(), 30);
                routeButton.getStyleClass().add("listButton");
                routeButton.setOnAction(e -> {
                    window.setScene(getRouteScene(currRoute, window));
                });
                Button delete = createButton(0, 0,50, 50, Color.BLACK, "X", 30);
                delete.setOnAction(e -> {
                    routes.remove(currRoute);
                    window.setScene(getListScene(window, "Route"));
                });
                i++;
                gridPane.add(routeButton, 0, i, 1, 1);
                gridPane.add(delete, 1, i, 1, 1);
            }
        } else {
            Iterator<Stop> iter = stops.iterator();
            while (iter.hasNext()) {
                Stop currStop = iter.next();
                Button stopButton = createButton(0, 0, 400,100,Color.BLACK, currStop.getName(), 30);
                stopButton.getStyleClass().add("listButton");
                stopButton.setOnAction(e -> {
                    window.setScene(getStopScene(currStop, window));
                });
                Button delete = createButton(0, 0,50, 50, Color.BLACK, "X", 30);
                delete.setOnAction(e -> {
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

        i++;
        gridPane.add(exit, 0, i, 1, 1);
        gridPane.setPadding(new Insets(100, 100, 100, 100));
        gridPane.setVgap(30);
        ScrollPane scroll = new ScrollPane(gridPane);
        Scene scene = new Scene(scroll, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getBusScene(Bus bus, Stage window) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid");

        Label title = createLabel("Bus Info", 0, 0, 50, Color.BLACK, 400);
        title.getStyleClass().add("title");

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
        Label location = createLabel("Location: " + bus.getLocation().x + ", " + bus.getLocation().y, 0, 0, 30, Color.BLACK, 300);

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
        gridPane.add(currStop, 0, 6, 1, 1);
        gridPane.add(nextStop, 0, 7, 1, 1);
        gridPane.add(location,0,8,1,1);
        gridPane.add(exit,0,9,1,1);

        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(30);

        Scene scene = new Scene(gridPane, globalWidth, globalHeight);
        scene.getStylesheets().add("styles/main.css");
        return scene;
    }

    public Scene getRouteScene(Route route, Stage window) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid");

        Label title = createLabel("Route Info", 0, 0, 50, Color.BLACK, 400);
        title.getStyleClass().add("title");

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

        String stops = "";
        for (Stop stop : route.getStops()) {
            System.out.println(stop.getName());
            stops = stops.concat(stop.getName() + ", ");
        }

        stops = stops.substring(0, stops.length() - 2);
        System.out.println(stops);
        Label listStops = createLabel("Stops: " + stops, 0, 0, 30, Color.BLACK, 400);
        listStops.setWrapText(true);

        gridPane.add(title, 0, 0, 1, 1);
        gridPane.add(name, 0, 1, 1, 1);
        gridPane.add(editName, 1, 1, 1, 1 );
        gridPane.add(id, 0, 2, 1,1);
        gridPane.add(editId, 1, 2, 1,1 );
        gridPane.add(listStops, 0, 3, 1, 1);
        gridPane.add(exit,0,4,1,1 );

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
        title.getStyleClass().add("title");

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
        Image newImage = new Image("\\resources\\" + path);
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

}
