package com.healthmedia.ws.service.rest.userdata.v1;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.healthmedia.ws.common.error.BadArgumentsException;
import com.healthmedia.ws.common.error.BadArgumentsException.BadArgumentError;
import com.healthmedia.ws.common.error.DataValidationException;
import com.healthmedia.ws.common.error.DataValidationException.DataValidationError;
import com.healthmedia.ws.common.error.GenericErrorException;
import com.healthmedia.ws.entity.dataSouce.v1.DataSourceType;
import com.healthmedia.ws.entity.user.v1.DataType;
import com.healthmedia.ws.entity.user.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.user.v1.UserDataType;
import com.healthmedia.ws.entity.user.v1.UserType;

/**
 * Mock service created to demonstrate setup of future REST based web services.
 * 
 * @author mlamber7
 *
 */
public class UserDataServiceImpl implements UserDataService {
	
	private static Logger LOGGER = Logger.getLogger(UserDataServiceImpl.class);
	
	private com.healthmedia.ws.common.v1.ObjectFactory commonFactory = new com.healthmedia.ws.common.v1.ObjectFactory();
	private com.healthmedia.ws.entity.user.v1.ObjectFactory userFactory = new com.healthmedia.ws.entity.user.v1.ObjectFactory();
	private com.healthmedia.ws.entity.dataSouce.v1.ObjectFactory dataSourceFactory = new com.healthmedia.ws.entity.dataSouce.v1.ObjectFactory();
	
	@Override
	public UserDataType create(UserDataType userdatatype) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing create");
		}
		try {
			Date current = new Date();
			
			UserDataType userData = userFactory.createUserDataType();
			
			GregorianCalendar currentDate = new GregorianCalendar();
			currentDate.setTime(current);
			
			XMLGregorianCalendar today = DatatypeFactory.newInstance().newXMLGregorianCalendar(currentDate);
			
			userData.setUpdateDate(commonFactory.createEntityTypeUpdateDate(today));
			userData.setCreateDate(commonFactory.createEntityTypeCreateDate(today));
			userData.setDataSource(userdatatype.getDataSource());
			userData.setId(commonFactory.createEntityTypeId("23"));
			
			return userData;
		
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public UserDataType findById(String id) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing find by id");
		}			
		try {
			GregorianCalendar collectionDate = new GregorianCalendar();
			collectionDate.set(2010, GregorianCalendar.JANUARY, 20);
			
			GregorianCalendar updateDate = new GregorianCalendar();
			updateDate.set(2008, GregorianCalendar.NOVEMBER, 20);
			
			GregorianCalendar createDate = new GregorianCalendar();
			createDate.set(2008, GregorianCalendar.SEPTEMBER, 20);
			
			DataSourceType dataSource = dataSourceFactory.createDataSourceType();
			dataSource.setId(commonFactory.createEntityTypeId("453ba2"));
			
			UserType user = new UserType();
			user.setId(commonFactory.createEntityTypeId("0d8a7c33"));
			
			DataType data = userFactory.createDataType();
			data.setName(userFactory.createDataTypeName("WeightPounds"));
			data.setValue(userFactory.createDataTypeValue("135"));
			
			UserDataType userData = userFactory.createUserDataType();
			userData.setId(commonFactory.createEntityTypeId("0d8a7c33"));
			
			userData.setCollectionDate(userFactory.createUserDataTypeCollectionDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(collectionDate)));
			userData.setDataSource(userFactory.createUserDataTypeDataSource(dataSource));
			userData.setUser(userFactory.createUserDataTypeUser(user));
			userData.setData(userFactory.createUserDataTypeData(data));
			userData.setUpdateDate(commonFactory.createEntityTypeUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(updateDate)));
			userData.setCreateDate(commonFactory.createEntityTypeCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(createDate)));
			
			return userData;
			
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public UserDataCollectionType findByQuery(String query, Date updateDate_start, Date updateDate_end, List<String> data_name, List<String> user_id) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing find by query");
		}
		if(query == null || query.equals("bad_query")) {
			
			BadArgumentsException badArgumentException = new BadArgumentsException();
			
			badArgumentException.getBadArgumentErrors().add(new BadArgumentError("query", query));
			badArgumentException.getBadArgumentErrors().add(new BadArgumentError("updateDate_end", updateDate_start)); 
			
			throw badArgumentException;
		}
		if(query.equals("data_def")) {
			
			DataValidationException validationException = new DataValidationException("123", "validation issue");
			
			validationException.getDataValidationErrors().add(new DataValidationError("ABC", "Foo", "This is foo"));
			validationException.getDataValidationErrors().add(new DataValidationError("DEF", "Bar", "This is bar"));
			
			throw validationException;
		}
		if(query.equals("error_code")) {
			throw new GenericErrorException("1234CODE", "Generic error message");
		}
		if(query.equals("throwable")) {
			throw new RuntimeException("this should be logged");
		}
		UserDataCollectionType collection = new UserDataCollectionType();
		
		collection.getUserData().add(this.findById("342a"));
		collection.getUserData().add(this.findById("34234a"));
		
		return collection;
	}

	@Override
	public UserDataCollectionType createCollection(UserDataCollectionType userdatacollectiontype) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing create collection");
		}
		List<UserDataType> userDataTypes = userdatacollectiontype.getUserData();
		
		for(UserDataType userData : userDataTypes) {
			create(userData);
		}
		return userdatacollectiontype;
	}
}
