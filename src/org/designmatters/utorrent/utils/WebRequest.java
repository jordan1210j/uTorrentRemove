package org.designmatters.utorrent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebRequest {
	private static Log log = LogFactory.getLog(WebRequest.class);
	DefaultHttpClient _client;
	HttpGet _get;
	HttpPost _post;
	HttpResponse _response;

	public WebRequest() {
		this._client = new DefaultHttpClient();
	}

	public static String GetResponseText(InputStream paramInputStream) throws IOException {
		BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
		StringBuilder localStringBuilder = new StringBuilder();
		while (true) {
			String str1 = localBufferedReader.readLine();
			if (str1 == null) {
				String str2 = localStringBuilder.toString();
				paramInputStream.close();
				localBufferedReader.close();
				return str2;
			}
			localStringBuilder.append(str1 + "\n");
		}
	}

	public String GetWithBasicAuthorization(String URL, String user, String password, List<Cookie> cookies) throws ClientProtocolException, IOException {
		this._get = new HttpGet(URL);
		//this._get.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(user, password), "UTF-8", false));

		this._get.addHeader("Authorization", "Basic " + Base64.encodeBase64(new StringBuilder(user).append(":").append(password).toString().getBytes()));
		Iterator<Cookie> cookieIter = cookies.iterator();
		while (true) {
			if (!cookieIter.hasNext()) {
				log.info("Get: "+this._get);
				this._response = this._client.execute(this._get);
				log.info("Response: "+this._response);
				return GetResponseText(this._response.getEntity().getContent());
			}
			Cookie localCookie = (Cookie) cookieIter.next();
			this._client.getCookieStore().addCookie(localCookie);
			log.info("Cookie Added: "+localCookie);
		}
	}

	public DefaultHttpClient getClient() {
		return this._client;
	}
}
