package com.ecw.deidtool.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix="ccda.storage")
@Component
@Getter
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	private final String location = "temp";


}
