package com.hsp.checkfresh.runnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity.Header;
import android.util.Log;

public class GetNet {
	public static GetNet getNet;
	public Handler msgHandler;

	public static Elements children;
	// public static List<Node> nodes;
	public static String[] bands;
	public static Map<String, String> bandsMap;
	public static String productionData;

	public static String brandMsgUri = "http://www.checkfresh.com/ajax.php?a=SwPomoc&szablo=";
	public static String brandDataUri = "http://www.checkfresh.com/ajax.php?a=SwWaznosc&szablon=";

	public GetNet(Handler msg) {
		msgHandler = msg;
	}

	public Runnable GetSelectRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				bandsMap = null;
				bandsMap = new HashMap<String, String>();

				Connection con = Jsoup.connect("http://www.checkfresh.com/");
				con.timeout(30000);
				Document document;
				document = con.get();
				Element select = document.getElementById("frmSzablon");
				// LogMessage.printMsg(document.toString());
				children = select.getElementsByTag("option");

				for (int i = 0; i < children.size(); i++) {
					if (children.get(i).attr("class") == "") {
						bandsMap.put(children.get(i).html(), children.get(i)
								.attr("value"));
					}
				}
				bands = null;
				bands = new String[bandsMap.size()];
				int j = 0;
				for (int i = 0; i < children.size(); i++) {
					if (children.get(i).attr("class") == "") {
						bands[j] = children.get(i).html();
						j++;
					}
				}
				msgHandler.sendEmptyMessage(MsgTag.GET_BANDS_SCS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msgHandler.sendEmptyMessage(MsgTag.IO_ERROR);
			}
		}
	};

	public Runnable GetBrandMsgRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Connection con = Jsoup.connect(brandMsgUri);
				con.timeout(30000);
				Document document;
				document = con.get();
				Elements select = document.getElementsByTag("p");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msgHandler.sendEmptyMessage(MsgTag.IO_ERROR);
			}
		}

	};

	public Runnable GetBrandDataRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
//				HttpClient client=new DefaultHttpClient();
//				HttpGet request=new HttpGet(brandDataUri);
//				Log.d("zjj", "uri:"+brandDataUri);
//				HttpResponse response= client.execute(request);
////				InputStreamReader reader=new InputStreamReader(response.getEntity().getContent());
////					byte[] buffer=new byte[1024];	
////					
////					response.getEntity().getContent().read(buffer);
//				
//					LogMessage.printMsg(EntityUtils.toString(response.getEntity()));
				Connection con = Jsoup.connect(brandDataUri);
				con.timeout(30000);
				Document document;
				document = con.get();
				// Elements select = document.getElementsByTag("p");

				LogMessage.printMsg(document.toString());
				Elements select = document.getElementsByTag("tr");
				LogMessage.printMsg("" + select.size());
				for(Element element:select){
					if(element.child(0).html().equals("Production date")){
						Message msg =new Message();
						Bundle data = new Bundle();
						data.putString("data", element.child(1).html());
						msg.setData(data);
						msg.what=MsgTag.PRODUCT_DATA;
						msgHandler.sendMessage(msg);
						break;
					}
//					Log.d("zjj", "node text:"+element.child(0).html());
				}
				// HttpClient client = new DefaultHttpClient();
				// HttpGet get = new HttpGet(brandDataUri);
				// HttpResponse response = client.execute(get);
				// // 判断是否正常返回
				// if (response.getStatusLine().getStatusCode() ==
				// HttpStatus.SC_OK) {
				// // 解析数据
				// String data = EntityUtils.toString(response.getEntity());
				// LogMessage.printMsg(data);
				// }

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msgHandler.sendEmptyMessage(MsgTag.IO_ERROR);
			}
		}

	};

}
