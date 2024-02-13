package com.backend.rinha;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.rinha.model.Extrato;
import com.backend.rinha.model.Transacao;
import com.backend.rinha.model.Transacao.SaldoAposTransacao;

@RestController	
@RequestMapping("/clientes")
public class ClienteController {
	
	private ClienteService service;
	
	public ClienteController(ClienteService service) {
		super();
		this.service = service;
	}

	@PostMapping("{id}/transacoes")
	public ResponseEntity<SaldoAposTransacao> addTransacao(
			@PathVariable Integer id,
			@RequestBody Transacao transacao) {
		return ResponseEntity.ok(service.processarTransacao(id, transacao));
	}
	
	@GetMapping("{id}/extrato")
	public ResponseEntity<Extrato> extrato(@PathVariable Integer id) {
		return ResponseEntity.ok(service.getExtratoDoCliente(id));
	}
	
	@ExceptionHandler 
    public ResponseEntity<Void> handle(NumberFormatException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
	

	@ExceptionHandler 
    public ResponseEntity<Void> handleJsonException(HttpMessageNotReadableException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
	
}
