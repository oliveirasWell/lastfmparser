package br.com.lastfmparser;


import br.com.tecsinapse.exporter.annotation.TableCellMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LastFMFaixa extends Faixa{

    private String data; // Trocar para data

    @TableCellMapping(columnIndex = 3) // converter = data
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return super.toString() +", data='" + data;
    }
}
