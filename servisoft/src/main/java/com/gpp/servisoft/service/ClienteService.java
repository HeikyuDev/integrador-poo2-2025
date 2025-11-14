package com.gpp.servisoft.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gpp.servisoft.model.entities.Cliente;
import com.gpp.servisoft.repository.ClienteRepository;

/**
 * *Cliente Service*
 * Métodos del servicio para manejar la lógica de negocio relacionada con la entidad Cliente.
 * Solo contiene el método "listarClientes" para probar los métodos de CuentaService.
 */

@Service
public class ClienteService {
    /**
     * Método para listar todos los clientes.
     * @return Lista de clientes.
     */

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }
}
