package com.healthmedia.ws.service.userdata.v1;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.healthmedia.ws.entity.userdata.v1.DataSourceType;
import com.healthmedia.ws.entity.userdata.v1.DataType;
import com.healthmedia.ws.entity.userdata.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.userdata.v1.UserDataType;
import com.healthmedia.ws.entity.userdata.v1.UserType;
import com.healthmedia.ws.service.userdata.v1.UserDataService;

public class UserDataServiceImpl implements UserDataService {

	@Override
	public UserDataType retrieve(String id) {
		try {
			GregorianCalendar collectionDate = new GregorianCalendar();
			collectionDate.set(2010, GregorianCalendar.JANUARY, 20);
			
			GregorianCalendar updateDate = new GregorianCalendar();
			collectionDate.set(2008, GregorianCalendar.NOVEMBER, 20);
			
			GregorianCalendar createDate = new GregorianCalendar();
			collectionDate.set(2008, GregorianCalendar.SEPTEMBER, 20);
			
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDataType create(UserDataType userdatatype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDataCollectionType createMultiple(UserDataCollectionType userdatacollectiontype) {
		// TODO Auto-generated method stub
		return null;
	}
}
