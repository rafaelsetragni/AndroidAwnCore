package me.carda.awesome_notifications.core.models.attributes;

public class Base64Secret extends AnemicAttribute<String>{
    public final String configReference;
    public Base64Secret(String configReference, String value) {
        super(value);
        this.configReference = configReference;
    }
}
