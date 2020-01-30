package spring.boot.webflu.ms.bancos.app.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.bancos.app.dao.BancoDao;
import spring.boot.webflu.ms.bancos.app.documents.Banco;
import spring.boot.webflu.ms.bancos.app.service.BancoService;

@Service
public class BancoServiceImpl implements BancoService {
	
	@Autowired
	public BancoDao clienteDao;
	
	@Override
	public Flux<Banco> findAllBanco()
	{
	return clienteDao.findAll();
	
	}
	@Override
	public Mono<Banco> findByIdBanco(String id)
	{
	return clienteDao.findById(id);
	
	}
	
	@Override
	public Mono<Banco> viewRucBanco(String ruc)
	{
	return clienteDao.viewRucBanco(ruc);
	
	}
	
	@Override
	public Mono<Banco> saveBanco(Banco bank)
	{
	return clienteDao.save(bank);
	}
	
	@Override
	public Mono<Void> deleteBanco(Banco bank) {
		return clienteDao.delete(bank);
	}
	
}
