package com.example.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.ws.io.entity.AddressEntity;
import com.example.app.ws.io.entity.UserEntity;
import com.example.app.ws.io.repository.AddressRepository;
import com.example.app.ws.io.repository.UserRepository;
import com.example.app.ws.service.AddressService;
import com.example.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {
		
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		List<AddressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null) return returnValue;
		
		List<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		
		addresses.forEach(addressEntity ->{
			returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
		});
		
		return returnValue;
	}

}
