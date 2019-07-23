package com.dsconsulting.batch.conf;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;

/**
 * Parse le fichier de configuration yaml et fournir les informations pour le singleton.
 */
public class Configuration {
	final static Logger logger = Logger.getLogger(Configuration.class);
	private static Configuration instance = null;
	private Config config = null;

	/**
	 * Construit cette classe en parssant le fichier yaml.
	 * Cherche egalement les serveurs bootstrap si besoin.
	 */
	private Configuration() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			String configLocation = System.getProperty("config.location");
			if (configLocation == null) configLocation = System.getenv("config.location");
			if (configLocation != null) {
				logger.info("Using config.location "+configLocation);
				File configFile = new File(configLocation);
				if (configFile.exists()){
					config = mapper.readValue(new FileInputStream(configFile), Config.class);
				}
				else{
					config = mapper.readValue(getClass().getClassLoader().getResourceAsStream(configLocation), Config.class);
				}
			} 

		} catch (Exception e) {
			logger.error(e);
		}
	}
	/**
	 * Retourne une instance de configuration. Parse en premier le fichier yaml au premier appel qui se trouve a l'emplacement indiquer par
	 *  <strong>config.location</strong>
	 * @return
	 */
	public static Configuration get() {
		if (instance == null) 
			instance = new Configuration();
		return instance;
	}

	public BashTriggerAppli getBashTriggerAppli() {
		return config.bashTriggerAppli;
	}
	private static class Config {
		@JsonProperty("bashTrigger.appli")
		public BashTriggerAppli bashTriggerAppli;
	}
	public static class BashTriggerAppli {
		public String scriptFilePath;
		public String apiUrl;
		public String dataFilePath;
		public String sleepDuration;
	}

}
