package spring.boot.webflu.ms.cuenta.banco.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.boot.webflu.ms.cuenta.banco.app.dao.TipoBancoProductoDao;
import spring.boot.webflu.ms.cuenta.banco.app.documents.TipoBancoCuenta;
import spring.boot.webflu.ms.cuenta.banco.app.service.TipoBancoProductoService;

@Service
public class TipoBancoProductoServiceImpl implements TipoBancoProductoService{

	
	@Autowired
	public TipoBancoProductoDao  tipoProductoDao;
	
	@Override
	public Flux<TipoBancoCuenta> findAllTipoproducto()
	{
	return tipoProductoDao.findAll();
	
	}
	@Override
	public Mono<TipoBancoCuenta> findByIdTipoProducto(String id)
	{
	return tipoProductoDao.findById(id);
	
	}
	
	@Override
	public Mono<TipoBancoCuenta> saveTipoProducto(TipoBancoCuenta tipoCliente)
	{
	return tipoProductoDao.save(tipoCliente);
	}
	
	@Override
	public Mono<Void> deleteTipo(TipoBancoCuenta tipoProducto) {
		return tipoProductoDao.delete(tipoProducto);
	}
	
}
