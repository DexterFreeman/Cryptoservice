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
import java.net.URL;
import java.nio.channels.Channel;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BotListener extends ListenerAdapter {

    private static HttpURLConnection connection;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("Scheduled task running");
            JDA jda = event.getJDA();
            Guild guild = jda.getGuildById("1123175549435641866");
            TextChannel textChannel = guild.getTextChannelById("1123175549435641869");
            for (String currency : BotUtils.crypto_currencies_list) {
                try {
                    String predictionUrl = "http://127.0.0.1:8000/predictions/get/?currency=" + currency;
                    JSONObject predictionJson = getRequestJson(predictionUrl);
                    System.out.println(predictionJson);
                    textChannel.sendMessage("Currency: " + predictionJson.get("currency")).queue();
                    textChannel.sendMessage("Predicted price: " + predictionJson.get("predicted_price")).queue();
                    double predictedPrice = Double.parseDouble((String) predictionJson.get("predicted_price"));

                    JSONObject bitcoinJson = getRequestJson("https://api.coingecko.com/api/v3/coins/" + BotUtils.crypto_currencies_names.get(BotUtils.crypto_currencies_list.indexOf(currency)) + "?localization=false&market_data=true");
                    JSONObject marketData = bitcoinJson.getJSONObject("market_data");
                    JSONObject currentPrice = marketData.getJSONObject("current_price");
                    double usdPrice = currentPrice.getDouble("usd");
                    System.out.println(usdPrice);
                    textChannel.sendMessage("Current price: " + usdPrice);

                    if (predictedPrice > usdPrice) {
                        textChannel.sendMessage("BUY").queue();
                    } else {
                        textChannel.sendMessage("SELL").queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, getInitialDelay(), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
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

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        if (!event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();
            if (message.charAt(0) != '!'){

                List<String> messageArray = List.of(message.split(" "));
                switch (messageArray.get(0)) {
                    case "!latestprediction":
                        try {
                            URL url = new URL("http://127.0.0.1:8000/predictions/api/latest?format=json");
                            connection = (HttpURLConnection) url.openConnection();

                            //Request setup
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);

                            int status = connection.getResponseCode();
                            if (status > 299) {
                                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                                while ((line = reader.readLine()) != null) {
                                    responseContent.append(line);
                                }
                                reader.close();
                            } else {
                                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while ((line = reader.readLine()) != null) {
                                    responseContent.append(line);
                                }
                            }
                            JSONObject jsonObject = new JSONObject(responseContent.toString());
                            System.out.println(jsonObject.get("predicted_price"));
                            event.getChannel().sendMessage("Timestamp: " + jsonObject.get("timestamp")).queue();
                            event.getChannel().sendMessage("Cryptocurrency: " + jsonObject.get("cryptocurrency")).queue();
                            event.getChannel().sendMessage("Predicted price: " + jsonObject.get("predicted_price")).queue();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //call latest prediction
                        break;

                    case "!createpredictions":
                        JDA jda = event.getJDA();
                        Guild guild = jda.getGuildById("1123175549435641866");
                        TextChannel textChannel = guild.getTextChannelById("1123175549435641869");
                        for (String currency : BotUtils.crypto_currencies_list) {
                            try {
                                String predictionUrl = "http://127.0.0.1:8000/predictions/get/?currency=" + currency;
                                JSONObject predictionJson = getRequestJson(predictionUrl);
                                System.out.println(predictionJson);
                                textChannel.sendMessage("Currency: " + predictionJson.get("currency")).queue();
                                textChannel.sendMessage("Predicted price: " + predictionJson.get("predicted_price")).queue();
                                double predictedPrice = Double.parseDouble((String) predictionJson.get("predicted_price"));

                                JSONObject bitcoinJson = getRequestJson("https://api.coingecko.com/api/v3/coins/" + currency + "?localization=false&market_data=true");
                                JSONObject marketData = bitcoinJson.getJSONObject("market_data");
                                JSONObject currentPrice = marketData.getJSONObject("current_price");
                                double usdPrice = currentPrice.getDouble("usd");
                                System.out.println(usdPrice);
                                textChannel.sendMessage("Current price: " + usdPrice);

                                if (predictedPrice > usdPrice) {
                                    textChannel.sendMessage("BUY").queue();
                                } else {
                                    textChannel.sendMessage("SELL").queue();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "!getprediction":
                        String currency = messageArray.get(1);
                        
                        //code for getting a prediction.
                        break;


                }
            }
            }


    }
}
