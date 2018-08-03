package com.Zapi.Utilities;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class CucumberJsonDataExtractor {
	
	String PASS = "passed";
	String FAIL = "failed";
	Config conf = new Config(true);
	
	private JSONArray readData(String path) throws IOException{
		JSONArray cucumberJson = null;
		File file = new File(path);
	    String content = FileUtils.readFileToString(file, "utf-8");	    
	    cucumberJson  = new JSONArray(content);	    
	    return cucumberJson;
	}
	
	@SuppressWarnings("static-access")
	public JSONObject getScenarioAndStepsStatus(String cucumberJsonPath) throws IOException{
		JSONArray jsonArray = readData(cucumberJsonPath);
		JSONObject result = new JSONObject();		
		for (Object object : jsonArray) {
			JSONObject tagObject = new JSONObject();			
			JSONObject obj = (JSONObject) object;
			JSONArray scenario = obj.getJSONArray("elements");				
			for(Object arr:scenario){	
				JSONObject details = new JSONObject();
				JSONObject scenarioObj = (JSONObject) arr;
				JSONArray stepsArray = scenarioObj.getJSONArray("steps");
				JSONArray tags = scenarioObj.getJSONArray("tags");
				String tagName = "";
				for(Object tag :tags){
					JSONObject tagList = (JSONObject) tag;					
					if(tagList.getString("name").toLowerCase().startsWith(("@"+conf.PROJECT_KEY).toLowerCase())){
						tagName = tagList.getString("name");
						break;
					}
				}				
				JSONArray stepStatusList = new JSONArray();
				details.put("scenarioStatus", PASS);
				for(Object stepObj:stepsArray){
					JSONObject steps = (JSONObject) stepObj;					
					String stepStatus = steps.getJSONObject("result").getString("status");	
					if(stepStatus.equals("failed")) {
						details.put("scenarioStatus", FAIL);
					}
					stepStatusList.put(stepStatus);				
				}		
				details.put("stepStatus", stepStatusList);
				tagObject.put(tagName, details);
				result.put(obj.getString("name"), tagObject);
			}			
		}
		return result;
	}	
	
	public static void main(String[] args) throws Exception {
		ZephyrAPI zapi = new ZephyrAPI();
		JiraAPI jira = new JiraAPI();
		CucumberJsonDataExtractor cucm = new CucumberJsonDataExtractor();
		JSONObject obj = cucm.getScenarioAndStepsStatus("report.json");
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
	}
}
