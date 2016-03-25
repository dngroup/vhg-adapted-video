package fr.labri.progress.comet.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;

import fr.labri.progress.comet.conf.CliConfSingleton;

@Service
public class SwiftServiceImp implements SwiftService {
	private static Logger LOGGER = LoggerFactory.getLogger(SwiftServiceImp.class);

	@Inject
	Client client;

	String SwiftLogin = CliConfSingleton.swiftLogin;
	String SwiftPassword = CliConfSingleton.swiftPassword;

	String url = CliConfSingleton.swiftUrl;
	String pathAuth = CliConfSingleton.swiftPathAuth;
	String sharedKey = CliConfSingleton.swiftSharedKey;

	String xAuthToken;
	URL xStorageUrl;

	@Override
	public void loginAndCreateContainer(String id) {
		WebTarget target = client.target(url + pathAuth);
		LOGGER.debug("Send request to server {}", target.getUri());
		Response response = target.request().header("X-Auth-User", SwiftLogin).header("X-Auth-Key", SwiftPassword)
				.get(Response.class);

		switch (Status.fromStatusCode(response.getStatus())) {
		case OK:
			LOGGER.debug("connetion to swift ok");

			break;
		case UNAUTHORIZED:
			LOGGER.error("Bad USERNAME or PASSWORD for swift");
			throw new WebApplicationException(response.getStatus());

		case BAD_REQUEST:
			LOGGER.error("Bad URL for swift");
			throw new WebApplicationException(response.getStatus());

		default:
			LOGGER.error("Swift connection probleme: {}", response.getStatus());
			throw new WebApplicationException(response.getStatus());
		}

		xAuthToken = response.getHeaderString("X-Auth-Token");

		try {
			xStorageUrl = new URL(response.getHeaderString("X-Storage-Url"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		target = client.target(xStorageUrl.toString());
		LOGGER.debug("Send request to server  {}", target.getUri());
		response = target.request().header("X-Account-Meta-Temp-URL-Key", sharedKey).header("X-Auth-Token", xAuthToken)
				.post(Entity.json("hello"), Response.class);

		switch (Status.fromStatusCode(response.getStatus())) {
		// TODO: verfy this return message
		case OK:		
		case NO_CONTENT:
		case ACCEPTED:
			LOGGER.debug("connetion to swift ok");

			break;
		case UNAUTHORIZED:
			LOGGER.error("Bad USERNAME or PASSWORD for swift");
			throw new WebApplicationException(response.getStatus());

		case BAD_REQUEST:
			LOGGER.error("Bad URL for swift");
			throw new WebApplicationException(response.getStatus());

		default:
			LOGGER.error("Swift connection probleme: {}", response.getStatus());
			throw new WebApplicationException(response.getStatus());
		}
//		try {
//			xStorageUrl = new URL(response.getHeaderString("X-Storage-Url"));
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		
		target = client.target(xStorageUrl.toString()+"/"+id);
		LOGGER.debug("Send request to server  {}", target.getUri());
		response = target.request().header("X-Auth-Token", xAuthToken)
				.put(Entity.json("hello"), Response.class);

		switch (Status.fromStatusCode(response.getStatus())) {
		// TODO: verfy this return message
		case ACCEPTED:
		case CREATED:
			LOGGER.debug("connetion to swift ok");

			break;
		case UNAUTHORIZED:
			LOGGER.error("Bad USERNAME or PASSWORD for swift");
			throw new WebApplicationException(response.getStatus());

		case BAD_REQUEST:
			LOGGER.error("Bad URL for swift");
			throw new WebApplicationException(response.getStatus());

		default:
			LOGGER.error("Swift connection probleme: {}", response.getStatus());
			throw new WebApplicationException(response.getStatus());
		}
//		try {
//			xStorageUrl = new URL(response.getHeaderString("X-Storage-Url"));
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}


	}
	


	@Override
	public URL GenerateReturnURI(String name, String id) {
		return GenerateReturnURI( name,  id,"PUT");
	}
	

	@Override
	public URL GenerateReturnURI(String name, String id,String methode) {


		// @debug:on
		if (xStorageUrl == null) {
			LOGGER.warn("Debug mode");
			try {
				xStorageUrl = new URL("http://swift-proxy.fr:8956/v1/auth_admin");
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// @debug:off

		long duration_in_seconds = 60 * 60 * 4;// 4 hours
		long expire = duration_in_seconds + (System.currentTimeMillis() / 1000L);
//		long expire = 1458746829L;
		String path = xStorageUrl.getPath() + "/" + id + "/" + name;
		String hmac_body = methode + "\n" + expire + "\n" + path;

		SecretKeySpec keySpec = new SecretKeySpec(sharedKey.getBytes(), "HmacSHA1");

		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(keySpec);
			byte[] rawHmac = mac.doFinal(hmac_body.getBytes());
			String result = BaseEncoding.base16().lowerCase().encode(rawHmac);
			String returnUrl = xStorageUrl.getProtocol() + "://" + xStorageUrl.getAuthority() + path + "?temp_url_sig="
					+ result + "&temp_url_expires=" + expire;

			return new URL(returnUrl);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException(e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException(e);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException(e);
		}

	}

}
