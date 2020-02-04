package spring.boot.webflu.ms.cuenta.banco.app.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.dao.TipoProductoDao;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TypeCurrentAccount;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoProductoService;

@Service
public class tipoProductoServiceImpl implements TipoProductoService{

	
	@Autowired
	public TipoProductoDao  tipoProductoDao;
	
	@Override
	public Flux<TypeCurrentAccount> findAllTipoproducto()
	{
	return tipoProductoDao.findAll();
	
	}
	@Override
	public Mono<TypeCurrentAccount> findByIdTipoProducto(String id)
	{
	return tipoProductoDao.findById(id);
	
	}
	
	@Override
	public Mono<TypeCurrentAccount> saveTipoProducto(TypeCurrentAccount tipoCliente)
	{
	return tipoProductoDao.save(tipoCliente);
	}
	
	@Override
	public Mono<Void> deleteTipo(TypeCurrentAccount tipoProducto) {
		return tipoProductoDao.delete(tipoProducto);
	}
	
}
