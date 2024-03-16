package me.carda.awesome_notifications.core.logs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {

    private static final String redColor = "\u001B[31m";
    private static final String greenColor = "\u001B[32m";
    private static final String blueColor = "\u001B[94m";
    private static final String yellowColor = "\u001B[33m";
    private static final String resetColor = "\u001B[0m";

    private static final DateFormat dateFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss (z)", Locale.US);

    static Logger instance;
    @NonNull
    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }

    private String getCurrentTime(){
        return dateFormat.format(new Date());
    }

    private String getLastLine(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if(stackTrace.length < 5)
            return "?";
        return String.valueOf(stackTrace[4].getLineNumber());
    }

    public void d(@NonNull String className, @NonNull String message){
        Log.d("Android: "+greenColor+"[AwN]"+resetColor,  message + " (" + className + ":" + getLastLine() + ")");
    }

    public void i(@NonNull String className, @NonNull String message){
        Log.i("Android: "+blueColor+"[AwN]",  message +  " (" + className + ":" + getLastLine() + ")" + resetColor);
    }

    public void w(@NonNull String className, @NonNull String message){
        Log.w("Android: "+yellowColor+"[AwN]", message + " (" + className + ":" + getLastLine() + ")" + resetColor);
    }

    public void e(@NonNull String className, @NonNull String message){
        e(className, message, null);
    }

    public void e(@NonNull String className, @NonNull String message, @Nullable Exception exception){
        Log.e("Android: "+redColor+"[AwN]", message + " (" + className + ":" + getLastLine() + ")" + resetColor);
        if (exception != null) {
            exception.printStackTrace();
        }
    }

}
