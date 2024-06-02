document.addEventListener('DOMContentLoaded', function () {
    // Este evento se dispara cuando el DOM ha sido completamente cargado y parseado
    console.log("Documento listo, adjuntando evento click.");

    // Selecciona todos los elementos con la clase 'accion-bloquear'
    var bloquearBtns = document.querySelectorAll('.accion-bloquear');

    // Itera sobre cada botón con la clase 'accion-bloquear'
    bloquearBtns.forEach(function (btn) {
        // Añade un event listener para el evento 'click' a cada botón
        btn.addEventListener('click', function (event) {
            // Evita que el evento click se propague a otros elementos
            event.stopPropagation();

            // Obtiene el valor del atributo 'data-email' del botón
            let email = btn.getAttribute('data-email');
            // Obtiene el valor del atributo 'data-bloqueado' y lo convierte a booleano
            let bloqueado = btn.getAttribute('data-bloqueado') === 'true';
            console.log("Email: " + email + ", Bloqueado: " + bloqueado);

            // Muestra un mensaje de confirmación al usuario
            if (!confirm("¿Está seguro de que desea " + (bloqueado ? 'desbloquear' : 'bloquear') + " el usuario " + email + "?")) {
                // Si el usuario cancela la confirmación, sale de la función
                return;
            }

            // Crea una nueva solicitud XMLHttpRequest
            let xhr = new XMLHttpRequest();
            // Configura la solicitud para que sea de tipo POST y apunte a la URL especificada
            xhr.open("POST", "/administrador/bloquear-usuario/" + email, true);

            // Define una función que se ejecutará cuando la solicitud se haya completado con éxito
            xhr.onload = function () {
                if (xhr.status === 200) {
                    // Si el servidor responde con un código 200, muestra la respuesta del servidor y recarga la página
                    console.log("Respuesta del servidor: ", xhr.responseText);
                    alert(xhr.responseText);
                    location.reload(); // Recarga la página
                } else {
                    // Si el servidor responde con un código diferente a 200, muestra un mensaje de error
                    console.error("Error en la solicitud: ", xhr.responseText);
                    alert("Error: " + xhr.responseText);
                }
            };

            // Define una función que se ejecutará si ocurre un error en la solicitud
            xhr.onerror = function () {
                console.error("Error en la solicitud: ", xhr.responseText);
                alert("Error: " + xhr.responseText);
            };

            // Envía la solicitud al servidor
            xhr.send();
        });
    });
});

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.editable').forEach(function (element) {
        element.addEventListener('click', function () {
            let input = document.createElement('input');
            input.type = 'text';
            input.value = element.innerText;
            input.dataset.field = element.dataset.field;
            input.dataset.id = element.dataset.id;
            element.innerHTML = '';
            element.appendChild(input);
            input.focus();

            input.addEventListener('blur', function () {
                let value = input.value;
                element.innerText = value;

                let xhr = new XMLHttpRequest();
                xhr.open('POST', `/administrador/actualizar-cliente/${input.dataset.id}`, true);
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.onload = function () {
                    if (xhr.status === 200) {
                        alert('Actualizado correctamente');
                    } else {
                        alert('Error al actualizar');
                    }
                };
                xhr.send(JSON.stringify({ field: input.dataset.field, value: value }));
            });
        });
    });
});