package edu.cmu.photogenome.business;

import java.util.Date;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.photogenome.dao.PhotoRegionDao;
import edu.cmu.photogenome.dao.PhotoRegionDaoImpl;
import edu.cmu.photogenome.dao.RegionCategoryDao;
import edu.cmu.photogenome.dao.RegionCategoryDaoImpl;
import edu.cmu.photogenome.dao.RegionCommentDao;
import edu.cmu.photogenome.dao.RegionCommentDaoImpl;
import edu.cmu.photogenome.dao.RegionCoordinateDao;
import edu.cmu.photogenome.dao.RegionCoordinateDaoImpl;
import edu.cmu.photogenome.domain.PhotoRegion;
import edu.cmu.photogenome.domain.RegionCategory;
import edu.cmu.photogenome.domain.RegionComment;
import edu.cmu.photogenome.domain.RegionCoordinate;

/**
 * The <code>EmbedRegion</code> class to embed region information for 
 * photos including adding, editing and deleting region categories 
 * and comments.
 *  
 * @author PhotoGenome
 *
 */

public class EmbedRegion {

	final Logger log = LoggerFactory.getLogger(EmbedRegion.class);
	
	private PhotoRegionDao photoRegionDao;
	private RegionCategoryDao regionCategoryDao;
	private RegionCommentDao regionCommentDao;
	private RegionCoordinateDao regionCoordinateDao;

	public EmbedRegion() {
		photoRegionDao = new PhotoRegionDaoImpl();
		regionCategoryDao = new RegionCategoryDaoImpl();
		regionCommentDao = new RegionCommentDaoImpl();
		regionCoordinateDao = new RegionCoordinateDaoImpl();
	}
	
	/**
	 * Constructor that also sets the Hibernate session to be used
	 * 
	 * @param session	Hibernate session to use when calling DAOs
	 */
	public EmbedRegion(Session session) {
		this();
		setSession(session);
	}
	
	/**
	 * Set the Hibernate session to use when calling DAOs
	 * 
	 * @param session
	 */
	public void setSession(Session session) {
		photoRegionDao.setSession(session);
		regionCategoryDao.setSession(session);
		regionCommentDao.setSession(session);
		regionCoordinateDao.setSession(session);
	}
	
	/**
	 * Save a region marked on a photo
	 * 
	 * @param photoId
	 * @param userId
	 * @param shapeId
	 * @param regionX
	 * @param regionY
	 * @param height
	 * @param width
	 * @return newly created region, otherwise null
	 */
	public PhotoRegion addPhotoRegion(int photoId, int userId, int shapeId, int regionX, int regionY, 
			int height, int width) {
		PhotoRegion region = null;
		if((region = addPhotoRegion(photoId, userId, shapeId)) == null) // try to save the region
			return null;
		else {
			RegionCoordinate coordinate = null;
			if((coordinate = addRegionCoordinate(region.getRegionId(), photoId, userId, regionX, regionY, 
					height, width)) == null) { // try to save the coordinates
				log.debug("Failed to save coordinate, deleting region with ID = {}", region.getRegionId());
				photoRegionDao.delete(region); // delete region if coordinates failed to save
				return null;
			}
		}
		
		return region;
	}
	
	/**
	 * Private helper method to save a photo region
	 * 
	 * @param photoId
	 * @param userId
	 * @param shapeId
	 * @return the saved photo region, or null if failed to save
	 */
	private PhotoRegion addPhotoRegion(int photoId, int userId, int shapeId) {
		PhotoRegion region = new PhotoRegion(photoId, userId, shapeId, new Date());
		
		log.debug("Saving photo region with photoId={}, userId={}, shapeId={}", photoId, userId, shapeId);
		if(photoRegionDao.save(region))
			return region;
		else
			return null;
	}

	/**
	 * Save a comment on a region
	 * 
	 * @param photoId
	 * @param userId
	 * @param regionId
	 * @param regionCommentText
	 * @return newly created region comment, otherwise null
	 */
	public RegionComment addRegionComment(int photoId, int userId, int regionId, String regionCommentText) {
		RegionComment comment = new RegionComment(photoId, regionId, userId, new Date());
		comment.setRegionCommentText(regionCommentText);
		
		log.debug("Saving region comment with photoId={}, userId={}, regionId={}, regionCommentText={}",
				photoId, userId, regionId, regionCommentText);
		if(regionCommentDao.save(comment))
			return comment;
		else
			return null;
	}
	
	/**
	 * Save a region category
	 * 
	 * @param regionId
	 * @param photoId
	 * @param userId
	 * @param categoryDetails
	 * @return newly created region category, otherwise null
	 */
	public RegionCategory addRegionCategory(int regionId, int photoId, int userId, 
			List<SimpleEntry<String, String>> categoryDetails) {
		RegionCategory category = new RegionCategory(regionId, photoId, userId, new Date());
		
		for(SimpleEntry<String, String> details : categoryDetails) {
			log.debug("Saving region category with regionId={}, photoId={}, userId={}, categoryName={}, categoryValue={}", 
					regionId, photoId, userId, details.getKey(), details.getValue());
			category.setCategoryName(details.getKey());
			category.setRegionCategoryText(details.getValue());
			if(!regionCategoryDao.save(category))
				return null;
		}
		
		log.debug("Region category added with regionCategoryId={}", category.getRegionCategoryId());
		return category;
	}
		
	/**
	 * Private helper method to save a set of region coordinates
	 * 
	 * @param regionId
	 * @param photoId
	 * @param userId
	 * @param regionX
	 * @param regionY
	 * @param height
	 * @param width
	 * @return the saved region coordinates, or null if failed to save
	 */
	private RegionCoordinate addRegionCoordinate(int regionId, int photoId, int userId, int regionX, int regionY, 
			int height, int width){
		RegionCoordinate coordinate = new RegionCoordinate(regionId, photoId, userId, regionX, regionY, 
				height, width, new Date());
		
		log.debug("Saving region coordinate with regionId={}, photoId={}, userId={}, regionX={}, regionY={}," +
				" height={}, width={}", regionId, photoId, userId, regionX, regionY, height, width);
		if(regionCoordinateDao.save(coordinate))
				return coordinate;
		else
			return null;
	}
	
	/**
	 * Edit the text of a region comment
	 * 
	 * @param comment
	 * @return true if the comment is updated, else false
	 */
	public boolean editRegionComment(RegionComment comment){
		log.debug("Updating region comment with ID = {}", comment.getRegionCommentId());
		if(regionCommentDao.update(comment))
			return true;
		else
			return false;
	}

	/**
	 * Edit region categories
	 * 
	 * @param regionCategory
	 * @return	true if category is updated, otherwise false
	 */
	public boolean editRegionCategory(RegionCategory regionCategory){
		log.debug("Updating region category with ID = {}", regionCategory.getRegionCategoryId());
		if(regionCategoryDao.update(regionCategory)) {
			return true;
		}
		else {
			log.error("Failed to update region category with ID = {}", regionCategory.getRegionCategoryId());
			return false;
		}
	}
	
	/**
	 * Edit a region coordinate
	 * @param coordinate
	 * @return true if the coordinate is updated, else false
	 */
	public boolean editRegionCoordinate(RegionCoordinate coordinate){
		log.debug("Updating region coordinate with ID = {}", coordinate.getRegionCoordinateId());
		if(regionCoordinateDao.update(coordinate))
			return true;
		else
			return false;
	}

	/**
	 * Delete a photo region
	 * @param regionId
	 * @return true if the region no longer exists, else false
	 */
	public boolean deletePhotoRegion(int regionId){
		PhotoRegion region;
		if((region = photoRegionDao.findById(regionId)) != null) {
			log.debug("Deleting photo region with ID = {}", regionId);
			if(!photoRegionDao.delete(region))
				return false;
		}
		else
			log.debug("Photo region with ID = {} does not exist. Nothing to delete", regionId);
		
		return true;
	}

	/**
	 * Delete a region comment
	 * @param commentId
	 * @return true if the region comment no longer exists, else false
	 */
	public boolean deleteRegionComment(int regionCommentId){
		RegionComment comment;
		if((comment = regionCommentDao.findById(regionCommentId)) != null) {
			log.debug("Deleting region comment with ID = {}", regionCommentId);
			if(!regionCommentDao.delete(comment))
				return false;
		}
		else
			log.debug("Region comment with ID = {} does not exist. Nothing to delete", regionCommentId);
		
		return true;
	}

	/**
	 * Delete a region category 
	 * @param regionCategoryId
	 * @return true if region category no longer exists, else false
	 */
	public boolean deleteRegionCategory(int regionCategoryId) {
		RegionCategory category;
		if((category = regionCategoryDao.findById(regionCategoryId)) != null) {
			log.debug("Deleting region category with ID = {}", regionCategoryId);
			if(!regionCategoryDao.delete(category)) {
				log.error("Failed to delete region category with ID = {}", regionCategoryId);
				return false;
			}
		}
		else
			log.debug("Region category with ID = {} does not exist. Nothing to delete", regionCategoryId);
		
		return true;
	}

	/**
	 * Delete a region coordinate
	 * @param coordinateId
	 * @return true if the region coordinate no longer exists, else false
	 */
	public boolean deleteRegionCoordinate(int regionCoordinateId) {
		RegionCoordinate coordinate;
		if((coordinate = regionCoordinateDao.findById(regionCoordinateId)) != null) {
			log.debug("Deleting region coordinate with ID = {}", regionCoordinateId);
			if(!regionCoordinateDao.delete(coordinate))
				return false;
		}
		else
			log.debug("Region coordinate with ID = {} does not exist. Nothing to delete", regionCoordinateId);
		
		return true;		
	}
}
