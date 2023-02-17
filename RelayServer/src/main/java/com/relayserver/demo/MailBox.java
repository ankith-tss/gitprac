package com.relayserver.demo;
import java.io.Serializable;


class Payload implements Serializable
{
    
	private String type;
	// need to check data type
    
	private byte data[];
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	
}
class DisplayInformation implements Serializable
{
	private String title;
	private String description;
	private String imageURL;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	
}
class NotificationToken implements Serializable
{
	private String type;
	private String tokenData;
	private String testString;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTokenData() {
		return tokenData;
	}
	public void setTokenData(String tokenData) {
		this.tokenData = tokenData;
	}
	public String getTestString() {
		return testString;
	}
	public void setTestString(String testString) {
		this.testString = testString;
	}
	
	
	
	
}
class MailBoxConfiguration implements Serializable
{
	String accessRights;
	String expiration;
	
	public String getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	
	
}
public class MailBox implements Serializable {
	
//	String uUIDString;
	//mailboxConfiguration 
	private Payload payload;
	private DisplayInformation displayInformation;
	private NotificationToken notificationToken;
	private MailBoxConfiguration mailBoxConfiguration;
	private String mailboxIndentifier;
	private String senderDeviceClaim;
	private String receiverDeviceClaim;
	 
	
	public String getSenderDeviceClaim() {
		return senderDeviceClaim;
	}
	public void setSenderDeviceClaim(String senderDeviceClaim) {
		this.senderDeviceClaim = senderDeviceClaim;
	}
	public String getReceiverDeviceClaim() {
		return receiverDeviceClaim;
	}
	public void setReceiverDeviceClaim(String receiverDeviceClaim) {
		this.receiverDeviceClaim = receiverDeviceClaim;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	public DisplayInformation getDisplayInformation() {
		return displayInformation;
	}
	public void setDisplayInformation(DisplayInformation displayInformation) {
		this.displayInformation = displayInformation;
	}
	public NotificationToken getNotificationToken() {
		return notificationToken;
	}
	public void setNotificationToken(NotificationToken notificationToken) {
		this.notificationToken = notificationToken;
	}
	public MailBoxConfiguration getMailBoxConfiguration() {
		return mailBoxConfiguration;
	}
	public void setMailBoxConfiguration(MailBoxConfiguration mailBoxConfiguration) {
		this.mailBoxConfiguration = mailBoxConfiguration;
	}
	public String getMailboxIndentifier() {
		return mailboxIndentifier;
	}
	public void setMailboxIndentifier(String mailboxIndentifier) {
		this.mailboxIndentifier = mailboxIndentifier;
	}
	
	
	
	
	
	
	

}
