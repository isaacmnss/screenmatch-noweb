package br.com.alura.ScreenMatch;

import br.com.alura.ScreenMatch.model.Serie;
import br.com.alura.ScreenMatch.service.ConsumoAPI;
import br.com.alura.ScreenMatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		Scanner sc = new Scanner(System.in);

		System.out.println("Digite o nome do título que deseja consultar");
		String titulo = sc.nextLine().trim().toLowerCase().replace(" ", "+");
		String endereco = "https://www.omdbapi.com/?t="+ titulo +"&apikey="+apiKey;
		ConsumoAPI consumoAPI = new ConsumoAPI();
		String json = consumoAPI.obterDados(endereco);
		System.out.println(json);
		ConverteDados converteDados = new ConverteDados();
		Serie dados = converteDados.obterDados(json, Serie.class);
		System.out.println(dados);
	}
}
