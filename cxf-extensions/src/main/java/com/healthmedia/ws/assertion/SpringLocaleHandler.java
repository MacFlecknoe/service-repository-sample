package com.healthmedia.ws.assertion;

import java.util.Locale;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

public class SpringLocaleHandler implements I18nHeaderInterceptor.ILocaleHandler {

	@Override
	public void setLocale(String i18nLocale) {
		
		if(!StringUtils.isEmpty(i18nLocale)) {
			Locale locale = new Locale(i18nLocale);
			LocaleContextHolder.setLocale(locale);
		}
	}

}
