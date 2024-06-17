package com.example.earthquake;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Earthquake extends Application {

    private Stage stage;
    private Scene tableScene, pieChartScene;
    private TableView<EarthquakeData> tableView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream("/earthquake.png"));
        primaryStage.getIcons().add(icon);

        // Connect to MySQL database (replace with your database credentials)
        String url = "jdbc:mysql://localhost:3306/EarthquakeRecords2023";
        String user = "root";
        String password = "";
        Connection conn = DriverManager.getConnection(url, user, password);

        // Set up TableView for earthquake data
        tableView = createTableView(conn);

        // Set up PieChart for earthquake data
        PieChart pieChart = createPieChart(conn);

        // Button to switch to PieChart scene
        Button switchToPieChartButton = new Button("Switch to Pie Chart");
        switchToPieChartButton.setOnAction(e -> stage.setScene(pieChartScene));

        // Button to switch back to TableView scene
        Button switchToTableViewButton = new Button("Back to Table View");
        switchToTableViewButton.setOnAction(e -> stage.setScene(tableScene));

        // Layout for TableView scene
        VBox tableLayout = new VBox(10);
        tableLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        tableLayout.getChildren().addAll(tableView, switchToPieChartButton);
        tableScene = new Scene(tableLayout, 800, 600);

        // Layout for PieChart scene
        VBox pieChartLayout = new VBox(10);
        pieChartLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        pieChartLayout.getChildren().addAll(pieChart, switchToTableViewButton);
        pieChartScene = new Scene(pieChartLayout, 800, 600);

        // Close database connection
        conn.close();

        // Set initial scene
        stage.setScene(tableScene);
        stage.setTitle("Earthquake Data 2023");
        stage.show();
    }

    private TableView<EarthquakeData> createTableView(Connection conn) throws Exception {
        TableView<EarthquakeData> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns
        TableColumn<EarthquakeData, String> dateColumn = new TableColumn<>("Date and Time");
        TableColumn<EarthquakeData, String> locationColumn = new TableColumn<>("Location");
        TableColumn<EarthquakeData, Double> magnitudeColumn = new TableColumn<>("Magnitude");
        TableColumn<EarthquakeData, Double> depthColumn = new TableColumn<>("Depth (km)");
        TableColumn<EarthquakeData, Double> latitudeColumn = new TableColumn<>("Latitude");
        TableColumn<EarthquakeData, Double> longitudeColumn = new TableColumn<>("Longitude");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        magnitudeColumn.setCellValueFactory(new PropertyValueFactory<>("magnitude"));
        depthColumn.setCellValueFactory(new PropertyValueFactory<>("depthKm"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));

        tableView.getColumns().addAll(dateColumn, locationColumn, magnitudeColumn, depthColumn, latitudeColumn, longitudeColumn);

        // Query data from database
        String query = "SELECT date_time, location, magnitude, depth_km, latitude, longitude FROM Earthquakes";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Populate table with data
        List<EarthquakeData> dataList = new ArrayList<>();
        while (resultSet.next()) {
            String dateTime = resultSet.getString("date_time");
            String location = resultSet.getString("location");
            double magnitude = resultSet.getDouble("magnitude");
            double depthKm = resultSet.getDouble("depth_km");
            double latitude = resultSet.getDouble("latitude");
            double longitude = resultSet.getDouble("longitude");
            EarthquakeData data = new EarthquakeData(dateTime, location, magnitude, depthKm, latitude, longitude);
            dataList.add(data);
        }

        tableView.getItems().addAll(dataList);

        // Close database connection
        resultSet.close();
        stmt.close();

        return tableView;
    }

    private PieChart createPieChart(Connection conn) throws Exception {
        // Query data from database
        String query = "SELECT location, COUNT(*) AS count FROM Earthquakes GROUP BY location";

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Prepare data for pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        while (resultSet.next()) {
            String location = resultSet.getString("location");
            int count = resultSet.getInt("count");
            pieChartData.add(new PieChart.Data(location, count));
        }

        // Close database connection
        resultSet.close();
        stmt.close();

        // Create PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Number of Earthquakes by Location");

        return pieChart;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // EarthquakeData class for TableView
    public static class EarthquakeData {
        private final String dateTime;
        private final String location;
        private final double magnitude;
        private final double depthKm;
        private final double latitude;
        private final double longitude;

        public EarthquakeData(String dateTime, String location, double magnitude, double depthKm, double latitude, double longitude) {
            this.dateTime = dateTime;
            this.location = location;
            this.magnitude = magnitude;
            this.depthKm = depthKm;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getDateTime() {
            return dateTime;
        }

        public String getLocation() {
            return location;
        }

        public double getMagnitude() {
            return magnitude;
        }

        public double getDepthKm() {
            return depthKm;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
