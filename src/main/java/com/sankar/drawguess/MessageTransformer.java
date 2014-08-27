package com.sankar.drawguess;

import java.io.IOException;
import java.io.StringReader;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.FloodFillMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public class MessageTransformer implements Decoder.Text<Message>, Encoder.Text<Message> {
	
	private Gson gson;

	@Override
	public void init(EndpointConfig epc) {
		this.gson = new GsonBuilder().create();
	}
	
	@Override
	public void destroy() {
		// Nothing to do
	}	

	@Override
	public String encode(Message msg) throws EncodeException {
		return gson.toJson(msg);
	}

	@Override
	public Message decode(String msg) throws DecodeException {
		try (JsonReader r = new JsonReader(new StringReader(msg))) {
			r.beginObject();
			String type = r.nextName();
			
			switch(type) {
			case "drawing": return gson.fromJson(msg, DrawingMessage.class);
			case "guess": return gson.fromJson(msg, GuessMessage.class);
			case "floodFill": return gson.fromJson(msg, FloodFillMessage.class);
			default: throw new UnknownMessageException();
			}
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public boolean willDecode(String msg) {
		return true;
	}

}
