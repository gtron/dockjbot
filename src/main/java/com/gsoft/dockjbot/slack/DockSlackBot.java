package com.gsoft.dockjbot.slack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;

/**
 * A Slack Bot sample. You can create multiple bots by just
 * extending {@link Bot} class like this one.
 *
 * @author ramswaroop
 * @version 1.0.0, 05/06/2016
 */
@Component
public class DockSlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(DockSlackBot.class);

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }
    
    
    /**
     * Invoked when the bot receives a direct mention (@botname: message)
     * or a direct message. 
     * 
     * @param session
     * @param event
     */
    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE}, pattern = "^(<[^>]+>[\\s:]+)?status(\\s+\\d+)?$")
    public void statusCommand(WebSocketSession session, Event event,  Matcher matcher) {
        
        
        String num = "";
        
        if ( matcher.groupCount() > 1 ) { 
            num = matcher.group(2);
            
            if ( num == null ) num = "";
        }
        
        String command = "dock status " + num ;
        String options = " -noColors";
        
        long diff = System.currentTimeMillis() - Math.round( Double.parseDouble( event.getTs() ) * 1000 );
        
        if ( diff > 10000 ) {
            logger.debug("Timed out command: " + command );
            return;
        }
        
        reply(session, event, new Message("Dock command: " + command + "\n" + execDockCommand(command,options)  ));

    }
// "^(<[^>]+>[\\s:]+)?(\\s*\\d+)\\s+(ps|stop|start|getLastBuild)$"
    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE}, pattern = "^(<[^>]+>[\\s:]+)?(\\s*\\d+)\\s+(ps|stop|start)$")
    public void singleCommand(WebSocketSession session, Event event,  Matcher matcher) {

        String num = matcher.group(2);
        
        String command = "dock " + num + " " + matcher.group(3);
        String options = " ";
        
        long diff = System.currentTimeMillis() - Math.round( Double.parseDouble( event.getTs() ) * 1000 );
        
        if ( diff > 10000 ) {
            logger.debug("Timed out command: " + command );
            return;
        }
        
        reply(session, event, new Message("Executed command: " + command + "\n" + execDockCommand(command,options)  ));

    } 
    
    protected StringBuffer execDockCommand(String command, String options) {
        logger.info("Running command: " + command );
        
        StringBuffer sb = new StringBuffer();
        
        Runtime rt = Runtime.getRuntime();
        
        try {
            Process pr = rt.exec( command + options);


            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = null; 

            try {
                while ((line = input.readLine()) != null)
                    sb.append(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return sb;


        } catch (Exception e) {

            e.printStackTrace();
            return sb.append(" ----- Error : \n" ).append( e.getMessage());

        }
    }
    
    
//    @Controller(events = EventType.DIRECT_MENTION, pattern = "^(<[^>]+>) dock (\\d) (ps)$")
//    public void channelMentionDockPsCommand(WebSocketSession session, Event event,  Matcher matcher) {
//        
//        String num = matcher.group(2);
//        
//        String command = "dock status " + num + " ps";
//        
//        long diff = System.currentTimeMillis() - Math.round( Double.parseDouble( event.getTs() ) * 1000 );
//        
//        if ( diff > 10000 ) {
//            logger.debug("Timed out command: " + command );
//            return;
//        }
//        
//        logger.info("Running command: " + command );
//        
//        
//        Runtime rt = Runtime.getRuntime();
//        
//        try {
//            StringBuffer sb = new StringBuffer();
//            Process pr = rt.exec( command );
//
//
//            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//            String line = null; 
//
//            try {
//                while ((line = input.readLine()) != null)
//                    sb.append(line + "\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            reply(session, event, new Message("Dock command: " + command + "\n" + sb.toString()  ));
//
//
//        } catch (Exception e) {
//
//            reply(session, event, new Message("Dock command: " + command + " ----- Error : \n" +  e.getMessage()  ));
//
//            e.printStackTrace();
//        }
//
//    }
    
    /*
{
    $chan
    "text": "$msg_pre",
    "attachments": [
        {
            "color": "#$color",
            "pretty": 1,
            "text":"$msg"
            $Fields
        }
    ]
}

     */


    /**
     * Invoked when the bot receives a direct mention (@botname: message)
     * or a direct message. NOTE: These two event types are added by jbot
     * to make your task easier, Slack doesn't have any direct way to
     * determine these type of events.
     *
     * @param session
     * @param event
     */
//    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
//    public void onReceiveDM(WebSocketSession session, Event event) {
//        reply(session, event, new Message("Hi, I am DockSlackBot with username: " + slackService.getCurrentUser().getName()));
//    }

    /**
     * Invoked when bot receives an event of type message with text satisfying
     * the pattern {@code ([a-z ]{2})(\d+)([a-z ]{2})}. For example,
     * messages like "ab12xy" or "ab2bc" etc will invoke this method.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.MESSAGE, pattern = "^([a-z ]{2})(\\d+)([a-z ]{2})$")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        reply(session, event, new Message("First group: " + matcher.group(0) + "\n" +
                "Second group: " + matcher.group(1) + "\n" +
                "Third group: " + matcher.group(2) + "\n" +
                "Fourth group: " + matcher.group(3)));
    }


}