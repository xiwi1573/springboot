package cn.org.xiwi.springboot.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.org.xiwi.springboot.msg.BankCardListMsg;
import cn.org.xiwi.springboot.msg.BankCardValidateInfoMsg;
import cn.org.xiwi.springboot.pay.bank.AliBankCardValidatedInfo;
import cn.org.xiwi.springboot.utils.JsonUtils.ToolType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSource;
import okio.Okio;

public class OkHttpUtils {
	private volatile static OkHttpUtils mInstance;

	private OkHttpClient mOkHttpClient;

	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

	private OkHttpUtils() {
		super();
		Builder clientBuilder = new OkHttpClient().newBuilder();
		clientBuilder.readTimeout(30, TimeUnit.SECONDS);
		clientBuilder.connectTimeout(15, TimeUnit.SECONDS);
		clientBuilder.writeTimeout(60, TimeUnit.SECONDS);
		mOkHttpClient = clientBuilder.build();
	}

	public static OkHttpUtils getInstance() {
		OkHttpUtils temp = mInstance;
		if (temp == null) {
			synchronized (OkHttpUtils.class) {
				temp = mInstance;
				if (temp == null) {
					temp = new OkHttpUtils();
					mInstance = temp;
				}
			}
		}
		return temp;
	}

	/**
	 * 设置请求头
	 * 
	 * @param headersParams
	 * @return
	 */
	private Headers setHeaders(Map<String, String> headersParams) {
		Headers headers = null;
		Headers.Builder headersbuilder = new Headers.Builder();
		if (headersParams != null) {
			Iterator<String> iterator = headersParams.keySet().iterator();
			String key = "";
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				headersbuilder.add(key, headersParams.get(key));
			}
		}
		headers = headersbuilder.build();
		return headers;
	}

	/**
	 * post请求参数
	 * 
	 * @param BodyParams
	 * @return
	 */
	private RequestBody setPostRequestBody(Map<String, String> BodyParams) {
		RequestBody body = null;
		FormBody.Builder formEncodingBuilder = new FormBody.Builder();
		if (BodyParams != null) {
			Iterator<String> iterator = BodyParams.keySet().iterator();
			String key = "";
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				formEncodingBuilder.add(key, BodyParams.get(key));
			}
		}
		body = formEncodingBuilder.build();
		return body;
	}

	/**
	 * Post上传图片的参数
	 * 
	 * @param BodyParams
	 * @param fileParams
	 * @return
	 */
	private RequestBody setFileRequestBody(Map<String, String> BodyParams, Map<String, String> filePathParams) {
		// 带文件的Post参数
		RequestBody body = null;
		MultipartBody.Builder MultipartBodyBuilder = new MultipartBody.Builder();
		MultipartBodyBuilder.setType(MultipartBody.FORM);
		RequestBody fileBody = null;
		if (BodyParams != null) {
			Iterator<String> iterator = BodyParams.keySet().iterator();
			String key = "";
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				MultipartBodyBuilder.addFormDataPart(key, BodyParams.get(key));
			}
		}
		if (filePathParams != null) {
			Iterator<String> iterator = filePathParams.keySet().iterator();
			String key = "";
			int i = 0;
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				i++;
				MultipartBodyBuilder.addFormDataPart(key, filePathParams.get(key));
				fileBody = RequestBody.create(MEDIA_TYPE_PNG, new File(filePathParams.get(key)));
				MultipartBodyBuilder.addFormDataPart(key, i + ".png", fileBody);
			}
		}
		body = MultipartBodyBuilder.build();
		return body;
	}

	/**
	 * get方法连接拼加参数
	 * 
	 * @param mapParams
	 * @return
	 */
	private String setGetUrlParams(Map<String, String> mapParams) {
		String strParams = "";
		if (mapParams != null) {
			Iterator<String> iterator = mapParams.keySet().iterator();
			String key = "";
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				strParams += "&" + key + "=" + mapParams.get(key);
			}
		}
		return strParams;
	}

	/**
	 * 实现post请求
	 * 
	 * @param reqUrl
	 * @param headersParams
	 * @param params
	 * @param callback
	 */
	public void doPost(String reqUrl, Map<String, String> headersParams, Map<String, String> params,
			final NetCallback callback) {
		Request.Builder RequestBuilder = new Request.Builder();
		RequestBuilder.url(reqUrl);// 添加URL地址
		RequestBuilder.method("POST", setPostRequestBody(params));
		RequestBuilder.headers(setHeaders(headersParams));// 添加请求头
		Request request = RequestBuilder.build();
		mOkHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call call, final Response response) throws IOException {
				BufferedSource bufferedSource = Okio.buffer(response.body().source());
				String tempStr = bufferedSource.readUtf8();
				bufferedSource.close();
				callback.onSuccess(0, tempStr);
				call.cancel();
			}

			@Override
			public void onFailure(final Call call, final IOException exception) {
				callback.onFailure(-1, exception.getMessage());
				call.cancel();
			}
		});
	}

	/**
	 * 实现get请求
	 * 
	 * @param reqUrl
	 * @param headersParams
	 * @param params
	 * @param callback
	 */
	public void doGet(String reqUrl, Map<String, String> headersParams, Map<String, String> params,
			final NetCallback callback) {
		Request.Builder RequestBuilder = new Request.Builder();
		RequestBuilder.url(reqUrl + setGetUrlParams(params));// 添加URL地址 自行加 ?
		RequestBuilder.headers(setHeaders(headersParams));// 添加请求头
		Request request = RequestBuilder.build();
		mOkHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call call, final Response response) throws IOException {
				BufferedSource bufferedSource = Okio.buffer(response.body().source());
				String tempStr = bufferedSource.readUtf8();
				bufferedSource.close();
				callback.onSuccess(0, tempStr);
				call.cancel();
			}

			@Override
			public void onFailure(final Call call, final IOException exception) {
				callback.onFailure(-1, exception.getMessage());
				call.cancel();
			}
		});
	}

	public static interface NetCallback {
		public void onFailure(int code, String msg);

		public void onSuccess(int code, String content);
	}

	public static void main(String[] args) {
		OkHttpUtils httpUtils = OkHttpUtils.getInstance();

		for (int i = 0; i < 100000; i++) {
////			new Thread(){
////				public void run() {
//					final MNetCallback<BankCardListMsg> callback = new MNetCallback<BankCardListMsg>(BankCardListMsg.class){
//
//						@Override
//						public void onFailure(BankCardListMsg error) {
//							System.out.println(error);
//						}
//
//						@Override
//						public void onSuccess(BankCardListMsg resp) {
//							System.out.println(resp);
//						}};
//					httpUtils.doGet(
////							"http://10.10.176.100:8080/bankCardValidate?cardNum=6228480402564890018",//BankCardValidateInfoMsg
//							"http://10.10.176.100:8080/bankCardList",//BankCardListMsg
//							null, null, callback);
////				};
////			}.start();
			
//			new Thread(){
//			public void run() {
				final MNetCallback<BankCardValidateInfoMsg> callback = new MNetCallback<BankCardValidateInfoMsg>(BankCardValidateInfoMsg.class){

					@Override
					public void onFailure(BankCardValidateInfoMsg error) {
						System.out.println(error);
					}

					@Override
					public void onSuccess(BankCardValidateInfoMsg resp) {
						System.out.println(resp);
					}};
				httpUtils.doGet(
						"http://127.0.0.1:8080/bankCardValidate?cardNum=6228480402564890018",//BankCardValidateInfoMsg
//						"http://10.10.176.100:8080/bankCardList",//BankCardListMsg  10.10.176.100
						null, null, callback);
//			};
//		}.start();
		}
	}

	//http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100
	//http://flash.weather.com.cn/wmaps/xml/china.xml
	//http://mobile.weather.com.cn/js/citylist.xml
	
	public static abstract class MNetCallback<T> implements NetCallback {

		private Class<T> type;
		
		public MNetCallback(Class<T> type){
			this.type = type;
		}
		
		@Override
		public void onFailure(int code, String msg) {
			try {
				onFailure(JsonUtils.fromJson(ToolType.FASTJSON, msg, type));
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof com.alibaba.fastjson.JSONException) {
					msg = "{code:-1,msg:\"数据解析异常\"}";
				}else {
					msg = "{code:-1,msg:\"数据解析异常\"}";
				}
				try {
					onFailure(JsonUtils.fromJson(ToolType.FASTJSON, msg, type));
				} catch (Exception e1) {
					
				}
			}
		}

		@Override
		public void onSuccess(int code, String content) {
			try {
				onSuccess(JsonUtils.fromJson(ToolType.FASTJSON, content, type));
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof com.alibaba.fastjson.JSONException) {
					content = "{code:-1,msg:\"数据解析异常\"}";
				}else {
					content = "{code:-1,msg:\"数据解析异常\"}";
				}
				try {
					onFailure(JsonUtils.fromJson(ToolType.FASTJSON, content, type));
				} catch (Exception e1) {
				
				}
			}
		}

		public abstract void onFailure(T error);
		public abstract void onSuccess(T resp);
	}
}
