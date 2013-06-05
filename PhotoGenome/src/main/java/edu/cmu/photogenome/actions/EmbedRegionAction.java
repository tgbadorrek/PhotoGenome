package edu.cmu.photogenome.actions;

import java.util.Date;

import com.opensymphony.xwork2.ActionSupport;

import edu.cmu.photogenome.actions.embedinformation.EmbedRegion;
import edu.cmu.photogenome.dao.PhotoDao;
import edu.cmu.photogenome.dao.PhotoDaoImpl;
import edu.cmu.photogenome.dao.PhotoRegionDao;
import edu.cmu.photogenome.dao.PhotoRegionDaoImpl;
import edu.cmu.photogenome.dao.RegionCommentDao;
import edu.cmu.photogenome.dao.RegionCommentDaoImpl;
import edu.cmu.photogenome.dao.RegionCoordinateDao;
import edu.cmu.photogenome.dao.RegionCoordinateDaoImpl;
import edu.cmu.photogenome.domain.PhotoRegion;
import edu.cmu.photogenome.domain.RegionComment;
import edu.cmu.photogenome.domain.RegionCoordinate;

public class EmbedRegionAction extends ActionSupport {

	private EmbedRegion embedRegion = new EmbedRegion();
	
	private Integer regionId;
	private int photoId;
	private int userId;
	private int shapeId;
	private String regionName;
	private Date photoRegionTimestamp;
	private Boolean photoRegionIsdeleted;
	private String photoRegionOption1;
	private String photoRegionOption2;
	private String photoRegionOption3;
	private String photoRegionOption4;
	private String photoRegionOption5;
	
	private Integer regionCommentId;
	private String regionCommentText;
	private Date regionCommentTimestamp;
	private Boolean regionCommentIsdeleted;
	private String regionCommentOption1;
	private String regionCommentOption2;
	private String regionCommentOption3;
	private String regionCommentOption4;
	private String regionCommentOption5;
	
	private Integer regionCoordinateId;
	private int regionX;
	private int regionY;
	private int height;
	private int width;
	private Date regionCoordinateTimestamp;
	private Boolean regionCoordinateIsdeleted;
	private String regionCoordinateOption1;
	private String regionCoordinateOption2;
	private String regionCoordinateOption3;
	private String regionCoordinateOption4;
	private String regionCoordinateOption5;
	
	private PhotoDao photoDao = new PhotoDaoImpl();
	private PhotoRegionDao photoRegionDao = new PhotoRegionDaoImpl();
	private RegionCommentDao regionCommentDao = new RegionCommentDaoImpl();
	private RegionCoordinateDao regionCoordinateDao = new RegionCoordinateDaoImpl();
	
	public String addPhotoRegion() {
		if(photoDao.findById(photoId) == null)
			return "invalid_photo";
		else {
			if(embedRegion.addPhotoRegion(photoId, userId, shapeId, regionX, regionY, height, width))
				return SUCCESS;
			else
				return ERROR;
		}
	}
	
	public String addRegionComment() {
		PhotoRegion region;
		if((region = photoRegionDao.findById(regionId)) == null || region.getPhotoId() != photoId)
			return "invalid_region";
		else {
			if(embedRegion.addRegionComment(photoId, userId, regionId, regionCommentText))
				return SUCCESS;
			else
				return ERROR;
		}
	}
	
	public String deletePhotoRegion() {
		if(embedRegion.deletePhotoRegion(regionId))
			return SUCCESS;
		else
			return ERROR;
	}
	
	public String deleteRegionComment() {
		if(embedRegion.deleteRegionComment(regionCommentId))
			return SUCCESS;
		else
			return ERROR;
	}
	
	public String editRegionComment() {
		RegionComment comment = null;
		if((comment = regionCommentDao.findById(regionCommentId)) == null 
				|| comment.getPhotoId() != photoId)
			return "invalid_region_comment";
		else {
			if(embedRegion.editRegionComment(comment))
				return SUCCESS;
			else
				return ERROR;
		}
	}
	
	public String editRegionCoordinate() {
		RegionCoordinate coordinate = null;
		if((coordinate = regionCoordinateDao.findById(regionCoordinateId)) == null 
				|| coordinate.getPhotoId() != photoId)
			return "invalid_region_coordinate";
		else {
			if(embedRegion.editRegionCoordinate(coordinate))
				return SUCCESS;
			else
				return ERROR;
		}
	}
	
}