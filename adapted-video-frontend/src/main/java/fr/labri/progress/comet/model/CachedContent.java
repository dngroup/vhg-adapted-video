package fr.labri.progress.comet.model;

import java.io.Serializable;
import java.lang.String;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;

import javax.persistence.*;

import jersey.repackaged.com.google.common.base.Throwables;
import fr.labri.progess.comet.model.Content;

/**
 * Entity implementation class for Entity: CachedContent
 *
 */
@Entity
public class CachedContent implements Serializable {

	private URI oldUri;
	private URI newUri;
	@Id
	private String id;
	private Date createdAt;
	private Date validTo;
	private static final long serialVersionUID = 1L;

	public CachedContent() {
		super();
	}

	public URI getOldUri() {
		return this.oldUri;
	}

	public void setOldUri(URI oldUri) {
		this.oldUri = oldUri;
	}

	public URI getNewUri() {
		return this.newUri;
	}

	public void setNewUri(URI newUri) {
		this.newUri = newUri;
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
		try {
			c.setOldUri(new URI(content.getUri()));
		} catch (URISyntaxException e) {
			Throwables.propagate(e);
		}

		return c;
	}

	public static Content toContent(CachedContent content) {
		Content c = new Content();
		if (content.createdAt != null)
			c.setCreated(new Date(content.createdAt.getTime()));
		c.setId(content.getId());
		if (content.getOldUri() != null)
			c.setUri(content.getOldUri().toString());
		if (content.getNewUri() != null)
			c.setNew_uri(content.getNewUri().toString());

		return c;
	}

}
