package com.healthmedia.ws.modality;

/**
 * Singleton instance which stores the relevant modality for the current transaction.
 * 
 * @author mlamber7
 *
 */
public class ModalityContextContainer {
	
	private static ModalityContextContainer instance;
	private static final ThreadLocal<String> modalityContainer = new ThreadLocal<String>();
	
	public ModalityContextContainer() {
		// intentionally empty
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