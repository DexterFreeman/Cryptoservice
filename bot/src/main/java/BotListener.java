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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BotListener extends ListenerAdapter {

    private static HttpURLConnection connection;

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        StringBuffer responseContent = new StringBuffer();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/London"));
        ZonedDateTime scheduledTime = now.withHour(16).withMinute(45).withSecond(0);

        //if its already past the scheduled time
        if (now.compareTo(scheduledTime) > 0) {
            scheduledTime = scheduledTime.plusDays(1);
        }

        Duration durationUntilScheduledTime = Duration.between(now, scheduledTime);
        long initialDelayScheduledTime = durationUntilScheduledTime.getSeconds();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("Scheduled task running");
            JDA jda = event.getJDA();
            Guild guild = jda.getGuildById("1123175549435641866");
            TextChannel textChannel = guild.getTextChannelById("1123175549435641869");
            for (String currency : BotUtils.crypto_currencies_list
            ) {
                String line = null;
                BufferedReader reader = null;
                JSONObject jsonObject = null;
                responseContent.delete(0, responseContent.length());
                System.out.println("http://127.0.0.1:8000/predictions/get/?currency=" + currency);
                try {
                    URL url = new URL("http://127.0.0.1:8000/predictions/get/?currency=" + currency);
                    connection = (HttpURLConnection) url.openConnection();

                    //Request setup
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(500000);

                    int status = connection.getResponseCode();
                    System.out.println(status);
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
                        reader.close();
                    }

                    System.out.println(responseContent.toString());
                    jsonObject = new JSONObject(responseContent.toString());
                    System.out.println(jsonObject);
                    textChannel.sendMessage("Currency: " + jsonObject.get("currency")).queue();
                    textChannel.sendMessage("Predicted price: " + jsonObject.get("predicted_price")).queue();


                } catch (MalformedURLException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, initialDelayScheduledTime, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        if (!event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();
            switch (message) {
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
                        System.out.println(responseContent.toString());
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
                    //create a new prediction for each crypto
                    break;

                case "!getprediction":
                    //code for getting a prediction.
                    break;
            }
        }
    }
}
