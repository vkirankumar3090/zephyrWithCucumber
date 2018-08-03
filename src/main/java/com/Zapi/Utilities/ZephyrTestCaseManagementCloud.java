package com.Zapi.Utilities;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.thed.zephyr.cloud.rest.ZFJCloudRestClient;
import com.thed.zephyr.cloud.rest.client.JwtGenerator;

public class ZephyrTestCaseManagementCloud extends Config{

	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	public String doGet(String url) throws Exception {
		Client client = ClientBuilder.newClient();
		Response response = null;
		if (url.contains(ZAPI_BASEURL)) {
			ZFJCloudRestClient client1 = ZFJCloudRestClient.restBuilder(ZAPI_BASEURL, ACCESS_KEY, SECRET_KEY, USER_NAME)
					.build();
			JwtGenerator jwtGenerator = client1.getJwtGenerator();
			URI uri = new URI(url);
			int expirationInSec = 360;
			String jwt = jwtGenerator.generateJWT("GET", uri, expirationInSec);
			response = client.target(uri).request().header("Authorization", jwt).header("zapiAccessKey", ACCESS_KEY)
					.get();
		} else {
			response = client.target(url).request(MediaType.APPLICATION_JSON_TYPE)
					.header("Authorization", JIRA_AUTHORIZATION).get();
		}
		log.info("GET : " + response.toString());
		if (response.getStatus() != 200) {
			throw new Exception("Unable to connect to JIRA");
		}
		return response.readEntity(String.class);
	}

	public String doDelete(String url) throws Exception {
		Client client = ClientBuilder.newClient();
		Response response = null;
		if (url.contains(ZAPI_BASEURL)) {
			ZFJCloudRestClient client1 = ZFJCloudRestClient.restBuilder(ZAPI_BASEURL, ACCESS_KEY, SECRET_KEY, USER_NAME)
					.build();
			JwtGenerator jwtGenerator = client1.getJwtGenerator();
			URI uri = new URI(url);
			int expirationInSec = 360;
			String jwt = jwtGenerator.generateJWT("DELETE", uri, expirationInSec);
			response = client.target(uri).request().header("Authorization", jwt).header("zapiAccessKey", ACCESS_KEY)
					.get();
		} else {
			response = client.target(url).request(MediaType.APPLICATION_JSON_TYPE)
					.header("Authorization", JIRA_AUTHORIZATION).delete();
		}
		if (response.getStatus() != 200) {
			throw new Exception("Unable to connect to JIRA");
		}
		return response.readEntity(String.class);
	}

	@SuppressWarnings("rawtypes")
	public String doPost(String url, String payload) throws Exception {
		Client client = ClientBuilder.newClient();
		Entity payloadEntity = Entity.json(payload);
		Response response = null;
		if (url.contains(ZAPI_BASEURL)) {
			ZFJCloudRestClient client1 = ZFJCloudRestClient.restBuilder(ZAPI_BASEURL, ACCESS_KEY, SECRET_KEY, USER_NAME)
					.build();
			JwtGenerator jwtGenerator = client1.getJwtGenerator();
			URI uri = new URI(url);
			int expirationInSec = 360;
			String jwt = jwtGenerator.generateJWT("POST", uri, expirationInSec);
			response = client.target(uri).request().header("Authorization", jwt).header("zapiAccessKey", ACCESS_KEY)
					.post(payloadEntity);
		} else {
			response = client.target(url).request(MediaType.APPLICATION_JSON_TYPE)
					.header("Authorization", JIRA_AUTHORIZATION).post(payloadEntity);
		}
		if (response.getStatus() != 200) {
			if (response.getStatus() != 201) {
				throw new Exception("Unable to connect to JIRA");
			}
		}
		return response.readEntity(String.class);
	}

	@SuppressWarnings("rawtypes")
	public String doPut(String url, String payload) throws Exception {
		Client client = ClientBuilder.newClient();
		Entity payloadEntity = Entity.json(payload);
		Response response = null;
		if (url.contains(ZAPI_BASEURL)) {
			ZFJCloudRestClient client1 = ZFJCloudRestClient.restBuilder(ZAPI_BASEURL, ACCESS_KEY, SECRET_KEY, USER_NAME)
					.build();
			JwtGenerator jwtGenerator = client1.getJwtGenerator();
			URI uri = new URI(url);
			int expirationInSec = 360;
			String jwt = jwtGenerator.generateJWT("PUT", uri, expirationInSec);
			response = client.target(uri).request().header("Authorization", jwt).header("zapiAccessKey", ACCESS_KEY)
					.put(payloadEntity);
		} else {
			response = client.target(url).request(MediaType.APPLICATION_JSON_TYPE)
					.header("Authorization", JIRA_AUTHORIZATION).put(payloadEntity);
		}
		if (response.getStatus() != 200) {
			throw new Exception("Unable to connect to JIRA");
		}
		return response.readEntity(String.class);
	}	
	
	public void updateTestCaseAndStepStatus(String cucumberJsonPath) {
		try {
			ZephyrAPI zapi = new ZephyrAPI();
			JiraAPI jira = new JiraAPI();
			CucumberJsonDataExtractor cucm = new CucumberJsonDataExtractor();
			JSONObject obj = cucm.getScenarioAndStepsStatus(cucumberJsonPath);
			for(String feature:obj.keySet()) {
				JSONObject featureObject = obj.getJSONObject(feature);
				for(String testCase:featureObject.keySet()) {
					String jiraKey = testCase.replace("@", "").trim();
					int issueId = jira.getIssueId(jiraKey);
					String executionId = zapi.createExecution(issueId);
					zapi.updateExecutioStatus(issueId, featureObject.getJSONObject(testCase).getString("scenarioStatus"), executionId);
					JSONObject stepObj = zapi.getTestStepIds(jiraKey, executionId, issueId);
					for(int i=0;i<stepObj.getJSONArray("testResultId").length();i++) {
						zapi.updateTestStepStatus(featureObject.getJSONObject(testCase).getJSONArray("stepStatus").getString(i), issueId, stepObj.getJSONArray("testStepId").getString(i), executionId, stepObj.getJSONArray("testResultId").getString(i));
					}
				}			
			}			
		}catch (Exception e) {
			log.info(e.getStackTrace());
			log.info("Unable to Update the Test case and step status");
		}
		
	}
}
