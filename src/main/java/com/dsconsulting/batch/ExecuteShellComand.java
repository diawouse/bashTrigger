package com.dsconsulting.batch;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsconsulting.batch.conf.Configuration;
import com.dsconsulting.batch.conf.Configuration.BashTriggerAppli;

public class ExecuteShellComand {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExecuteShellComand.class);

	public static void main(String[] args) throws InterruptedException {
//		if (args.length<4){
//			LOG.info("Arguments less than 4");
//			LOG.info("args[0] Must be a file containing shell commands to excute\n"
//					+ "\t \t \t \t \t \t  args[1] Rest API url to send mail\n"
//					+ "\t \t \t \t \t \t  args[2] Must be a json file containing API parameters\n"
//					+ "\t \t \t \t \t \t  args[3] The number of seconds between two executions of the scripts");
//			return;
//		}
		BashTriggerAppli batchConf = Configuration.get().getBashTriggerAppli();
		while(true){
			ExecuteShellComand obj = new ExecuteShellComand();
			try {
				File file = new File(batchConf.scriptFilePath);
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if(obj.executeCommand(line)!=0){
						Send(batchConf.apiUrl, batchConf.dataFilePath);
					}
				}
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Thread.sleep(Integer.parseInt(batchConf.sleepDuration));
		}
	}
	
	/**
	 * This method execute shell command in parameter and return Exit code
	 * @param command : shell command to execute
	 * @return Exit code of command execution
	 */
	private int executeCommand(String command) {
		int exitStatus= -1;
		Process p;
		try {
			LOG.info("Execute command : "+command);
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			exitStatus = p.exitValue();
			LOG.info("Exit code : "+exitStatus);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return exitStatus;
	}
	/**
	 * This method call a Rest API with json data as parameter
	 * @param urlRest : Rest API url to send mail
	 * @param jsonDataFile : json data file containing full details of recipient users
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void Send(String urlRest, String jsonDataFile) throws ClientProtocolException, IOException{
		LOG.info("Send email : post method");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(urlRest);
		httppost.addHeader("content-type", "application/json");
		File file = new File(jsonDataFile);
		FileEntity entity = new FileEntity(file);
		httppost.setEntity(entity);
		LOG.info("Executing request " + httppost.getRequestLine() );
		httpclient.execute(httppost);
		httpclient.close();
	}

}