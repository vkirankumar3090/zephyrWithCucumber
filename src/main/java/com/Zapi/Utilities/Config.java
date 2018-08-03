package com.Zapi.Utilities;

import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Config {

	public boolean initilizestatus = false;
	public InputStream isConfig;
	public static String curdir;
	public static Configuration config = null;
	
	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	static String JIRA_BASEURL = "";
	static String PROJECT_KEY = "";
	static String RELEASE_VERSION = "";
	static String TESTCYCLE_NAME = "";
	static String ZAPI_URI_CONTEXT = "/public/rest/api/1.0/";
	static String JIRA_API_CONTEXT = "/rest/api/2/";
	static String PROJECT_ID = "";
	static String JIRA_AUTHORIZATION = "";
	static String ZAPI_BASEURL = "";
	static String ACCESS_KEY = "";
	static String SECRET_KEY = "";
	static String USER_NAME = "";
	static String VERSION_ID = "";
	static String CYCLE_ID = "";
	
	public Config() {
		
	}
	
	public Config(boolean intialize) {
		try {
			config = initialize();
			JIRA_BASEURL = config.getString("jira.baseurl");
			PROJECT_KEY = config.getString("project.key");
			RELEASE_VERSION = config.getString("release.version");
			TESTCYCLE_NAME = config.getString("testcycle.name");
			JIRA_AUTHORIZATION = config.getString("jira.authorization");
			JIRA_AUTHORIZATION = "Basic " + JIRA_AUTHORIZATION;
			PROJECT_ID = config.getString("project.id");
			ZAPI_BASEURL = config.getString("zapi.baseurl");
			ACCESS_KEY = config.getString("zapi.accesskey");
			SECRET_KEY = config.getString("zapi.secretkey");
			USER_NAME = config.getString("zapi.userName");
			VERSION_ID = JiraAPI.getVersionId();
			CYCLE_ID = ZephyrAPI.getCycleId();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("Unable to load properties file");
		}
	}
	
	public Configuration initialize() {
		curdir = System.getProperty("user.dir");
		System.setProperty("currentDir", curdir);
		PropertyConfigurator.configure("log4j.properties");
		if (!initilizestatus) {
			log.info("------------------initilizing----------------");
			try {
				ConfigurationFactory factory = new ConfigurationFactory(
						"config.xml");
				config = factory.getConfiguration();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			initilizestatus = true;
		} else {
			log.info("Initilization is Already Done");
		}
		return config;
	}
	
	public enum Status{
		passed,failed;
	}
}
