package com.backend.rinha.model;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Extrato(
	Saldo saldo,
	@JsonProperty("ultimas_transacoes")	List<Transacao> ultimasTransacoes) {
	
	public static record Saldo(
		Integer total,
		@JsonProperty("data_extrato") Instant dataExtrato,
		Integer limite) {}
}
