package cn.lemon.fileservice.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.csource.oss.AliossConfig;
import org.csource.oss.StorageClient;
import org.springframework.stereotype.Service;

import cn.lemon.fileservice.service.IFileClient;

/**
 * 阿里云oss文件服务器上传
 * Created by lonyee on 2017/8/2.
 *
 */
@Service("aliossClient")
public class AliossClient implements IFileClient {
	
	@Resource(name="aliossConfig")
	private AliossConfig ossConfig;
	
	private StorageClient storageClient;
	
	/** 
     * 构造方法执行后，初始化fastdfs配置 
     */
    @PostConstruct
    public void init() {
    	storageClient = new StorageClient(ossConfig);
	}
	
    /**
     * 上传文件
     */
	@Override
	public String uploadFile(byte[] fileBytes, long size, String ext) throws IOException {
		InputStream inputStream = new ByteArrayInputStream(fileBytes);
		String filePath = "upload";
		String fileId = storageClient.uploadFile(filePath, inputStream, size, ext);
		return fileId;
	}

	/**
     * 查看下载文件
     */
	@Override
	public byte[] downloadFile(String filePath) throws IOException {
		return storageClient.download(filePath, null);
	}
	
	/**
     * 查看下载压缩文件 等比缩放
     */
	@Override
	public byte[] downloadThumb(String filePath, int size) throws IOException {
		String style = String.format("image/resize,m_mfit,w_%s,h_%s", size, size);
		return storageClient.download(filePath, style);
	}

	/**
     * 查看下载压缩文件 裁剪缩放
     */
	@Override
	public byte[] downloadThumb(String filePath, int width, int height) throws IOException {
		String style = String.format("image/crop,w_%s,h_%s,x_%s,y_%s", width, height, width, height);
		return storageClient.download(filePath, style);
	}

}
