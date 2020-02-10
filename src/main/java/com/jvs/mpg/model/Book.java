package com.jvs.mpg.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Udinei Silva
 * 
 * This class retrieves from Penguin Random House REST API, 
 * a book by the title and name of the authors, and returns 
 * the name and desired name of one of the authors of the book.
 * 
 */
public class Book {

	private static final String KUSER = "testuser";
	private static final String KPASS = "testpassword";
	private static String nameSurNameAuthor = "Dan Howell";
	
	private static String URL_SERVER = "https://reststop.randomhouse.com/";
	private static String METHOD_RESOURCE = "resources/titles";
	private static String INPUT_METHOD = "?search=";
	
	public Book() {

		// authenticate user
		Authenticator.setDefault(new RHAuthenticator());
	}

	

	// get author by name of book
	public static String getAuthorsByBookName(String titleBoock)  {
		HttpURLConnection con = null;		
		String xmlString = "";
		Document doc = null;
		String result = "";
		List<String> listTitlesAuthors = new ArrayList<>();

		try {
			
			con = mountRequestServer(titleBoock);
			
			xmlString  = readResponseServer(con);
			
			doc = stringXmlToDocument(xmlString);
			
			listTitlesAuthors = getListTitlesAndAuthors(doc);
			
			result = checksAuthorExistList(listTitlesAuthors);
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Result: " + result);
		
		return result;
	}
	

	// mount request and return the established connection
	private static HttpURLConnection mountRequestServer(String paramSearch) throws MalformedURLException, IOException {
			
		// replacing space with ASCII value (hexadecimal). %20 a space. 
		//In order to avoid http encoding errors
		String titleSearch = paramSearch.replace(" ", "%20");
		
		HttpURLConnection con = urlConection(URL_SERVER, METHOD_RESOURCE, INPUT_METHOD, titleSearch);
		 
		return con;
				
	}

	

	// get the response from the server and a string
	private static String readResponseServer(HttpURLConnection con) throws IOException {
		String xmlString = "";
		String strLine = "";
		
		InputStream is = con.getInputStream();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while ((strLine = reader.readLine()) != null) {
			xmlString = strLine;
		}
		
		reader.close();
		return xmlString;
	}
	
	

	// connect to the url server informing, inform search parameters and return the connection
	private static HttpURLConnection urlConection(String urlServer, String methodResource, String inputMethod, String paramSearch) throws MalformedURLException, IOException {
		
		String pathUrl = urlServer.concat(methodResource).concat(inputMethod).concat(paramSearch);
	
		URL url = new URL(pathUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		return con;
	}
	

	 
	// get the list of titles, subtitles, authors and isbn
	private static List<String> getListTitlesAndAuthors(Document doc) throws Exception {
		List<String> titleAuthisbn = new ArrayList<>();

		if (doc != null) {
           
			// get the list of all nodes contained in the <titlesubtitleauthisbn> tag
			NodeList nList = doc.getElementsByTagName("titlesubtitleauthisbn");
            
			// 
			// generates a list of all children's content from "titlesubtitleauthisbn"
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				Node eElement = nNode.getFirstChild();
				titleAuthisbn.add(eElement.getTextContent());
			}
		}

		return titleAuthisbn;

	}


	// check if the desired author exists in the list passed as a parameter
	private static String checksAuthorExistList(List<String> titleAuthisbn) {
		boolean isAuthor = false;
	
		for (String x : titleAuthisbn) {
			 
			// the name of the desired author is contained in the title of the work 	
			if (x.toLowerCase().contains(nameSurNameAuthor.toLowerCase())) {
    			isAuthor = true;
				break;
			}
		}

		return isAuthor ? nameSurNameAuthor : null;
	}


	// Authenticates user at Penguin Random House
	static class RHAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return (new PasswordAuthentication(KUSER, KPASS.toCharArray()));
		}
	}


	// Convert an xml string to an XML Document
	private static Document stringXmlToDocument(String xml) throws Exception {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			return db.parse(new ByteArrayInputStream(xml.getBytes()));

		} catch (Exception e) {
			throw new Exception(e.getCause());

		}

	}

}
