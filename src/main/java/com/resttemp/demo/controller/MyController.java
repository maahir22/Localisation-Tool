package com.resttemp.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resttemp.demo.dao.RepoMonitorDao;
import com.resttemp.demo.dto.RepoMonitor;
import com.resttemp.demo.services.EnglishOrCode;
import com.resttemp.demo.services.LocaliseService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MyController {
	private static final String PRUrl = "https://api.github.com/repos/maahir22/LocalisationTest/pulls";

	@Autowired
	private LocaliseService localiseService;
	
	@Autowired
	private EnglishOrCode englishOrCode;
	
	@Autowired
	private RepoMonitorDao repoMonitorDao;
	
	private static Integer demoRateLimiter = 0;

	@GetMapping("/testClose")
	public void close(@RequestParam String testId) {
		localiseService.addComments(testId, "TESTING");
	}
	
	@PostMapping("/tokenNLP")
	public Boolean invoc(@RequestBody String input) throws Exception{
		input = input.replaceAll("\"", "");
		input = input.replaceAll("\\\\", "");
		System.out.println(input);
		return englishOrCode.tokenCoder(input) && englishOrCode.RegEXEvaluation(input);
	}
	
	@PostMapping("/createEntry")
	public String createEntry(@RequestBody Map<String,String> mp) {
		String repoLink = mp.get("repo");
		String pat = mp.get("pat");
		
		RepoMonitor entry = new RepoMonitor();
		entry.repoLink = repoLink;
		entry.accessToken = pat;
		repoMonitorDao.save(entry);
		
		System.out.println(repoLink + " " + pat);
		return "OK";
	}
	
	@Scheduled(cron = "0 0 * ? * *")
	public void getPRs() throws JsonParseException, JsonMappingException, IOException {
		RepoMonitor repoMonitor = repoMonitorDao.findAll().iterator().next();
		if(repoMonitor == null) {
			return;
		}
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Object[]> response = restTemplate.getForEntity(PRUrl, Object[].class);
		Object[] allPR = response.getBody();
		for (Object currPR : allPR) {
			if (demoRateLimiter == 3) {
				break;
			}
			String firstJson = new ObjectMapper().writeValueAsString(currPR);
			firstJson = firstJson.replaceAll("\\|", "");
			Map<String, Object> resp = new ObjectMapper().readValue(firstJson, HashMap.class);
			String currentNumber = String.valueOf(resp.get("number"));
			localiseService.gitPropogatorParser(currentNumber);
			demoRateLimiter++;
		}
	}
	
	@GetMapping("/init")
	@Scheduled(cron = "0 0 * ? * *")
	public Object getAllPR() throws JsonParseException, JsonMappingException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Object[]> response = restTemplate.getForEntity(PRUrl, Object[].class);
		Object[] allPR = response.getBody();
		for (Object currPR : allPR) {
			if (demoRateLimiter == 3) {
				break;
			}
			String firstJson = new ObjectMapper().writeValueAsString(currPR);
			firstJson = firstJson.replaceAll("\\|", "");
			Map<String, Object> resp = new ObjectMapper().readValue(firstJson, HashMap.class);
			String currentNumber = String.valueOf(resp.get("number"));
			localiseService.gitPropogatorParser(currentNumber);
			demoRateLimiter++;
		}
		return "Localisation Correction done successfully";
	}
}