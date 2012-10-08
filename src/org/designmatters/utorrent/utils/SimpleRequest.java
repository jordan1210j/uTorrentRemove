package org.designmatters.utorrent.utils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;

public class SimpleRequest {
	static Logger log = Logger.getLogger(SimpleRequest.class);
	private String user;
	private String password;

	public SimpleRequest(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public String get(String URL, List<Cookie> cookies) throws ClientProtocolException, IOException {
		Authenticator.setDefault(new MyAuthenticator());
		String str = "";
		
		CookieManager manager = new CookieManager();
	    manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
	    CookieHandler.setDefault(manager);
	   
	    try {
	    	log.info("URL for : "+URL);
			URL url = new URL(URL);
			URLConnection connection = url.openConnection();
		    Object obj = connection.getContent();
			connection = url.openConnection();
		    obj = connection.getContent();
			str = obj.toString();
			
			CookieStore cookieJar = manager.getCookieStore();
		    for (HttpCookie cookie: cookieJar.getCookies()) {
		      log.info("Cookie: "+cookie);
		    }
		} catch (MalformedURLException e) {
			log.error(e,e);
		} catch (IOException e) {
			log.error(e,e);
		}
		return str;
	}

	public class MyAuthenticator extends Authenticator {
		protected PasswordAuthentication getPasswordAuthentication() {			
			return new PasswordAuthentication(user, password.toCharArray());
		}
	}
}
