package fr.labri.progess.comet.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HeaderFilter {

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	String header;
	String value;

}
