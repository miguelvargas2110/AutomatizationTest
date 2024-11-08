Feature: Operaciones de Monitoreo de Servicios
  Como usuario de la API
  Quiero registrar y consultar servicios monitoreados
  Para poder mantener la salud de los microservicios

  Scenario: Registro de servicio exitoso
    Given un nuevo servicio con detalles válidos
    When se envía una solicitud para registrar el servicio con los datos del servicio
    Then el estado de la respuesta de health debe ser 201
    And el mensaje de respuesta de health debe ser "Servicio añadido con éxito"
    And la respuesta de health debe cumplir con el esquema JSON "JSONSchemaMonitoring/SuccessfulServiceRegistration.json"

  Scenario: Registro de servicio inválido
    Given un nuevo servicio con detalles inválidos
    When se envía una solicitud para registrar el servicio con datos inválidos
    Then el estado de la respuesta de health debe ser 500
    And el mensaje de error de respuesta de health debe ser "Error al añadir el servicio"
    And la respuesta de health debe cumplir con el esquema JSON "JSONSchemaMonitoring/UnsuccessfulServiceRegistration.json"

  Scenario: Consulta de todos los servicios monitoreados
    Given existen servicios registrados en el sistema
    When se envía una solicitud para obtener el estado de salud de todos los servicios
    Then el estado de la respuesta debe de health ser 200
    And la respuesta debe contener una lista de servicios
    And la respuesta de health debe cumplir con el esquema JSON "JSONSchemaMonitoring/ServiceList.json"

  Scenario: Consulta de servicios sin registros
    Given no existen servicios registrados
    When se envía una solicitud para obtener el estado de salud de todos los servicios
    Then el estado de la respuesta de health debe ser 500
    And el mensaje de error de respuesta de health debe ser "Error al obtener los servicios"
    And la respuesta de health debe cumplir con el esquema JSON "JSONSchemaMonitoring/NoServicesFound.json"

  Scenario: Consulta de un servicio específico exitoso
    Given existe un servicio registrado con nombre "serviceA"
    When se envía una solicitud para obtener el estado de salud del servicio "serviceA"
    Then el estado de la respuesta de health debe ser 200
    And el mensaje de respuesta de health debe ser "Servicio encontrado"
    And la respuesta de health debe cumplir con el esquema JSON "JSONSchemaMonitoring/ServiceFound.json"

  Scenario: Consulta de un servicio específico no existente
    Given no existe un servicio registrado con nombre "serviceB"
    When se envía una solicitud para obtener el estado de salud del servicio "serviceB"
    Then el estado de la respuesta de health debe ser 404
    And el mensaje de error de respuesta de health debe ser "Servicio no encontrado"
    And la respuesta de health debe cumplir con el esquema JSON "JSONSchemaMonitoring/ServiceNotFound.json"
