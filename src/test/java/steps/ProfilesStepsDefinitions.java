package steps;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProfilesStepsDefinitions {

    private Response response;
    private String perfilId;
    private String perfilData;
    private String baseUrl = "http://profileService:8000/api";
    private ProfilesDatabaseUtil profilesDatabaseUtil = new ProfilesDatabaseUtil();

    public DockerClient dockerClientInit() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

    public void encenderContenedor() {
        DockerClient dockerClient = dockerClientInit();
        InspectContainerResponse container = dockerClient.inspectContainerCmd("proyectomicros-profileService-1").exec();

        // Verifica si el contenedor está detenido antes de iniciarlo
        if ("exited".equals(container.getState().getStatus())) {
            dockerClient.startContainerCmd(container.getId()).exec();
            System.out.println("Iniciando contenedor: " + container.getName());

            // Espera activa: verifica cada segundo si el contenedor está en "running"
            int maxRetries = 15;  // Intentar por hasta 30 segundos
            int retries = 0;
            while (!"running".equals(container.getState().getStatus()) && retries < maxRetries) {
                try {
                    Thread.sleep(2000); // Espera de 5 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                container = dockerClient.inspectContainerCmd("proyectomicros-profileService-1").exec();
                retries++;
            }

            if ("running".equals(container.getState().getStatus())) {
                System.out.println("Contenedor iniciado correctamente.");

                // Espera adicional para asegurar que la aplicación esté lista
                waitForApplicationToBeReady();
            } else {
                System.out.println("Tiempo de espera agotado. El contenedor no se inició.");
            }
        } else {
            System.out.println("El contenedor ya está en ejecución: " + container.getName());
        }
    }

    private void waitForApplicationToBeReady() {
        int maxRetries = 15;
        int retries = 0;
        while (retries < maxRetries) {
            try {
                Response response = given()
                        .baseUri("http://localhost:8001")
                        .get("/health");

                if (response.statusCode() == 200 &&
                        response.jsonPath().getString("status").equals("UP")) {
                    System.out.println("La aplicación está lista.");
                    return;
                } else {
                    System.out.println("Esperando a que la aplicación esté lista...");
                }
            } catch (Exception e) {
                System.out.println("Error al verificar la aplicación: " + e.getMessage());
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retries++;
        }

        System.out.println("Tiempo de espera agotado. La aplicación no está lista.");
    }

    // Escenarios de Creación de Perfil
    @Given("un perfil valido")
    public void unPerfilConDetallesValidos() {
        encenderContenedor();
        verificarPerfilNoE();
        perfilData = "{ \"id_usuario\": \"123\", \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";
    }

    // Escenarios de Creación con Error (falta id_usuario)
    @Given("un perfil invalido")
    public void unPerfilSinElCampoIdUsuario() {
        encenderContenedor();
        perfilData = "{ \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";  // Sin el campo "id_usuario"
    }

    @When("se envía una solicitud POST para crear un perfil")
    public void seEnvíaUnaSolicitudPOSTParaCrearPerfil() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .post(baseUrl + "/profiles/");
    }

    @Then("el estado de la respuesta Profiles debe ser {int}")
    public void elEstadoDeLaRespuestaDebeSer(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("el mensaje de error de respuesta Profiles debe ser {string}")
    public void elMensajeDeErrorDeRespuestaDebeSer(String errorMessage) {
        String mensajeError = response.jsonPath().getString("message");
        assertEquals(errorMessage, mensajeError);
    }

    // Escenarios de obtención de perfiles
    @Given("existen perfiles registrados en el servicio de perfiles")
    public void existenPerfilesRegistrados() {
        encenderContenedor();
        verificarPerfilE();
    }

    @Given("no existen perfiles registrados en el servicio de perfiles")
    public void noExistenPerfilesRegistradosEnElServicioDePerfiles() {
        encenderContenedor();
        profilesDatabaseUtil.borrarTodosLosPerfiles();
    }

    @When("se envía una solicitud GET para obtener todos los perfiles")
    public void seEnvíaUnaSolicitudGETParaObtenerPerfiles() {
        response = given().get(baseUrl + "/profiles/");
    }

    @Then("la respuesta debe contener una lista de perfiles")
    public void laRespuestaDebeContenerUnaListaDePerfiles() {
        String perfiles = response.jsonPath().getString("data"); // "data" contiene los perfiles según ApiSuccess
        assertNotNull(perfiles);
    }


    @Given("un perfil inexistente")
    public void unPerfilInexistente() {
        encenderContenedor();
        verificarPerfilIne();
        perfilId = "999";
    }

    @When("se envía una solicitud GET para obtener el perfil")
    public void seEnvíaUnaSolicitudGETParaObtenerPerfilEspecifico() {
        response = given().get(baseUrl + "/profiles/" + perfilId + "/");
    }

    // Escenarios de actualización de perfil
    @Given("un perfil existente")
    public void unPerfilExistente() {
        encenderContenedor();
        verificarPerfilE();
        perfilId = "123";
    }

    @And("los datos de perfil son válidos")
    public void losDatosDePerfilSonVálidos() {
        perfilData = "{ \"informacion_publica\": \"true\", \"apodo\": \"Juancho\" }";
    }

    @And("los datos de perfil son inválidos")
    public void losDatosDePerfilSonInválidos() {
        perfilData = "{ \"informacion_publica\": \"Si\", \"apodo\": \"Juancho\" }";
    }

    @When("se envía una solicitud PUT para actualizar el perfil")
    public void seEnvíaUnaSolicitudPUTParaActualizarPerfil() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .put(baseUrl + "/profiles/" + perfilId + "/");
    }

    @When("se envía una solicitud DELETE para eliminar el perfil")
    public void seEnvíaUnaSolicitudDELETEParaEliminarPerfil() {
        response = given().delete(baseUrl + "/profiles/" + perfilId + "/");
    }

    @Then("el mensaje de respuesta Profiles debe ser {string}")
    public void elMensajeDeRespuestaEliminarPerfilDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    @And("la respuesta Profiles debe cumplir con el esquema JSON {string}")
    public void laRespuestaDeErrorDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    public void verificarPerfilIne(){
        boolean existe = profilesDatabaseUtil.verificarPerfilExistente(999);
        if(existe){
            given().delete(baseUrl + "/profiles/999/");
        }
    }

    public void verificarPerfilNoE(){
        boolean existe = profilesDatabaseUtil.verificarPerfilExistente(123);
        if(existe){
            given().delete(baseUrl + "/profiles/123/");
        }
    }

    public void verificarPerfilE(){
        boolean existe = profilesDatabaseUtil.verificarPerfilExistente(123);
        if(!existe){
            perfilData = "{ \"id_usuario\": \"123\", \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";
            given().contentType("application/json").body(perfilData).post(baseUrl + "/profiles/");
        }
    }
}
