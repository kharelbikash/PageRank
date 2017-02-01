//to run this program we need MyURL.java and scrape.txt
//Q3 project
package project;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.regex.Pattern;

public class PageRank {
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
	
public static void scanAndPrint(Scanner in, String str){
		String page = in.useDelimiter(Pattern.compile("\\Z")).next();
		int individualPage = -1;
		int beg = -1;
		Map<String , Integer>URLList = new HashMap<String, Integer>(); 
		Map<Integer , String>URLtoSequence = new HashMap<Integer, String>(); 
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
		//System.out.println("going for internal pages");
		// we can call UrlExtractor.scanAndPrint with return type hashmap
		// for internal url
		for (int i = 0; i < page.length(); i++) {
			if(page.length() - i >= 9){
					String tag = page.substring(i, i + 8);
					if(tag.equalsIgnoreCase("<a href=")){ // ignore case of tags
							beg=i;
							}
					}
				String closetag = page.substring(i , i + 1);
			
				//http:">“ther
				if(closetag.equals(">") && beg >= 0){
					String temp= page.substring(beg+9, i);
					String[] split = temp.split(" ");
					String Url="";
					try{
						if(!(split[0].length()<1))
					 Url = split[0].substring(0,split[0].length()-1);
						//System.out.println("here i am ");
					}
					catch(Exception e){
						System.out.println("here");
					}
					String parsedUrl="";
					if(!(Url.equals("http:")||Url.equals("http://")||Url.equals("http:\">“ther")||Url.equals("http://\">“ther")))
					{
					 parsedUrl = MyURL.parseURL(Url); 
					//System.out.println(parsedUrl);
					
					try{
							if((parsedUrl != null) && matchServer(parsedUrl,str) ){ /* checking returned value*/
								
								//System.out.println("parsedUrl: "+parsedUrl);
								if(!URLList.containsKey(parsedUrl)){
								URLList.put(parsedUrl, count);
								count++;
								}
							
							}
					}catch(Exception e){
						System.out.println("errorUrl: "+parsedUrl);
						System.out.println(e);
					}
					beg=-1;
					}
				}
				
		}		
		//System.out.println("clear exit");
		// printing Hash values
		//URLList.forEach((key,value) -> System.out.println(key + " "+value));
		URLList.forEach((key,value) -> URLtoSequence.put(value, key));
		//Creating Matrix
		int arraySize = URLList.size();
		System.out.println("arraysize: "+arraySize);
		int[][] AdjacencyMatrix = new int[arraySize][arraySize];
		int[] outDegree = new int[arraySize];
		String[] split= page.split("\n");
		int NumberOfMainPage = split.length;
		//System.out.println(NumberOfMainPage);
		for(int j=0; j<NumberOfMainPage;j++){
			
				String[] splitUrl= split[j].split("\\s");
				String mainPageUrl = splitUrl[0];
				if(URLList.containsKey(mainPageUrl)){
					 int hashValue = URLList.get(mainPageUrl);
					 int outDegreeTemp=0;
					 for(int k=0;k<arraySize;k++){
						 String testUrl = URLtoSequence.get(k);
						int numberOfOccurance=0;
						
						 if(testUrl != null && split[j].substring(mainPageUrl.length()).contains(testUrl)){
							 //System.out.println(testUrl);
							  numberOfOccurance= split[j].substring(mainPageUrl.length()).split(testUrl).length-1;
							 outDegreeTemp=outDegreeTemp+numberOfOccurance;
						 }
						 AdjacencyMatrix[hashValue][k]=numberOfOccurance;
						 
					 }
					
					 outDegree[hashValue]=outDegreeTemp;
					
			}
				
				
			
		}
		//printing AdjacencyMatrix
	//	for(int i =0; i< arraySize;i++){
		//	for(int j=0;j<arraySize;j++){
			//	System.out.print(AdjacencyMatrix[i][j]);
			//}
			//System.out.println("");
	//	}
		//calculating transition matrix
		Double[][] A= new Double[arraySize][arraySize];
		A=transitionMatrix(AdjacencyMatrix,outDegree);
	
		System.out.println("outDegree");

		for(int i=0; i < arraySize;i++){
			System.out.print(outDegree[i]);
		}
		System.out.println();
		for(int i=0; i <arraySize ;i++){
			for(int j=0; j<arraySize;j++){
			//System.out.print(A[i][j]);
			}
			//System.out.println();
		}
		//Row vector R 
		double[] R = new double[arraySize];
		R=PageRankMatrix(A);
		System.out.println("");
		System.out.print("PageRank:");
		double sum=0.0;
		for(int j=0;j<arraySize;j++){
			sum+=R[j];
			System.out.print(R[j]);
			
		}

	//sorting array
		Map<String , Double>URLListed = new HashMap<String, Double>(); 
		for(int i=0;i<arraySize;i++){
			URLListed.put(URLtoSequence.get(i), R[i]);
		}
		//URLListed.forEach((key,value) -> System.out.println(key + ":"+value));
		System.out.println();
		//System.out.println("sorted one");
        
		 List<Map.Entry<String, Double>> list =
	                new LinkedList<Map.Entry<String, Double>>(URLListed.entrySet());
		 
		 Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
	            public int compare(Map.Entry<String, Double> o1,
	                               Map.Entry<String, Double> o2) {
	                return (o1.getValue()).compareTo(o2.getValue());
	            }
	        });
		 
		 Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	        for (Map.Entry<String, Double> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        //sortedMap.forEach((key,value)-> System.out.println(key+":"+value));


	        //top 10% values
	        System.out.println("10% top");
	        for(int i=0;i<arraySize/10;i++){
	        	System.out.println(sortedMap.entrySet().toArray()[arraySize-1-i]);
	        }
	      //least 10% values
	        System.out.println("10% least");
	        for(int i=0;i<arraySize/10;i++){
	        	System.out.println(sortedMap.entrySet().toArray()[i]);
	        }
	}
private static Double[][] transitionMatrix(int[][]AdjacencyMatrix,int[]outDegree){
	int arraySize=outDegree.length;
	Double[][] A= new Double[arraySize][arraySize];
	double individualProbability = 1.0/arraySize;
	//System.out.println("probability"+individualProbability);
	for(int i=0;i<arraySize;i++){
		for(int j=0;j<arraySize;j++){
			if(outDegree[i]==0.0){
				A[i][j]=individualProbability+0.0;//since it is Dangling Node
				
			}
			else{
			
				A[i][j]=0.1*individualProbability+0.9*(double)AdjacencyMatrix[i][j]/(double)outDegree[i];
				
			}
		}
		//System.out.println();
	}
	return A;
}
private static double[] PageRankMatrix(Double[][] A)
{
	int arraySize=A.length;
	
	double[] R = new double[arraySize];
	R[0]=1.0;
	boolean r=false;
	
	while(r==false){
		double[] newR= new double[arraySize];
		for(int j=0; j< arraySize;j++){
			for(int k=0; k<arraySize;k++){
				newR[j]+= R[k]* A[k][j];
				
			}
		}
		boolean[] change=new boolean[arraySize];
		///////////////////checking change percentage
		for(int c=0;c<arraySize;c++){
			//System.out.print(((R[c]-newR[c])/R[c])*100);
		if ((((R[c]-newR[c])/R[c])*100)<0.1 && R[c]!=0){
			change[c]=true;
		}else{
		change[c]=false;	
		}
		//System.out.println(change[c]);
		}
		for(int c=0;c<arraySize;c++){
			if(change[c]==false){
				r=false;
				break;
			}else{
				r=true;
			}
		}
		for(int j=0; j<arraySize;j++){
			R[j]=newR[j];
		}

		
	}	
	return R;
}
public static void main(String[] args){

	try{
		File fileTest = new File("src\\scrape.txt");
		Scanner in =new Scanner(fileTest,"UTF-8");
		scanAndPrint(in,"guardian");
		in.close();
		
		}
	
	catch(Exception e){
		System.out.println(e);
	
	}
	}
}
