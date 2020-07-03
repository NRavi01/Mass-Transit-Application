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
    private int globalWidth = 2000;
    private int globalHeight = 1000;

    private Collection<Bus> buses;
    private Collection<Route> routes;
    private Collection<Stop> stops;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setScene(getHomeScene(primaryStage));
        primaryStage.show();
    }

    public Scene getHomeScene(Stage window) {
        ImageView marta_background = createImage("marta_background.png", 0, 0, globalWidth, globalHeight);

        Label prompt = createLabel("Select a start time", globalWidth / 40, globalHeight / 2 + 50, 35, Color.BLACK, 500);
        prompt.setFont(Font.font("Verdana", FontWeight.BOLD, 35));

        Label dateChoiceDesc = createLabel("Day: ", globalWidth / 50, globalHeight / 2 + 150, 35, Color.BLACK, 100);
        dateChoiceDesc.setPrefHeight(50);
        ChoiceBox<String> dateChoiceBox = new ChoiceBox<>();
        dateChoiceBox.getItems().addAll("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        dateChoiceBox.setValue("Sunday");
        dateChoiceBox.setLayoutX(globalWidth / 50 + 100);
        dateChoiceBox.setPrefHeight(50);
        dateChoiceBox.setPrefWidth(110);
        dateChoiceBox.setLayoutY(globalHeight / 2 + 150);

        Label timeChoiceDesc = createLabel("Hour:", globalWidth / 50, globalHeight / 2 + 250, 35, Color.BLACK, 100);
        timeChoiceDesc.setPrefHeight(50);

        ChoiceBox<String> hourChoiceBox = new ChoiceBox<>();
        hourChoiceBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        hourChoiceBox.setValue("12");
        hourChoiceBox.setLayoutX(globalWidth / 50 + 100);
        hourChoiceBox.setPrefHeight(50);
        hourChoiceBox.setPrefWidth(110);
        hourChoiceBox.setLayoutY(globalHeight / 2 + 250);

        Label colon = createLabel(":", globalWidth / 50 + 215, globalHeight / 2 + 250, 35, Color.BLACK, 10);
        timeChoiceDesc.setPrefHeight(50);

        ChoiceBox<String> minChoiceBox = new ChoiceBox<>();
        minChoiceBox.getItems().addAll("00", "30");
        minChoiceBox.setValue("00");
        minChoiceBox.setLayoutX(globalWidth / 50 + 230);
        minChoiceBox.setPrefHeight(50);
        minChoiceBox.setPrefWidth(110);
        minChoiceBox.setLayoutY(globalHeight / 2 + 250);

        ChoiceBox<String> ampm = new ChoiceBox<>();
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
        window.setScene(scene);

        beginSim.setOnAction(e -> {
            //ALL CORE SIM LOGIC AND DATABASE RETRIEVAL TEAM WORK HERE - leads to array of all simobjects
            buses = new ArrayList<>();
            routes = new ArrayList<>();
            //For now, fill with random values
            for (int i = 0; i < 6; i ++) {
                Collection<Stop> tempStops = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    Stop tempStop = new Stop("stop", (int) (Math.random() * 20), 1, new Point((int) (Math.random() * (5000)), (int)(Math.random() * (5000))));
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
                Route route = new Route("route", 1, tempStops, routeColor);
                routes.add(route);
                Iterator<Stop> iter = route.getStops().iterator();
                Stop stop1 = iter.next();
                Stop stop2 = iter.next();
                int x = (int) (stop1.getLocation().getX() + stop2.getLocation().getX()) / 2;
                int y = (int) (stop1.getLocation().getY() + stop2.getLocation().getY()) / 2;
                Point startingLoc = new Point(x - 30, y - 20);
                Bus newBus = new Bus("Bus", (int) (Math.random() * 100) , 10, 10, route, stop1, stop2, startingLoc);
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
        for (int i = 0; i < numLinesVert;i ++) {
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
            moveAll(50, 0);
            window.setScene(getMainScreen(window));
        });

        navigationLeft.setOnAction(e -> {
            moveAll(-50, 0);
            window.setScene(getMainScreen(window));
        });

        navigationUp.setOnAction(e -> {
            moveAll(0, -50);
            window.setScene(getMainScreen(window));
        });

        navigationDown.setOnAction(e -> {
            moveAll(0, 50);
            window.setScene(getMainScreen(window));
        });

        Button busList = createButton(0, 0, 150, 50, Color.BLACK, "Buses", 30);
        busList.setOnAction(e -> {
            window.setScene(getBusListScene(window));
        });

        Button routeList = createButton(0, 0, 150, 50, Color.BLACK, "Routes", 30);
        routeList.setOnAction(e -> {
            window.setScene(getRouteListScene(window));
        });

        Button stopList = createButton(0, 0, 150, 50, Color.BLACK, "Stops", 30);
        stopList.setOnAction(e -> {
            window.setScene(getStopListScene(window));
        });

        VBox lists = new VBox(10);
        lists.getChildren().addAll(busList, routeList, stopList);

        Group navGroup = new Group();
        navGroup.getChildren().addAll(navigationCenter, navigationDown, navigationLeft, navigationRight, navigationUp);

        GridPane grid = new GridPane();
        grid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        msGroup.setPrefSize(globalWidth * 3/4, globalHeight);
        msGroup.setClip(new Rectangle(msGroup.getPrefWidth(), msGroup.getPrefHeight()));
        grid.add(msGroup, 0, 0, 1, 1);
        grid.add(navGroup, 1, 1, 1, 1);
        grid.add(lists, 1, 0, 1, 1);

        ColumnConstraints column1 = new ColumnConstraints(globalWidth * 3 / 4);
        ColumnConstraints column2 = new ColumnConstraints(globalWidth * 1/4);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        Scene scene = new Scene(grid, globalWidth, globalHeight);
        return scene;
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

    public Scene getBusListScene(Stage window) {
        VBox vbox = new VBox(10);

        Button exit = createButton(0, 0, globalWidth, 200, Color.DARKRED, "Exit", 50);
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        vbox.getChildren().add(exit);

        Iterator<Bus> iter = buses.iterator();
        while (iter.hasNext()) {
            Bus currBus = iter.next();
            Button busButton = createButton(0, 0, globalWidth,100,Color.BLACK, currBus.getName(), 30);
            busButton.setOnAction(e -> {
                window.setScene(getBusScene(currBus, window));
            });
            vbox.getChildren().add(busButton);
        }

        Scene scene = new Scene(vbox, globalWidth, globalHeight);
        return scene;
    }

    public Scene getRouteListScene(Stage window) {
        VBox vbox = new VBox(10);

        Button exit = createButton(0, 0, globalWidth, 200, Color.DARKRED, "Exit", 50);
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        vbox.getChildren().add(exit);

        Iterator<Route> iter = routes.iterator();
        while (iter.hasNext()) {
            Route currRoute = iter.next();
            Button routeButton = createButton(0, 0, globalWidth,100,Color.BLACK, currRoute.getName(), 30);
            routeButton.setOnAction(e -> {
                window.setScene(getRouteScene(currRoute, window));
            });
            vbox.getChildren().add(routeButton);
        }

        Scene scene = new Scene(vbox, globalWidth, globalHeight);
        return scene;
    }

    public Scene getStopListScene(Stage window) {
        VBox vbox = new VBox(10);

        Button exit = createButton(0, 0, globalWidth, 200, Color.DARKRED, "Exit", 50);
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        vbox.getChildren().add(exit);

        Iterator<Stop> iter = stops.iterator();
        while (iter.hasNext()) {
            Stop currStop = iter.next();
            Button stopButton = createButton(0, 0, globalWidth,100,Color.BLACK, currStop.getName(), 30);
            stopButton.setOnAction(e -> {
                window.setScene(getStopScene(currStop, window));
            });
            vbox.getChildren().add(stopButton);
        }

        Scene scene = new Scene(vbox, globalWidth, globalHeight);
        return scene;
    }

    public Scene getBusScene(Bus bus, Stage window) {
        VBox vbox = new VBox(50);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Button exit = createButton(0, 0, globalWidth, 200, Color.DARKRED, "Exit", 30);
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        Label name = createLabel("Name: " + bus.getName(), 0, 0, 30, Color.BLACK, globalWidth);
        Label id = createLabel("ID: " + bus.getID(), 0, 0, 30, Color.BLACK, globalWidth);
        Label numPassengers = createLabel("# of Passengers: " + bus.getNumPassengers(), 0, 0, 30, Color.BLACK, globalWidth);
        Label avgSpeed = createLabel("Average Speed: " + bus.getAvgSpeed(), 0, 0, 30, Color.BLACK, globalWidth);
        Label route = createLabel("Route: " + bus.getRoute().getName(), 0, 0, 30, Color.BLACK, globalWidth);
        Label currStop = createLabel("Current Stop: " + bus.getCurrStop().getName(), 0, 0, 30, Color.BLACK, globalWidth);
        Label nextStop = createLabel("Next Stop: " + bus.getNextStop().getName(), 0, 0, 30, Color.BLACK, globalWidth);
        Label location = createLabel("Location: " + bus.getLocation().x + ", " + bus.getLocation().y, 0, 0, 30, Color.BLACK, globalWidth);

        vbox.getChildren().addAll(exit, name, id, numPassengers, avgSpeed, route, currStop, nextStop, location);

        Scene scene = new Scene(vbox, globalWidth, globalHeight);
        return scene;
    }

    public Scene getRouteScene(Route route, Stage window) {
        Label routeName = createLabel("Name: " + route.getName(), 0, 0, 32, Color.BLACK, 500);
        routeName.setFont(Font.font("Verdana", 32));

        Label routeId = createLabel("ID: " + route.getID(), 0, 0, 32, Color.BLACK, 500);
        routeName.setFont(Font.font("Verdana", 32));

        String stops = "";
        for (Stop stop : route.getStops()) {
            System.out.println(stop.getName());
            stops = stops.concat(stop.getName() + ", ");
        }
        stops = stops.substring(0, stops.length() - 2);
        System.out.println(stops);
        Label listStops = createLabel("Stops: " + stops, 0, 0, 32, Color.BLACK, 500);
        routeName.setFont(Font.font("Verdana", 32));

        Button exit = createButton(0, 0, globalWidth, 200, Color.DARKRED, "Exit", 30);
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        VBox vbox = new VBox(50);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.getChildren().addAll(exit, routeName, routeId, listStops);

        Scene scene = new Scene(vbox, globalWidth, globalHeight);
        return scene;
    }

    public Scene getStopScene(Stop stop, Stage window) {
        VBox vbox = new VBox(50);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Button exit = createButton(0, 0, globalWidth, 200, Color.DARKRED, "Exit", 30);
        exit.setOnAction(e -> {
            window.setScene(getMainScreen(window));
        });

        Label stopName = createLabel("Name: " + stop.getName(), 0, 0, 32, Color.BLACK, 500);
        stopName.setFont(Font.font("Verdana", 32));

        Label stopId = createLabel("ID: " + stop.getID(), 0, 0, 32, Color.BLACK, 500);
        stopId.setFont(Font.font("Verdana", 32));

        Label numPassengers = createLabel("Number of Passengers: " + stop.getNumPassengers(), 0, 0, 32, Color.BLACK, 500);
        numPassengers.setFont(Font.font("Verdana", 32));

        Label loc = createLabel("Location: (" + stop.getLocation().getX() + ", " + stop.getLocation().getY() + ")", 0, 0, 32, Color.BLACK, 500);
        loc.setFont(Font.font("Verdana", 32));

        vbox.getChildren().addAll(exit, stopName, stopId, numPassengers, loc);
        Scene scene = new Scene(vbox, globalWidth, globalHeight);
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
        startButton.setStyle("-fx-background-color: transparent;");
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
