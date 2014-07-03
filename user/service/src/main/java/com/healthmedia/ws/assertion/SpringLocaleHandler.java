package com.healthmedia.ws.assertion;

import java.util.Locale;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.w3._2005._09.ws_i18n.International;

public class SpringLocaleHandler implements I18nHeaderInterceptor.ILocaleHandler {

	@Override
	public void setLocale(International international) {
		
		String i18nLocale = international.getLocale();
		
		if(!StringUtils.isEmpty(i18nLocale)) {
			Locale locale = new Locale(i18nLocale);
			LocaleContextHolder.setLocale(locale);
		}
	}

}
