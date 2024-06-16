package com.fiap.springblog.Service.ServiceImpl;

import com.fiap.springblog.Model.Artigo;
import com.fiap.springblog.Model.Autor;
import com.fiap.springblog.Repository.ArtigoRepository;
import com.fiap.springblog.Repository.AutorRepository;
import com.fiap.springblog.Service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {


    private final MongoTemplate mongoTemplate;

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

    @Override
    public Artigo obterPorCodigo(String codigo) {
        return this.artigoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Artigo Não Existe!"));
    }

    @Override
    public Artigo criar(Artigo artigo) {
        if(artigo.getAutor().getCodigo() != null){
            Autor autor = this.autorRepository.
                    findById(artigo.getAutor().getCodigo()).
                    orElseThrow(() -> new IllegalArgumentException("Autor Inexistente"));

            artigo.setAutor(autor);
        }else {
            artigo.setAutor(null);
        }

        return this.artigoRepository.save(artigo);
    }

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
    public void atualizar(Artigo updateArtigo) {
        this.artigoRepository.save(updateArtigo);
    }


}
