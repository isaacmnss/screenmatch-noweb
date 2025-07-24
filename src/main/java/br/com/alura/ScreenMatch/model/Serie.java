package br.com.alura.ScreenMatch.model;

import java.util.OptionalDouble;

public class Serie {
    private String titulo;

    private Integer totalTemporadas;

    private Double avaliacao;

    private Categoria genero;

    private String atores;

    private String poster;

    private String sinopse;

    public Serie() {
    }

    public Serie(SerieRecord serieRecord) {
        this.titulo = serieRecord.titulo();
        this.totalTemporadas = serieRecord.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(serieRecord.avaliacao())).orElse(0);
        this.genero = Categoria.fromString(serieRecord.genero().split(",")[0]);
        this.atores = serieRecord.atores();
        this.poster = serieRecord.poster();
        this.sinopse = serieRecord.sinopse();
    }

    public Serie(String titulo, Integer totalTemporadas, Double avaliacao, Categoria genero, String atores, String poster, String sinopse) {
        this.titulo = titulo;
        this.totalTemporadas = totalTemporadas;
        this.avaliacao = avaliacao;
        this.genero = genero;
        this.atores = atores;
        this.poster = poster;
        this.sinopse = sinopse;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return
                ", genero=" + genero +
                "titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", avaliacao=" + avaliacao +
                ", atores='" + atores + '\'' +
                ", poster='" + poster + '\'' +
                ", sinopse='" + sinopse + '\'';
    }
}
