package com.example.barnaclebot.chantel.model;

import com.google.gson.annotations.SerializedName;


public class Data{

	@SerializedName("files_type")
	private String filesType;

	@SerializedName("file_name")
	private String fileName;

	@SerializedName("files_content")
	private String filesContent;

	public void setFilesType(String filesType){
		this.filesType = filesType;
	}

	public String getFilesType(){
		return filesType;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public String getFileName(){
		return fileName;
	}

	public void setFilesContent(String filesContent){
		this.filesContent = filesContent;
	}

	public String getFilesContent(){
		return filesContent;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"files_type = '" + filesType + '\'' + 
			",file_name = '" + fileName + '\'' + 
			",files_content = '" + filesContent + '\'' + 
			"}";
		}
}