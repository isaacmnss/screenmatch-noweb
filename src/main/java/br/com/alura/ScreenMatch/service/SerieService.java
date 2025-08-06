package br.com.alura.ScreenMatch.service;

import br.com.alura.ScreenMatch.DTO.EpisodioDTO;
import br.com.alura.ScreenMatch.DTO.SerieDTO;
import br.com.alura.ScreenMatch.model.Serie;
import br.com.alura.ScreenMatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> obterTodasAsSerires(){
        return converteParaDTO(serieRepository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteParaDTO(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos(){
        return converteParaDTO(serieRepository.lancamentosRecentes());
    }

    public SerieDTO obterPorID(Long id){
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();

            return new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse());
        }
        return null;
    }

    public List<SerieDTO> converteParaDTO(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse()))
                .collect(Collectors.toList());
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id){
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();

            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
