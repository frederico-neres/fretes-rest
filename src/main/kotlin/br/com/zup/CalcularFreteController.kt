package br.com.zup

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

@Controller
class CalcularFreteController(val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): CalcularFreteResponse {

        val request = FretesRequest.newBuilder()
            .setCep(cep)
            .build()

        val response = grpcClient.calculaFrete(request)
        return CalcularFreteResponse(response)
    }
}

data class CalcularFreteResponse(val cep: String, val Valor: Double) {
    constructor(response: FretesResponse): this(response.cep, response.valor)
}