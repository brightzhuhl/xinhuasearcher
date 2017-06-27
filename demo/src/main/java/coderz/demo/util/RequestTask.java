package coderz.demo.util;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

public class RequestTask implements Runnable{
	
	private static Log logger = LogFactory.getLog(RequestTask.class);
	
	private CloseableHttpClient client;
	private HttpUriRequest req;
	private HttpClientContext context;
	private ResponseHandler<Void> handler;

	
	public  RequestTask(CloseableHttpClient client,HttpUriRequest req,ResponseHandler<Void> handler) {
		this.client = client;
		this.req = req;
		this.handler = handler;
	}	
	
	
	
	@Override
	public void run(){
		try {
			if(context != null){
				client.execute(req, handler, context);
			}else{
				client.execute(req, handler);
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException,"+e.getMessage());
		} catch (IOException e) {
			logger.error("IOException"+e.getMessage());
		}
	}
	
	
	
	public CloseableHttpClient getClient() {
		return client;
	}

	
	
	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}
	
	
	
	public HttpClientContext getContext() {
		return context;
	}

	
	
	public void setContext(HttpClientContext context) {
		this.context = context;
	}
	
}
