package fr.labri.progress.comet.service;

public interface StateMonitoring {
	


	
	/**
	 * get the number of video during the last X second
	 * 
	 * @param second the number of second
	 * @return 
	 */
	public int getNbVideoLast(long second);
	
	
	/**
	 * get the Second of video during the last X second
	 * 
	 * @param id
	 *            the Id of the abstract resource enclosure
	 * @return 
	 */
	public int getSecondVideoLast(long second);


	public int getQueueSize();

}
