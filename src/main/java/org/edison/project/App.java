package org.edison.project;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import javax.swing.JFileChooser;

import org.edison.project.be.ScrapPattern;
import org.edison.project.control.Controller;
/**
 * Hello world!
 *
 */
public class App 
{
	static String absolutePathToSave ="";
	
	 public static void main( String[] args ) throws InvalidPropertiesFormatException, FileNotFoundException, IOException, InterruptedException
	    {	

			
	        System.out.println("Welcome to Web Pattern Scraper");
	        System.out.println("------------------------------");
	        System.out.println("This program look for patterns (listed in Config.xml) in different websites in file: URLList.txt ");
	        System.out.println("To Start please select the folder where you want to save the results and click OK...");
	        Thread.sleep(100);
	        
	    	JFileChooser fileChooser = new JFileChooser();
	    	fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
	    	
	    	//Setting the folder where all the files will be generated
	    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    	int result = fileChooser.showOpenDialog(null);
	    	if (result == JFileChooser.APPROVE_OPTION) {
	    	    File selectedFile = fileChooser.getSelectedFile();
	    	    absolutePathToSave = selectedFile.getAbsolutePath();
	    	    System.out.println("Selected folder: " + selectedFile.getAbsolutePath());
	    	}
	    	else {
	    		System.out.println("No Folder was selected, please run again");
	    		return;
	    	}
	    	
	    	
	        try 
	        {	List<ScrapPattern> patterns = Controller.GetPatterns();
	        
	        	int count = 0;
	        	System.out.println("\nMENU ");
	        	System.out.println("-------");
	        	System.out.println("PATTERNS TO SEARCH:");
	        	for(ScrapPattern p : patterns) 
				{	
		        	count++;
		        	System.out.println(count +"- " + p.getName());
				}
		        
		        System.out.println("\nChoose an option between 1 and " + count + "... ");
		        
		        String input = null;
		        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		        input = bufferedReader.readLine();
		        int n = Integer.parseInt(input);
		        
		        while(n <1 || n >count) {
		        	
		        	System.out.println("Bad entry, please try again choosing an option between 1 and " + count + "... ");
		 	        
		        	input = null;
			        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			        input = bufferedReader.readLine();
			        n = Integer.parseInt(input);
		        }
		        
	        

		    	//Getting the URLs from File: URLList.txt, this could be set dynamically for now it is set in the project root folder
	        	List<String>  urls = Controller.GetURLs();
	        	
	        	final int i =n-1;
	        	
	        	urls.parallelStream().forEach((url) ->  {
	        		String html = Controller.GetHTMLFromURL(url);
	        		
	        		String domain = url.replace('/', '\0').replace("https", "").replace("http", "").replace(":", "").trim();
	        		
	        		System.out.println("Searching Patterns for Domain : " + domain);
	        		
	        		try {
	        			Controller.GeneratePatternFile(domain, patterns.get(i).getName(), patterns.get(i).getRegularExpression(), html, absolutePathToSave  );
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	});
	       	
		
	        	System.out.println("\nShowing Generated Files...");
				Desktop.getDesktop().open(new File(absolutePathToSave));
				
				System.out.println("\nEND OF THE PROCESS");
	        
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
	    }
}
