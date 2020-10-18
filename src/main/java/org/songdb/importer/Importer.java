package org.songdb.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Importer {
	
	private List<String> errorList;
	private Map<Integer,String> fieldMap;
	private CloseableHttpClient httpclient;
	private int totalcount;
	private FileWriter logfw;
	
	public static final void main(String[] args) {
		
		Importer i = new Importer();
		String filename="./attachments/listaCanti_2018_2.txt";
		
		String hostname="localhost";
		if (args.length==2) {
			hostname=args[0];
			filename=args[1];
		}
		try {
			i.loginfo("IMPORT STARTING");
			i.importFromTabFile("http://"+hostname+":8080",filename);
			i.closelog();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closelog() {
		if (logfw!=null)
			try {
				logfw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private void logerr(String line) {
		log("ERROR",line);
	}
	private void loginfo(String line)  {
		log("INFO",line);
	}

	private void log (String type,String line)   {
		System.out.println("["+type+"] "+line+"\n");
		try {
			if (logfw==null){
				logfw = new FileWriter(new File("importer.log"));
			}
			logfw.write("["+type+"] "+line+"\n");
		} catch (Exception e) { e.printStackTrace(); }
	}

	
	public void importFromTabFile(String url,String filename) throws IOException{
		
		errorList=new ArrayList<String>();
		httpclient = HttpClients.createDefault();
		
		fieldMap = new HashMap<Integer,String>();
		LineNumberReader fr = new LineNumberReader(
				new InputStreamReader(
                new FileInputStream(filename),"UTF-8"));
		String line = null;
		StringBuffer sb = null;
		int lineno = 0;
		int colno = 0;
		totalcount=-1; //first row is header so not counting
		Set<String> tags = null;
		while ((line=fr.readLine())!=null){
			totalcount++;
			StringTokenizer st = new StringTokenizer(line,"\t",true);
			boolean hasContent = false;
			sb = new StringBuffer();
			sb.append("{\n");
			boolean lastTokenWasSep=false;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (lineno==0) {
					if (!token.equals("\t")) {
						colno++;
						mapField(colno,token);
					}
				}
				else {
					colno++;
					if (token.equals("\t")) {
						lastTokenWasSep=true;
					}
					else {
						if (lastTokenWasSep) colno--;
						lastTokenWasSep=false;
						//System.out.println(colno+"=>"+token);
						if (fieldMap.get(colno)!=null) {
							if (fieldMap.get(colno).startsWith("tags")) {
								if (tags==null) tags=new HashSet<String>();
								tags.add(token);
							}
							else {
								if (hasContent) sb.append(",\n");
								sb.append("\""+fieldMap.get(colno)+"\": \"");
								String clean = token.replaceAll("\"", "");
								sb.append(clean);
								sb.append("\"");
								hasContent = true;
							}
						}
					}
				}
			}
			//finalize tag array
			if (tags!=null) {
				if (hasContent) sb.append(",\n");
				sb.append("\"tags\": [ ");
				boolean first = true;
				for (Iterator<String> it = tags.iterator();it.hasNext();) {
					if (!first) sb.append(",");
					first = false;
					String coll=it.next().replaceAll("\"", "").replaceAll(" ","");
					String[] list = coll.split(",");
					for (int i = 0;i<list.length;i++) {
						if (i!=0) sb.append(",");
						if (!list[i].startsWith("\"")); sb.append("\"");
						sb.append(convertTag(list[i]).toUpperCase());
						if (!list[i].startsWith("\"")); sb.append("\"");
					}
				}
				sb.append(" ]");
				tags = null;
				hasContent = true;
			}
			sb.append("\n}");
			if (hasContent) {
				System.out.println(sb);
				postData(url,sb);
			}
			lineno++;
			//if (lineno==10) System.exit(0);
			colno=0;
		}
		httpclient.close();
		fr.close();
		loginfo("TOTAL FOUND:" +totalcount+" - ERRORS: "+errorList.size());
		for (Iterator<String> it=errorList.iterator();it.hasNext();) logerr(it.next());
	}
	
	private String convertTag(String input) {
		if ("AVV".equalsIgnoreCase(input)) return "Avvento";
		if ("ORD".equalsIgnoreCase(input)) return "TempoOrdinario";
		if ("OM".equalsIgnoreCase(input)) return "OrdinarioMessa";
		if ("QUA".equalsIgnoreCase(input)) return "Quaresima";
		if ("EUC".equalsIgnoreCase(input)) return "Eucaristico";
		if ("PAS".equalsIgnoreCase(input)) return "Pasqua";
		if ("INGR".equalsIgnoreCase(input)) return "Ingresso";
		if ("ING".equalsIgnoreCase(input)) return "Ingresso";
		if ("NAT".equalsIgnoreCase(input)) return "Natale";
		if ("BAT".equalsIgnoreCase(input)) return "Battesimo";
		if ("OFF".equalsIgnoreCase(input)) return "Offertorio";
		if ("ESE".equalsIgnoreCase(input)) return "Esequie";
		if ("MAR".equalsIgnoreCase(input)) return "Maria";
		if ("MAT".equalsIgnoreCase(input)) return "Matrimonio";
		if ("FIN".equalsIgnoreCase(input)) return "Finale";
		if ("SPI".equalsIgnoreCase(input)) return "SpiritoSanto";
		if ("SAL".equalsIgnoreCase(input)) return "Salmo";
		if ("SAN".equalsIgnoreCase(input)) return "Santi";
		if ("ASP".equalsIgnoreCase(input)) return "Aspersione";
		if ("COM".equalsIgnoreCase(input)) return "Comunione";
		if ("RIP".equalsIgnoreCase(input)) return "Incontri";
		if ("LIT".equalsIgnoreCase(input)) return "Liturgia";
		return input;
	}

	private void postData(String url,StringBuffer sb)  {
		int retry = 3;
		boolean success=false;
		String error = null;
		while (!success && retry>0){
			HttpPost p = new HttpPost(url+"/songs");
			p.setHeader("Content-Type","application/json;charset=UTF-8");
			try {
				p.setEntity(new StringEntity(sb.toString(),"UTF-8"));
				CloseableHttpResponse response = httpclient.execute(p);
				try {
					HttpEntity entity = response.getEntity();
					loginfo(entity.toString());
					if (response.getStatusLine().getStatusCode()!=200) {
						error="{ \"object\": \""+sb.toString()+"\", \"message\": \""+response.getStatusLine()+"\"}";	
						logerr(error);
						retry--;
						loginfo("FAILURE -- RETRY counter=="+retry);
					}
					else {
						success=true;
						loginfo("SUCCESS!");
					}
				} finally {
					response.close();
				}
			} catch (Exception e) {
				logerr(Arrays.toString(e.getStackTrace()));
				error="{ \"object\": \""+sb.toString()+"\", \"exception\": \""+e.getMessage()+"\"}";
				logerr(error);
			} 
		}
		if (!success && error!=null){
			errorList.add(error);
			logerr("FATAL FAILURE after retries for song "+sb.toString());
		}
	}
	
	private void mapField(int colno,String token) {
		System.out.println(token);
		if ("CG".equalsIgnoreCase(token)) fieldMap.put(colno, "id");
		if ("titolo".equalsIgnoreCase(token)) fieldMap.put(colno, "title");
		if ("musica".equalsIgnoreCase(token)) fieldMap.put(colno, "author");
		if ("stile".equalsIgnoreCase(token)) fieldMap.put(colno, "genre");
		if ("testo".equalsIgnoreCase(token)) fieldMap.put(colno, "authorText");
		if ("rn".equalsIgnoreCase(token)) fieldMap.put(colno, "ref1");
		if ("cdp".equalsIgnoreCase(token)) fieldMap.put(colno, "ref2");
		if ("tempo".equalsIgnoreCase(token)) fieldMap.put(colno, "tags");
		if ("collocaz".equalsIgnoreCase(token)) fieldMap.put(colno, "tags2");
		if ("raccolta".equalsIgnoreCase(token)) fieldMap.put(colno, "album");
		if ("annopub".equalsIgnoreCase(token)) fieldMap.put(colno, "yearpublished");
		if ("editore".equalsIgnoreCase(token)) fieldMap.put(colno, "published");
		
		if (fieldMap.get(colno)!=null) System.out.println("MAP: "+colno+"=>"+fieldMap.get(colno));
	}
}
