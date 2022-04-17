package com.resttemp.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTranslateDTO {
	
	@JsonProperty(value = "region")
	public String region;
	
	@JsonProperty(value = "translation")
	public String translation;
}
