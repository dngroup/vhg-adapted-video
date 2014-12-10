package fr.labri.progess.comet.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileType {
	String extension;

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
