package org.designmatters.utorrent.remover;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
	
	public static void main(String... args) throws IOException {
		configureLogging();
			
		log.info("Call Args "+Arrays.toString(args));
        String rowHash = args[0];
        String torrentState = args[1];
        if(torrentState.equals("Downloading") || torrentState.equals("Finished") || torrentState.equals("Completed") || torrentState.equals("Seeding")){
        	Remover remover = new Remover();
        	String result = Jsoup.parse(remover.removeTorrent(rowHash)).text();
        	log.info("Removing the torrent was a "+result);     
        }
        
        log.info("Process Completed");
    }

	private static void configureLogging() {
		File dir = new File("");
		String path = dir.getAbsolutePath();	
		//System.out.println("Path: "+path);
		PropertyConfigurator.configureAndWatch(path+"\\log4j.properties");
		log.info("Log4J Setup Completed");
	}

	public String removeTorrent(String rowHash) throws ClientProtocolException, IOException {
		log.info("removeTorrent("+rowHash+")");
		authenticate();
		String action = "";
//		action = "remove";
		
		// WebRequest localWebRequest = new WebRequest();
		// String str2 = localWebRequest.GetWithBasicAuthorization("http://192.168.0.3:8990/gui/?token=" + this._authToken + "&action="+action+"&hash=" + rowHash, "Admin", "J147258j", this._cookies);
		// this._cookies = localWebRequest.getClient().getCookieStore().getCookies();
		
		SimpleRequest lSimpleReq = new SimpleRequest("Admin","J147258j");
		String str2 = lSimpleReq.get("http://Vault:8990/gui/?token=" + this._authToken + "&action="+action+"&hash=" + rowHash, this._cookies);

		return str2;
	}

	public void authenticate() throws ClientProtocolException, IOException{
		log.info("authenticate()");
		WebRequest localWebRequest = new WebRequest();
		String str2 = localWebRequest.GetWithBasicAuthorization("http://vault:8990/gui/token.html", "Admin", "J147258j", this._cookies);
		this._cookies = localWebRequest.getClient().getCookieStore().getCookies();
		//SimpleRequest lSimpleReq = new SimpleRequest("Admin","J147258j");
		//String str2 = lSimpleReq.get("http://VAULT:8990/gui/token.html", this._cookies);
		this._authToken = Jsoup.parse(str2).text();
		log.info("Token: "+this._authToken);
	}

}
