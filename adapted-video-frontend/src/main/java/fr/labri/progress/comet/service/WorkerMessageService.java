package fr.labri.progress.comet.service;


public interface WorkerMessageService {

	public void sendTranscodeOrder(String uri, String id);


	public void setupResultQueue();


//	public void getHardQueue();

}
