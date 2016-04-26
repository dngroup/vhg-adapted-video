package fr.labri.progress.comet.service;

import java.net.MalformedURLException;
import java.net.URI;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.BaseEncoding;

import fr.labri.progress.comet.conf.CliConfSingleton;

@Service
public class SwiftServiceImp implements SwiftService {
	private static Logger LOGGER = LoggerFactory.getLogger(SwiftServiceImp.class);

	@Inject
	Client client;

	final String SwiftLogin = CliConfSingleton.swiftLogin;
	final String SwiftPassword = CliConfSingleton.swiftPassword;

	final String url = CliConfSingleton.swiftUrl;
	final String pathAuth = CliConfSingleton.swiftPathAuth;
	final String sharedKey = CliConfSingleton.swiftSharedKey;

	String xAuthToken = null;
	URI xStorageUrl = null;

	@Override
	public void loginAndCreateContainer(String id) {
		WebTarget target = client.target(url + pathAuth);
		LOGGER.debug("Send request to server to login {}", target.getUri());
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
			xStorageUrl = UriBuilder.fromUri(response.getHeaderString("X-Storage-Url")).build();
		} catch (IllegalArgumentException e) {
			LOGGER.error("can not converte the URI: {}", response.getHeaderString("X-Storage-Url"), e);
		} catch (UriBuilderException e) {
			LOGGER.error("can not converte URL builder have a problem", e);
		}

		target = client.target(xStorageUrl.toString());
		LOGGER.debug("Send request to server to put shared key {}", target.getUri());
		response = target.request().header("X-Account-Meta-Temp-URL-Key", sharedKey).header("X-Auth-Token", xAuthToken)
				.post(Entity.json("hello"), Response.class);

		switch (Status.fromStatusCode(response.getStatus())) {
		case OK:
		case NO_CONTENT:
		case ACCEPTED:
			LOGGER.debug("shared key added");

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

		target = client.target(xStorageUrl.toString() + "/" + id);
		LOGGER.debug("Send request to server to creat conteiner {}", target.getUri());
		response = target.request().header("X-Auth-Token", xAuthToken).header("X-Container-Read"," .r:*").header("X-Container-Meta-Access-Control-Allow-Origin", "*").header("X-Container-Meta-Access-Control-Allow-Method", "GET").put(Entity.json("hello"), Response.class);

		switch (Status.fromStatusCode(response.getStatus())) {
		case ACCEPTED:
		case CREATED:
			LOGGER.debug("Container created on swift");

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

	}

	@Override
	public URL GenerateReturnURI(String name, String id) {
		return GenerateReturnURI(name, id, "PUT");
	}

	@Override
	public URL GenerateReturnURI(String name, String id, String method) {
		try {
			final long duration = 60 * 60 * 4;// 4 hours (value in second)
			long expire = duration + (System.currentTimeMillis() / 1000L);
			URI object = UriBuilder.fromUri(xStorageUrl).path(id).path(name).build();

			String hmac_body = method + "\n" + expire + "\n" + object.getPath();

			SecretKeySpec keySpec = new SecretKeySpec(sharedKey.getBytes(), "HmacSHA1");

			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(keySpec);
			byte[] rawHmac = mac.doFinal(hmac_body.getBytes());
			String result = BaseEncoding.base16().lowerCase().encode(rawHmac);
			URI returnUrl = UriBuilder.fromUri(object).queryParam("temp_url_sig", result).queryParam("temp_url_expires", expire).build();
			return returnUrl.toURL();
		} catch (InvalidKeyException e) {
			LOGGER.error("the key :\"{}\" is not correct", e);
			throw new WebApplicationException(e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("The chosen algorithm was not found ", e);
			throw new WebApplicationException(e);
		} catch (MalformedURLException e) {
			LOGGER.error("the URL is not correct maybe someting not coorect verfie this {}", xStorageUrl);
			throw new WebApplicationException(e);
		}

	}

}
