$(document).ready(function() {
    $('#verificarRespuestaBtn').on('click', function() {
        var respRec = $('#respRec').val();

        $.ajax({
            type: 'POST',
            url: '/verificarRespuestaRecuperacion',
            data: {
                respRec: respRec
            },
            success: function(response) {
                if (response.error) {
                    $('#mensaje').text(response.error);
                } else {
                    $('#mensaje').text("Su contraseña es: " + response.clave);
                }
            },
            error: function(xhr, status, error) {
                $('#mensaje').text("Hubo un error al procesar la solicitud. Inténtelo de nuevo.");
            }
        });
    });
});
