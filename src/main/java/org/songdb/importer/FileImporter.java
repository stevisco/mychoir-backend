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
					uploadFile(url,name);
				}
			}
			
		}
		httpclient.close();
	}

	private String[] getSong(String url,String id)  {
		HttpGet p = new HttpGet(url+"/song/"+id);
		System.out.println("GETTING: "+p.getURI());
		try {
			CloseableHttpResponse response = httpclient.execute(p);
			try {
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            String retSrc = EntityUtils.toString(entity); 
		            if (retSrc.length()==0) return new String[] {};
		            JSONObject result = new JSONObject(retSrc); 
		            JSONArray arr = (JSONArray) result.get("attachments");
		            if (arr==null) return new String[] {};
		            String[] res = new String[arr.length()];
		            for (int i =0;i<res.length;i++) res[i]=arr.getString(i);
		            return res;
		        }
		        if (response.getStatusLine().getStatusCode()!=200) {
		        	String error="{ \"object\": \""+entity.toString()+"\", \"message\": \""+response.getStatusLine()+"\"}";
		        	System.out.println("ERROR "+error);
		        	return null;
		        }
		    } finally {
		        response.close();
		    }
		} catch (Exception e) {
			e.printStackTrace();
			String error="{ \"object\": \""+"\", \"exception\": \""+e.getMessage()+"\"}";
			return null;
		} 
		return null;
	}
	
	private int uploadFile(String url,String filename) throws IOException {
		
		System.out.println("Uploading file: "+filename);
		File f = new File("./uploads/"+filename);
		HttpEntity entity = MultipartEntityBuilder
			    .create()
			    .addBinaryBody("upload_file", f, ContentType.create("application/octet-stream"), filename)
			    .addTextBody("filename", filename)
			    .build();

			HttpPost httpPost = new HttpPost(url+"/songattachupload");
			httpPost.setEntity(entity);
			HttpResponse response = httpclient.execute(httpPost);
			System.out.println(response.getStatusLine());
		return response.getStatusLine().getStatusCode();
	}

}
