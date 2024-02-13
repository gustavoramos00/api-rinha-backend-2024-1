package com.backend.rinha;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.rinha.model.Extrato;
import com.backend.rinha.model.Transacao;
import com.backend.rinha.model.Transacao.SaldoAposTransacao;

@Service
public class ClienteService {
	
	private ClienteRepository repo;
	
	public ClienteService(ClienteRepository repo) {
		super();
		this.repo = repo;
	}

	public Extrato getExtratoDoCliente(Integer clienteId) {
		final var transacoes = repo.consultaTransacoesConta(clienteId);
		var valorSaldo = 0;
		var limite = 0;
		if (transacoes.isEmpty()) {
			final var conta = repo.consultaContaCliente(clienteId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			valorSaldo = conta.saldo();
			limite = conta.limite();
		} else {
			valorSaldo = transacoes.get(0).saldo();
			limite = transacoes.get(0).limite();
		}
		final var data = Instant.now();
		final var saldo = new Extrato.Saldo(valorSaldo, data, limite);
		return new Extrato(saldo, transacoes);
	}

	public SaldoAposTransacao processarTransacao(Integer clienteId, Transacao transacao) {
		transacao.validarTransacao();
		return repo.processarTransacao(clienteId, transacao);
	}

}
