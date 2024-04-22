package com.petbuddyz.petbuddyzapp.Exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;

    // Default constructor
    public CustomErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.status = 0; // Set default status, you can change it to any default value you prefer
        this.error = "Unknown Error";
    }
}
