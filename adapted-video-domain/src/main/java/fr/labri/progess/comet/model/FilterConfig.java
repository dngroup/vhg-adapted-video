package fr.labri.progess.comet.model;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FilterConfig {

	private Collection<FileType> fileTypes=new HashSet<FileType>();
	private Collection<HeaderFilter> headerFilters=new HashSet<HeaderFilter>();

	public Collection<FileType> getFileTypes() {
		return fileTypes;
	}

	public void setFileTypes(Collection<FileType> fileTypes) {
		this.fileTypes = fileTypes;
	}

	public Collection<HeaderFilter> getHeaderValues() {
		return headerFilters;
	}

	public void setHeaderValues(Collection<HeaderFilter> headerValues) {
		this.headerFilters = headerValues;
	}

}
