package com.healthmedia.ws.wsdl.user.v1;

import java.util.Locale;

import com.healthmedia.ws.common.error.v1.ErrorCollectionType;
import com.healthmedia.ws.common.error.v1.ErrorType;
import com.healthmedia.ws.common.error.v1.ReasonCollectionType;
import com.healthmedia.ws.common.error.v1.ReasonType;
import com.healthmedia.ws.entity.dataSouce.v1.DataSourceType;
import com.healthmedia.ws.entity.user.v1.DataType;
import com.healthmedia.ws.entity.user.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.user.v1.UserDataType;
import com.healthmedia.ws.entity.user.v1.UserType;

public class UserServiceImpl implements UserService {

	private com.healthmedia.ws.entity.v1.ObjectFactory entityFactory = new com.healthmedia.ws.entity.v1.ObjectFactory();
	private com.healthmedia.ws.entity.user.v1.ObjectFactory userFactory = new com.healthmedia.ws.entity.user.v1.ObjectFactory();
	private com.healthmedia.ws.entity.dataSouce.v1.ObjectFactory dataSourceFactory = new com.healthmedia.ws.entity.dataSouce.v1.ObjectFactory();
	
	@Override
	public ImportUserResponse importUser(ImportUserRequest parameters) throws Error {
		
		try {
			
			UserType input = parameters.getUser();
			if(input.getId() == null) {
				throw new IllegalArgumentException("id cannot be null");
			}
			UserType userReference = userFactory.createUserType();
			userReference.setId(entityFactory.createEntityTypeId("1"));
			
			DataSourceType sourceReference = dataSourceFactory.createDataSourceType();
			sourceReference.setId(entityFactory.createEntityTypeId("1A"));
			
			DataType dataA = userFactory.createDataType();
			dataA.setName(userFactory.createDataTypeName("WeightPounds"));
			dataA.setValue(userFactory.createDataTypeValue("145"));
			
			UserDataType userDataA = userFactory.createUserDataType();
			userDataA.setId(entityFactory.createEntityTypeId("A"));
			userDataA.setUser(userFactory.createUserDataTypeUser(userReference));
			userDataA.setDataSource(userFactory.createUserDataTypeDataSource(sourceReference));
			userDataA.setData(userFactory.createUserDataTypeData(dataA));
			
			DataType dataB = userFactory.createDataType();
			dataB.setName(userFactory.createDataTypeName("HeightInches"));
			dataB.setValue(userFactory.createDataTypeValue("73"));
			
			UserDataType userDataB = userFactory.createUserDataType();
			userDataB.setId(entityFactory.createEntityTypeId("B"));
			userDataB.setUser(userFactory.createUserDataTypeUser(userReference));
			userDataB.setDataSource(userFactory.createUserDataTypeDataSource(sourceReference));
			userDataB.setData(userFactory.createUserDataTypeData(dataB));
			
			UserDataCollectionType userDataCollection = userFactory.createUserDataCollectionType();
			userDataCollection.getUserData().add(userDataA);
			userDataCollection.getUserData().add(userDataB);
			
			UserType user = userFactory.createUserType();
			
			user.setId(entityFactory.createEntityTypeId("1"));
			user.setFirstName(userFactory.createPersonTypeFirstName("Michael"));
			user.setLastName(userFactory.createPersonTypeLastName("Lambert"));
			user.setUserDatas(userFactory.createUserTypeUserDatas(userDataCollection));
			
			ImportUserResponse response = new ImportUserResponse();
			
			response.setUser(user);
			
			return response;
			
		} catch(Throwable t) {
			
			ErrorType error = new ErrorType();
			error.setCode("FooCode");
			error.setReasons(new ReasonCollectionType());
			error.setSubErrors(new ErrorCollectionType());
			
			ReasonType reason = new ReasonType();
			reason.setLang(Locale.US.getLanguage());
			reason.setValue("No real reason");
			
			error.getReasons().getReason().add(reason);
			
			ErrorType subError = new ErrorType();
			subError.setCode("BadSomething");
			subError.setReasons(new ReasonCollectionType());
			
			ReasonType subReason = new ReasonType();
			subReason.setLang(Locale.US.getLanguage());
			subReason.setValue("This is no big deal... really.");
			subError.getReasons().getReason().add(subReason);
			
			error.getSubErrors().getError().add(subError);
			
			throw new com.healthmedia.ws.wsdl.user.v1.Error("This is a message", error, t);
		}
	}
}
