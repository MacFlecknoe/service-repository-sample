package com.healthmedia.ws.service.userdata.v1;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.healthmedia.ws.entity.userdata.v1.DataSourceType;
import com.healthmedia.ws.entity.userdata.v1.DataType;
import com.healthmedia.ws.entity.userdata.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.userdata.v1.UserDataType;
import com.healthmedia.ws.entity.userdata.v1.UserType;
import com.healthmedia.ws.service.userdata.v1.UserDataService;

/**
 * Mock service created to demonstrate setup of future REST based web services.
 * 
 * @author mlamber7
 *
 */
public class UserDataServiceImpl implements UserDataService {

	@Override
	public UserDataType retrieve(String id) {
		try {
			GregorianCalendar collectionDate = new GregorianCalendar();
			collectionDate.set(2010, GregorianCalendar.JANUARY, 20);
			
			GregorianCalendar updateDate = new GregorianCalendar();
			updateDate.set(2008, GregorianCalendar.NOVEMBER, 20);
			
			GregorianCalendar createDate = new GregorianCalendar();
			createDate.set(2008, GregorianCalendar.SEPTEMBER, 20);
			
			DataSourceType dataSource = new DataSourceType();
			dataSource.setId("453ba2");
			
			UserType user = new UserType();
			user.setId("0d8a7c33");
			
			DataType data = new DataType();
			data.setName("WeightPounds");
			data.setValue("134");
			
			UserDataType userData = new UserDataType();
			userData.setId(id);
			userData.setCollectionDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(collectionDate));
			userData.setDataSource(dataSource);
			userData.setUser(user);
			userData.setData(data);
			userData.setUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(updateDate));
			userData.setCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(createDate));
			
			return userData;
			
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UserDataCollectionType search(String query, Date updateDate_start, Date updateDate_end, String data_name, String user_id) {
		
		UserDataCollectionType collection = new UserDataCollectionType();
		
		collection.getUserData().add(this.retrieve("342a"));
		collection.getUserData().add(this.retrieve("34234a"));
		
		return collection;
	}

	@Override
	public UserDataType create(UserDataType userdatatype) {
		try {
			Date current = new Date();
			
			GregorianCalendar currentDate = new GregorianCalendar();
			currentDate.setTime(current);
			
			userdatatype.setUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(currentDate));
			userdatatype.setCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(currentDate));
			
			return userdatatype;
		
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UserDataCollectionType createMultiple(UserDataCollectionType userdatacollectiontype) {
		
		List<UserDataType> userDataTypes = userdatacollectiontype.getUserData();
		
		for(UserDataType userData : userDataTypes) {
			create(userData);
		}
		return userdatacollectiontype;
	}
}
