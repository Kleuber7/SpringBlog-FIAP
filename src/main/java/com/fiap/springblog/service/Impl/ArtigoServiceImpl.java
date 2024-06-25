package com.fiap.springblog.service.Impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {


    private final MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongo;

    public ArtigoServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    private ArtigoRepository artigoRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Override
    public List<Artigo> obterTodos() {
        return this.artigoRepository.findAll();
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro de concorrencia: O Artigo foi atualizado por outro usuario."
        + "Por favor, tente novamente!");
    }


    @Override
    @Transactional(readOnly = true)
    public Artigo obterPorCodigo(String codigo) {
        return this.artigoRepository.findById(codigo).orElseThrow(() ->
                new IllegalArgumentException("Artigo Não Existe!"));
    }

    @Override
    public ResponseEntity<?> criar(Artigo artigo) {
        if(artigo.getAutor().getCodigo() != null) {
            Autor autor = this.autorRepository.
                    findById(artigo.getAutor().getCodigo()).
                    orElseThrow(() -> new IllegalArgumentException("Autor Inexistente"));

            artigo.setAutor(autor);
        }else {
            artigo.setAutor(null);
        }
        try{

            this.artigoRepository.save(artigo);
            return ResponseEntity.status(HttpStatus.CREATED).body(artigo);
        }catch (DuplicateKeyException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Artigo já existe na coleção");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar artigo: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo) {
        try {
            Artigo existenteArtigo =
                    this.artigoRepository.findById(id).orElse(null);

            if(existenteArtigo == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Artigo não encontrado na coleção");
            }

            existenteArtigo.setTitulo(artigo.getTitulo());
            existenteArtigo.setData(artigo.getData());
            existenteArtigo.setTexto(artigo.getTexto());
            this.artigoRepository.save(existenteArtigo);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar artigo: " + e.getMessage());
        }
    }


    /*@Override
    @Transactional
    public Artigo criar(Artigo artigo) {
        if(artigo.getAutor().getCodigo() != null){
            Autor autor = this.autorRepository.
                    findById(artigo.getAutor().getCodigo()).
                    orElseThrow(() -> new IllegalArgumentException("Autor Inexistente"));

            artigo.setAutor(autor);
        }else {
            artigo.setAutor(null);
        }

        try {
            return this.artigoRepository.save(artigo);
        }catch (OptimisticLockingFailureException ex){

            Artigo atualizado = artigoRepository.findById(artigo.getCodigo()).orElse(null);

            if(atualizado != null){

                atualizado.setTitulo(artigo.getTitulo());
                atualizado.setStatus(artigo.getStatus());
                atualizado.setTexto(artigo.getTexto());

                atualizado.setVersion(atualizado.getVersion() + 1);

                return this.artigoRepository.save(atualizado);
            }else{
                throw new RuntimeException("Artigo não encontrado: " + artigo.getCodigo());
            }

        }
    }*/

    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {
        Query query = new Query(Criteria.where("data").gt(data));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status) {
        Query query = new Query(Criteria.where("data").is(data).and("status").is(status));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    @Transactional
    public void atualizar(Artigo updateArtigo) {
        this.artigoRepository.save(updateArtigo);
    }

    @Override
    @Transactional
    public void atualizarArtigo(String id, String novaURL) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("url", novaURL);
        this.mongoTemplate.updateFirst(query,update,Artigo.class);
    }

    @Override
    @Transactional
    public void deleteById(String id){
        this.artigoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteArtigoById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        this.mongoTemplate.remove(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data) {
        return this.artigoRepository
                .findByStatusAndDataGreaterThan(status, data);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return this.artigoRepository.obterArtigoPorDataHora(de,ate);
    }

    @Override
    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo) {

        Criteria criteria = new Criteria();
        criteria.and("data").lte(data);
        if(status != null){
            criteria.and("status").is(status);
        }

        if(titulo != null && !titulo.isEmpty()){
            criteria.and("titulo").regex(titulo,"i");
        }

        Query query = new Query(criteria);

        return this.mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public Page<Artigo> findAll(Pageable pageable) {
        Sort sort = Sort.by("titulo").ascending();
        Pageable paginacao =
                PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize(),sort);

        return this.artigoRepository.findAll(paginacao);
    }

    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return this.artigoRepository.findByStatusOrderByTituloAsc(status);
    }

    @Override
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status) {
        return this.artigoRepository.obterArtigoPorStatusComOrdenacao(status);
    }

    @Override
    public List<Artigo> findByTexto(String searchTerm) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(searchTerm);
        TextQuery query = new TextQuery(criteria).sortByScore();
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<ArtigoStatusCount> contarArtigoPorStatus() {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.group("status").count().as("quantidade"),
                        Aggregation.project("quantidade").and("status")
                                .previousOperation()
                );
        AggregationResults<ArtigoStatusCount> results =
                mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);
        return results.getMappedResults();
    }

    @Override
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.match(
                                Criteria.where("data").gte(dataInicio.atStartOfDay())
                                        .lt(dataFim.plusDays(1).atStartOfDay())
                        ),
                        Aggregation.group("autor").count().as("totalArtigos"),
                        Aggregation.project("totalArtigos").and("autor")
                        .previousOperation()
                );
        AggregationResults<AutorTotalArtigo> results =
                mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);
        return results.getMappedResults();
    }

}
