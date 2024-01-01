package br.com.joaovbarreto.bff.cep;

public class CepResponse {
    private String cidade;

    public CepResponse(String cidade) {
        this.cidade = cidade;
    }

    public String getCidade() {
        return cidade;
    }
}
