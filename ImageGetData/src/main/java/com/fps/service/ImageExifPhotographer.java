package com.fps.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fps.domain.ImageMeta;

@Component
public class ImageExifPhotographer {

	@Autowired
	JdbcTemplate jdbc;

	// 3244580 a good day to die hard
	// for local
	// private static final String exifpath = "/usr/local/bin/exiftool";
	// for server
	private static final String exifpath = "/mnt/bigvol/scripts/tools/Image-ExifTool-10.23/exiftool";
	private static final Logger log = Logger.getLogger(new ImageExifPhotographer().getClass());
	private static int batchCount = 0;

	/**
	 * get photographer from each image
	 * 
	 * @param projectID
	 */
	public void executeExif(Long projectID) {
		try {
			final long startTime = System.currentTimeMillis();
			log.info("process started @ " + new Date());

			// project id
			final long projectId = projectID;

			// query to get project address list
			final String sql = "select i.id, concat('/mnt/',p.images_location,'/Images/',p.`alfresco_title_1`,"
					+ "'/',p.`alfresco_title_2`,'/Master/',b.name,'/',i.name) as imagePath from image i "
					+ "inner join batch b on b.id = i.batch_id " + "inner join projects p on p.id = b.project_id"
					+ " where p.id =" + projectId;

			final List<Map<String, Object>> images = jdbc.queryForList(sql);
			log.info("PROJECT SELECTED : " + projectId);
			log.info("TOTAL IMAGES IN PROJECT : " + images.size());
			List<ImageMeta> imageMetaList = new ArrayList<ImageMeta>();

			for (Map<String, Object> imageDomain : images) {
				final long imageID = (long) imageDomain.get("id");
				final String imageAdd = imageDomain.get("imagePath").toString();
				final String command = exifpath + " " + imageAdd;

				Process p;
				p = Runtime.getRuntime().exec(command);
				p.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = "";
				boolean flag = true;
				while ((line = reader.readLine()) != null) {

					final String[] op = line.split(" : ");

					if (op.length == 2) {

						if (op[0].trim().equalsIgnoreCase("artist")) {

							final String photographer = op[1];
							log.info("Photographer added for image :" + imageAdd + " -- " + photographer);
							final ImageMeta metaObj = new ImageMeta(projectId, imageID, photographer);

							imageMetaList.add(metaObj);
							flag = false;
						}
					}
				}
				if (flag == true) {

					final ImageMeta metaObj = new ImageMeta(projectId, imageID, "");

					imageMetaList.add(metaObj);
					log.info("NO photographer added for image :" + imageAdd);
				}
				// insert records at batch of 10000
				if ((imageMetaList.size() == 100) || (batchCount == images.size())) {

					insertBatch(imageMetaList);
					imageMetaList.clear();
					log.info(batchCount + " records inserted @ " + new Date());
				}
				batchCount++;
			}

			final long endTime = System.currentTimeMillis();
			log.info("That whole process took " + (endTime - startTime) + " milliseconds");
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertBatch(final List<ImageMeta> imageList) {
		try {
			final String sql = "insert ignore into image_temp(id,photographer) values(?,?)";
			jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ImageMeta meta = imageList.get(i);

					ps.setLong(1, meta.getImageID());
					
					ps.setString(2, meta.getPhotographer());
					// TODO Auto-generated method stub

				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return imageList.size();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
