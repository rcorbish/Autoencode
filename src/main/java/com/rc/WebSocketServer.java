package com.rc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class WebSocketServer {
	private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

	private volatile Thread t = null ;
	public static Session sender ;
	public static DBNA dbna ; 
	public static int layers[] ;

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		sender = user ;
		log.info("Client WS connected" ) ;
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		log.info("Client WS finished" ) ;
		sender = null ;
	}

	@OnWebSocketMessage
	public void onMessage( Session user, String message) {
		if( "start".equalsIgnoreCase(message) ) {
			begin() ;
			int tmp[] = new int[ (layers.length * 2) - 1 ] ;
			for( int i=0 ; i<layers.length ; i++ ) {
				tmp[i] = layers[i] ;
				tmp[ tmp.length - i -1 ] = layers[i] ;
			}
			send( Arrays.toString(tmp) ) ;
		}
		log.info("Client WS msg received: {}", message ) ;
	}

	@OnWebSocketMessage
	public void onBinaryMessage( Session user, byte buf[], int offset, int length ) {
		log.info("Client WS msg received: {}", length ) ;
		int data[] = new int[ 784 ] ;
		for( int i=0 ; i<data.length ; i++ ) {
			data[i] = buf[i*4+3] == 0 ? 0 : 1 ;
		}
		/*
		StringBuilder sb = new StringBuilder() ;
		for( int r=0 ; r<28 ; r++ ) {
			sb.setLength(0);
			for( int c=0 ; c<28 ; c++ ) {
				sb.append( data[ c + r*28 ] == 0 ? '.' : '*' ) ;
			}			
			log.info( sb.toString() ) ;
		}
		log.info( "Char done" ) ;
		*/
		byte response[] = dbna.classify( data ) ;
		ByteBuffer rsp = ByteBuffer.wrap( response ) ;
		try {
			sender.getRemote().sendBytes( rsp ) ;
		} catch( IOException ioe ) {
			throw new RuntimeIOException( ioe ) ;
		}
	}

	public static void send( String msg ) {
		if( sender != null && sender.isOpen() ) {
			try {
				sender.getRemote().sendString( msg ) ;
			} catch( IOException ioe ) {
				throw new RuntimeIOException( ioe ) ;
			}
		}
	}

	public void begin() {
		try {
			if( t != null ) {
				t.interrupt(); 
				t.join();
			}
		} catch( InterruptedException ignore ) {

		}
		t = new Thread( this::loop ) ;
		t.start();		
	}

	public void loop() {
		try {
			while( true ) {    		
				Thread.sleep( 1000 ) ;
				WebSocketServer.send(  String.valueOf( dbna.model.score() ) ); ;
			}
		} catch( InterruptedException iex ) {
			// ignore
		}
		t = null ;
	}
}

