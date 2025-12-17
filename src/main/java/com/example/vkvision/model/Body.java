package com.example.vkvision.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public  class Body {
    @JsonProperty("multiobject_labels")
    private List<MultiobjectLabel> multiobject_labels;
}
