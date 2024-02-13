package com.backend.rinha.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TransacaoConta(
		@JsonIgnore Integer id,
		Integer valor,
		String tipo,
		String descricao,
		@JsonProperty("realizada_em") Instant realizadaEm,
		@JsonIgnore Integer saldo,
		@JsonIgnore Integer limite) {
	
	public static class CustomRowMapper implements RowMapper<TransacaoConta> {

		@Override
		public TransacaoConta mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new TransacaoConta(rs.getInt("id"), 
					rs.getInt("valor"), 
					rs.getString("tipo"), 
					rs.getString("descricao"), 
					rs.getTimestamp("realizada_em").toInstant(), 
					rs.getInt("saldo"), 
					rs.getInt("limite"));
		}
		
	}
}
