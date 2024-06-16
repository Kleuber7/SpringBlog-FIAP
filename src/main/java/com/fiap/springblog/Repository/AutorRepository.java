package com.fiap.springblog.Repository;

import com.fiap.springblog.Model.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AutorRepository extends MongoRepository<Autor, String> {
}