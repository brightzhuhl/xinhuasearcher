package coderz.demo.util.httpclient;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public abstract class TransitionHandler implements ResponseHandler<Void> {
	
	protected static Log logger = LogFactory.getLog(TransitionHandler.class);
	
	
	protected CloseableHttpClient client;
	protected HttpClientContext localContext;
	
	
	protected ExecutorService requestExecutorService,handleExecutorService;
	
	protected TransitionHandler(CloseableHttpClient client){
		this.client = client;
	}
	
	@Override
	public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		String html = EntityUtils.toString(response.getEntity(),"utf-8");
		handleExecutorService.execute(()->{
			handle(html);
		});
		return null;
	}

	protected abstract void handle(String res);

	
	public HttpClientContext getLocalContext() {
		return localContext;
	}

	public void setLocalContext(HttpClientContext localContext) {
		this.localContext = localContext;
	}

	public ExecutorService getRequestExecutorService() {
		return requestExecutorService;
	}

	public void setRequestExecutorService(ExecutorService requestExecutorService) {
		this.requestExecutorService = requestExecutorService;
	}

	public ExecutorService getHandleExecutorService() {
		return handleExecutorService;
	}

	public void setHandleExecutorService(ExecutorService handleExecutorService) {
		this.handleExecutorService = handleExecutorService;
	}
	
	
}
