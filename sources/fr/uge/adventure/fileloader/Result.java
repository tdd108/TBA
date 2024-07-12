package fr.uge.adventure.fileloader;

import java.util.Objects;

public record Result(Token token, String content) {
	public Result {
		Objects.requireNonNull(token);
		Objects.requireNonNull(content);
	}
	
	public boolean isToken(Token token) {
		return this.token() == token ? true : false;
	}
}