package fr.labri.progress.comet.service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkerMessage {
	String quality;
	String main_task_id;
	Boolean complete;
	String md5;
	String timestart;
	String timeend;

	public String getQuality() {
		return quality;
	}



	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * @return the timestart
	 */
	public String getTimestart() {
		return timestart;
	}

	/**
	 * @param timestart the timestart to set
	 */
	public void setTimestart(String timestart) {
		this.timestart = timestart;
	}

	/**
	 * @return the timeend
	 */
	public String getTimeend() {
		return timeend;
	}

	/**
	 * @param timeend the timeend to set
	 */
	public void setTimeend(String timeend) {
		this.timeend = timeend;
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

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}