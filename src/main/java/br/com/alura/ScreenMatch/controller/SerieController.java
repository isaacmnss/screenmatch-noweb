package br.com.alura.ScreenMatch.controller;

import br.com.alura.ScreenMatch.DTO.SerieDTO;
import br.com.alura.ScreenMatch.repository.EpisodioRepository;
import br.com.alura.ScreenMatch.repository.SerieRepository;
import br.com.alura.ScreenMatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping ("/series")
    public List<SerieDTO> obterSeries(){
        return serieService.obterTodasAsSerires();
    }


    @GetMapping("/series/top5")
    public List<SerieDTO> obterTop5Series(){
        return serieService.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return serieService.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return serieService.obterPorID(id);
    }

    @GetMapping("/{id}/temporadas")
    public List<EpisodioDTO> obterTodasTemporadas(Long id){
        return serieService.obterTemporadas(id);
    }
}
