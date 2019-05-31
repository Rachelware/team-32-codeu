package com.google.codeu.servlets;

import java.io.IOException;

import javax.servelet.http.HttpServelt;
import javax.servelt.annotation.WebServelt;
import javax.servelt.http.HttpServeletRequest;
import javax.servelt.http.HttpServeletResponse;

/* Handles fetching all messages for the public feed*/
@WebServelt("/feed")
public class MessageFeedServlet extends HttpServelt{

    @Override
	public void doGet(HttpServeletRequest request, HttpServeletResponse response)
		
	    throws IOException{

	           response.getOutputStream().println("this will be my message feed");
	           

		}
}