package br.com.lastfmparser;

import br.com.tecsinapse.exporter.importer.parser.SpreadsheetParser;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class LasfFMParser {

    //    Map<Artista, Map<Album, List<Faixa>>>
    static Map<String, Map<String, List<Faixa>>> bibliotecaDeMusicas = new HashMap<>();
    static List<Faixa> faixasMaisOuvidas = new ArrayList<>();
    static Map<Map.Entry<String, List<Faixa>>, Long> albunsMaisOuvidos = new HashMap<>();
    static List<LastFMFaixa> lastFMFaixas = new ArrayList<>();
    static List<Faixa> faixas = new ArrayList<>();

    public static void main(String... args) throws Exception {

        SpreadsheetParser<LastFMFaixa> parser = getLastFMFaixaSpreadsheetParser();

        lastFMFaixas = parser.parse();

        faixas = getFaixasValidas();

        bibliotecaDeMusicas = getBiblioteca();

        atualizaContagemExecucoes();

        faixasMaisOuvidas = getFaixasMaisOuvidas();

        albunsMaisOuvidos = getAlbunsMaisOuvidos();

    }

    private static Map<Map.Entry<String, List<Faixa>>, Long> getAlbunsMaisOuvidos() {
        return bibliotecaDeMusicas.entrySet()
                .stream()
                .flatMap(c -> c.getValue().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.summingLong(d -> d.getValue().stream().mapToLong(Faixa::getExecucoes).sum())
                ));
    }

    private static List<Faixa> getFaixasMaisOuvidas() {
        return bibliotecaDeMusicas.entrySet()
                .stream()
                .flatMap(c -> c.getValue().entrySet().stream().flatMap(d -> d.getValue().stream()))
                .sorted(Comparator.comparing(Faixa::getExecucoes).reversed())
                .limit(50)
                .collect(Collectors.toList());
    }

    private static void atualizaContagemExecucoes() {
        faixas.stream()
                .collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                ).entrySet()
                .forEach(c -> {
                            Faixa faixa = getFaixaNaBiblioteca(c.getKey());
                            faixa.setExecucoes(c.getValue());
                        }
                );
    }

    private static Map<String, Map<String, List<Faixa>>> getBiblioteca() {
        return faixas
                .stream()
                .distinct()
                .collect(
                        Collectors.groupingBy(
                                Faixa::getArtista,
                                Collectors.groupingBy(Faixa::getAlbum)
                        )
                );
    }

    private static List<Faixa> getFaixasValidas() {
        return lastFMFaixas
                .stream()
                .filter(c -> !"".equals(c.getData()) && !"".equals(c.getNome()) && !"".equals(c.getArtista()))
                .map(Faixa::new)
                .collect(Collectors.toList());
    }

    private static SpreadsheetParser<LastFMFaixa> getLastFMFaixaSpreadsheetParser() throws URISyntaxException, FileNotFoundException {
        URI path = LasfFMParser.class.getResource("/well_oliveira.xls").toURI();
        File entrada = new File(path);
        InputStream targetStream = new FileInputStream(entrada);

        System.out.println("Iniciando importação");

        SpreadsheetParser<LastFMFaixa> parser = new SpreadsheetParser<>(LastFMFaixa.class, targetStream, entrada.getName());
        parser.setHeadersRows(0);
        return parser;
    }

    private static Map.Entry<String, List<Faixa>> getAlbumEntryNaBiblioteca(Faixa faixa) {

        final String artista = faixa.getArtista();
        final String album = faixa.getAlbum();

        final Map.Entry<String, List<Faixa>> albumEntry = bibliotecaDeMusicas.get(artista)
                .entrySet()
                .stream()
                .filter(c -> c.getKey().contains(album))
                .findAny()
                .get();

        return Optional.of(albumEntry).orElseGet(null);
    }

    private static Faixa getFaixaNaBiblioteca(Faixa faixa) {

        final String artista = faixa.getArtista();
        final String album = faixa.getAlbum();
        final String nome = faixa.getNome();

        final Faixa faixaParaRetorno = bibliotecaDeMusicas.get(artista).get(album)
                .stream()
                .filter(c -> c.getNome().equals(nome))
                .findAny()
                .get();
        return Optional.of(faixaParaRetorno).orElseGet(null);
    }
}
