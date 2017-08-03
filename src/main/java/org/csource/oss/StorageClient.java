package org.csource.oss;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.google.common.base.Strings;

/**
 * 文件上传至阿里oss
 * @author lonyee
 * 2017年7月29日
 *
 */
public class StorageClient {
	private static Log logger = LogFactory.getLog(StorageClient.class);
	
	private AliossConfig ossConfig;
	
	public StorageClient(AliossConfig ossConfig) {
		this.ossConfig = ossConfig;
	}
	
	/**
	 * 文件上传至OSS
	 * @param filePath 上传文件存储路径
	 * @param inputStream 数据流
	 * @param size 文件大小
	 * @param ext 文件扩展名
	 * @return 文件访问路径
	 */
	public String uploadFile(String filePath, InputStream inputStream, long size, String ext) {
		String fileId = UUID.randomUUID().toString().replace("-", "");
		String fileName = String.format("%s/%s.%s", filePath, fileId, ext);
		this.upload(inputStream, size, fileName);
		return fileName;
	}
	
	/**
	 * 文件上传至OSS
	 * @param filePath 上传文件存储路径
	 * @param file 数据文件
	 */
	public String uploadFile(String filePath, File file) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(file);
		String ext = StringUtils.getFilenameExtension(file.getName());
		return this.uploadFile(filePath, inputStream, file.length(), ext);
	}
	
	/**
	 * 文件上传OSS
	 */
	private void upload(final InputStream inputStream, final long size, final String fileName) {
		OSSClient ossClient = new OSSClient(ossConfig.getEndPoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
		try {
			
			this.createBucket(ossClient, ossConfig.getBucketName(), true);
			
			// 创建上传Object的Metadata
			ObjectMetadata meta = new ObjectMetadata();
			// 必须设置ContentLength
			meta.setContentLength(size);
			meta.setHeader("Content-Disposition", "inline");			
			PutObjectResult result = ossClient.putObject(ossConfig.getBucketName(), fileName, inputStream, meta);
			logger.info("upload oss file " + fileName + " success, tag: " +result.getETag());
		} catch (Exception e) {
			logger.error("upload oss file " + fileName + " error. ", e);
			throw new RuntimeException("upload oss file error", e);
		} finally {
			try {
				inputStream.close();
				ossClient.shutdown();
			} catch (IOException e) {}
			logger.info(String.format("thread %d finished", this.hashCode()));
		}
	}
	
	/**
	 * 文件下载OSS
	 */
	public byte[] download(final String fileName, String style) {
		OSSClient ossClient = new OSSClient(ossConfig.getEndPoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
		try {
			GetObjectRequest request = new GetObjectRequest(ossConfig.getBucketName(), fileName);
			if (!Strings.isNullOrEmpty(style)) {
			    request.setProcess(style);
			}
			OSSObject result = ossClient.getObject(request);
			InputStream inputStream = result.getObjectContent();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
			byte[] buff = new byte[1024];
	        int rc = 0;  
	        while ((rc = inputStream.read(buff, 0, buff.length)) > 0) {
	        	outputStream.write(buff, 0, rc);
	        }
	        byte[] in2b = outputStream.toByteArray();
			inputStream.close();
	        outputStream.close();
			logger.info("download oss file " + fileName + " success, tag: " +result.getObjectMetadata().getETag());
	        return in2b;
		} catch (Exception e) {
			logger.error("download oss file " + fileName + " error. ", e);
			throw new RuntimeException("download oss file error", e);
		} finally {
			ossClient.shutdown();
			logger.info(String.format("thread %d finished", this.hashCode()));
		}
	}
	
	
	/**
	 * 创建bucket，阿里设定最多10个bucketName
	 * @param bucketName bucket名称
	 * @param isPublic 是否可以公开访问
	 */
	public void createBucket(OSSClient ossClient, String bucketName, Boolean isPublic){
		boolean hasExists = ossClient.doesBucketExist(bucketName);
		if (!hasExists) {
			// 新建一个Bucket
			ossClient.createBucket(bucketName);
			// 设置操作权限对外公开
			ossClient.setBucketAcl(bucketName, isPublic? CannedAccessControlList.PublicRead: CannedAccessControlList.Private); 
		}
	}
}
