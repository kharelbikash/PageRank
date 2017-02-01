//to run this program we need MyURL.java and article2content.txt
package project;

import java.io.Console;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ArticleIndexer {

private static boolean matchServer(String url, String str){
		
		boolean status = false;
		if(url != null && str != null){
			String[] urlSplit = url.split("://");
			urlSplit= urlSplit[1].split("/");
			String server= urlSplit[0];
			status = server.toLowerCase().contains(str.toLowerCase());
		}
		
		return status;
	}
	
	public static Map<String,Integer> scanAndPrint(Scanner in, String str){
		String page = in.useDelimiter(Pattern.compile("\\Z")).next();
		int individualPage = -1;
		int beg = -1;
		Map<String , Integer>URLList = new HashMap<String, Integer>(); 
		int count =0;
		for (int i = 0; i < page.length()-4; i++) {
			// for external URL
			if(i == 0){
				if(page.substring(i, i+4).equalsIgnoreCase("http")){
				String[] split = page.split("\\s");
				//System.out.println(split[0] );
				String parsedUrl =MyURL.parseURL(split[0]);
				if(!parsedUrl.equals(null) && matchServer(parsedUrl,str) ){ /* checking returned value*/
					URLList.put(parsedUrl, count);
					count++;
					//System.out.println(parsedUrl);
				}
				}
			}
			if(i>5){
				if(page.substring(i,i+4).equalsIgnoreCase("http") &&( page.substring(i-5, i).equalsIgnoreCase("</p>\n"))){
					String[] split = page.split("\n");
					String[] splitUrl = split[count].split("\\s");
					String parsedUrl =MyURL.parseURL(splitUrl[0]);
					if(!parsedUrl.equals(null) && matchServer(parsedUrl,str) ){ /* checking returned value*/
						URLList.put(parsedUrl, count);
						count++;
						//System.out.println(parsedUrl);
					}
				}
			}
		}
		
		// we can call UrlExtractor.scanAndPrint with return type hashmap
		// for internal url
		for (int i = 0; i < page.length(); i++) {
			if(page.length() - i >= 8){
					String tag = page.substring(i, i + 8);
					if(tag.equalsIgnoreCase("<a href=")){ // ignore case of tags
							beg=i;
							}
					}
					String closetag = page.substring(i , i + 1);
				if(closetag.equals(">") && beg >= 0){
					String temp = page.substring(beg+9, i);
					String[] split = temp.split(" ");
					String Url = split[0].substring(0,split[0].length()-1);
					String parsedUrl =MyURL.parseURL(Url); 
							if(parsedUrl!=null && matchServer(parsedUrl,str) ){ /* checking returned value*/
								
								if(!URLList.containsKey(parsedUrl)){
								URLList.put(parsedUrl, count);
								count++;
								//System.out.println(parsedUrl);
								}
							}
							else{
								
							}
					beg=-1;
					
					}
				
		}		
		// printing Hash values
		return(URLList);
		//URLList.forEach((key,value) -> System.out.println(key + " "+value));
		


	}

	
	public static void main(String[] args){
		
		try{
			File fileTest = new File("src\\article2content.txt");
			Scanner in =new Scanner(fileTest,"UTF-8");
	
			Map<String,Integer>ArticleIndex =	scanAndPrint(in,"guardian"); // UrlExtractor.scanAndPrint is also fesiable if we make so changes there
			ArticleIndex.forEach((key,value) -> System.out.println(key + " "+value));
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		
	}
}
