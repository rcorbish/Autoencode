package com.rc;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.ClasspathTemplateLoader;
import de.neuland.jade4j.template.TemplateLoader;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.jade.JadeTemplateEngine;

public class WebServer {
	private DBNA dbna ;
	
	public WebServer( DBNA dbna, int layers[] ) throws IOException {
		this.dbna = dbna ;
		WebSocketServer.dbna = dbna ;
		WebSocketServer.layers = layers ;
		
		staticFiles.location(".");
		staticFiles.expireTime(600);

		webSocket("/data", WebSocketServer.class);

		exception( Exception.class, (exception, request, response) -> {
			response.body( "<pre>" + exception.toString() + "</pre>" ) ; 
		});

		TemplateLoader templateLoader = new ClasspathTemplateLoader();
		JadeConfiguration jadeConfig = new JadeConfiguration() ;
		jadeConfig.setCaching( false ) ;
		jadeConfig.setTemplateLoader(templateLoader);
	
		get( "/", this::home, new JadeTemplateEngine( jadeConfig ) ) ;
	}
	
	public ModelAndView home( Request request, Response response ) throws Exception {
		Map<String,Object> map = new HashMap<>() ;
		dbna.model.score() ;
		return new ModelAndView( map, "templates/home" )  ;
	}

}
