package app.wendo.wallet.models;

public enum PaymentRequestStatus {
    PENDING,    // Payment request is created but not yet processed
    PAID,       // Payment request has been processed and marked as paid
    REJECTED    // Payment request has been rejected
}