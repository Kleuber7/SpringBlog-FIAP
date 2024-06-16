package com.fiap.springblog.Service.ServiceImpl;

import com.fiap.springblog.Model.Autor;
import com.fiap.springblog.Repository.AutorRepository;
import com.fiap.springblog.Service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutorServiceImpl implements AutorService {

    @Autowired
    private AutorRepository autorRepository;

    @Override
    public Autor criar(Autor autor) {
        return this.autorRepository.save(autor);
    }

    @Override
    public Autor obterPorCodigo(String codigo) {
        return this.autorRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Autor não existe!"));
    }

}
