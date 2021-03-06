package com.paypal.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;

import com.paypal.exception.SSLConfigurationException;

/**
 * A special HttpConnection for use on Google App Engine.
 * 
 * In order to activate this feature, set 'http.GoogleAppEngine = true' in the SDK config file.
 * 
 * @author Benjamin Possolo
 */
public class GoogleAppEngineHttpConnection extends HttpConnection {

	@Override
	public void setupClientSSL(String certPath, String certKey, boolean trustAll) throws SSLConfigurationException {
		
		if( certPath != null || certKey != null )
			LoggingManager.warn(GoogleAppEngineHttpConnection.class, "The PayPal SDK cannot be used with client SSL on Google App Engine; configure the SDK to use a PayPal API Signature instead");
		
		if( trustAll )
			LoggingManager.warn(GoogleAppEngineHttpConnection.class, "The PayPal SDK cannot be configured to trust all certificates when used on Google App Engine. The 'http.TrustAllConnection' config property will be ignored");
	}

	@Override
	public void CreateAndconfigureHttpConnection(HttpConfiguration clientConfiguration)
			throws MalformedURLException, UnknownHostException, IOException {
		
		this.config = clientConfiguration;
		
		URL url = new URL(this.config.getEndPointUrl());
		
		//Google App Engine does not support the javax.net.ssl.HttpsURLConnection class.
		//However, one can use use URL.openConnection() with a https:// URL and it will  
		//return an HttpURLConnection that is capable of retrieving HTTPS data.
		//see https://groups.google.com/forum/?fromgroups#!topic/google-appengine-java/ZEskGLwyE_0
		
		//Google App Engine does not require any proxy settings so we can skip  
		//that configuration entirely.
		
		//As of GAE 1.6.5, SSL behaviour can't really be configured, therefore we cannot
		//use SSLUtil to install the custom trust manager that relaxes verification of 
		//server certificates.
		
		//Other Google issues that can be starred to add better support:
		//   http://code.google.com/p/googleappengine/issues/detail?id=1036
		
		this.connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
		
		this.connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		this.connection.setDoInput(true);
		this.connection.setDoOutput(true);
		this.connection.setRequestMethod("POST");
		this.connection.setConnectTimeout(this.config.getConnectionTimeout());
		this.connection.setReadTimeout(this.config.getReadTimeout());
	}

}
