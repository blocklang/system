package com.blocklang.system.utils;

import java.util.UUID;

public class IdGenerator {

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
}
