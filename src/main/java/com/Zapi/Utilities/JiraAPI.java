package com.Zapi.Utilities;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class JiraAPI extends Config{

	static Logger log = Logger.getLogger("JiraAPI");
	
	public static String createVersion() throws Exception {
		ZephyrTestCaseManagementCloud tcm = new ZephyrTestCaseManagementCloud();
		String versionId = "";
		String payload = "{\"description\": \"New Version Created through Automation\",\"name\":\"" + RELEASE_VERSION
				+ "\",\"archived\": false,\"released\": " + config.getBoolean("released") + ",\"releaseDate\": \""
				+ config.getString("release.endDate") + "\",\"startDate\": \""+config.getString("release.startDate")+"\",\"project\":\"" + PROJECT_KEY + "\",\"projectId\": \"" + PROJECT_ID + "\"}";
		String responseString = tcm.doPost(JIRA_BASEURL + JIRA_API_CONTEXT + "version", payload);
		JSONObject obj = new JSONObject(responseString);
		versionId = obj.getString("id");
		log.info("Version Created : " + versionId);
		return versionId;
	}
	
	public static String getVersionId() throws Exception {
		ZephyrTestCaseManagementCloud tcm = new ZephyrTestCaseManagementCloud();
		String versionId = "";
		String responseString = tcm.doGet(JIRA_BASEURL + JIRA_API_CONTEXT + "project/" + PROJECT_KEY + "/versions");
		JSONArray arr = new JSONArray(responseString);
		for (Object object : arr) {
			JSONObject obj = (JSONObject) object;
			if (RELEASE_VERSION.equals(obj.getString("name"))) {
				versionId = obj.getString("id");
			}
		}
		if (versionId.isEmpty()) {
			try {
				versionId = createVersion();
			} catch (Exception e) {
				throw new Exception("Unable to find the release version " + RELEASE_VERSION);
			}
		}
		log.info("Version Id Retrived : " + versionId);
		return versionId;
	}
	
	public int getIssueId(String jiraKey) throws Exception {
		ZephyrTestCaseManagementCloud tcm = new ZephyrTestCaseManagementCloud();
		int issueId;
		String responseString = tcm.doGet(JIRA_BASEURL + JIRA_API_CONTEXT + "issue/" + jiraKey + "?fields=id");
		JSONObject obj = new JSONObject(responseString);
		issueId = Integer.valueOf(obj.getString("id"));
		log.info("Issue Id Retrived : " + issueId);
		return issueId;
	}	
}
