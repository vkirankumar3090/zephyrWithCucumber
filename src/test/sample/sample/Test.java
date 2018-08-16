package sample;

import com.Zapi.Utilities.ZephyrTestCaseManagementCloud;

public class Test {

	public static void main(String[] args) {
		//Prerequisite - Update the application.properties file.
		//Just call this method in your after suite and pass the cucumber json file path as parameter this will take care of updating the status in Jira.
		ZephyrTestCaseManagementCloud tcm = new ZephyrTestCaseManagementCloud();
		tcm.updateTestCaseAndStepStatus("report.json");
	}

}
