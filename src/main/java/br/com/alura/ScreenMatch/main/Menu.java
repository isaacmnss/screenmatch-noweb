package br.com.alura.ScreenMatch.main;

import br.com.alura.ScreenMatch.model.Serie;
import br.com.alura.ScreenMatch.model.SerieRecord;
import br.com.alura.ScreenMatch.model.TemporadaRecord;
import br.com.alura.ScreenMatch.repository.SerieRepository;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;


public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    List<SerieRecord> serieRecordList = new ArrayList<>();

    private SerieRepository repositorio;

    public Menu(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                
                0 - Sair
                """;

        while (opcao != 0){
            System.out.println(menu);
            boolean inputValido = false;

            while (!inputValido) {
                try {
                    opcao = scanner.nextInt();
                    inputValido = true;
                } catch (InputMismatchException e) {
                    System.out.println("Opção inválida, tente novamente");
                    scanner.nextLine();
                }
            }

            scanner.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente");
            }
        }

        System.out.println(menu);
        scanner.nextLine();

    }

    private void buscarSerieWeb() {
        SerieRecord dados = getDadosSerie();
        Serie serie = new Serie(dados);
       // serieRecordList.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private void listarSeriesBuscadas(){
        List<Serie> series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
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
