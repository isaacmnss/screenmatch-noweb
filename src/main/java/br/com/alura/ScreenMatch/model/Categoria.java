package br.com.alura.ScreenMatch.model;

public enum Categoria{
    ACAO ("Action", "Ação"),
    ROMANCE ("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private String categoriaOmdb;
    private String categoriaemPtBr;

    Categoria(String categoriaOmdb, String categoriaEmPtBr) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaemPtBr = categoriaEmPtBr;
    }

    public static Categoria fromString(String texto){
        for(Categoria categoria : Categoria.values()){
            if (categoria.categoriaOmdb.equalsIgnoreCase(texto)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma Categoria encontrada.");
    }

    public static Categoria fromPtBr(String texto){
        for(Categoria categoria : Categoria.values()){
            if (categoria.categoriaemPtBr.equalsIgnoreCase(texto)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma Categoria encontrada.");
    }
}
