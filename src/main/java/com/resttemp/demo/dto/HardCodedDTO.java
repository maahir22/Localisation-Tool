package com.resttemp.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HardCodedDTO {
	@JsonProperty(value = "raw_string")
	public String rawString;
	
	@JsonProperty(value = "uuid")
	public Long uid;
	
	@JsonProperty(value = "pos")
	public String pos;
}
