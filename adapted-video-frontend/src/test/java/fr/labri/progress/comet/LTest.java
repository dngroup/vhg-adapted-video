/**
 * 
 */
package fr.labri.progress.comet;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.WebApplicationException;
import com.google.common.io.BaseEncoding;

import org.junit.Test;

import java.security.SignatureException;



/**
 * @author dbourasseau
 *
 */
public class LTest {


	@Test
	public void generateUrl() throws MalformedURLException {
		URL xStorageUrl = new URL("http://localhost:8080/api/bla");
		String HMAC_SHA1_ALGORITHM = "HmacSHA1";

		String sharedKey = "azerty";
		String method = "POST";
		long duration_in_seconds = 60 * 60 * 24;
		long expire = duration_in_seconds + (System.currentTimeMillis() / 1000L);
		String path = "/v1/AUTH_a422b2-91f3-2f46-74b7-d7c9e8958f5d30/container/object";
		String hmac_body = method + "\n" + expire + "\n" + path;

		SecretKeySpec keySpec = new SecretKeySpec(sharedKey.getBytes(), HMAC_SHA1_ALGORITHM);

		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(keySpec);
			byte[] rawHmac = mac.doFinal(hmac_body.getBytes());
			String result = BaseEncoding.base16().lowerCase().encode(rawHmac);
			String s = xStorageUrl.getProtocol() + "://" + xStorageUrl.getAuthority() + path + "/?temp_url_sig="
					+ result + "&temp_url_expires=" + expire;
			System.out.println(new URL(s));
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
