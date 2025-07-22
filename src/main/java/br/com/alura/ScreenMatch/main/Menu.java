package br.com.alura.ScreenMatch.main;

import br.com.alura.ScreenMatch.model.EpisodioRecord;
import br.com.alura.ScreenMatch.model.SerieRecord;
import br.com.alura.ScreenMatch.model.TemporadaRecord;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();

    public void exibeMenu(){
        System.out.println("Digite o nome da série");
        String nomeSerie = scanner.nextLine().trim().replace(" ", "+");
        String endereco = "https://www.omdbapi.com/?t="+ nomeSerie +"&apikey="+API_KEY;


        String json = consumoAPI.obterDados(endereco);

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

        System.out.println("\n 5 melhores episódios da série");
        episodiosList.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(EpisodioRecord::avaliacao, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .forEach(System.out::println);


    }
}
