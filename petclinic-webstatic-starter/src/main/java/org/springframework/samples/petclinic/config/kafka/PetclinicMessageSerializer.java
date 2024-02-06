package org.springframework.samples.petclinic.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vssavin.usmancore.spring6.user.User;
import com.github.vssavin.usmancore.spring6.user.UserMapper;
import com.github.vssavin.usmancore.spring6.user.UserMapperImpl;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author vssavin on 06.02.2024.
 */
public class PetclinicMessageSerializer implements Serializer<PetclinicMessage> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final UserMapper userMapper = new UserMapperImpl();

	@Override
	public byte[] serialize(String topic, PetclinicMessage message) {
		try {
			if (message == null) {
				return new byte[0];
			}
			if (message.getMessage() instanceof User user) {
				return objectMapper.writeValueAsBytes(userMapper.toDto(user));
			}

			return new byte[0];
		}
		catch (Exception e) {
			throw new SerializationException("Error when serializing User to byte[]: ", e);
		}
	}

}
