package com.healthmedia.ws.wsdl.user.v1;

import com.healthmedia.ws.entity.dataSouce.v1.DataSourceType;
import com.healthmedia.ws.entity.user.v1.DataType;
import com.healthmedia.ws.entity.user.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.user.v1.UserDataType;
import com.healthmedia.ws.entity.user.v1.UserType;

public class UserServiceImpl implements UserService {

	@Override
	public AddUserResponse addUser(AddUserRequest parameters) {
		
		UserType user = new UserType();
		
		user.setId("1");
		user.setFirstName("Michael");
		user.setLastName("Lambert");
		
		UserType userReference = new UserType();
		userReference.setId("1");
		
		DataSourceType sourceReference = new DataSourceType();
		sourceReference.setId("1a");
		
		DataType data = new DataType();
		data.setName("WeightPounds");
		data.setValue("145");
		
		UserDataType userData = new UserDataType();
		userData.setId("a");
		userData.setUser(userReference);
		userData.setDataSource(sourceReference);
		userData.setData(data);
		
		UserDataCollectionType userDataCollection = new UserDataCollectionType();
		userDataCollection.getUserData().add(userData);
		
		user.setUserDataCollection(userDataCollection);
		
		AddUserResponse response = new AddUserResponse();
		response.setUser(user);
		
		return response;
	}

	@Override
	public AddUserDataResponse addUserData(AddUserDataRequest parameters) {
		// TODO Auto-generated method stub
		return null;
	}
}
