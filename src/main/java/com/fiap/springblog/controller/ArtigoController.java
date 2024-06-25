package com.fiap.springblog.controller;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    @GetMapping
    public List<Artigo> obterTodos() {
        return this.artigoService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo) {
        return this.artigoService.obterPorCodigo(codigo);
    }

    /*@PostMapping
    public Artigo criar(@RequestBody Artigo artigo){
        return this.artigoService.criar(artigo);
    }*/

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Artigo artigo) {
        return this.artigoService.criar(artigo);
    }

    @PutMapping("/atualizar-artigo/{id}")
    public ResponseEntity<?> atualizarArtigo(
            @PathVariable("id") String id,
            @RequestBody Artigo artigo) {
        return this.artigoService.atualizarArtigo(id, artigo);
    }


    @GetMapping("/maiordata")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime data){

        return this.artigoService.findByDataGreaterThan(data);
    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(
            @RequestParam("data") LocalDateTime data,
            @RequestParam("status") Integer status){
        return this.artigoService.findByDataAndStatus(data,status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo updateArtigo) {
        this.artigoService.atualizar(updateArtigo);
    }

    @PutMapping("/{id}")
    public void atualizarArtigo(
            @PathVariable String id,
            @RequestBody String novaURL) {
       this.artigoService.atualizarArtigo(id,novaURL);
    }


    @DeleteMapping("/{id}")
    public void deletaArtigo(
            @PathVariable String id){
        this.artigoService.deleteById(id);
    }

    @DeleteMapping("/delete")
    public void deleteArtigoById(
          @RequestParam("Id") String id) {
        this.artigoService.deleteArtigoById(id);
    }

    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime data){
        return this.artigoService.findByStatusAndDataGreaterThan(status,data);
    }

    @GetMapping("/data-hora")
    public List<Artigo> obterArtigoPorDataHora(
            @RequestParam("de") LocalDateTime de,
            @RequestParam("ate") LocalDateTime ate) {
        return this.artigoService.obterArtigoPorDataHora(de,ate);
    }


    @GetMapping("/artigo-complexo")
    public List<Artigo> encontrarArtigosComplexos(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime data,
            @RequestParam("titulo") String titulo){
        return this.artigoService.encontrarArtigosComplexos(status,data,titulo);
    }

    @GetMapping("/pagina-artigos")
    public ResponseEntity<Page<Artigo>> findAll(
             Pageable pageable) {
        Page<Artigo> artigos = this.artigoService.findAll(pageable);
        return ResponseEntity.ok(artigos);
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(
            @RequestParam("status") Integer status) {
        return this.artigoService.findByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(
            @RequestParam("status") Integer status) {
        return this.artigoService.obterArtigoPorStatusComOrdenacao(status);
    }

    @GetMapping("/texto")
    public List<Artigo> findByTexto(
            @RequestParam("texto") String searchTerm) {
        return this.artigoService.findByTexto(searchTerm);
    }

    @GetMapping("/contar-artigo")
    public List<ArtigoStatusCount> contarArtigoPorStatus() {
        return this.artigoService.contarArtigoPorStatus();
    }

    @GetMapping("/calcular-total-artigos")
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        return this.artigoService.calcularTotalArtigosPorAutorNoPeriodo(dataInicio,dataFim);
    }

}