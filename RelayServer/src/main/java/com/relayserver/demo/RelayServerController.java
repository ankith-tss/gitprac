package com.relayserver.demo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.UUID;

class UUIDGenerator {

	// generated a Unique Identifier for a MailBox
    public static UUID generateUUID() {
        // Initialize the SecureRandom
        SecureRandom rand = new SecureRandom();

        // Generate the random bytes
        byte[] data = new byte[16];
        rand.nextBytes(data);

        // Set the two most significant bits of the clock_seq_hi_and_reserved to zero and one
        data[6] &= 0x0f;
        data[6] |= 0x40;

        // Set the four most significant bits of the time_hi_and_version to the 4-bit version number
        data[8] &= 0x3f;
        data[8] |= 0x80;

        // Create the UUID object
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        return new UUID(msb, lsb);
    }
}


@RestController
public class RelayServerController {
	
	// checks whether all fields of a given object are NOT NULL.
	public boolean isAllFieldsNotNull(Object obj) {
		
		if(obj == null) {
			return false;
		}
		
	    for (Field field : obj.getClass().getDeclaredFields()) {
	        field.setAccessible(true);
	        try {
	            if (field.get(obj) == null) {
	                return false;
	            }
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }
	    return true;
	}

	
	// MailBoxIdentifier - MailBoxObject
	public Map<String, MailBox> mailboxesMap = new TreeMap<String, MailBox>();
	
	
	
	@GetMapping("/")
	public String test(){
		
		return    "APIs: \n"
				+ "1. CreateMailBox\n"
				+ "2. UpdateMailBox\n"
				+ "3. DeleteMailBox\n"
				+ "4. ReadDisplayInformationFromMailbox\n"
				+ "5. ReadSecureInformationFromMailbox\n";
	}
	
	@PostMapping(value = "/v1/m", consumes = "application/json")
	public ResponseEntity<Map<String, Object>> CreateMailBox(@RequestBody MailBox mailBox, @RequestHeader Map<String, String> headers){
		
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		// Checking for senderDeviceClaim
		
		String senderDeviceClaim = headers.get("deviceclaim");
		
		if(senderDeviceClaim == null) {
			responseMap.put("message", "Please provide your device claim!");
			
			// 401
			return new ResponseEntity<>(responseMap, HttpStatus.UNAUTHORIZED);
		}
			
		/*** need to map this senderDeviceClaim ***/
		
		// Checking Required Fields
		Payload payload = mailBox.getPayload();
		NotificationToken notificationToken = mailBox.getNotificationToken();
		DisplayInformation displayInformation = mailBox.getDisplayInformation();
		MailBoxConfiguration mailBoxConfiguration = mailBox.getMailBoxConfiguration();
		
		// Whenever mailBoxConfiguration is not null, Expiration is required.
		if(mailBoxConfiguration != null && mailBoxConfiguration.getExpiration() == null){
			responseMap.put("message", "All required fields are not present!");
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		
		if(isAllFieldsNotNull(payload) == false || isAllFieldsNotNull(notificationToken) == false || isAllFieldsNotNull(displayInformation) == false){
			responseMap.put("message", "All required fields are not present!");
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		
		
		
		// Generating UUID for the mailBox and returning the Response.
		try {
			
			UUID uuid = UUIDGenerator.generateUUID();
			mailBox.setMailboxIndentifier(uuid.toString());
			mailBox.setSenderDeviceClaim(senderDeviceClaim);
			
			// DB
			mailboxesMap.put(mailBox.getMailboxIndentifier(), mailBox);
			
			
			responseMap.put("urlLink", "http://localhost:8080/v1/m/" + mailBox.getMailboxIndentifier());
			responseMap.put("isPushNotificationSupported", false);
			
			return new ResponseEntity<>(responseMap, HttpStatus.OK); 

			
		} catch (Exception e) {
			
			responseMap.clear();
			responseMap.put("message", e.getMessage());
			
		}
		
		return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		

	}
	
	@PutMapping(value = "/v1/m", consumes = "application/json", params = {"mailboxIdentifier"})
	public ResponseEntity<Map<String, Object>> UpdateMailBox(@RequestParam String mailboxIdentifier,  @RequestBody MailBox mailBoxReq, @RequestHeader Map<String, String> headers){
		
		
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		// Checking for senderDeviceClaim
		
		String receiverDeviceClaim = headers.get("deviceClaim");
		
		if(receiverDeviceClaim == null) {
			responseMap.put("message", "Please provide your device claim!");
			
			// 401
			return new ResponseEntity<>(responseMap, HttpStatus.UNAUTHORIZED);
		}
		
		
		
		Payload payloadReq = mailBoxReq.getPayload();
		NotificationToken notificationTokenReq = mailBoxReq.getNotificationToken();
		
		if(isAllFieldsNotNull(payloadReq) == false || (notificationTokenReq != null && isAllFieldsNotNull(payloadReq) == false)) {
			responseMap.put("message", "All required fields are not present!");
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		
		// check whether there is a MailBoxObj in DB corresponding to the mailBoxIdentifier.
		if(mailboxesMap.containsKey(mailboxIdentifier) == false) {
			responseMap.put("message", "Not Found - mailbox with provided mailboxIdentifier not found");
			
			return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
		}
		
		try {
			
			
			// DB 
			MailBox mailBox = mailboxesMap.get(mailboxIdentifier);
			
			mailBox.setPayload(payloadReq);
			mailBox.setNotificationToken(notificationTokenReq);
			
			// DB
			mailboxesMap.replace(mailboxIdentifier, mailBox);
			
			responseMap.put("isPushNotificationSupported", mailboxesMap.get(mailboxIdentifier));
			
			return new ResponseEntity<>(responseMap, HttpStatus.OK); 
			
			
		} catch (Exception e) {
			// DB
			responseMap.clear();

			responseMap.put("message", e.getMessage());
		}
		
		
		return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@DeleteMapping(value = "/v1/m", params = {"mailboxIdentifier"})
	public ResponseEntity<Map<String, Object>> DeleteMailBox(@RequestParam String mailboxIdentifier){
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		if(mailboxesMap.containsKey(mailboxIdentifier) == false) {

			responseMap.put("message", "Not Found - mailbox with provided mailboxIdentifier not found");
			
			return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
		}
		
		try {
			mailboxesMap.remove(mailboxIdentifier);

			return new ResponseEntity<>(responseMap, HttpStatus.OK); 
			
		} catch (Exception e) {
			
			responseMap.clear();
			responseMap.put("message", e.getMessage());
			
		}
			
		return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@GetMapping(value = "/v1/m", params = {"mailboxIdentifier"})
	public ResponseEntity<Map<String, Object>> ReadDisplayInformationFromMailbox(@RequestParam String mailboxIdentifier){
		
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		if(mailboxesMap.containsKey(mailboxIdentifier) == false) {

			responseMap.put("message", "Not Found - mailbox with provided mailboxIdentifier not found");
			
			return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
		}
		
		try {
			
			MailBox mailBox = mailboxesMap.get(mailboxIdentifier);
			
			
			responseMap.put("displayInformation", mailBox.getDisplayInformation());
			
			return new ResponseEntity<>(responseMap, HttpStatus.OK); 
			
		} catch (Exception e) {
			
			responseMap.clear();
			responseMap.put("message", e.getMessage());
			
		}
			
		return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@PostMapping(value = "/v1/m", params = {"mailboxIdentifier"})
	public ResponseEntity<Map<String, Object>> ReadSecureInformationFromMailbox(@RequestParam String mailboxIdentifier){
		
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		if(mailboxesMap.containsKey(mailboxIdentifier) == false) {
			
			responseMap.put("message", "Not Found - mailbox with provided mailboxIdentifier not found");
			
			return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
		}
		
		try {
			
			MailBox mailBox = mailboxesMap.get(mailboxIdentifier);
			

			responseMap.put("displayInformation", mailBox.getDisplayInformation());
			responseMap.put("payload", mailBox.getPayload());
			// ***
			responseMap.put("expiration", mailBox.getMailBoxConfiguration().getExpiration());
			
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
			
		} catch (Exception e) {
			
			responseMap.clear();
			responseMap.put("message", e.getMessage());
			
		}
		
		return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@GetMapping("/mailboxes")
	public Map<String, MailBox> MailBoxes(){
		
		return mailboxesMap;
	}

}
