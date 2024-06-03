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

            // Obtiene el valor del atributo 'data-bloqueado' y lo convierte a booleano (true si data-bloqueado es true y false si data-bloqueado es false)
            //A bloqueado se le asigna el resultado de la comparación de que el atributo data-bloqueado sea true
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
    // Selecciona todos los elementos con la clase 'editable'
    document.querySelectorAll('.editable').forEach(function (element) {
        // Añade un event listener para el evento 'click' a cada elemento editable
        element.addEventListener('click', function () {
            // Crea un nuevo elemento de entrada de texto
            let input = document.createElement('input');
            input.type = 'text';
            input.value = element.innerText;
            input.dataset.field = element.dataset.field;
            input.dataset.id = element.dataset.id;
            element.innerHTML = ''; // Limpia el contenido del elemento editable
            element.appendChild(input); // Adjunta el elemento de entrada de texto al elemento editable
            input.focus(); // Establece el foco en el nuevo elemento de entrada de texto

            // Añade un event listener para el evento 'blur' al elemento de entrada de texto
            input.addEventListener('blur', function () {
                // Obtiene el valor del elemento de entrada de texto
                let value = input.value;
                element.innerText = value; // Establece el texto del elemento editable al valor ingresado

                // Crea una nueva solicitud XMLHttpRequest
                let xhr = new XMLHttpRequest();
                xhr.open('POST', `/administrador/actualizar-cliente/${input.dataset.id}`, true);
                xhr.setRequestHeader('Content-Type', 'application/json');

                // Define una función que se ejecutará cuando la solicitud se haya completado con éxito
                xhr.onload = function () {
                    if (xhr.status === 200) {
                        alert('Actualizado correctamente');
                    } else {
                        alert('Error al actualizar');
                    }
                };

                // Envía la solicitud al servidor con los datos actualizados
                xhr.send(JSON.stringify({ field: input.dataset.field, value: value }));
            });
        });
    });
});
