package com.example.SpringBatchTutorial.job.filereadwrite.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
}
