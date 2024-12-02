import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
public class Main extends Frame {
    private TextField cityInput;
    private TextArea resultArea;
    private final String apiKey = "036939265f38b7943c9d6acc5a5db0b0";
    // Your OpenWeatherMap API key
    public Main() {
        setTitle("Weather Forecasting System");
        setSize(400, 300);
        setLayout(new FlowLayout());
        Label cityLabel = new Label("Enter City:");
        cityInput = new TextField(20);
        Button forecastButton = new Button("Get Forecast");
        resultArea = new TextArea(10, 30);
        add(cityLabel);
        add(cityInput);
        add(forecastButton);
        add(resultArea);
        forecastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getWeatherForecast();
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }
    private void getWeatherForecast() {
        String city = cityInput.getText();
        if (city.isEmpty()) {
            resultArea.setText("Please enter a city name.");
            return;
        }
        try {
            String response = fetchWeatherData(city);
            if (response != null) {
                parseAndDisplayWeatherData(response);
            } else {
                resultArea.setText("Error fetching weather data. Please try again.");
            }
        } catch (IOException e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
    private String fetchWeatherData(String city) throws IOException {
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + apiKey + "&units=metric";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                Scanner scanner = new Scanner(errorStream);
                StringBuilder errorResponse = new StringBuilder();
                while (scanner.hasNext()) {
                    errorResponse.append(scanner.nextLine());
                }
                scanner.close();
                System.out.println("Error Response: " + errorResponse.toString());
// Debugging output
            }
            return null;
        }
    }
    private void parseAndDisplayWeatherData(String response) {
        String temperature = "N/A";
        String humidity = "N/A";
        String condition = "N/A";
// Simple JSON parsing without using libraries
        try {
            int tempIndex = response.indexOf("\"temp\":") + 7;
            int tempEndIndex = response.indexOf(",", tempIndex);
            temperature = response.substring(tempIndex, tempEndIndex) + "°C";
            int humidityIndex = response.indexOf("\"humidity\":") + 11;
            int humidityEndIndex = response.indexOf(",", humidityIndex);
            humidity = response.substring(humidityIndex, humidityEndIndex) + "%";
            int conditionIndex = response.indexOf("\"main\":\"") + 8;
            int conditionEndIndex = response.indexOf("\"", conditionIndex);
            condition = response.substring(conditionIndex, conditionEndIndex);
        } catch (Exception e) {
            resultArea.setText("Error parsing weather data.");
            return;
        }
        String forecast = "Weather in " + cityInput.getText() + ":\n" +
                "Temperature: " + temperature + "\n" +
                "Humidity: " + humidity + "\n" +
                "Condition: " + condition;
        resultArea.setText(forecast);
    }
    public static void main(String[] args) {
        new Main();
    }
        }
