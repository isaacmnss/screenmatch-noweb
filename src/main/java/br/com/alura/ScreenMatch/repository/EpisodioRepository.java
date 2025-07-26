package br.com.alura.ScreenMatch.repository;

import br.com.alura.ScreenMatch.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodioRepository extends JpaRepository<Episodio, Long> {
}
