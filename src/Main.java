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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Cursor;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
            ArrayList<Bus> buses = new ArrayList<>();
            ArrayList<Route> routes = new ArrayList<>();
            //For now, fill with random values
            for (int i = 0; i < 3; i ++) {
                Stop[] stops = new Stop[4];
                for (int j = 0; j < 4; j++) {
                    stops[j]  = new Stop("stop", (int) (Math.random() * 20), 1, new Point((int) (Math.random() * (globalWidth - 50)), (int)(Math.random() * (globalHeight - 50))));
                    System.out.println("stop" + stops[j].getLocation());
                }
                Color routeColor;
                if (i == 0) {
                    routeColor = Color.BLUE;
                }
                else if (i == 1) {
                    routeColor = Color.GREEN;
                }
                else {
                    routeColor = Color.RED;
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

    public Scene getMainScreen(ArrayList<Bus> buses, ArrayList<Route> routes, Stage window) {
        ArrayList<ImageView> busImages = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
            ImageView newBus = createImage("bus_icon.PNG", (int) b.getLocation().getX(), (int) b.getLocation().getY(), 80, 50);
            busImages.add(newBus);
        }

        ArrayList<Circle> stopImages = new ArrayList<>();
        ArrayList<Line> routeLines = new ArrayList<>();
        ArrayList<Label> stopLabels = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            for (int j = 0; j < routes.get(i).getStops().length; j++) {
                Stop stop = routes.get(i).getStops()[j];
                Circle newStop = new Circle(stop.getLocation().getX(), stop.getLocation().getY(), 20);
                newStop.setFill(routes.get(i).getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getLocation().getX() - 5, (int) stop.getLocation().getY() - 10, 15, Color.BLACK, 20);
                stopLabels.add(stopLabel);

                Line stopLine = null;
                if (j != routes.get(i).getStops().length - 1) {
                    Point stop1 = routes.get(i).getStops()[j].getLocation();
                    Point stop2 = routes.get(i).getStops()[j + 1].getLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(routes.get(i).getColor());
                    stopLine.setStrokeWidth(10);
                } else {
                    Point stop1 = routes.get(i).getStops()[j].getLocation();
                    Point stop2 = routes.get(i).getStops()[0].getLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(routes.get(i).getColor());
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

        Button sideBar = createButton(globalWidth * 6 / 8, globalHeight * 1/4, 350, 50, Color.ORANGE, "SIDEBAR", 50);
        sideBar.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        sideBar.setOnMouseEntered(e -> sideBar.setTextFill(Color.RED));
        sideBar.setOnMouseExited(e -> sideBar.setTextFill(Color.ORANGE));

        sideBar.setOnAction(e -> {
            window.setScene(getMainWithSidebar(buses, routes, window));
        });

        msGroup.getChildren().add(sideBar);

        Scene scene = new Scene(msGroup, globalWidth, globalHeight);
        return scene;
    }

    public Scene getMainWithSidebar(ArrayList<Bus> buses, ArrayList<Route> routes, Stage window) {
        ArrayList<ImageView> busImages = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
            ImageView newBus = createImage("bus_icon.PNG", (int) b.getLocation().getX(), (int) b.getLocation().getY(), 80, 50);
            busImages.add(newBus);
        }

        ArrayList<Circle> stopImages = new ArrayList<>();
        ArrayList<Line> routeLines = new ArrayList<>();
        ArrayList<Label> stopLabels = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            for (int j = 0; j < routes.get(i).getStops().length; j++) {
                Stop stop = routes.get(i).getStops()[j];
                Circle newStop = new Circle(stop.getLocation().getX(), stop.getLocation().getY(), 20);
                newStop.setFill(routes.get(i).getColor());
                stopImages.add(newStop);

                Label stopLabel = createLabel(Integer.toString(stop.getID()), (int) stop.getLocation().getX() - 5, (int) stop.getLocation().getY() - 10, 15, Color.BLACK, 20);
                stopLabels.add(stopLabel);

                Line stopLine = null;
                if (j != routes.get(i).getStops().length - 1) {
                    Point stop1 = routes.get(i).getStops()[j].getLocation();
                    Point stop2 = routes.get(i).getStops()[j + 1].getLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(routes.get(i).getColor());
                    stopLine.setStrokeWidth(10);
                } else {
                    Point stop1 = routes.get(i).getStops()[j].getLocation();
                    Point stop2 = routes.get(i).getStops()[0].getLocation();
                    stopLine = new Line(stop1.getX(), stop1.getY(), stop2.getX(), stop2.getY());
                    stopLine.setStroke(routes.get(i).getColor());
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

        Scene scene = new Scene(msGroup, globalWidth, globalHeight);
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


