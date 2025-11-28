package com.gpp.servisoft.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gpp.servisoft.model.entities.Cliente;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.TipoCliente;
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
    
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(int id) {
        return clienteRepository.findById(id);
    }
    
    @Transactional
    public Cliente guardar(Cliente cliente) {
        // Validar que el correo no exista
        if (existeCorreo(cliente.getCorreoElectronico())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo electrónico");
        }
        return clienteRepository.save(cliente);
    }
    
    @Transactional
    public Cliente actualizar(Cliente cliente) {
        // Verificar que el cliente existe
        if (cliente.getIdCliente() <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        Cliente clienteExistente = clienteRepository.findById(cliente.getIdCliente())
            .orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));
        
        // Verificar correo solo si cambió
        if (!clienteExistente.getCorreoElectronico().equals(cliente.getCorreoElectronico())) {
            if (clienteRepository.existsByCorreoElectronico(cliente.getCorreoElectronico())) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso por otro cliente");
            }
        }
        
        // Actualizar campos directamente en el cliente existente
        clienteExistente.setTipoCliente(cliente.getTipoCliente());
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setDireccion(cliente.getDireccion());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setCorreoElectronico(cliente.getCorreoElectronico());
        clienteExistente.setEstado(cliente.getEstado());
        
        return clienteRepository.save(clienteExistente);
    }
    
    @Transactional
    public void eliminar(int id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));
        
        // Borrado lógico: cambiar estado a INACTIVO
        cliente.setEstado(Estado.INACTIVO);
        clienteRepository.save(cliente);
    }
    
    @Transactional(readOnly = true)
    public List<Cliente> listarPorEstado(Estado estado) {
        return clienteRepository.findByEstado(estado);
    }
    
    @Transactional(readOnly = true)
    public List<Cliente> listarPorTipo(TipoCliente tipoCliente) {
        return clienteRepository.findByTipoCliente(tipoCliente);
    }
    
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correoElectronico) {
        return clienteRepository.existsByCorreoElectronico(correoElectronico);
    }
    

    
    public void cambiarEstado(int id, Estado nuevoEstado) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));
        cliente.setEstado(nuevoEstado);
        clienteRepository.save(cliente);
    }
}
