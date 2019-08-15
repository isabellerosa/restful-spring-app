package com.example.app.ws.ui.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.ws.exceptions.UserServiceException;
import com.example.app.ws.service.AddressService;
import com.example.app.ws.service.UserService;
import com.example.app.ws.shared.dto.AddressDTO;
import com.example.app.ws.shared.dto.UserDTO;
import com.example.app.ws.ui.model.request.UserDetailsRequestModel;
import com.example.app.ws.ui.model.response.AddressRest;
import com.example.app.ws.ui.model.response.ErrorMessages;
import com.example.app.ws.ui.model.response.OperationStatusModel;
import com.example.app.ws.ui.model.response.RequestOperationName;
import com.example.app.ws.ui.model.response.RequestOperationStatus;
import com.example.app.ws.ui.model.response.UserRest;;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resources<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> userResponseModelList = new ArrayList<>();
		List<UserDTO> users = userService.getUsers(page, limit);
		
		users.forEach(userDto -> {
			UserRest userModel = new ModelMapper().map(userDto, UserRest.class);
			userResponseModelList.add(userModel);
		});

		return new Resources<>(userResponseModelList);
	}

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resource<UserRest> getUser(@PathVariable String id) {
		UserDTO userDto = userService.getUserByUserId(id);

		UserRest userResponseModel = new ModelMapper().map(userDto, UserRest.class);

		return new Resource<>(userResponseModel);
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resource<UserRest> createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {
		UserRest userResponseModel = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		ModelMapper modelMapper = new ModelMapper();
		UserDTO userDto = modelMapper.map(userDetails, UserDTO.class);

		UserDTO createdUser = userService.createUser(userDto);
		userResponseModel = modelMapper.map(createdUser, UserRest.class);

		return new Resource<>(userResponseModel);
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resource<UserRest> updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		ModelMapper modelMapper = new ModelMapper();

		UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);

		UserDTO updatedUser = userService.updateUser(id, userDTO);
		
		UserRest userResponseModel = modelMapper.map(updatedUser, UserRest.class);

		return new Resource<>(userResponseModel);
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resource<OperationStatusModel> deleteUser(@PathVariable String id) {
		OperationStatusModel operationStatusResponseModel = new OperationStatusModel();
		
		operationStatusResponseModel.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);
		
		operationStatusResponseModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return new Resource<>(operationStatusResponseModel);
	}

	@GetMapping(path = "/{userId}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resources<AddressRest> getUserAddresses(@PathVariable String userId) {
		List<AddressRest> addressResponseModelList = new ArrayList<>();
		List<AddressDTO> addressDTOList = addressService.getAddresses(userId);

		if (addressDTOList != null && !addressDTOList.isEmpty()) {
			Type listType = new TypeToken<List<AddressRest>>() {
			}.getType();
			addressResponseModelList = new ModelMapper().map(addressDTOList, listType);
			
			addressResponseModelList.forEach(address -> {
				address.add(linkTo(methodOn(UserController.class).getUser(userId)).withRel("user"));
				address.add(linkTo(methodOn(UserController.class).getUserAddress(userId, address.getAddressId()))
						.withSelfRel());
			});
		}

		return new Resources<>(addressResponseModelList);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public Resource<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressDTO addressDto = addressService.getAddress(addressId);

		AddressRest addressResponseModel = new ModelMapper().map(addressDto, AddressRest.class);

		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

		addressResponseModel.add(addressLink);
		addressResponseModel.add(userLink);
		addressResponseModel.add(addressesLink);

		return new Resource<>(addressResponseModel);
	}
}
