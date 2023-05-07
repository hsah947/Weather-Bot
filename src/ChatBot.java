import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatBot extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JLabel response;
    private JTextField input;
    private JButton button;
    private JPanel panel;
    private boolean running = true;
    private String weatherApiKey = "";
    private String movieApiKey = " ";
    private boolean askedForWeather = false;
    private boolean askedForMovie = false;

    public ChatBot() {
        setTitle("ChatBot");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        response = new JLabel("Hi! How can I help you?");
        input = new JTextField(20);
        button = new JButton("Submit");
        button.addActionListener(this);

        panel = new JPanel();
        panel.add(response);
        panel.add(input);
        panel.add(button);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String inputText = input.getText();
        if (inputText.equals("quit")) {
            running = false;
        } else {
            String result = "";
            if (!askedForWeather && inputText.matches(".*(weather|Weather).*\\bof\\b.*")) {
                String city = inputText.replaceAll(".*(weather|Weather).*\\bof\\b", "").trim();
                result = getWeather(city);
                askedForWeather = true;
                askedForMovie = false;
            } else if (askedForWeather && !askedForMovie && !inputText.isEmpty()) {
                String movie = inputText.trim();
                result = getMovieRating(movie);
                askedForWeather = false;
                askedForMovie = true;
            } else {
                result = "Sorry, I didn't understand your input. Please ask for the weather of a city or the IMDb rating of a movie.";
            }
            response.setText("<html>" + response.getText() + "<br>" + result + "</html>");
        }
        input.setText("");
    }

    public String getWeather(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + weatherApiKey;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        try {
            JsonNode responseNode = new ObjectMapper().readTree(EntityUtils.toString(client.execute(request).getEntity()));
            double temp = responseNode.get("main").get("temp").asDouble() - 273.15;
            double humidity = responseNode.get("main").get("humidity").asDouble();
            double windSpeed = responseNode.get("wind").get("speed").asDouble();
            String weather = String.format("Temperature: %.2f°C | Humidity: %.0f%% | Wind Speed: %.1f m/s", temp, humidity, windSpeed);
            return weather;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    public String getMovieRating(String movie) {
        String url = "http://www.omdbapi.com/?t=" + movie + "&apikey=" + movieApiKey;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        try {
            JsonNode responseNode = new ObjectMapper().readTree(EntityUtils.toString(client.execute(request).getEntity()));
            String rating = responseNode.get("imdbRating").asText();
            return "IMDb rating: " + rating;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        new ChatBot();
    }

	
		
	
}

