package com.backend.rinha.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Conta(
	Integer id,
	@JsonProperty("cliente_id")	Integer clienteId,
	Integer saldo,
	Integer limite) {
	
	public static class CustomRowMapper implements RowMapper<Conta> {

		@Override
		public Conta mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Conta(
					rs.getInt("id"),
					rs.getInt("cliente_id"),
					rs.getInt("saldo"),
					rs.getInt("limite"));
		}
		
	}
}
