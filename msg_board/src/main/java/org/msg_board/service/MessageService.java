package org.msg_board.service;

import org.msg_board.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageService {

    private List<Message> messages;

    public MessageService() {
        messages = new ArrayList<>();
    }

    public void add(Message message) {
        if (findByTitle(message.getTitle()) == null) {
            messages.add(message);
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    private Message findByTitle(String title) {
        return messages.stream()
                .filter(n -> n.getTitle()
                        .equals(title))
                .findFirst().orElse(null);
    }
}
