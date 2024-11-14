package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MailStepDefinitions {

    private String[] destinatarios;
    private String cuerpo;
    private String asunto;
    private String nombreServicio;
    private String endpointServicio;
    private Response response;
    private final String baseUrlMail = "http://mailService:8005/mail/enviar";

    @Given("un destinatario con el correo {string}")
    public void unDestinatarioConLosCorreos(String correos) {
        destinatarios = correos.replace("[", "").replace("]", "").replace("\"", "").split(",");
    }

    @And("un cuerpo de correo con el mensaje {string}")
    public void unCuerpoDeCorreoConElMensaje(String mensaje) {
        cuerpo = mensaje;
    }

    @And("un asunto {string}")
    public void unAsunto(String asuntoCorreo) {
        asunto = asuntoCorreo;
    }

    @And("el nombre del servicio {string}")
    public void elNombreDelServicio(String nombre) {
        nombreServicio = nombre;
    }

    @And("el endpoint del servicio {string}")
    public void elEndpointDelServicio(String endpoint) {
        endpointServicio = endpoint;
    }

    @When("se envía una solicitud POST a {string} con los datos del correo")
    public void seEnviaUnaSolicitudPostConLosDatosDelCorreo(String url) {
        String body = String.format(
                "{ \"subject\": \"%s\", \"body\": \"%s\", \"serviceName\": \"%s\", \"serviceEndpoint\": \"%s\", \"recipients\": [\"%s\"] }",
                asunto,
                cuerpo,
                nombreServicio,
                endpointServicio,
                String.join("\", \"", destinatarios)
        );

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrlMail);
    }

    @Then("la respuesta debe tener un código de estado {int}")
    public void laRespuestaDebeTenerUnCodigoDeEstado(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("el mensaje debe ser {string}")
    public void elMensajeDebeSer(String mensajeEsperado) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(mensajeEsperado, mensaje);
    }

    @When("ocurre un error en el envío del correo")
    public void ocurreUnErrorEnElEnvioDelCorreo() {
        // Simular un error en el servicio de correo; podríamos alterar la URL o desactivar el servicio
        response = given()
                .contentType("application/json")
                .body("{}")
                .post("http://localhost:8005/mail/enviar");
    }
}
