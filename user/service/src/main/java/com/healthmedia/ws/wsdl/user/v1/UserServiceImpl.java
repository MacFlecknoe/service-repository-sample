package com.healthmedia.ws.wsdl.user.v1;

import com.healthmedia.ws.entity.dataSouce.v1.DataSourceType;
import com.healthmedia.ws.entity.user.v1.DataType;
import com.healthmedia.ws.entity.user.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.user.v1.UserDataType;
import com.healthmedia.ws.entity.user.v1.UserType;

public class UserServiceImpl implements UserService {

	@Override
	public AddUserResponse addUser(AddUserRequest parameters) {
		
		UserType userReference = new UserType();
		userReference.setId("1");
		
		DataSourceType sourceReference = new DataSourceType();
		sourceReference.setId("1a");
		
		DataType dataA = new DataType();
		dataA.setName("WeightPounds");
		dataA.setValue("145");
		
		UserDataType userDataA = new UserDataType();
		userDataA.setId("a");
		userDataA.setUser(userReference);
		userDataA.setDataSource(sourceReference);
		userDataA.setData(dataA);
		
		DataType dataB = new DataType();
		dataB.setName("HeightInches");
		dataB.setValue("145");
		
		UserDataType userDataB = new UserDataType();
		userDataB.setId("b");
		userDataB.setUser(userReference);
		userDataB.setDataSource(sourceReference);
		userDataB.setData(dataB);
		
		UserDataCollectionType userDataCollection = new UserDataCollectionType();
		userDataCollection.getUserData().add(userDataA);
		userDataCollection.getUserData().add(userDataB);
		
		UserType user = new UserType();
		
		user.setId("1");
		user.setFirstName("Michael");
		user.setLastName("Lambert");
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
