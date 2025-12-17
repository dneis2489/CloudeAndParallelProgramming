package com.example.vkvision.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class MultiobjectLabel {
    private int status;
    private String name;
    private List<ObjectLabel> labels;
}
