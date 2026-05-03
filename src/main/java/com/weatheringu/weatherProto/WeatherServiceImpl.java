package com.exemplo.server.service;

import com.exemplo.weather.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class WeatherServiceImpl extends WeatherServiceGrpc.WeatherServiceImplBase {

    // Banco de dados em memória simulado
    private final Map<String, Double> cidadesDb = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public WeatherServiceImpl() {
        // Cidades iniciais
        cidadesDb.put("Urutai", 28.5);
        cidadesDb.put("Goiania", 30.0);
    }

    @Override
    public void obterTemperaturaAtual(CityRequest request, StreamObserver<TemperatureResponse> responseObserver) {
        String cidade = request.getNome();
        double temp = cidadesDb.getOrDefault(cidade, 0.0); // Retorna 0 se não achar
        
        TemperatureResponse response = TemperatureResponse.newBuilder()
                .setTemperatura(temp)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void previsaoCincoDias(CityRequest request, StreamObserver<ForecastResponse> responseObserver) {
        String cidade = request.getNome();
        double baseTemp = cidadesDb.getOrDefault(cidade, 25.0);
        
        ForecastResponse.Builder responseBuilder = ForecastResponse.newBuilder();
        for (int i = 0; i < 5; i++) {
            // Simula variação de temperatura (-2 a +2 graus)
            responseBuilder.addTemperaturasProximosDias(baseTemp + (random.nextDouble() * 4 - 2));
        }
        
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listarCidades(Empty request, StreamObserver<CityListResponse> responseObserver) {
        CityListResponse response = CityListResponse.newBuilder()
                .addAllCidades(cidadesDb.keySet())
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void cadastrarCidade(CityRegistrationRequest request, StreamObserver<RegistrationResponse> responseObserver) {
        cidadesDb.put(request.getNome(), request.getTemperaturaInicial());
        
        RegistrationResponse response = RegistrationResponse.newBuilder()
                .setSucesso(true)
                .setMensagem("Cidade " + request.getNome() + " cadastrada com sucesso!")
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void estatisticasClimaticas(CityRequest request, StreamObserver<StatsResponse> responseObserver) {
        String cidade = request.getNome();
        double baseTemp = cidadesDb.getOrDefault(cidade, 25.0);
        
        // Simulando estatísticas
        StatsResponse response = StatsResponse.newBuilder()
                .setMedia(baseTemp)
                .setMaxima(baseTemp + 5.0)
                .setMinima(baseTemp - 5.0)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
