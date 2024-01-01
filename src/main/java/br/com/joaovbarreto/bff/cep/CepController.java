package br.com.joaovbarreto.bff.cep;

import java.util.HashMap;
import java.util.Map;

import org.apache.juli.logging.Log;
import org.apache.logging.slf4j.Log4jLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/cep")
public class CepController {

    private static Logger logger = LoggerFactory.getLogger(CepController.class);

    private final RestTemplate restTemplate;

    @Autowired
    public CepController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<String> getCepDetails(@PathVariable String cep) {
        String apiUrl = "https://viacep.com.br/ws/" + cep + "/json/";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // return response;
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            // Processar a resposta JSON para extrair a cidade
            String cidade = extrairCidadeDaRespostaJson(responseBody);
            // return ResponseEntity.ok("Cidade correspondente ao CEP " + cep + ": " +
            // cidade);
            CepResponse cepResponse = new CepResponse(cidade);
            return ResponseEntity.ok(cepResponse.getCidade());
            // return ResponseEntity.ok(extrairCidadeDaRespostaJson(responseBody));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Falha ao obter informações do CEP " + cep);
        }
    }

    private String extrairCidadeDaRespostaJson(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Certifique-se de que o JSON possui a chave "localidade" (cidade)
            if (jsonNode.has("localidade")) {
                String cidade = jsonNode.get("localidade").asText();
                return cidade;
            } else {
                return "Cidade não encontrada na resposta JSON";
            }
        } catch (Exception e) {
            // Trate exceções de parsing JSON, se necessário
            return "Erro ao processar a resposta JSON";
        }
    }
}