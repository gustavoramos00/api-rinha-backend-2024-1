package com.backend.rinha;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.rinha.model.Conta;
import com.backend.rinha.model.Transacao;
import com.backend.rinha.model.Transacao.SaldoAposTransacao;
import com.backend.rinha.model.TransacaoConta;

@Repository
@Transactional(readOnly = true)
public class ClienteRepository {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public ClienteRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final String QUERY_CONTA_CLIENTE = """
			select 
				id, 
				cliente_id, 
				limite, 
				saldo
			from conta
			where cliente_id = :cliente_id
			""";
	private static final String QUERY_TRANSACAO_JOIN_CONTA = """
			select 
				t.id, 
				t.valor, 
				t.tipo, 
				t.descricao, 
				t.realizada_em,
				c.saldo,
				c.limite
			from transacao t
			inner join conta c on t.cliente_id = c.cliente_id
			where t.cliente_id = :cliente_id
			order by t.realizada_em desc
			limit 10
			""";
	private static final String QUERY_UPDATE_CONTA = """
			update conta
			set saldo = saldo + :valor
			where cliente_id = :cliente_id
			""";
	private static final String QUERY_INSERT_TRANSACAO = """
			insert into transacao (cliente_id, valor, tipo, descricao, realizada_em)
			 values (:cliente_id, :valor, :tipo, :descricao, :realizada_em)
			""";
	
	public Optional<Conta> consultaContaCliente(Integer clienteId) {
		SqlParameterSource params = new MapSqlParameterSource("cliente_id", clienteId);
		try {
			final var conta = jdbcTemplate.queryForObject(
					QUERY_CONTA_CLIENTE,
					params,
					new Conta.CustomRowMapper());
			return Optional.of(conta);
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	public List<TransacaoConta> consultaTransacoesConta(Integer clienteId) {
		SqlParameterSource params = new MapSqlParameterSource("cliente_id", clienteId);
		return jdbcTemplate.query(
				QUERY_TRANSACAO_JOIN_CONTA,
				params,
				new TransacaoConta.CustomRowMapper());
	}

	@Transactional
	public void insereTransacao(Integer clienteId, Transacao transacao) {
		SqlParameterSource paramsInsert = new MapSqlParameterSource(
				Map.of("cliente_id", clienteId,
				"valor", transacao.valorInt(),
				"tipo", transacao.tipo(),
				"descricao", transacao.descricao(),
				"realizada_em", Timestamp.from(Instant.now())));
		jdbcTemplate.update(QUERY_INSERT_TRANSACAO, paramsInsert);
	}

	@Transactional
	public SaldoAposTransacao atualizaConta(Integer clienteId, int valor) {
		SqlParameterSource paramsUpdate = new MapSqlParameterSource(
				Map.of("cliente_id", clienteId,
				"valor", valor));
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String[] columnNames = {"saldo", "limite"};
		jdbcTemplate.update(QUERY_UPDATE_CONTA, paramsUpdate, keyHolder, columnNames);
		if (keyHolder.getKeys() == null || keyHolder.getKeys().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		} else {
			final var saldo = (Integer) keyHolder.getKeys().get("saldo");
			final var limite = (Integer) keyHolder.getKeys().get("limite");
			if (saldo < -limite) {
				throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
			} else {
				return new SaldoAposTransacao(saldo, limite);				
			}
		}
	}
}
