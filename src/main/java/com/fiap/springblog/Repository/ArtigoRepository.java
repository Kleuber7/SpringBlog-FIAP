package com.fiap.springblog.Repository;

import com.fiap.springblog.Model.Artigo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArtigoRepository extends MongoRepository<Artigo, String> {


}