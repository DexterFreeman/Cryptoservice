import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;


import java.net.*;


public class DiscordBot {

    public static final String url = "http://127.0.0.1:8000/predictions/";
    private static HttpURLConnection connection;
    public static void main(String[] args) throws LoginException {
        //Key hidden from github :)
        JDA bot = JDABuilder.createDefault(Keys.bot_key).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new BotListener()).build();

    }



}
