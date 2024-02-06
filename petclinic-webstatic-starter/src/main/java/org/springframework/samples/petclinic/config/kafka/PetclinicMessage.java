package org.springframework.samples.petclinic.config.kafka;

/**
 * @author vssavin on 06.02.2024.
 */
public class PetclinicMessage {

	private final Object message;

	public PetclinicMessage(Object message) {
		this.message = message;
	}

	public Object getMessage() {
		return message;
	}

}
