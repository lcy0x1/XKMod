package dev.lcy0x1.json;

import com.google.gson.JsonElement;

public class JsonException extends Exception {

	public enum Type {
		TYPE_MISMATCH, TAG, UNEXPECTED_NULL, FUNC, INTERNAL, UNDEFINED
	}

	private static final long serialVersionUID = 6451473277106188516L;

	public JsonException(Type type, JsonElement elem, String str) {
		super(str);
		// TODO
	}

}
