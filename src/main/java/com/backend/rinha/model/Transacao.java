package com.backend.rinha.model;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Transacao(
	@JsonIgnore Integer id,
	String valor,
	String tipo,
	String descricao,
	@JsonProperty("realizada_em") Instant realizadaEm) {
	
	@JsonIgnore
	public int getValorReal() {
		return "c".equals(tipo) ? 
				Integer.valueOf(valor) : - Integer.valueOf(valor);
	}
	
	public void validarTransacao() {
		if (valor == null || 
				valor.isEmpty() ||
				Integer.parseInt(valor) <= 0 ||
				descricao == null || 
				descricao.isEmpty() ||
				descricao.length() > 10 ||
				!Arrays.asList("c", "d").contains(tipo)
				) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	public Integer valorInt() {
		return Integer.valueOf(valor);
	}
	
	public static record SaldoAposTransacao(
		Integer saldo,
		Integer limite) {}
}