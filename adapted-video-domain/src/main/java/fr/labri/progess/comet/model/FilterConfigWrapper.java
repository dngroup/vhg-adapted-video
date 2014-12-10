package fr.labri.progess.comet.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FilterConfigWrapper {

	public Set<FilterConfig> getFilterConfigs() {
		return filterConfigs;
	}

	public void setFilterConfigs(Set<FilterConfig> filterConfigs) {
		this.filterConfigs = filterConfigs;
	}

	Set<FilterConfig> filterConfigs = new HashSet<FilterConfig>();
}
