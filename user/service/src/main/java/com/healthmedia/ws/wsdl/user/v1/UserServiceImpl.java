package com.healthmedia.ws.wsdl.user.v1;

import java.util.Locale;

import com.healthmedia.ws.common.error.v1.CompoundErrorType;
import com.healthmedia.ws.common.error.v1.ErrorCollectionType;
import com.healthmedia.ws.common.error.v1.ErrorType;
import com.healthmedia.ws.common.error.v1.ReasonType;
import com.healthmedia.ws.entity.dataSouce.v1.DataSourceType;
import com.healthmedia.ws.entity.user.v1.DataType;
import com.healthmedia.ws.entity.user.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.user.v1.UserDataType;
import com.healthmedia.ws.entity.user.v1.UserType;

public class UserServiceImpl implements UserService {

	private com.healthmedia.ws.common.v1.ObjectFactory commonFactory = new com.healthmedia.ws.common.v1.ObjectFactory();
	private com.healthmedia.ws.entity.user.v1.ObjectFactory userFactory = new com.healthmedia.ws.entity.user.v1.ObjectFactory();
	private com.healthmedia.ws.entity.dataSouce.v1.ObjectFactory dataSourceFactory = new com.healthmedia.ws.entity.dataSouce.v1.ObjectFactory();
	
	@Override
	public ImportUserResponse importUser(ImportUserRequest parameters) throws Fault {
		
		try {
			
			UserType input = parameters.getUser();
			if(input.getId() == null) {
				throw new IllegalArgumentException("id cannot be null");
			}
			UserType userReference = userFactory.createUserType();
			userReference.setId(commonFactory.createEntityTypeId("1"));
			
			DataSourceType sourceReference = dataSourceFactory.createDataSourceType();
			sourceReference.setId(commonFactory.createEntityTypeId("1A"));
			
			DataType dataA = userFactory.createDataType();
			dataA.setName(userFactory.createDataTypeName("WeightPounds"));
			dataA.setValue(userFactory.createDataTypeValue("145"));
			
			UserDataType userDataA = userFactory.createUserDataType();
			userDataA.setId(commonFactory.createEntityTypeId("A"));
			userDataA.setUser(userFactory.createUserDataTypeUser(userReference));
			userDataA.setDataSource(userFactory.createUserDataTypeDataSource(sourceReference));
			userDataA.setData(userFactory.createUserDataTypeData(dataA));
			
			DataType dataB = userFactory.createDataType();
			dataB.setName(userFactory.createDataTypeName("HeightInches"));
			dataB.setValue(userFactory.createDataTypeValue("73"));
			
			UserDataType userDataB = userFactory.createUserDataType();
			userDataB.setId(commonFactory.createEntityTypeId("B"));
			userDataB.setUser(userFactory.createUserDataTypeUser(userReference));
			userDataB.setDataSource(userFactory.createUserDataTypeDataSource(sourceReference));
			userDataB.setData(userFactory.createUserDataTypeData(dataB));
			
			UserDataCollectionType userDataCollection = userFactory.createUserDataCollectionType();
			userDataCollection.getUserData().add(userDataA);
			userDataCollection.getUserData().add(userDataB);
			
			UserType user = userFactory.createUserType();
			
			user.setId(commonFactory.createEntityTypeId("1"));
			user.setFirstName(userFactory.createUserTypeFirstName("Michael"));
			user.setLastName(userFactory.createUserTypeLastName("Lambert"));
			user.setUserDataCollection(userFactory.createUserTypeUserDataCollection(userDataCollection));
			
			ImportUserResponse response = new ImportUserResponse();
			
			response.setUser(user);
			
			return response;
			
		} catch(Throwable t) {
			
			CompoundErrorType error = new CompoundErrorType();
			error.setCode("FooCode");
			
			ReasonType reason = new ReasonType();
			reason.setLang(Locale.US.getLanguage());
			reason.setValue("No real reason");
			
			error.getReason().add(reason);
			
			ErrorType subError = new ErrorType();
			subError.setCode("BadSomething");
			
			ReasonType subReason = new ReasonType();
			subReason.setLang(Locale.US.getLanguage());
			subReason.setValue("This is no big deal... really.");
			subError.getReason().add(subReason);
			
			ErrorCollectionType errorCollection = new ErrorCollectionType();
			errorCollection.getError().add(subError);
			error.setErrors(errorCollection);
			
			Fault fault = new Fault("This is a message", error, t);
			throw fault;
		}
	}
}
