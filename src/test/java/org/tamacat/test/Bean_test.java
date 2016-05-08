package org.tamacat.test;

import org.tamacat.util.ClassUtils;

public class Bean_test {

	public static void main(String[] args) throws Exception {
		
		//String name = Introspector.decapitalize("page_settings");
		
		String type = "page_settings_test";
		if (type.indexOf('_')>=0) {
			type = type.replaceFirst("_", ".");
			
			String[] pkgClass = type.split("\\.");
			if (pkgClass.length>=2) {
				type = pkgClass[0] + "." + ClassUtils.getCamelCaseName(pkgClass[1]);
			}
		}
		
		System.out.println(type);

	}

}
