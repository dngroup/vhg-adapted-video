package fr.labri.progress.comet.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import fr.labri.progess.comet.model.Content;

/**
 * Entity implementation class for Entity: CachedContent
 *
 */
@Entity
public class CachedContent implements Serializable {

	private String oldUri;

	@Id
	private String id;
	//	TODO: this flow ligne is for get time to transcode
	private Date requestDate;
	private Date createdAt;
	private Date validTo;
	@ElementCollection(targetClass = String.class, fetch=FetchType.EAGER)
	private List<String> qualities = new ArrayList<String>();
	private static final long serialVersionUID = 1L;

	public CachedContent() {
		super();
	}

	public String getOldUri() {
		return this.oldUri;
	}

	public void setOldUri(String oldUri) {
		this.oldUri = oldUri;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getValidTo() {
		return this.validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public static CachedContent fromContent(Content content) {
		CachedContent c = new CachedContent();
		if (content.getCreated() != null)
			c.setCreatedAt(new Date(content.getCreated().getTime()));

		c.setId(content.getId());
		c.getQualities().clear();
		c.getQualities().addAll(content.getQualities());

		c.setOldUri(content.getUri());

		return c;
	}

	public static Content toContent(CachedContent content) {
		Content c = new Content();
		if (content.createdAt != null)
			c.setCreated(new Date(content.createdAt.getTime()));
		else if (content.requestDate != null)
			c.setCreated(new Date(content.requestDate.getTime()));

		c.setId(content.getId());
		if (content.getOldUri() != null)
			c.setUri(content.getOldUri());
		
		c.getQualities().clear();
		c.getQualities().addAll(content.getQualities());
		return c;
	}

	
	public List<String> getQualities() {
		return qualities;
	}

	public void setQualities(List<String> qualities) {
		this.qualities = qualities;
	}

	/**
	 * @return the requestDate
	 */
	public Date getRequestDate() {
		return requestDate;
	}

	/**
	 * @param requestDate the requestDate to set
	 */
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

}
