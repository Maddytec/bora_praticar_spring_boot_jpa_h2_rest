package br.com.maddytec.cliente.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository repository;

    public Uni<Cliente> salvar(Cliente c){
        return Panache.withTransaction(() -> repository.persist(c).replaceWith(c));
    }

    public Uni<List<Cliente>> listar(){
        return repository.findAll().list();
    }

    public Uni<Cliente> buscarPorId(Long id){
        return repository.findById(id);
    }

    public Uni<Void> removerPorId(Long id){
        return Panache.withTransaction(() -> repository.deleteById(id))
                .replaceWithVoid();
    }
}
