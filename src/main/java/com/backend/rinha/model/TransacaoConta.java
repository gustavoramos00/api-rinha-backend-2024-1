package com.backend.rinha.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.springframework.jdbc.core.RowMapper;

public record TransacaoConta(
	Integer id,
	Integer valor,
	String tipo,
	String descricao,
	Instant realizadaEm,
	Integer saldo,
	Integer limite) {
	
	public Transacao transacao() {
		return new Transacao(id, valor.toString(), tipo, descricao, realizadaEm);
	}
	
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
