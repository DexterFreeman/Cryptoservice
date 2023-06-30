import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BotListener extends ListenerAdapter {

    private static HttpURLConnection connection;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            JDA jda = event.getJDA();
            Guild guild = jda.getGuildById("1123175549435641866");
            TextChannel textChannel = guild.getTextChannelById("1123175549435641869");
            createDailyTask(textChannel);

    }, getInitialDelay(), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);



    }
    private void createDailyTask(TextChannel textChannel) {
        for (String currency : BotUtils.crypto_currencies_list) {
            try {
                String predictionUrl = "http://127.0.0.1:8000/predictions/get/?currency=" + currency;
                JSONObject predictionJson = getRequestJson(predictionUrl);
                double predictedPrice = Double.parseDouble((String) predictionJson.get("predicted_price"));
                textChannel.sendMessage("Currency: " + predictionJson.get("currency")).queue();
                textChannel.sendMessage("Predicted price: " + predictedPrice).queue();
                double currentPriceUSD = getCurrentPrice(BotUtils.crypto_currencies_names.get(BotUtils.crypto_currencies_list.indexOf(currency)));
                textChannel.sendMessage("Current price: " + currentPriceUSD).queue();

                if (predictedPrice > currentPriceUSD) {
                    textChannel.sendMessage("BUY").queue();
                } else {
                    textChannel.sendMessage("SELL").queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private double getCurrentPrice(String currency) throws IOException {
        String apiUrl = "https://api.coingecko.com/api/v3/coins/" + currency + "?localization=false&market_data=true";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl))
                .GET()
                .timeout(Duration.ofSeconds(25))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode <= 299) {
                JSONObject bitcoinJson = new JSONObject(response.body());
                JSONObject marketData = bitcoinJson.getJSONObject("market_data");
                JSONObject currentPrice = marketData.getJSONObject("current_price");

                Optional<Double> usdPriceOptional = getDoubleFromJSONObject(currentPrice, "usd");
                return usdPriceOptional.orElseThrow(() -> new IOException("USD price not found in API response."));
            } else {
                throw new IOException("Failed to fetch data from: " + apiUrl + ", status code: " + statusCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP request was interrupted.", e);
        }
    }

    private Optional<Double> getDoubleFromJSONObject(JSONObject jsonObject, String key) {
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            return Optional.of(jsonObject.getDouble(key));
        }
        return Optional.empty();
    }




    private long getInitialDelay() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/London"));
        ZonedDateTime scheduledTime = now.withHour(12).withMinute(8).withSecond(0);
        if (now.compareTo(scheduledTime) > 0) {
            scheduledTime = scheduledTime.plusDays(1);
        }
        Duration durationUntilScheduledTime = Duration.between(now, scheduledTime);
        return durationUntilScheduledTime.getSeconds();
    }

    private JSONObject getRequestJson(String url) throws IOException {
        StringBuilder responseContent = new StringBuilder();
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            //Very high because my django api is very slow :(
            connection.setConnectTimeout(25000);
            connection.setReadTimeout(2500000);

            int status = connection.getResponseCode();
            System.out.println(status);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    status > 299 ? connection.getErrorStream() : connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JSONObject(responseContent.toString());
    }

    private void getLatestPrediction(MessageReceivedEvent event) {
        String apiUrl = "http://127.0.0.1:8000/predictions/api/latest?format=json";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode <= 299) {
                JSONObject jsonObject = new JSONObject(response.body());

                String timestamp = jsonObject.getString("timestamp");
                String cryptocurrency = jsonObject.getString("cryptocurrency");

                Optional<Double> predictedPriceOptional = getDoubleFromJSONObject(jsonObject, "predicted_price");
                double predictedPrice = predictedPriceOptional.orElseThrow(() -> new IOException("Predicted price not found in API response."));

                event.getChannel().sendMessage("Timestamp: " + timestamp).queue();
                event.getChannel().sendMessage("Cryptocurrency: " + cryptocurrency).queue();
                event.getChannel().sendMessage("Predicted price: " + predictedPrice).queue();
            } else {
                throw new IOException("Failed to fetch data from: " + apiUrl + ", status code: " + statusCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch the latest prediction.", e);
        }
    }

    public void createPrediction(MessageReceivedEvent event){
        for (String currency : BotUtils.crypto_currencies_list) {
            try {
                String predictionUrl = "http://127.0.0.1:8000/predictions/get/?currency=" + currency;
                JSONObject predictionJson = getRequestJson(predictionUrl);
                System.out.println(predictionJson);
                event.getChannel().sendMessage("Currency: " + predictionJson.get("currency")).queue();
                event.getChannel().sendMessage("Predicted price: " + predictionJson.get("predicted_price")).queue();
                double predictedPrice = Double.parseDouble((String) predictionJson.get("predicted_price"));
                double currentPriceUSD = getCurrentPrice(BotUtils.crypto_currencies_names.get(BotUtils.crypto_currencies_list.indexOf(currency));

                event.getChannel().sendMessage("Current price: " + currentPriceUSD).queue();

                if (predictedPrice > currentPriceUSD) {
                    event.getChannel().sendMessage("BUY").queue();
                } else {
                    event.getChannel().sendMessage("SELL").queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void getPrediction(String currency, MessageReceivedEvent event){
        try {
            String predictionUrl = "http://127.0.0.1:8000/predictions/latest/?currency=" + currency;
            JSONObject predictionJson = getRequestJson(predictionUrl);
            event.getChannel().sendMessage("Timestamp: " + predictionJson.get("timestamp")).queue();
            event.getChannel().sendMessage("Cryptocurrency: " + predictionJson.get("cryptocurrency")).queue();
            event.getChannel().sendMessage("Predicted price: " + predictionJson.get("predicted_price")).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        System.out.println("Messaged received");

        if (!event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();
            if (message.charAt(0) == '!') {
                List<String> messageArray = List.of(message.split(" "));
                switch (messageArray.get(0)) {
                    case "!latestprediction":
                            getLatestPrediction(event);
                        break;

                    case "!createpredictions":
                            createPrediction(event);
                        break;

                    case "!getprediction":
                        String currency = messageArray.get(1);
                        getPrediction(currency, event);
                        break;
                }
            }
        }
    }
}
