package br.com.alura.ScreenMatch.main;

import br.com.alura.ScreenMatch.model.Serie;
import br.com.alura.ScreenMatch.model.Temporada;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
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

        Serie dadosSerie = converteDados.obterDados(json, Serie.class);
        System.out.println(dadosSerie);

        List<Temporada> temporadas = new ArrayList<>();

        for (int i = 1; i < dadosSerie.totalTemporadas(); i++) {
            json = consumoAPI.obterDados("https://www.omdbapi.com/?t="+ nomeSerie +"&season=1&apikey="+API_KEY);
            Temporada temporada = converteDados.obterDados(json, Temporada.class);
            temporadas.add(temporada);
        }
        temporadas.forEach(System.out::println);

    }
}
