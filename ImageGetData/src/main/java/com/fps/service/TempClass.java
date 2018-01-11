package com.fps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my")
public class TempClass {
	private static final String exifpath = "/usr/local/bin/exiftool";
	private static final String address = "/mnt/newvol05/dropbox-Life_of_Pi_Retouch/Images/TIFF/";
	private static final Logger log = Logger.getLogger(new TempClass()
			.getClass());

	@Autowired
	JdbcTemplate jdbc;
	private List<String> files = new ArrayList<String>();

	public List<String> fileNames() {
		return this.files;
	}

	public void print() {
		List<String> fi = fileNames();
		for (String s : fi) {
			System.out.println(s);
		}
	}

	public void dothis() throws IOException, InterruptedException {
		int count = 1;
		String command = "ls " + address;

		log.info("command : " + command);
		Process p;
		p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		String line = "";

		String photographer = null;
		while ((line = reader.readLine()) != null) {
			log.info("============> " + count);
			count++;
			String imageName = line;
			String imageAdress = address + line;
			String command2 = exifpath + " -s -Creator " + imageAdress;
			// log.info("image address : " + imageAdress);
			log.info("command 2 : " + command2);
			p = Runtime.getRuntime().exec(command2);
			p.waitFor();
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line2 = "";

			// if ((line2 = reader2.readLine()) != null) {
			line2 = reader2.readLine();
			if (line2 == null) {
				log.info("no photographer found for image - " + imageName);
				photographer = "not found";
				String insert = "INSERT ignore INTO fox_image_photographer (image_name,photographer) values (?,?)";

				jdbc.update(insert, imageName, photographer);
			} else {
				final String[] op = line2.split(" : ");

				if (op.length == 2) {

					photographer = op[1].trim();
					// log.info("-- " + imageName + " - " + photographer);
					String insert = "INSERT ignore INTO fox_image_photographer (image_name,photographer) values (?,?)";

					jdbc.update(insert, imageName, photographer);
					log.info("photo updated : " + imageName + " - "
							+ photographer);

				}
			}
		}
	}
}
