$(document).ready(function() {
    // Espera a que el DOM esté completamente cargado y listo para manipular

    $('#verificarRespuestaBtn').on('click', function() {
        // Asocia una función al evento 'click' del botón con el id 'verificarRespuestaBtn'

        let respRec = $('#respRec').val();
        // Obtiene el valor del elemento de entrada con el id 'respRec'

        $.ajax({
            // Realiza una solicitud AJAX
            type: 'POST', // Tipo de solicitud: POST
            url: '/verificarRespuestaRecuperacion', // URL a la que se enviará la solicitud
            data: {
                respRec: respRec // Datos a enviar con la solicitud
            },
            success: function(response) {
                // Función que se ejecuta si la solicitud tiene éxito
                if (response.error) {
                    // Si hay un error en la respuesta, muestra el mensaje de error
                    $('#mensaje').text(response.error);
                } else {
                    // Si no hay error, muestra la contraseña recuperada
                    $('#mensaje').text("Su contraseña es: " + response.clave);
                }
            },
            error: function(xhr, status, error) {
                // Función que se ejecuta si hay un error en la solicitud
                $('#mensaje').text("Hubo un error al procesar la solicitud. Inténtelo de nuevo.");
            }
        });
    });
});

