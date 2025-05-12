package controllers;

import entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.paint.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TransportBookAdminController {
    @FXML private VBox chatContainer;
    @FXML private TextField inputField;
    @FXML private ScrollPane scrollPane;

    private TransportChatbot chatbot = new TransportChatbot();

    public void initialize() {
        // Initial bot message
        addMessage("Bot", "Any new transportation to be added? (yes/no)", false);

        // Auto-scroll to bottom when new messages are added
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });
    }

    @FXML
    private void handleSend() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        addMessage("You", userInput, true);
        inputField.clear();

        String botResponse = chatbot.processInput(userInput);
        addMessage("Bot", botResponse, false);
    }

    private void addMessage(String sender, String text, boolean isUser) {
        // Create message container
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5, 10, 5, 10));

        // Create message bubble
        VBox messageBubble = new VBox();
        messageBubble.getStyleClass().add("message-bubble");
        messageBubble.getStyleClass().add(isUser ? "user-message" : "bot-message");

        // Add sender label (only for bot messages)
        if (!isUser) {
            Label senderLabel = new Label(sender);
            senderLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");
            messageBubble.getChildren().add(senderLabel);
        }

        // Add message text
        Text messageText = new Text(text);
        messageText.setWrappingWidth(250);
        messageBubble.getChildren().add(messageText);

        // Add timestamp
        Label timeLabel = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("message-time");
        timeLabel.setPadding(new Insets(5, 0, 0, 0));
        messageBubble.getChildren().add(timeLabel);

        messageContainer.getChildren().add(messageBubble);
        chatContainer.getChildren().add(messageContainer);
    }
}
