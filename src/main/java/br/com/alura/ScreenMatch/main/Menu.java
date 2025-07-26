package br.com.alura.ScreenMatch.main;

import br.com.alura.ScreenMatch.model.*;
import br.com.alura.ScreenMatch.repository.SerieRepository;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;
import java.util.stream.Collectors;


public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Menu(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }



    public void exibeMenu() {
        var opcao = -1;
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - Buscar série por título
                5 - Buscar série por ator
                6 - Buscar 5 melhores séries
                7 - Buscar séries por categoria
                8 - Filtrar série por número de temporadas e avaliação
                9 - Buscar episódio por trecho
                10 - Listar melhores episódios de uma série
                11 - Buscar episódios a partir de uma data
               
                
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
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopEpisodiosSerie();
                    break;
                case 11:
                    buscarEpisodioAposData();
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
        repositorio.save(serie);
        System.out.println(dados);
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
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
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série");
        var nomeSerie = scanner.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<TemporadaRecord> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + "&apikey="+ API_KEY);
                TemporadaRecord dadosTemporada = converteDados.obterDados(json, TemporadaRecord.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d-> d.episodioRecordList().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }else{
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSeriePorTitulo(){
        System.out.println("Escolha uma série pelo nome");
        var nomeSerie = scanner.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBusca.isPresent()){
            System.out.println("Dados da série: "+ serieBusca.get());

        }else{
            System.out.println("Série não encontrada");
        }


    }
    private void buscarSeriePorAtor(){
        System.out.println("Digite o nome do ator");
        var nomeAtor = scanner.nextLine();
        System.out.println("Avaliações a partir de: ");
        var avaliacao = scanner.nextDouble();
        List<Serie> seriesBuscadas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        if (!seriesBuscadas.isEmpty()){
            System.out.println("Séries com "+nomeAtor+": ");
            seriesBuscadas.forEach(s -> System.out.println(s.getTitulo()));
        }else{
            System.out.println("Nenhuma série encontrada com este ator");
        }
    }

    private void buscarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s -> System.out.println(s.getTitulo() + " avaliação: "+ s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Deseja séries de que gênero? ");
        var inputGenero = scanner.nextLine();
        Categoria categoria = Categoria.fromPtBr(inputGenero);

        List<Serie> seriesBuscadas = repositorio.findByGenero(categoria);
        if (!seriesBuscadas.isEmpty()){
            System.out.println("Séries de "+categoria.toString().toLowerCase()+":");
            seriesBuscadas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: "+ s.getAvaliacao()));
        }else{
            System.out.println("Nenhuma série encontrada com este ator");
        }
    }

    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = scanner.nextDouble();
        scanner.nextLine();
        List<Serie> filtroSeries = repositorio.filtrarPorTemporadaeAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Qual o nome do episódio para busca?");
        var trechoEpisodio = scanner.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.buscarEpisodioPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %S\n",
                                                    e.getSerie().getTitulo(), e.getTemporada(),
                                                    e.getNumeroEpisodio(), e.getTitulo()));

    }

    private void buscarTopEpisodiosSerie(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List <Episodio> topEpisodios = repositorio.topEpisodiosSerie(serie);
            topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %S\n",
                    e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo()));
        }
    }
    private void buscarEpisodioAposData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento");
            var inputAno = scanner.nextInt();
            scanner.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, inputAno);
            episodiosAno.forEach(System.out::println);
        }
    }
}
