package br.com.maddytec.cliente.quarkus;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.smallrye.mutiny.Uni;
import java.util.List;

@Path("/api/v1/cliente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService service;

    @POST
    public Uni<Response> salvar(Cliente cliente){
        return service.salvar(cliente)
                .map(saved -> Response.status(Response.Status.CREATED).entity(saved).build());
    }

    @GET
    public Uni<List<Cliente>> listar(){
        return service.listar();
    }

    @GET
    @Path("/{id}")
    public Uni<Response> buscarPorId(@PathParam("id") Long id){
        return service.buscarPorId(id)
                .onItem().ifNull().failWith(new NotFoundException("Cliente nao encontrado."))
                .map(Response::ok)
                .map(Response.ResponseBuilder::build);
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> atualizar(@PathParam("id") Long id, Cliente in){
        return service.buscarPorId(id)
                .onItem().ifNull().failWith(new NotFoundException("Cliente nao encontrado."))
                .flatMap(base -> {
                    if (in.nome != null) base.nome = in.nome;
                    if (in.email != null) base.email = in.email;
                    if (in.cpf != null) base.cpf = in.cpf;
                    return service.salvar(base);
                })
                .replaceWith(Response.noContent().build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> remover(@PathParam("id") Long id){
        return service.removerPorId(id)
                .replaceWith(Response.noContent().build());
    }
}
