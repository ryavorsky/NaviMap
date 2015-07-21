package com.navimap;

import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

import java.io.IOException;
import java.net.HttpURLConnection;

public final class MyUrlConnectionClient extends UrlConnectionClient {
	@Override
	protected HttpURLConnection openConnection(Request request) {
		HttpURLConnection connection;
		try {
			connection = super.openConnection(request);
			connection.setConnectTimeout(60 * 1000);
			connection.setReadTimeout(60 * 1000);
			return connection;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
