package fr.labri.progress.comet.service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkerMessage {
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getMain_task_id() {
		return main_task_id;
	}
	public void setMain_task_id(String main_task_id) {
		this.main_task_id = main_task_id;
	}
	public Boolean getComplete() {
		return complete;
	}
	public void setComplete(Boolean complete) {
		this.complete = complete;
	}
	String quality;
	String main_task_id;
	Boolean complete;
}