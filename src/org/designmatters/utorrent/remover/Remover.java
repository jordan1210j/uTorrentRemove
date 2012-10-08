package org.designmatters.utorrent.remover;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.designmatters.utorrent.utils.SimpleRequest;
import org.designmatters.utorrent.utils.WebRequest;
import org.jsoup.Jsoup;


public class Remover {
	static Logger log = Logger.getLogger(Remover.class);
	private List<Cookie> _cookies = new ArrayList<Cookie>();
	private String _authToken = "";
	private String url, username, password;
	
	public Remover(String url, String username, String password){
		this.setUrl(url);
		this.setUsername(username);
		this.setPassword(password);
	}
	
	public static void main(String... args) throws IOException {
		configureLogging();
		log.info("Call Args "+Arrays.toString(args));        
        String url = args[0];
        String username = args[1];
        String password = args[2];
        String rowHash = args[3];
        String torrentState = args[4];
        
        if(torrentState.equals("Downloading") || torrentState.equals("Finished") || torrentState.equals("Completed") || torrentState.equals("Seeding")){
        	Remover remover = new Remover(url, username, password);
    		String action = "";
//    		action = "remove";
        	String result = Jsoup.parse(remover.processAction(action,rowHash)).text();
        	log.info("Removing the torrent was a "+result);     
        }
        
        log.info("Process Completed");
    }

	private static void configureLogging() {
		File dir = new File("");
		String path = dir.getAbsolutePath();	
		PropertyConfigurator.configureAndWatch(path+"\\log4j.properties");
		log.info("Log4J Setup Completed");
	}

	public String processAction(String action, String rowHash) throws ClientProtocolException, IOException {
		log.info("removeTorrent("+rowHash+")");
		authenticate();		
		WebRequest localWebRequest = new WebRequest();
		String result = localWebRequest.GetWithBasicAuthorization(url+"/gui/?token=" + this._authToken + "&action="+action+"&hash=" + rowHash, username, password, this._cookies);
		this._cookies = localWebRequest.getClient().getCookieStore().getCookies();
		if(result.trim().equals("")){
			SimpleRequest lSimpleReq = new SimpleRequest(username,password);
			result = lSimpleReq.get(url+"/gui/?token=" + this._authToken + "&action="+action+"&hash=" + rowHash, this._cookies);
		}
		return result;
	}

	public void authenticate() throws ClientProtocolException, IOException{
		log.info("authenticate()");
		WebRequest localWebRequest = new WebRequest();
		String result = localWebRequest.GetWithBasicAuthorization(url+"/gui/token.html", username, password, this._cookies);
		this._cookies = localWebRequest.getClient().getCookieStore().getCookies();
		if(result.trim().equals("")){
			SimpleRequest lSimpleReq = new SimpleRequest(username,password);
			result = lSimpleReq.get(url+"/gui/token.html", this._cookies);
		}
		this._authToken = Jsoup.parse(result).text();
		log.info("Token: "+this._authToken);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
