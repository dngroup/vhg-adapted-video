package fr.labri.progress.comet.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fr.labri.progess.comet.model.Content;

/**
 * Entity implementation class for Entity: CachedContent
 *
 */
@Entity
@NamedQuery(name = "CachedContent.findAll", query = "SELECT p FROM CachedContent p")
@NamedEntityGraph(name = "CachedContent.detail", attributeNodes = @NamedAttributeNode("qualities"))
public class CachedContent implements Serializable {
	private static final long serialVersionUID = 1L;
	private String oldUri;

	@Id
	private String id;
	// TODO: this flow line is for get time to encode
	private Date requestDate;
	private Date createdAt;
	private Date validTo;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable( name="QualitesContent", joinColumns=@JoinColumn(name="cachedContent"), inverseJoinColumns = @JoinColumn( name="Quality"))
	private List<Quality> qualities = new ArrayList<Quality>();

	// @ElementCollection(targetClass = String.class, fetch=FetchType.EAGER)
	// private List<String> qualities = new ArrayList<String>();

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

		c.getQualities().addAll(Lists.transform(content.getQualities(), stringToQuality));

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
		c.getQualities().addAll(Lists.transform(content.getQualities(), qualityToString));
		return c;
	}

	// public List<String> getQualities() {
	// return qualities;
	// }
	//
	// public void setQualities(List<String> qualities) {
	// this.qualities = qualities;
	// }

	/**
	 * @return the requestDate
	 */
	public Date getRequestDate() {
		return requestDate;
	}

	/**
	 * @param requestDate
	 *            the requestDate to set
	 */
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	/**
	 * @return the qualities2
	 */
	public List<Quality> getQualities() {
		return qualities;
	}

	/**
	 * @param qualities2
	 *            the qualities2 to set
	 */
	public void setQualities2(List<Quality> qualities) {
		this.qualities = qualities;
	}

	static Function<String, Quality> stringToQuality = new Function<String, Quality>() {

		@Override
		public Quality apply(String name) {
			Quality quality = new Quality();
			quality.setName(name);
			return quality;
		}

	};

	static Function<Quality, String> qualityToString = new Function<Quality, String>() {

		@Override
		public String apply(Quality quality) {

			return quality.getName();
		}

	};

}
