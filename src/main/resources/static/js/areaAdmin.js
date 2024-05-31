document.addEventListener('DOMContentLoaded', function () {
    console.log("Documento listo, adjuntando evento click.");

    var bloquearBtns = document.querySelectorAll('.accion-bloquear');

    bloquearBtns.forEach(function (btn) {
        btn.addEventListener('click', function (event) {
            event.stopPropagation();
            let email = btn.getAttribute('data-email');
            let bloqueado = btn.getAttribute('data-bloqueado') === 'true';
            console.log("Email: " + email + ", Bloqueado: " + bloqueado);

            if (!confirm("¿Está seguro de que desea " + (bloqueado ? 'desbloquear' : 'bloquear') + " el usuario " + email + "?")) {
                return;
            }

            let xhr = new XMLHttpRequest();
            xhr.open("POST", "/administrador/bloquear-usuario/" + email, true);
            xhr.onload = function () {
                if (xhr.status === 200) {
                    console.log("Respuesta del servidor: ", xhr.responseText);
                    alert(xhr.responseText);
                    location.reload(); // Recarga la página
                } else {
                    console.error("Error en la solicitud: ", xhr.responseText);
                    alert("Error: " + xhr.responseText);
                }
            };
            xhr.onerror = function () {
                console.error("Error en la solicitud: ", xhr.responseText);
                alert("Error: " + xhr.responseText);
            };
            xhr.send();
        });
    });
});
