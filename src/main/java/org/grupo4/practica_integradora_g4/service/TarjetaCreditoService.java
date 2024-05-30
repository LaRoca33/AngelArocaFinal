package org.grupo4.practica_integradora_g4.service;

import org.grupo4.practica_integradora_g4.model.entidades.Cliente;
import org.grupo4.practica_integradora_g4.model.entidades.TarjetaCredito;
import org.grupo4.practica_integradora_g4.repositories.TarjetaCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TarjetaCreditoService {
    @Autowired
    private TarjetaCreditoRepository tarjetaCreditoRepository;

    public void save(Set<TarjetaCredito> tarjetasCredito){
            tarjetaCreditoRepository.saveAll(tarjetasCredito);
    }
}
