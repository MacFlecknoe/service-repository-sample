package com.healthmedia.ws.modality;

/**
 * Singleton instance which stores the relevant modality for the current transaction.
 * 
 * @author mlamber7
 *
 */
public class ModalityContextContainer {
	// singleton instance
	private static ModalityContextContainer instance;
	// modality is scoped to the current thread
	private final ThreadLocal<String> modalityContainer;
	
	private ModalityContextContainer() {
		this.modalityContainer = new ThreadLocal<String>();
	}
	
	public String getModality() {
		return modalityContainer.get();
	}
	
	public void setModality(String modality) {
		modalityContainer.set(modality);
	}
	
	public static ModalityContextContainer getInstance() {
		if(instance == null) {
			instance = new ModalityContextContainer();
		}
		return instance;
	}
}