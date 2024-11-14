package steps;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.github.dockerjava.api.DockerClient;

import java.time.Duration;

@Transactional
public class ApiGatewayStepDefinitions {
    private String username = "";
    private String password = "";
    private String perfilData;
    private String baseUrl = "http://apiGateway:3000";
    private Response response;
    private String token;
    UserCrudStepDefinitions userCrudStepDefinitions = new UserCrudStepDefinitions();
    ProfilesStepsDefinitions profilesStepsDefinitions = new ProfilesStepsDefinitions();

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

    @Given("un usuario con credenciales válidas ApiGateway")
    public void unUsuarioConCredencialesValidasApiGateway() {
        encenderContenedor("proyectomicros-authUser-1");
        userCrudStepDefinitions.verificarUsuarioNoE();
        username = "Ortiz";
        password = "1234";
    }

    @Given("un usuario con credenciales inválidas ApiGateway")
    public void unUsuarioConCredencialesInvalidasApiGateway() {
        encenderContenedor("proyectomicros-authUser-1");
        userCrudStepDefinitions.verificarUsuarioNoE();
        username = "Ortiz";
        password = "123456";
    }

    @Given("un perfil valido ApiGateway")
    public void unPerfilConDetallesValidosApiGateway() {
        encenderContenedor("proyectomicros-profileService-1");
        profilesStepsDefinitions.verificarPerfilNoE();
        perfilData = "{ \"id_usuario\": \"123\", \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";
    }

    @When("se envía la solicitud al servicio de Usuarios")
    public void seEnvíaUnaSolicitudDeAutenticacionConEsasCredencialesApiGateway() {
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/auth/login");
    }

    @When("se envía la solicitud al servicio de Usuarios y el servidor no responde")
    public void seEnvíaUnaSolicitudDeAutenticacionConEsasCredencialesFalloApiGateway() {
        apagarContenedor("proyectomicros-authUser-1");

        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/auth/login");
    }

    @When("se envía la solicitud al servicio de Perfiles")
    public void seEnvíaLaSolicitudAlServicioDePerfilesApiGateway() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .post(baseUrl + "/profile/profiles/");
    }

    @When("se envía la solicitud al servicio de Perfiles y el servidor no responde")
    public void seEnvíaLaSolicitudAlServicioDePerfilesFalloApiGateway() {
        apagarContenedor("proyectomicros-profileService-1");
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .post(baseUrl + "/profile/profiles/");
    }

    @Then("la respuesta ApiGateway obtenida debe tener un código de estado {int}")
    public void elEstadoDeLaRespuestaDebeSerApiGateway(Integer statusCode) {
        assertEquals(statusCode.intValue(), response.getStatusCode());
    }

    @And("el mensaje de respuesta ApiGateway debe ser {string}")
    public void elMensajeDeRespuestaDebeSerApiGateway(String message) {
        try {
            String mensaje = response.jsonPath().getString("message");
            assertEquals(message, mensaje);
        } catch (JsonPathException e) {
            // Si no se puede parsear, asumimos que es un texto plano
            String body = response.getBody().asString();
            assertEquals(message, body);
        }
    }

    @Then("se debe devolver un token JWT al ApiGateway")
    public void seDebeDevolverUnTokenJWTApiGateway() {
        token = response.jsonPath().getString("jwt");
        assertNotNull(token);
    }

    @And("la respuesta ApiGateway debe cumplir con el esquema JSON {string}")
    public void laRespuestaDebeCumplirConElEsquemaJSONApiGateway(String schemaPath) {
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }

    public void apagarContenedor(String containerName) {
        DockerClient dockerClient = dockerClientInit();
        InspectContainerResponse container = dockerClient.inspectContainerCmd(containerName).exec();

        // Verifica si el contenedor está en ejecución antes de intentar detenerlo
        if ("running".equals(container.getState().getStatus())) {
            dockerClient.stopContainerCmd(container.getId()).exec();
            System.out.println("Contenedor " + container.getName() + " detenido");
        } else {
            System.out.println("El contenedor ya está detenido: " + container.getName());
        }
    }


    public void encenderContenedor(String containerName) {
        DockerClient dockerClient = dockerClientInit();
        InspectContainerResponse container = dockerClient.inspectContainerCmd(containerName).exec();

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
                container = dockerClient.inspectContainerCmd("proyectomicros-authUser-1").exec();
                retries++;
            }

            if ("running".equals(container.getState().getStatus())) {
                System.out.println("Contenedor iniciado correctamente.");

                // Espera adicional para asegurar que la aplicación esté lista
                waitForApplicationToBeReady(containerName);
            } else {
                System.out.println("Tiempo de espera agotado. El contenedor no se inició.");
            }
        } else {
            System.out.println("El contenedor ya está en ejecución: " + container.getName());
        }
    }

    private void waitForApplicationToBeReady(String containerName) {
        int maxRetries = 15;
        int retries = 0;
        while (retries < maxRetries) {
            try {
                if(containerName.equals("proyectomicros-authUser-1")) {
                    Response response = given()
                            .baseUri("http://authUser:8001")
                            .get("/health");
                }else{
                    Response response = given()
                            .baseUri("http://profileService:8000")
                            .get("/health");
                }

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
}
