Feature: Operacion de la API Gateway
    Como usuario de la API
    Quiero redirigir las solicitudes a los microservicios correspondientes
    Para poder acceder a los servicios de manera sencilla

    Scenario: Redireccionamiento exitoso de la solicitud a servicio de Usuarios de Login con inicio exitoso
        Given un usuario con credenciales válidas
        When se envía la solicitud al servicio de Usuarios
        Then la respuesta obtenida debe tener un código de estado 200
        And se debe devolver un token JWT
        And la respuesta debe cumplir con el esquema JSON "SuccesfulJWTGeneration.json"

    Scenario: Redirigir solicitud a servicio de Usuarios de Login con inicio fallido
        Given un usuario con credenciales inválidas
        When se envía la solicitud al servicio de Usuarios
        Then la respuesta obtenida debe tener un código de estado 401
        And el mensaje de respuesta debe ser "Usuario o contraseña no validos"
        And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"

    Scenario: Redireccionamiento fallido de la solicitud a servicio de Usuarios de Login
        Given un usuario con credenciales válidas
        When se envía la solicitud al servicio de Usuarios y el servidor no responde
        Then la respuesta obtenida debe tener un código de estado 500
        And el mensaje de respuesta debe ser '"Internal Server Error"'

    Scenario: Redireccionamiento exitoso de la solicitud a servicio de perfiles de creación
        Given un perfil valido
        When se envía la solicitud al servicio de Perfiles
        Then la respuesta obtenida debe tener un código de estado 201
        And el mensaje de respuesta debe ser "Perfil creado exitosamente"
        And la respuesta debe cumplir con el esquema JSON "SuccesfulOperation.json"

    Scenario: Redireccionamiento fallido de la solicitud a servicio de perfiles de creación
        Given un perfil valido
        When se envía la solicitud al servicio de Perfiles y el servidor no responde
        Then la respuesta obtenida debe tener un código de estado 500
        And el mensaje de respuesta debe ser '"Error interno"'


