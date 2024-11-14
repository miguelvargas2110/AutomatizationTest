Feature: Operaciones CRUD de perfiles a través de API Gateway
  Como usuario de la API
  Quiero gestionar los perfiles de los usuarios a través de la API Gateway
  Para poder crear, actualizar, eliminar y consultar perfiles de manera eficiente

  # Escenarios de creación de perfil
  Scenario: Registro de perfil exitoso
    Given un perfil con detalles válidos y un campo "id_usuario" proporcionado
    When se envía una solicitud POST a la API Gateway para crear un perfil con los datos del perfil
    Then el estado de la respuesta debe ser 201
    And el mensaje de respuesta debe ser "Perfil creado exitosamente"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/PerfilCreado.json"

  Scenario: Registro de perfil con "id_usuario" faltante
    Given un perfil sin el campo "id_usuario" proporcionado
    When se envía una solicitud POST a la API Gateway para crear un perfil sin el campo requerido
    Then el estado de la respuesta debe ser 400
    And el mensaje de error de respuesta debe ser "id_usuario es requerido"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/ErrorPerfilCreacion.json"

  # Escenarios de obtención de perfiles
  Scenario: Obtención de todos los perfiles exitosamente
    Given existen perfiles registrados en el servicio de perfiles
    When se envía una solicitud GET a la API Gateway para obtener todos los perfiles
    Then el estado de la respuesta debe ser 200
    And la respuesta debe contener una lista de perfiles
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/ListaPerfiles.json"

  Scenario: Obtención de perfiles cuando no existen registros
    Given no existen perfiles registrados en el servicio de perfiles
    When se envía una solicitud GET a la API Gateway para obtener todos los perfiles
    Then el estado de la respuesta debe ser 404
    And el mensaje de error de respuesta debe ser "No se encontraron perfiles"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/NoPerfilesEncontrados.json"

  Scenario: Obtención de un perfil específico exitoso
    Given existe un perfil registrado con "id_usuario" "123"
    When se envía una solicitud GET a la API Gateway para obtener el perfil con "id_usuario" "123"
    Then el estado de la respuesta debe ser 200
    And el mensaje de respuesta debe ser "Perfil encontrado"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/PerfilEncontrado.json"

  Scenario: Intento de obtener un perfil específico no existente
    Given no existe un perfil con "id_usuario" "999"
    When se envía una solicitud GET a la API Gateway para obtener el perfil con "id_usuario" "999"
    Then el estado de la respuesta debe ser 404
    And el mensaje de error de respuesta debe ser "Perfil no encontrado"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/PerfilNoEncontrado.json"

  # Escenarios de actualización de perfil
  Scenario: Actualización de perfil exitoso
    Given un perfil con "id_usuario" "123" existente
    When se envía una solicitud PUT a la API Gateway para actualizar el perfil con "id_usuario" "123" con nuevos datos
    Then el estado de la respuesta debe ser 200
    And el mensaje de respuesta debe ser "Perfil actualizado exitosamente"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/PerfilActualizado.json"

  Scenario: Intento de actualizar un perfil con datos inválidos
    Given un perfil con "id_usuario" "123" existente
    When se envía una solicitud PUT a la API Gateway para actualizar el perfil con "id_usuario" "123" con datos inválidos
    Then el estado de la respuesta debe ser 400
    And el mensaje de error de respuesta debe ser "Error al actualizar el perfil"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/ErrorPerfilActualizado.json"

  # Escenarios de eliminación de perfil
  Scenario: Eliminación de perfil exitoso
    Given un perfil con "id_usuario" "123" existente
    When se envía una solicitud DELETE a la API Gateway para eliminar el perfil con "id_usuario" "123"
    Then el estado de la respuesta debe ser 200
    And el mensaje de respuesta debe ser "Perfil eliminado exitosamente"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/PerfilEliminado.json"

  Scenario: Intento de eliminar un perfil no existente
    Given no existe un perfil con "id_usuario" "999"
    When se envía una solicitud DELETE a la API Gateway para eliminar el perfil con "id_usuario" "999"
    Then el estado de la respuesta debe ser 404
    And el mensaje de error de respuesta debe ser "Perfil no encontrado"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/PerfilNoEncontrado.json"

  # Escenarios de errores generales al interactuar con el servicio de perfiles
  Scenario: Error al contactar con el servicio de perfiles
    Given el servicio de perfiles no está disponible
    When se envía una solicitud a la API Gateway para realizar una operación sobre los perfiles
    Then el estado de la respuesta debe ser 500
    And el mensaje de error de respuesta debe ser "Error interno del servidor"
    And la respuesta debe cumplir con el esquema JSON "JSONSchemaPerfil/ErrorInterno.json"
