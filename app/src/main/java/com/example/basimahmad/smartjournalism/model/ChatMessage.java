package com.example.basimahmad.smartjournalism.model;

/**
 * Created by basim on 09/05/2018.
 */

public class ChatMessage {
    public boolean left;
    public String message;
    public String pic;

    public ChatMessage(boolean left, String message, String pic) {
        super();
        this.left = left;
        this.message = message;
        this.pic = pic;
    }
}