package tk.fungy.wsformc.managers;

import java.util.concurrent.TimeUnit;

public class TimeCounterManager {
    private long startTime;
    private long stopTime;
    public static boolean running;

    public TimeCounterManager() {
        reset();
    }

    public void start() {
        if (!running) {
            this.startTime = System.currentTimeMillis();
            this.running = true;
        }
    }

    public void stop() {
        if (running) {
            this.stopTime = System.currentTimeMillis();
            this.running = false;
        }
    }

    public void reset() {
        this.startTime = 0;
        this.stopTime = 0;
        this.running = false;
    }

    public String getTimeCounter() {
        long elapsedTime = running ? System.currentTimeMillis() - startTime : stopTime - startTime;
        long days = TimeUnit.MILLISECONDS.toDays(elapsedTime);
        long years = days / 365;
        days = days % 365;
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) - (TimeUnit.MILLISECONDS.toHours(elapsedTime) * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - (TimeUnit.MILLISECONDS.toMinutes(elapsedTime) * 60);
        if (years > 0) {
            return years + " years, " + days + " days, " + String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        if (days > 0) {
            return days + " days, " + String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
