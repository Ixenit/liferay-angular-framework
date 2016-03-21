package com.ixenit.liferay.angular.portlet.parameter;

import com.ixenit.liferay.angular.portlet.annotation.ResourceParam;

import java.io.File;

/**
 * Data transfer object to hold the reference to the uploaded file and the name of the file from the upload
 * request. This class could be used by the {@link ResourceParam} annotation.
 *
 * @author Benjamin Hajnal
 *
 */
public class FileWrapper {

	private File file;

	private String fileName;

	public FileWrapper(File file, String fileName) {
		this.file = file;
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
