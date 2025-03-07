package app.wendo.users.models;

public enum RegistrationStatus {
    STEP_1_COMPLETE,               // Basic registration with phone number
    PASSENGER_COMPLETE,            // Full passenger registration 
    DRIVER_DOCS_COMPLETE,          // Driver with personal documents
    REGISTRATION_COMPLETE          // Full registration (including car info for drivers)
}