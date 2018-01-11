package com.fps.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fps.domain.ImageSubjectKeyword;

@Service
@Component
public class ImageExifSubjectKeyword {
	@Autowired
	private JdbcTemplate jdbc;

	@Value("${exif}")
	private String exifpath;
	
	private static final Logger log = Logger
			.getLogger(new ImageExifSubjectKeyword().getClass());

	private static int batchCount = 0;

	/**
	 * get subject keyword from each image and insert into batches on 1000
	 * 
	 * @param projectID
	 */
	public  List<Map<String,Object>> subjectKeyword(Long projectID, String ingest_date) {
		List<Map<String, Object>> images = null ;
		try {
			log.info("db table updated : image_subject_keyword_reload");

			final long startTime = System.currentTimeMillis();
			// project id
			log.info("process started @ " + new Date());

			final long projectId = projectID;
			// query to get project address list
			log.info("PROJECT SELECTED : " + projectId);

			final String sql = "select i.id, concat('/mnt/',p.images_location,'/Images/',p.`alfresco_title_1`,"
					+ "'/',p.`alfresco_title_2`,'/Master/',b.name,'/',i.name) as imagePath from image i "
					+ "inner join batch b on b.id = i.batch_id "
					+ "inner join projects p on p.id = b.project_id"
					+ " where p.id ="
					+ projectId
					+ " and i.ingest_time > '"
					+ ingest_date + "'";

			images = jdbc.queryForList(sql);

			log.info("TOTAL IMAGES IN PROJECT : " + images.size());

			List<ImageSubjectKeyword> equalSubKey = new ArrayList<ImageSubjectKeyword>();
			log.info("data insertion started..");
			for (Map<String, Object> imageDomain : images) {
				final long imageID = (long) imageDomain.get("id");
				final String imageAdd = imageDomain.get("imagePath").toString();
				//log.info("ID : "+imageID+" <====> ImageAddress : "+imageAdd );
				
				final String command = exifpath + " -s  -Subject -Keywords "
						+ imageAdd;
				Process p;
				p = Runtime.getRuntime().exec(command);
				p.waitFor();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				String line = "";
				ImageSubjectKeyword subKey = new ImageSubjectKeyword();
				subKey.setImageID(imageID);
				subKey.setProjectID(projectId);

				while ((line = reader.readLine()) != null) {
					final String[] op = line.split(" : ");
					String attribute = null;
					String value = null;
					if (op.length >= 2) {
						attribute = op[0].trim();
						value = op[1].trim();
						if (attribute.equals("Subject")) {
							subKey.setSubject(value);
						} else if (attribute.equals("Keywords")) {
							subKey.setKeyword(value);
						}
					}

				}
				// check for null values
				subKey.checkForNull();
				log.info("object : " + subKey.toString());
				// add object into List
				equalSubKey.add(subKey);
				batchCount++;
				// insert records by batch of 100
				if ((equalSubKey.size() == 100)
						|| (batchCount == images.size())) {

					insertAll(equalSubKey);
					equalSubKey.clear();
					log.info(batchCount + " records inserted @ " + new Date());
				}

			}

			final long endTime = System.currentTimeMillis();
			log.info("Total records inserted : " + batchCount);
			log.info("That whole process took " + (endTime - startTime)
					+ " milliseconds");

			
		} catch (Exception e) {

			e.printStackTrace();
		}
		return images;
	}

	private void insertAll(final List<ImageSubjectKeyword> equalSubKey) {
		try {
			final String sql = "insert ignore into image_subject_keyword(image_id,project_id,subject,keyword) values(?,?,?,?)";
			jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i)
						throws SQLException {
					ImageSubjectKeyword imageSubKey = equalSubKey.get(i);
					ps.setLong(1, imageSubKey.getImageID());
					ps.setLong(2, imageSubKey.getProjectID());
					ps.setString(3, imageSubKey.getSubject());
					ps.setString(4, imageSubKey.getKeyword());
				}

				@Override
				public int getBatchSize() {

					return equalSubKey.size();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
