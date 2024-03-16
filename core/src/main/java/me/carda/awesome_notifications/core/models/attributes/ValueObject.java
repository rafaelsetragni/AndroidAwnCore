package me.carda.awesome_notifications.core.models.attributes;

abstract public class ValueObject<T> {
    T value;
    public T getValue() { return value; }
    protected ValueObject(T value){
        this.value = value;
    }
}
