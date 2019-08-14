package com.example.app.ws.service;

import java.util.List;

import com.example.app.ws.shared.dto.AddressDTO;

public interface AddressService {
	List<AddressDTO> getAddresses(String userId);
}
