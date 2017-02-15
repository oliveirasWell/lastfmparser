package br.com.lastfmparser;

import br.com.tecsinapse.exporter.annotation.TableCellMapping;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Faixa  {

    private String nome;

    private String artista;

    private String album;

    @Setter
    private Long execucoes;

    public Faixa(LastFMFaixa lastFMFaixa) {
        this.nome = lastFMFaixa.getNome();
        this.artista = lastFMFaixa.getArtista();
        this.album = lastFMFaixa.getAlbum();
    }

    @TableCellMapping(columnIndex = 0)
    public void setArtista(String artista) {
        this.artista = artista;
    }

    @TableCellMapping(columnIndex = 1)
    public void setAlbum(String album) {
        this.album = album;
    }

    @TableCellMapping(columnIndex = 2)
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "nome='" + nome + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", execucoes='" + execucoes + '\'';
    }
}