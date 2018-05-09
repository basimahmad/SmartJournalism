package com.example.basimahmad.smartjournalism.Notifications;

/**
 * Created by basim on 22/04/2018.
 */

public class NotificationModel {
    private String title, description, intent, dateTime, image;

    public NotificationModel() {
    }

    public NotificationModel(String title, String description, String intent, String dateTime, String image) {
        this.title = title;
        this.description = description;
        this.intent = intent;
        this.dateTime = dateTime;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
