package com.exemplo.client.controller;

import com.exemplo.weather.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class WeatherController {

    // Injeta o stub para comunicação síncrona (bloqueante) com o servidor gRPC
    @GrpcClient("weather-service")
    private WeatherServiceGrpc.WeatherServiceBlockingStub weatherStub;

    @PostMapping("/cidade")
    public String cadastrarCidade(@RequestParam String nome, @RequestParam double temperatura) {
        CityRegistrationRequest request = CityRegistrationRequest.newBuilder()
                .setNome(nome)
                .setTemperaturaInicial(temperatura)
                .build();
        RegistrationResponse response = weatherStub.cadastrarCidade(request);
        return response.getMensagem();
    }

    @GetMapping("/cidades")
    public List<String> listarCidades() {
        CityListResponse response = weatherStub.listarCidades(Empty.newBuilder().build());
        return response.getCidadesList();
    }

    @GetMapping("/temperatura")
    public double obterTemperatura(@RequestParam String cidade) {
        CityRequest request = CityRequest.newBuilder().setNome(cidade).build();
        TemperatureResponse response = weatherStub.obterTemperaturaAtual(request);
        return response.getTemperatura();
    }

    @GetMapping("/previsao")
    public List<Double> previsaoCincoDias(@RequestParam String cidade) {
        CityRequest request = CityRequest.newBuilder().setNome(cidade).build();
        ForecastResponse response = weatherStub.previsaoCincoDias(request);
        return response.getTemperaturasProximosDiasList();
    }

    @GetMapping("/estatisticas")
    public String estatisticasClimaticas(@RequestParam String cidade) {
        CityRequest request = CityRequest.newBuilder().setNome(cidade).build();
        StatsResponse response = weatherStub.estatisticasClimaticas(request);
        return String.format("Média: %.2f | Mínima: %.2f | Máxima: %.2f", 
                response.getMedia(), response.getMinima(), response.getMaxima());
    }
}
