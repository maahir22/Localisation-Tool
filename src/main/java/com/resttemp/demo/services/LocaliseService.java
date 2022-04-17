package com.resttemp.demo.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resttemp.demo.dao.LocaliseDBDao;
import com.resttemp.demo.dto.GoogleTranslateDTO;
import com.resttemp.demo.dto.HardCodedDTO;
import com.resttemp.demo.dto.LocaliseDB;
import com.resttemp.demo.dto.PathFileDTO;

@Service
public class LocaliseService {

	private static final String pullCommitURL = "https://api.github.com/repos/maahir22/LocalisationTest/pulls/";
	private static final String pullSuffix = "/commits";
	private static final String commitCodeEndPoint = "https://api.github.com/repos/maahir22/LocalisationTest/commits/";
	private static final String addCommentEndPoint = "https://api.github.com/repos/maahir22/LocalisationTest/issues/";
	private static final String commentSuffix = "/comments";
	private static Integer demoRateLimiter = 0;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Autowired
	private LocaliseDBDao localiseDBDao;
	
	public void gitPropogatorParser(String pullNumber) throws JsonProcessingException {
		String customCommitURL = pullCommitURL + pullNumber + pullSuffix;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Object[]> response = restTemplate.getForEntity(customCommitURL, Object[].class);
		for (Object currObject : response.getBody()) {
			if (demoRateLimiter == 3) {
				break;
			}
			String firstJson = new ObjectMapper().writeValueAsString(currObject);
			firstJson = firstJson.replaceAll("\\|", "");
			try {
				Map<String, Object> resp = new ObjectMapper().readValue(firstJson, HashMap.class);
				String commitHash = String.valueOf(resp.get("sha"));
				System.out.println(commitHash);
				getCommitData(commitHash, pullNumber);
				demoRateLimiter++;
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(response.getBody());
	}

	public void getCommitData(String commitHash, String prID) throws IOException {
		String customCommitFetchURL = commitCodeEndPoint + commitHash;
		RestTemplate restTemplate = new RestTemplate();
		Boolean shouldClose = false;
		ResponseEntity<Object> response = restTemplate.getForEntity(customCommitFetchURL, Object.class);
		String firstJson;
		try {
			firstJson = new ObjectMapper().writeValueAsString(response.getBody());
			firstJson = firstJson.replaceAll("\\|", "");
			Map<String, Object> resp = new ObjectMapper().readValue(firstJson, HashMap.class);
			Object outerPatch = resp.get("files");
			String outerPatchString = new ObjectMapper().writeValueAsString(outerPatch);
			outerPatchString = outerPatchString.replace("\\|", "");
			System.out.println(outerPatch);
			List<PathFileDTO> file = new ObjectMapper().readValue(outerPatchString,
					new TypeReference<List<PathFileDTO>>() {
					});
//			Map<String, Object> files = new ObjectMapper().convertValue(resp.get("files"), HashMap.class);
			for (PathFileDTO currDTO : file) {
				Object currPatch = currDTO.rawUrl;
				String patchStr = (String.valueOf(currPatch));
				ResponseEntity<String> allCode = restTemplate.getForEntity(patchStr, String.class);
				System.out.println("CONTENT *** ");
				
				Map<String, String> req = new HashMap<>();
				req.put("input", allCode.getBody());
				HttpHeaders header = new HttpHeaders();
				HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, header);
				
				String hardCoded = restTemplate.postForObject("http://localhost:4444/fileParser", entity, String.class);
				List<HardCodedDTO> hardCodedStrings = new ObjectMapper().readValue(hardCoded,
						new TypeReference<List<HardCodedDTO>>() {
						});
				String comment = "Consider removing hardcoded strings - ";
				for(HardCodedDTO currHard : hardCodedStrings) {
					comment += currHard.rawString + " @ Line Number : ";
					comment += currHard.pos;
					comment += " ,";
					System.out.println(currHard.rawString + " : " + currHard.uid);
					String[] valuesInQuotes = StringUtils.substringsBetween(currHard.rawString , "\"", "\"");
					if(!ObjectUtils.isEmpty(valuesInQuotes)) {
						threadPoolTaskExecutor.submit(()->proactiveMechanism(valuesInQuotes[0], currHard.uid));
						System.out.println("Need localisation : " + valuesInQuotes[0]);
					}
				}
				if(!ObjectUtils.isEmpty(hardCodedStrings)) {
					shouldClose = true;
					System.out.println(comment);
					addComments(prID, comment);
				}
				System.out.println(hardCodedStrings);
			}
			if(shouldClose) {
				closePR(prID, "Closing due to presence of hardcoded strings");
			}
//			System.out.println(file);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(response.getBody());
	}

	private void proactiveMechanism(String string, Long uid) {
		System.out.println("Consumed and acknowledged !!!  " + string + " " + uid);
		RestTemplate restTemplate = new RestTemplate();
		
		Map<String, String> req = new HashMap<>();
		req.put("input", string);
		HttpHeaders header = new HttpHeaders();
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, header);
		
		String translatedString = restTemplate.postForObject("http://localhost:6666/fakeGoogleTranslate", entity, String.class);
		try {
			List<GoogleTranslateDTO> translatedStrings = new ObjectMapper().readValue(translatedString,
					new TypeReference<List<GoogleTranslateDTO>>() {
					});
			LocaliseDB localiseDB1 = new LocaliseDB();
			localiseDB1.content = string;
			localiseDB1.region = "en";
			localiseDB1.messageID = uid;
			localiseDBDao.save(localiseDB1);
			for(GoogleTranslateDTO currTranslate : translatedStrings) {
				LocaliseDB localiseDB = new LocaliseDB();
				localiseDB.content = currTranslate.translation;
				localiseDB.region = currTranslate.region;
				localiseDB.messageID = uid;
				localiseDBDao.save(localiseDB);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closePR(String prID, String patchStr) {
		addComments(prID, patchStr);
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> req = new HashMap<>();
		req.put("state", "closed");
		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", "token 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");
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, header);
		System.out.println(entity);
		Object response = restTemplate.postForObject(pullCommitURL + prID, entity, Object.class);
		System.out.println(response);
	}

	public void addComments(String prID, String patchStr) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> req = new HashMap<>();
		req.put("body", patchStr);
		HttpHeaders header = new HttpHeaders();
		String overallURL = addCommentEndPoint + prID + commentSuffix;
		System.out.println(overallURL);
		header.add("Authorization", "token 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");
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, header);
		System.out.println(entity);
		Object response = restTemplate.postForObject(overallURL, entity, Object.class);
		System.out.println(response);
	}
}
