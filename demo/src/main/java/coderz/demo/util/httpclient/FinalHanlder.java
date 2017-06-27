package coderz.demo.util.httpclient;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;

public abstract class FinalHanlder implements ResponseHandler<Void> {
	
	protected static Log logger = LogFactory.getLog(FinalHanlder.class);
	
	protected HttpClientContext context;
	protected ExecutorService service;
	
	protected FinalHanlder(HttpClientContext context,ExecutorService service){
		this.context = context;
		this.service = service;
	}
	
	@Override
	public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		String html = EntityUtils.toString(response.getEntity(),"utf-8");
		service.execute(()->{
			handle(html);
		});
		return null;
	}
	
	
	protected abstract void handle(String res);
}
