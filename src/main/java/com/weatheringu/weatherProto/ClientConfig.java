package com.weatheringu.weatherProto.config;

import com.weatheringu.weatherProto.grpc.WeatherServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public WeatherServiceGrpc.WeatherServiceBlockingStub weatherServiceStub() {
        // Cria o canal de comunicação com o servidor
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // Retorna o Stub que o Controller tanto deseja
        return WeatherServiceGrpc.newBlockingStub(channel);
    }
}
