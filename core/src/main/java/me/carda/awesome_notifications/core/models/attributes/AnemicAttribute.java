package me.carda.awesome_notifications.core.models.attributes;

abstract public class AnemicAttribute<T> {
    T value;
    public T getValue() { return value; }
    AnemicAttribute(T value){
        this.value = value;
    }
}
