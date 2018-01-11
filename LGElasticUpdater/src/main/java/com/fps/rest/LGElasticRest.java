package com.fps.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fps.service.ImageExifSubjectKeyword;

import org.springframework.http.MediaType;

@RestController
public class LGElasticRest {

	@Autowired
	private ImageExifSubjectKeyword imageExif;

	@Value("${project}")
	private String projectID;

	@Value("${jar}")
	private String jarFile;

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public void getDataFromByExif() throws IOException, InterruptedException {
		StringBuffer output = new StringBuffer();
		LocalDate currentDate = LocalDate.now();
		final String ingest_date = (DateTimeFormatter.ofPattern("yyy-MM-dd")
				.format(currentDate)).concat(" 00:00:00");

		System.out.println("ingest_date :  " + ingest_date);
		System.out.println("projectID :  " + projectID);

		final String command[] = { "/bin/bash",jarFile, projectID, ingest_date };

		ProcessBuilder pb = new ProcessBuilder(command);
		Process process = pb.start();
		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		String line = "";
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
			System.out.println(line);

		}
	}
}
