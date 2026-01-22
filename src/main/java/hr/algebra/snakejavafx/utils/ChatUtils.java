package hr.algebra.snakejavafx.utils;

import hr.algebra.snakejavafx.SnakeApplication;
import hr.algebra.snakejavafx.jndi.ConfigurationKey;
import hr.algebra.snakejavafx.jndi.ConfigurationReader;
import hr.algebra.snakejavafx.rmi.ChatRemoteService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;

public class ChatUtils {

    public static Timeline getChatRefreshTimeline(ChatRemoteService chatRemoteService,
                                                  TextArea chatMessageTextArea) {
        Timeline chatMessageRefreshTimeLine = new Timeline(new KeyFrame(Duration.ZERO, e ->{
            try {
                List<String> chatMessages = chatRemoteService.getAllMessages();

                StringBuilder textMessagesBuilder = new StringBuilder();

                for (String message : chatMessages) {
                    textMessagesBuilder.append(message).append("\n");
                }

                chatMessageTextArea.setText(textMessagesBuilder.toString());

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }), new KeyFrame(Duration.seconds(1)));
        chatMessageRefreshTimeLine.setCycleCount(Animation.INDEFINITE);
        return chatMessageRefreshTimeLine;
    }

    public static void SendChatMessage(ChatRemoteService chatRemoteService, TextField chatMessageTextField) {
        String chatMessage = chatMessageTextField.getText();
        try {
            chatRemoteService.sendChatMessage(SnakeApplication.playerType + ":" + chatMessage);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<ChatRemoteService> initializeChatRemoteService() {
        Optional<ChatRemoteService> chatRemoteServiceOptional = Optional.empty();
        try{
            Registry registry = LocateRegistry.getRegistry(ConfigurationReader.getStringValueForKey(
                            ConfigurationKey.HOSTNAME),
                    ConfigurationReader.getIntegerValueForKey(ConfigurationKey.RMI_PORT));
            chatRemoteServiceOptional = Optional.of((ChatRemoteService) registry.lookup(ChatRemoteService.REMOTE_OBJECT_NAME));
        }catch(RemoteException | NotBoundException e){
            throw new RuntimeException(e);
        }
        return chatRemoteServiceOptional;
    }
}
