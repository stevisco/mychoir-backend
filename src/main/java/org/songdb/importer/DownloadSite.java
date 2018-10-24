package org.songdb.importer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DownloadSite {

	private CloseableHttpClient httpclient;
	
	public void download() {
		httpclient = HttpClients.createDefault();
		Pattern p = Pattern.compile(".*href=.*/Spartiti/([^\\\"]*.pdf)\".*");
		Pattern p2 = Pattern.compile(".*href=.*/Mp3/([^\\\"]*.zip)\".*");
		Pattern p3 = Pattern.compile(".*href=.*/Accordi/([^\\\"]*.pdf)\".*");
		try {
			LineNumberReader fr = new LineNumberReader(
				new InputStreamReader(
                new FileInputStream("./Libretto dei canti.html"),"UTF-8"));
			String line = null;
			while ((line=fr.readLine())!=null){
				Matcher m = p.matcher(line);
				if (m.matches()) {
					System.out.println(m.group(1));
					/*String url="http://www.parrocchiaspiritosanto.it/Audio/LibrettoCanti/Spartiti/";
					HttpGet r = new HttpGet(url+m.group(1));
					System.out.println("GETTING: "+r.getURI());
					CloseableHttpResponse response = httpclient.execute(r);
					
			        HttpEntity entity = response.getEntity();
			        if (entity != null) {
			        	System.out.println(entity.getContentType());
			        	try (FileOutputStream outstream = new FileOutputStream("./sitedownload/"+m.group(1))) {
			                entity.writeTo(outstream);
			            }
			        }
				    response.close();    */
				}
				Matcher m2 = p2.matcher(line);
				if (m2.matches()) {
					System.out.println(m2.group(1));
					/*String url="http://www.parrocchiaspiritosanto.it/Audio/LibrettoCanti/Mp3/";
					HttpGet r = new HttpGet(url+m2.group(1));
					System.out.println("GETTING: "+r.getURI());
					CloseableHttpResponse response = httpclient.execute(r);
					
			        HttpEntity entity = response.getEntity();
			        if (entity != null) {
			        	System.out.println(entity.getContentType());
			        	try (FileOutputStream outstream = new FileOutputStream("./sitedownload/"+m2.group(1))) {
			                entity.writeTo(outstream);
			            }
			        }
				    response.close();   */ 
				}
				//141-QualeGioia.pdf
				//http://www.parrocchiaspiritosanto.it/Audio/LibrettoCanti/Mp3/
				//http://www.parrocchiaspiritosanto.it/Audio/LibrettoCanti/Accordi/
				Matcher m3 = p3.matcher(line);
				if (m3.matches()) {
					System.out.println(m3.group(1));
					String url="http://www.parrocchiaspiritosanto.it/Audio/LibrettoCanti/Accordi/";
					HttpGet r = new HttpGet(url+m3.group(1));
					System.out.println("GETTING: "+r.getURI());
					CloseableHttpResponse response = httpclient.execute(r);
					
			        HttpEntity entity = response.getEntity();
			        if (entity != null) {
			        	System.out.println(entity.getContentType());
			        	try (FileOutputStream outstream = new FileOutputStream("./sitedownload/"+m3.group(1))) {
			                entity.writeTo(outstream);
			            }
			        }
				    response.close();    
				}
			}
			fr.close();
			httpclient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)  {
		DownloadSite ds = new DownloadSite();
		ds.download();
	}

}
