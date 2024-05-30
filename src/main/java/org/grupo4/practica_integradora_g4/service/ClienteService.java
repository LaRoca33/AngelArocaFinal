package org.grupo4.practica_integradora_g4.service;

import jakarta.transaction.Transactional;
import org.grupo4.practica_integradora_g4.model.entidades.Cliente;
import org.grupo4.practica_integradora_g4.repositories.ClienteRepository;
import org.grupo4.practica_integradora_g4.repositories.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private DireccionRepository direccionRepository;
    public void save(Cliente cliente){
        clienteRepository.save(cliente);
    }
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }
    @Transactional
    public void deleteById(UUID id) {
        direccionRepository.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }
  /* public List<Cliente> busquedaParametrizada(String nombre, String apellidos, String email, String pais, String genero,String telefono, String tipoDoc, String doc, String comentarios){
        return clienteRepository.findByNombreContainingIgnoreCaseAndApellidosContainingIgnoreCaseAndUsuarioEmail_EmailContainingIgnoreCaseAndPaisNombreContainingIgnoreCaseAndGeneroGenContainingIgnoreCaseAndTelefonoMovilContainingIgnoreCaseAndTipoDocumentoClienteContainingIgnoreCaseAndDocumentoContainingIgnoreCaseAndComentariosContainingIgnoreCase(
                nombre, apellidos, email, pais, genero,telefono, tipoDoc, doc, comentarios);
    }
    */


    public List<Cliente> buscarParam(String nombre,String apellidos,String email,String pais,String genero,String telefonoMovil,String documento,String tipoDocumentoCliente,String comentarios){
        return clienteRepository.findByNombreContainingIgnoreCaseAndApellidosContainingIgnoreCaseAndUsuarioEmailEmailContainingIgnoreCaseAndPaisNombreContainingIgnoreCaseAndGeneroGenContainingIgnoreCaseAndTelefonoMovilContainingIgnoreCaseAndDocumentoContainingIgnoreCaseAndTipoDocumentoClienteContainingIgnoreCaseAndComentariosContainingIgnoreCase(nombre,apellidos,email,pais,genero,telefonoMovil,documento,tipoDocumentoCliente,comentarios);
    }


}
