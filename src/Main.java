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
            Collection<Bus> buses = new ArrayList<>();
            Collection<Route> routes = new ArrayList<>();
            //For now, fill with random values
            for (int i = 0; i < 6; i ++) {
                Stop[] stops = new Stop[10];
                for (int j = 0; j < 10; j++) {
                    stops[j]  = new Stop("stop", (int) (Math.random() * 20), 1, new Point((int) (Math.random() * (5000)), (int)(Math.random() * (5000))));
                    System.out.println("stop" + stops[j].getLocation());
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
                Route route = new Route("route", 1, stops, routeColor);
                routes.add(route);
                int x = (int) (route.getStops()[0].getLocation().getX() + route.getStops()[1].getLocation().getX()) / 2;
                int y = (int) (route.getStops()[0].getLocation().getY() + route.getStops()[1].getLocation().getY()) / 2;
                Point startingLoc = new Point(x - 30, y - 20);
                Bus newBus = new Bus("Bus", (int) (Math.random() * 100) , 10, 10, route, route.getStops()[0], route.getStops()[1], startingLoc);
                buses.add(newBus);
                System.out.println("bus" + newBus.getLocation());
            }

            window.setScene(getMainScreen(buses, routes, window));
        });

        return scene;
    }

    public Scene getMainScreen(Collection<Bus> buses, Collection<Route> routes, Stage window) {
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
            for (int j = 0; j < currRoute.getStops().length; j++) {
                Stop stop = currRoute.getStops()[j];
                Circle newStop = new Circle(stop.getScreenLocation().getX(), stop.getScreenLocation().getY(), 20);
                newStop.setFill(currRoute.getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getScreenLocation().getX() - 5, (int) stop.getScreenLocation().getY() - 10, 15, Color.BLACK, 20);
                stopLabels.add(stopLabel);

                Line stopLine = null;
                if (j != currRoute.getStops().length - 1) {
                    Point stop1 = currRoute.getStops()[j].getScreenLocation();
                    Point stop2 = currRoute.getStops()[j + 1].getScreenLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                } else {
                    Point stop1 = currRoute.getStops()[j].getScreenLocation();
                    Point stop2 = currRoute.getStops()[0].getScreenLocation();
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

        Button sideBar = createButton(globalWidth * 54 / 64, globalHeight * 1 / 256, 350, 50, Color.ORANGE, "", 50);
        sideBar.setGraphic(createImage("hamburger.png", (int) sideBar.getLayoutX(), (int) sideBar.getLayoutY(), 50, 50));

        sideBar.setOnAction(e -> {
            window.setScene(getMainWithSidebar(buses, routes, window));
        });

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
            moveAll(50, 0, buses, routes);
            window.setScene(getMainScreen(buses, routes, window));
        });

        navigationLeft.setOnAction(e -> {
            moveAll(-50, 0, buses, routes);
            window.setScene(getMainScreen(buses, routes, window));
        });

        navigationUp.setOnAction(e -> {
            moveAll(0, -50, buses, routes);
            window.setScene(getMainScreen(buses, routes, window));
        });

        navigationDown.setOnAction(e -> {
            moveAll(0, 50, buses, routes);
            window.setScene(getMainScreen(buses, routes, window));
        });

        Group navGroup = new Group();
        navGroup.getChildren().addAll(sideBar, navigationCenter, navigationDown, navigationLeft, navigationRight, navigationUp);

        GridPane grid = new GridPane();
        grid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        /*
        VBox sideBox = new VBox(10);
        sideBox.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));

        sideBox.getChildren().addAll(
                sideBar, navGroup
        );

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(sideBox);
        scroll.pannableProperty().set(true);
        scroll.fitToHeightProperty().set(true);
        scroll.fitToWidthProperty().set(true);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
         */
        msGroup.setPrefSize(globalWidth * 3/4, globalHeight);
        msGroup.setClip(new Rectangle(msGroup.getPrefWidth(), msGroup.getPrefHeight()));
        grid.add(msGroup, 0, 0, 1, 1);
        grid.add(navGroup, 1, 0, 1, 1);

        ColumnConstraints column1 = new ColumnConstraints(globalWidth * 3 / 4);
        ColumnConstraints column2 = new ColumnConstraints(globalWidth * 1/4);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        Scene scene = new Scene(grid, globalWidth, globalHeight);
        return scene;
    }

    public void moveAll(int x, int y, Collection<Bus> buses, Collection<Route> routes) {
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
            Stop[] newStopList = new Stop[route.getStops().length];
            for (int i = 0; i < route.getStops().length; i++) {
                Stop stop = route.getStops()[i];
                int newPointX = (int) stop.getScreenLocation().getX() + x;
                int newPointY = (int) stop.getScreenLocation().getY() + y;
                stop.setScreenLocation(new Point(newPointX, newPointY));
                newStopList[i] = stop;
            }
            route.setStops(newStopList);
            newRouteList.add(route);
        }
        routes.removeAll(routes);
        for (int i = 0; i < newRouteList.size(); i++) {
            routes.add(newRouteList.get(i));
        }
    }

    public Scene getMainWithSidebar(Collection<Bus> buses, Collection<Route> routes, Stage window) {
        ArrayList<ImageView> busImages = new ArrayList<>();
        Iterator<Bus> iterator = buses.iterator();
        while(iterator.hasNext()) {
            Bus b = iterator.next();
            ImageView newBus = createImage("bus_icon.PNG", (int) b.getLocation().getX(), (int) b.getLocation().getY(), 80, 50);
            busImages.add(newBus);
        }

        ArrayList<Circle> stopImages = new ArrayList<>();
        ArrayList<Line> routeLines = new ArrayList<>();
        ArrayList<Label> stopLabels = new ArrayList<>();
        Iterator<Route> routeIterator = routes.iterator();

        while (routeIterator.hasNext()){
            Route currRoute = routeIterator.next();
            for (int j = 0; j < currRoute.getStops().length; j++) {
                Stop stop = currRoute.getStops()[j];
                Circle newStop = new Circle(stop.getLocation().getX(), stop.getLocation().getY(), 20);
                newStop.setFill(currRoute.getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getLocation().getX() - 5, (int) stop.getLocation().getY() - 10, 15, Color.BLACK, 20);
                stopLabels.add(stopLabel);

                Line stopLine = null;
                if (j != currRoute.getStops().length - 1) {
                    Point stop1 = currRoute.getStops()[j].getLocation();
                    Point stop2 = currRoute.getStops()[j + 1].getLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                } else {
                    Point stop1 = currRoute.getStops()[j].getLocation();
                    Point stop2 = currRoute.getStops()[0].getLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(currRoute.getColor());
                    stopLine.setStrokeWidth(10);
                }
                routeLines.add(stopLine);
            }
        }


        Group msGroup = new Group();

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

        GridPane grid = new GridPane();
        grid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox sideBar = new VBox(10);
        sideBar.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));

        Button sideBarToggle = createButton(globalWidth * 54 / 64, globalHeight * 1 / 256, 50, 50, Color.ORANGE, "", 50);
        sideBarToggle.setGraphic(createImage("hamburger.png", (int) sideBarToggle.getLayoutX(), (int) sideBarToggle.getLayoutY(), 50, 50));

        sideBarToggle.setOnAction(e -> {
            window.setScene(getMainScreen(buses, routes, window));
        });

        Label busLab = createLabel("Buses", 100, 50, 35, Color.BLACK, 500);
        Label routeLab = createLabel("Routes", 100, 50, 35, Color.BLACK, 500);
        Label stopLab = createLabel("Stops", 100, 50, 35, Color.BLACK, 500);

        sideBar.setMargin(busLab, new Insets(30, 20, 0, 40));
        sideBar.setMargin(routeLab, new Insets(30, 20, 0, 40));
        sideBar.setMargin(stopLab, new Insets(30, 20, 00, 40));

        // Dummy Data
        Button bus1 = createButton(0, 0, 100, 50, Color.BLACK, "Bus 1", 20);
        Button bus2 = createButton(0, 0, 100, 50, Color.BLACK, "Bus 2", 20);
        Button route1 = createButton(0, 0, 100, 50, Color.BLACK, "Route 1", 20);
        Button route2 = createButton(0, 0, 100, 50, Color.BLACK, "Route 2", 20);
        Button stop1 = createButton(0, 0, 100, 50, Color.BLACK, "Stop 1", 20);
        Button stop2 = createButton(0, 0, 100, 50, Color.BLACK, "Stop 2", 20);

        sideBar.setMargin(bus1, new Insets(0, 0, 0, 60));
        sideBar.setMargin(bus2, new Insets(0, 0, 0, 60));
        sideBar.setMargin(route1, new Insets(0, 0, 0, 60));
        sideBar.setMargin(route2, new Insets(0, 0, 0, 60));
        sideBar.setMargin(stop1, new Insets(0, 0, 0, 60));
        sideBar.setMargin(stop2, new Insets(0, 0, 0, 60));

        sideBar.getChildren().addAll(
                sideBarToggle,
                busLab, bus1, bus2,
                routeLab, route1, route2,
                stopLab, stop1, stop2
        );

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(sideBar);
        scroll.pannableProperty().set(true);
        scroll.fitToHeightProperty().set(true);
        scroll.fitToWidthProperty().set(true);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);

        grid.add(msGroup, 0, 0, 1, 1);
        grid.add(scroll, 1, 0, 1, 1);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(75);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(25);
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        grid.getRowConstraints().add(row1);

        // VBox inside a scroll pane for sidebar, everything inside a grid.
        Scene scene = new Scene(grid, globalWidth, globalHeight);
        return scene;
    }

    /*

    public Scene getBusScene(Bus bus) {

    }

    public Scene getRouteScene(Route route) {

    }

    public Scene getStopScene(Stop stop) {

    }
    */

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
