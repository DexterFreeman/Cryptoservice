import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class DiscordBot extends ListenerAdapter {


    public static void main(String[] args) throws LoginException {
        //Key hidden from github :)
        JDA bot = JDABuilder.createDefault(Keys.bot_key).addEventListeners(new DiscordBot()).build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(!event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();

            switch (message){

                case "!latestpredictions":
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
