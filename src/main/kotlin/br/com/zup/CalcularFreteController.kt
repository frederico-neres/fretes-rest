package br.com.zup

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException

@Controller
class CalcularFreteController(val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): CalcularFreteResponse {

        val request = FretesRequest.newBuilder()
            .setCep(cep)
            .build()

        try {
            val response = grpcClient.calculaFrete(request)
            return CalcularFreteResponse(response)

        } catch (ex: StatusRuntimeException) {

            val description = ex.status.description
            val statusCode = ex.status.code

            if(statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if(statusCode == Status.Code.PERMISSION_DENIED) {
                val statusProto = StatusProto.fromThrowable(ex)
                if(statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                }

                val anyDetails = statusProto.detailsList.get(0)
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)
                throw HttpStatusException(HttpStatus.FORBIDDEN,"${errorDetails.code} ${errorDetails.message}")

            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }
    }
}

data class CalcularFreteResponse(val cep: String, val Valor: Double) {
    constructor(response: FretesResponse): this(response.cep, response.valor)
}