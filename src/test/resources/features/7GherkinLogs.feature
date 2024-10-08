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
    And la respuesta de logs debe cumplir con el esquema JSON "NoLogsFound.json"

  Scenario: Verificar la creación de un usuario y el registro de su log
    Given que un usuario llamado "testUser" no existe en el sistema
    When el usuario "testUser" es creado en el sistema de autenticación
    Then se debe registrar un log con el mensaje "El usuario testUser fue registrado exitosamente" en el sistema de logs
    And el mensaje de la respuesta debe ser "Log encontrado, con la descripción: El usuario testUser fue registrado exitosamente"
