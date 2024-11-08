Feature: Envío de correos de alerta desde MailController

  Scenario: Enviar un correo de alerta exitosamente
    Given un destinatario con el correo "migueto2003@gmail.com"
    And un cuerpo de correo con el mensaje "El servicio está caído."
    And un asunto "Alerta de servicio caído"
    And el nombre del servicio "User Service"
    And el endpoint del servicio "http://localhost:8081/health"
    When se envía una solicitud POST a "/mail/enviar" con los datos del correo
    Then la respuesta debe tener un código de estado 200
    And el mensaje debe ser "El correo fue enviado exitosamente"

  Scenario: Error al enviar un correo de alerta
    Given un destinatario con el correo "migueto2003@gmail.com"
    And un cuerpo de correo con el mensaje "El servicio está caído."
    And un asunto "Alerta de servicio caído"
    And el nombre del servicio "User Service"
    And el endpoint del servicio "http://localhost:8081/health"
    When se envía una solicitud POST a "/mail/enviar" con los datos del correo
    And ocurre un error en el envío del correo
    Then la respuesta debe tener un código de estado 500
    And el mensaje debe ser "No se pudo enviar el correo"
