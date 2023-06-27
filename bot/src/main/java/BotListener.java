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


public class BotListener extends ListenerAdapter {

    private static HttpURLConnection connection;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
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
