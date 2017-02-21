package com.sand;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class Test3 {

	/**
	 * 调用Http servlet 可在不同协议间调用用，如在soap协议中调用servlet即可
	 * 
	 * @throws HttpException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void testHttpClient() throws HttpException, IOException,
			URISyntaxException {

		HttpClient client = new HttpClient();
		String url = "http://175.102.15.131/msg/HttpVarSM";
		PostMethod postMethod = new PostMethod(url);
		NameValuePair [] nvps = new NameValuePair[7];
		nvps[0] = new NameValuePair("account","laoyukeji");
		nvps[1] = new NameValuePair("pswd","2mU4@JrJ");
		nvps[2] = new NameValuePair("msg","test:{$var},1,2,3！");
		nvps[3] = new NameValuePair("params","13917022174,188");
		nvps[4] = new NameValuePair("needstatus","true");
		nvps[5] = new NameValuePair("product","");
		nvps[6] = new NameValuePair("extno","317");
		
		postMethod.addParameters(nvps);

		int statusCode = client.executeMethod(postMethod);
		System.out.println("status code:" + statusCode);

		if (statusCode == 200) {
			// response : stream byte[] String and so on
			// InputStream is = postMethod.getResponseBodyAsStream();
			// byte [] bytes = postMethod.getResponseBody();
			String result = postMethod.getResponseBodyAsString();
			System.out.println("result msg " + result);
		}

	}

	public static void main(String[] args) throws Exception {

		Test3.testHttpClient();

	}

}
