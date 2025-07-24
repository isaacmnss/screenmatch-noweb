package br.com.alura.ScreenMatch.main;

import br.com.alura.ScreenMatch.model.SerieRecord;
import br.com.alura.ScreenMatch.model.TemporadaRecord;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;


public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    public void exibeMenu() {
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                
                0 - Sair
                """;

        System.out.println(menu);
        var opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }

    private void buscarSerieWeb() {
        SerieRecord dados = getDadosSerie();
        System.out.println(dados);
    }

    private SerieRecord getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = scanner.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.trim().toLowerCase().replace(" ", "+") + "&apikey="+ API_KEY);
        return converteDados.obterDados(json, SerieRecord.class);
    }

    private void buscarEpisodioPorSerie(){
        SerieRecord dadosSerie = getDadosSerie();
        List<TemporadaRecord> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumoAPI.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + "&apikey="+ API_KEY);
            TemporadaRecord dadosTemporada = converteDados.obterDados(json, TemporadaRecord.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }
}
