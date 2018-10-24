package org.songdb.importer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import ch.qos.logback.core.net.server.Client;

public class FileImporter {

	
	private CloseableHttpClient httpclient;
	
	public static void main(String[] args) {
		
		String dir = "./uploads";
		String hostname = "localhost";
		if (args.length==2) {
			hostname=args[0];
			dir=args[1];
		}
		FileImporter i = new FileImporter();
		try {
			i.uploadAll(dir,"http://"+hostname+":8080");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void uploadAll(String dir,String url) throws IOException {
		httpclient = HttpClients.createDefault();		
		File folder = new File(dir);
		File[] list = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
            	try {
					String name = pathname.getCanonicalFile().getName();
					if (name.matches("CG[0-9][0-9][0-9]_.*")||name.matches("ECG[0-9][0-9][0-9]_.*")) return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
                return false;
            }
        });
		for (int i =0;i<list.length;i++) {
			String name = list[i].getName();
			System.out.println("VERIFYING: "+name);
			String id = name.substring(0,name.indexOf("_"));
			String[] attachments = getSong(url,id);
			boolean found = false;
			if (attachments!=null) {
				for (int k=0;k<attachments.length;k++) if (name.equalsIgnoreCase(attachments[k])) found = true;
				if (!found) {
					//post it!
					uploadFile(dir,url,name);
				}
			}
			
		}
		httpclient.close();
	}

	private String[] getSong(String url,String id)  {
		HttpGet p = new HttpGet(url+"/song/"+id);
		System.out.println("GETTING: "+p.getURI());
		String[] res = null;
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(p);
			 
	        HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            String retSrc = EntityUtils.toString(entity); 
	            if (retSrc.length()==0) return new String[] {};
	            JSONObject result = new JSONObject(retSrc); 
	            JSONArray arr = null;
	            try {
	            	arr = (JSONArray) result.get("attachments");
	            } catch (Exception e) { }
	            if (arr==null) return new String[] {};
	            res = new String[arr.length()];
	            for (int i =0;i<res.length;i++) res[i]=arr.getString(i);
	        }
	        else if (response.getStatusLine().getStatusCode()!=200) {
	        	String error="{ \"object\": \""+entity.toString()+"\", \"message\": \""+response.getStatusLine()+"\"}";
	        	System.out.println("ERROR "+error);
	        	res = null;
	        }
		  
		} catch (Exception e) {
			e.printStackTrace();
			String error="{ \"object\": \""+"\", \"exception\": \""+e.getMessage()+"\"}";
			return null;
		} finally {
	        if (response!=null)
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    }
		return res;
	}
	
	private int uploadFile(String dir,String url,String filename) throws IOException {
		
		System.out.println("Uploading file: "+filename);
		File f = new File(dir+"/"+filename);
		HttpEntity entity = MultipartEntityBuilder
			    .create()
			    .addBinaryBody("upload_file", f, ContentType.create("application/octet-stream"), filename)
			    .addTextBody("filename", filename)
			    .build();

		HttpPost httpPost = new HttpPost(url+"/songattachupload");
		httpPost.setEntity(entity);
		CloseableHttpResponse response = httpclient.execute(httpPost);
		System.out.println(response.getStatusLine());
		int code = response.getStatusLine().getStatusCode();
		response.close();
		return code;
	}

}
