$(document).ready(function () {
    $(".accion-bloquear").click(function (event) {
        event.stopPropagation();
        let email = $(this).data("email");
        let bloqueado = $(this).data("bloqueado");
        if (!confirm("¿Está seguro de que desea " + (bloqueado ? 'desbloquear' : 'bloquear') + " el usuario " + email + "?")) {
            return;
        }
        $.ajax({
            type: "POST",
            url: "/administrador/bloquear-usuario/" + email,
            success: function (response) {
                console.log(response);
                alert(response);
                location.reload(); // Recarga la página
            },
            error: function (xhr, status, error) {
                console.error(xhr.responseText);
                alert("Error: " + xhr.responseText);
            }
        });
    });}