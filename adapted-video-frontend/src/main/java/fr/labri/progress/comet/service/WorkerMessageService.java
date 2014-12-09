package fr.labri.progress.comet.service;


public interface WorkerMessageService {

	public void sendDownloadOrder(String uri, String id);

	public void setupResultQueue();

}
