package com.medi.imesh.drone.common;

/**
 * This class maintains common constants used in the application.
 */
public class ApplicationConstants {

    public static final int LOADING_BATTERY_THRESHOLD = 25;
    public static final int MONITORING_INTERVAL = 120000; // defined in milliseconds
    public enum DroneState {
        IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING
    }


}
