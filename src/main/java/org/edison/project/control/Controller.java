package org.edison.project.control;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edison.project.be.ScrapPattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Controller {

	static String USER_AGENT = "Mozilla/5.0";
   
    // This method gets all the patterns listed in config.xml
    public static List<ScrapPattern> GetPatterns()  throws InvalidPropertiesFormatException, FileNotFoundException, IOException
    {
    	String rootPath   = Thread.currentThread().getContextClassLoader().getResource("").getPath();

		Path path = Paths.get(rootPath.substring(1)).getParent().getParent();
		
		String configPath = path.toString() + "\\config.xml";

		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(configPath));
		
		
		List<ScrapPattern> patterns = new ArrayList<ScrapPattern>();
        
		for (Map.Entry<Object, Object> e :props.entrySet()) {
	        
	        String option = ((String)e.getKey());
	        String regExpression = ((String)e.getValue()).replace("%3C", "<").replace("%3E", ">");
	        
	        ScrapPattern temp = new ScrapPattern();
	        
	        temp.setName(option);
	        temp.setRegularExpression(regExpression);
	        
	        patterns.add(temp);
	    }
		
		return patterns;
    	
    }
    
    // This method scraps a URL and get the content using Jsoup
    public static String GetHTMLFromURL(String url) {
    	
    	StringBuffer response = new StringBuffer();
    	Document doc ;
    	try 
        {
	    	System.out.println(url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
	
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			 doc = Jsoup.parse(response.toString());
			 return doc.body().text(); 
			 //return response.toString();
			
        }
		catch(IOException e){
			e.printStackTrace();
		}
    	
    	return "";
    	
    }
    
    // This Method gets the URLs listed in URLList.txt
    public static List<String> GetURLs(){
    	BufferedReader reader;
    	List<String> urls = new ArrayList<String>();
    	
    	String rootPath   = Thread.currentThread().getContextClassLoader().getResource("").getPath();

		Path path = Paths.get(rootPath.substring(1)).getParent().getParent();
		
		String fileListPath = path.toString() + "\\URLList.txt";
		
        try 
        {	reader = new BufferedReader(new FileReader(fileListPath));
			String url = reader.readLine();
			while(url !=null) 
			{	
				urls.add(url);
				url = reader.readLine();
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
        return urls;
   }
    
    
    // This method generates the files with the found Patterns, if no patterns were found the file wont be generated
    public static void GeneratePatternFile(String domainName, String patternName, String regExpression, String response, String filePath) throws FileNotFoundException, UnsupportedEncodingException
    {
    	try 
    	{
	    	System.out.println(regExpression);
	    	
			Pattern pattern  = Pattern.compile(regExpression); 
			   
			Matcher m = pattern.matcher(response); 
			
			Set<String>  keyWords = new HashSet<String>();
			
			String fileName =filePath +"// "+  domainName + "_" + patternName + ".txt";
			
			int count=0;
			List<String> patterns = new ArrayList<String>();
	        while (m.find()) {
				
				String key = response.substring( m.start() , m.end());
				
				if(!keyWords.contains(key)) {
					keyWords.add(key);
					patterns.add(key);
			       //System.out.println("Pattern: " + key);
			        count++;
				}
				
			}

			if(count >0) {
				PrintWriter writer = new PrintWriter(fileName, "UTF-8");

				for(String p :patterns) {
					writer.println(p );
				}
				writer.close();
				System.out.println("\n" + count + " PATTERNS  "+ "(" + patternName +")" + " were found for "+ domainName);
				
			}
			else
				System.out.println("\n NO PATTERNS  "+ "(" + patternName +")" + " were found for "+ domainName);
			
			//ProcessBuilder pb = new ProcessBuilder("Notepad.exe",fileName);
			//pb.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	
}
