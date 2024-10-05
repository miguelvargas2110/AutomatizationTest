Feature: Operaciones de Gestión de Logs
  Como usuario de la API
  Quiero gestionar logs (crear, leer)
  Para poder registrar y consultar eventos en el sistema

  Scenario: Creación de log exitoso
    Given un nuevo log con detalles válidos
    When se envía una solicitud de creación de log con los datos del log
    Then el estado de la respuesta de logs debe ser 201
    And el mensaje de respuesta de logs debe ser "Log creado con éxito"
    And la respuesta de logs debe cumplir con el esquema JSON "SuccessfulLogCreation.json"

  Scenario: Creación de log inválido
    Given un nuevo log con detalles inválidos
    When se envía una solicitud de creación de log con los datos inválidos
    Then el estado de la respuesta de logs debe ser 500
    And el mensaje de error de logs debe ser "Error al crear el log"
    And la respuesta de logs debe cumplir con el esquema JSON "UnsuccessfulLogCreation.json"

  Scenario: Consulta de logs exitosa
    Given existen logs registrados en el sistema
    When se envía una solicitud de consulta de logs con filtros válidos
    Then el estado de la respuesta de logs debe ser 200
    And la respuesta debe contener una lista de logs
    And la respuesta de logs debe cumplir con el esquema JSON "LogList.json"

  Scenario: Consulta de logs sin resultados
    Given no existen logs que cumplan con los filtros
    When se envía una solicitud de consulta de logs con filtros
    Then el estado de la respuesta de logs debe ser 404
    And el mensaje de error debe ser "Logs no encontrados"
    And la respuesta de logs debe cumplir con el esquema JSON "NoLogsFound.json"
