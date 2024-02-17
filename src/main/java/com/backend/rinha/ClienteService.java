package com.backend.rinha;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.rinha.model.Conta;
import com.backend.rinha.model.Extrato;
import com.backend.rinha.model.Transacao;
import com.backend.rinha.model.Transacao.SaldoAposTransacao;

import jakarta.annotation.PostConstruct;

@Service
public class ClienteService {
	
	private ClienteRepository repo;
	private Set<Integer> clientes = new HashSet<>();
	
	@PostConstruct
	void initCacheClienteExiste() {
		this.clientes = new HashSet<>(repo.getClientes());
	}
	
	public ClienteService(ClienteRepository repo) {
		super();
		this.repo = repo;
	}

	public Extrato getExtratoDoCliente(Integer clienteId) {
		if (!clientes.contains(clienteId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		final var transacoes = repo.consultaTransacoesConta(clienteId);
		var valorSaldo = 0;
		var limite = 0;
		if (transacoes.isEmpty()) {
			final var conta = consultaContaCliente(clienteId);
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

	private Conta consultaContaCliente(Integer clienteId) {
		return repo.consultaContaCliente(clienteId);
	}

	public SaldoAposTransacao processarTransacao(Integer clienteId, Transacao transacao) {
		transacao.validarTransacao();
		if (!clientes.contains(clienteId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		final var optSaldoAposTransacao = repo.atualizaConta(clienteId, transacao.getValorReal());
		if (optSaldoAposTransacao.isPresent()) {
			repo.insereTransacao(clienteId, transacao);
			return optSaldoAposTransacao.get();
		} else {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

}
