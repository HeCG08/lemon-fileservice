package org.csource.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.base.Strings;

/**
 * 阿里OSS上传服务配置
 * @author lonyee
 *
 */
@ConfigurationProperties(prefix = "alioss")
public class AliossConfig {
	private String bucketName;
	private String endPoint;
	private String baseOssUri;
	private String accessKeyId;
	private String accessKeySecret;
	
	public String getBaseOssUri() {
		if (Strings.isNullOrEmpty(endPoint)) {
			return endPoint;
		}
		if (!Strings.isNullOrEmpty(baseOssUri)) {
			return baseOssUri;
		}
		int idx = endPoint.indexOf("://")+3;
		baseOssUri = endPoint.substring(idx) + this.bucketName + endPoint.substring(idx, endPoint.length())+"/";
		return baseOssUri;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	public String getAccessKeyId() {
		return accessKeyId;
	}
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	public String getAccessKeySecret() {
		return accessKeySecret;
	}
	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
}
