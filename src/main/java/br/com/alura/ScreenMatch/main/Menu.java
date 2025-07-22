package br.com.alura.ScreenMatch.main;

import br.com.alura.ScreenMatch.model.Episodio;
import br.com.alura.ScreenMatch.model.EpisodioRecord;
import br.com.alura.ScreenMatch.model.SerieRecord;
import br.com.alura.ScreenMatch.model.TemporadaRecord;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados converteDados = new ConverteDados();

    public void exibeMenu(){
        System.out.println("Digite o nome da série");
        String nomeSerie = scanner.nextLine().trim().replace(" ", "+");
        String endereco = "https://www.omdbapi.com/?t="+ nomeSerie +"&apikey="+API_KEY;


        String json = consumoAPI.obterDados(endereco);
        System.out.println(json);

        SerieRecord dadosSerieRecord = converteDados.obterDados(json, SerieRecord.class);
        System.out.println(dadosSerieRecord);

        List<TemporadaRecord> temporadaRecords = new ArrayList<>();

        for (int i = 1; i <= dadosSerieRecord.totalTemporadas(); i++) {
            json = consumoAPI.obterDados("https://www.omdbapi.com/?t="+ nomeSerie +"&season="+i+"&apikey="+API_KEY);
            TemporadaRecord temporadaRecord = converteDados.obterDados(json, TemporadaRecord.class);
            temporadaRecords.add(temporadaRecord);
        }
        temporadaRecords.forEach(System.out::println);

        temporadaRecords.forEach(t -> t.episodioRecordList().forEach(e-> System.out.println(e.titulo())));

        List<EpisodioRecord> episodiosList = temporadaRecords.stream()
                .flatMap(t -> t.episodioRecordList().stream()).collect(Collectors.toList());

//        System.out.println("\n 10 melhores episódios da série");
//        episodiosList.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Filtrando episódios não avaliados" + e))
//                .sorted(Comparator.comparing(EpisodioRecord::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação por avaliação " + e))
//                .limit(10)
//                .peek(e -> System.out.println("limitando ao top 10 " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento "+ e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadaRecords.stream()
                .flatMap(t -> t.episodioRecordList().stream()
                        .map(d-> new Episodio(t.numeroTemporada(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.println("\nDigite o trecho de um título de algum episódio\n");
//        var trechoTitulo = scanner.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains((trechoTitulo).toLowerCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: "+episodioBuscado.get().getTemporada());
//        }else{
//            System.out.println("Episódio não encontrado");
//        }
//
//        System.out.println("\nA partir de que ano deseja ver os episódios?");
//        Integer ano = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episódio: " + e.getTitulo() +
//                                " Data lançamento: " + e.getDataLancamento().format(formatador)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect((Collectors.summarizingDouble(Episodio::getAvaliacao)));

        System.out.println("Total de episódios: "+ est.getCount());
        System.out.println("Média de avaliações da série: "+ est.getAverage());
        System.out.println("Melhor episódio: "+ est.getMax());
        System.out.println("Pior episódio: "+ est.getMin());
    }
}
