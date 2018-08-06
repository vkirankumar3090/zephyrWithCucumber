package sample;

import com.Zapi.Utilities.ZephyrTestCaseManagementCloud;

public class Test {

	public static void main(String[] args) {
		ZephyrTestCaseManagementCloud tcm = new ZephyrTestCaseManagementCloud();
		tcm.updateTestCaseAndStepStatus("report.json");
	}

}
