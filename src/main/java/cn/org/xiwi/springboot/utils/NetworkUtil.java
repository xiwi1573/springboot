package cn.org.xiwi.springboot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class NetworkUtil {

	public static String httpPostInvoke(String url,Object obj) throws Exception {
		
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(JSONObject.toJSONString(obj),"UTF-8");
        entity.setContentType("application/json;charset=UTF-8");
        post.setEntity(entity);
        post.setHeader("accept", "application/json");
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        HttpResponse response = httpClient.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        String str=null;
        if(statusCode == 200){
        	 str = EntityUtils.toString(response.getEntity(),"UTF-8");
        }else {
        	throw new Exception("Http接口状态出错("+ response.getStatusLine().getStatusCode() + ")");
        }
        return null != str ? str:null;
    }
	
    public static String httpPostInvoke(String url, Map<String, Object> map) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			String value = null;
			Object obj = null;
			if((obj=map.get(key))!=null){
				value = obj.toString();
			}
			params.add(new BasicNameValuePair(key, value));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(entity);
		try {
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception("Http接口状态出错("
						+ response.getStatusLine().getStatusCode() + ")");
			}
			String str = EntityUtils.toString(response.getEntity());
			return str;
		} finally {
			client.getConnectionManager().shutdown();
		}
    }
    
    public static String httpPostInvoke(String url, String xml) {
		try {
			if (url == null) {
				throw new Exception("请求地址不能为空");
			}
			if (xml == null) {
				throw new Exception("请求内容不能为空");
			}
			String result = "";
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setRequestMethod("POST");
			byte[] contentByte = xml.getBytes();
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setRequestProperty("Content-Length", "" + contentByte.length);
			conn.setRequestProperty("Content-Language", "en-US");
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(contentByte);
			os.flush();
			os.close();
			InputStream is = conn.getInputStream();
			StringBuffer sb = new StringBuffer();
			int i = 0;
			while (i != -1) {
				i = is.read();
				if (i != -1) {
					sb.append((char) i);
				}
			}
			is.close();
			result = new String(sb.toString().getBytes("iso-8859-1"), "UTF-8");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception";
		}
	}
    
//    @SuppressWarnings("finally")
//	public static String httpsPostInvoke(String url,Map<String, Object> map) throws IOException, ServiceException {
//		
//    	HttpClient client = new DefaultHttpClient();
//    	String str = "";
//    	
//        try { 
//        	X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager 
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {} 
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {} 
//                public X509Certificate[] getAcceptedIssuers() { return null; }
//            };
//            SSLContext ctx = SSLContext.getInstance("TLS"); 
//            ctx.init(null, new TrustManager[]{xtm}, null); 
//            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx); 
//            client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory)); 
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>();//构建POST请求的参数
//    		for (String key : map.keySet()) {
//    			String value = null;
//    			Object obj = null;
//    			if((obj = map.get(key)) != null){
//    				value = obj.toString();
//    			}
//    			params.add(new BasicNameValuePair(key, value));
//    		}
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
//    		HttpPost post = new HttpPost(url);//创建HttpPost 
//    		post.setEntity(entity);
//            
//             
//    		HttpResponse response = client.execute(post);
//			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//				throw new ServiceException("Http接口状态出错("
//						+ response.getStatusLine().getStatusCode() + ")");
//			}
//			str = EntityUtils.toString(response.getEntity());
//        } catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (KeyManagementException e) {
//			e.printStackTrace();
//		} finally { 
//        	client.getConnectionManager().shutdown(); 
//        	return str;
//        } 
//    } 
    public static void main(String[] args) throws CertificateException, IOException{
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	InputStream caInput = NetworkUtil.class.getResourceAsStream("/HKHaiYangGongYuan.cer");
    	Certificate ca;
    	try {
        	ca = cf.generateCertificate(caInput);
        	System.out.println("getEncoded()-->"+ca.getEncoded());
        	System.out.println("getSubjectDN()-->" + ((X509Certificate)ca).getSubjectDN());
        	System.out.println("getPublicKey()-->"+((X509Certificate)ca).getPublicKey());
    	} finally {
    		caInput.close();
    	}
    }
    
    
    public static String httpsPostInvoke(String url,Map<String, Object> map,String caName) throws Exception {
    	InputStream inputStream = null;
    	HttpClient httpClient = new DefaultHttpClient();
    	String result = "";
    	
    	try{
    		//从 inputStream 加载 CA 证书
    		inputStream = NetworkUtil.class.getResourceAsStream("/"+caName);
    		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    		Certificate certificate = certificateFactory.generateCertificate(inputStream);
    		
    		//构造含有信任 CA 证书的 KeyStore
        	KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        	trustStore.load(null, null);
        	trustStore.setCertificateEntry("ca", certificate);
        	
        	SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
        	
            List<NameValuePair> params = new ArrayList<NameValuePair>();//构建POST请求的参数
    		for (String key : map.keySet()) {
    			String value = null;
    			Object obj = null;
    			if((obj = map.get(key)) != null){
    				value = obj.toString();
    			}
    			params.add(new BasicNameValuePair(key, value));
    		}
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
    		HttpPost post = new HttpPost(url);
    		post.setEntity(entity);
    		
    		HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception("Http接口状态出错("
						+ response.getStatusLine().getStatusCode() + ")");
			}
			result = EntityUtils.toString(response.getEntity());
    	} catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} finally {
    		
    		if(null != inputStream){
    			inputStream.close();
    		}
    		
    		httpClient.getConnectionManager().shutdown(); 
    	}
    	
    	return result;
    	
    }
    
}
