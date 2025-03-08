package app.wendo.trip.models;

public enum TripStatus {
    CREATED,      // Trip is created but not yet started
    SEARCHING,    // Driver is searching for passengers
    CONFIRMED,    // Trip is confirmed with passenger(s)
    IN_PROGRESS,  // Trip is in progress
    COMPLETED,    // Trip is completed
    CANCELLED     // Trip is cancelled
}
