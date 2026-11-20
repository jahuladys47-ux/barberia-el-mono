// Menú móvil
const botonMenu = document.getElementById("botonMenu");
const menu = document.getElementById("menu");
botonMenu.addEventListener("click", () => {
    const abierto = menu.classList.toggle("abierto");
    botonMenu.setAttribute("aria-expanded", abierto);
});
menu.addEventListener("click", e => {
    if (e.target.tagName === "A") { menu.classList.remove("abierto"); botonMenu.setAttribute("aria-expanded", false); }
});

// Año del pie de página
document.getElementById("anio").textContent = new Date().getFullYear();

// Validación del formulario
const form = document.getElementById("formulario");
const hoy = new Date();
const hoyTexto = hoy.getFullYear() + "-" + String(hoy.getMonth() + 1).padStart(2, "0") + "-" + String(hoy.getDate()).padStart(2, "0");
form.fecha.min = hoyTexto; // no se puede agendar en días pasados
const exito = document.getElementById("exito");

function validar(campo, errorId, mensaje, valido) {
    const error = document.getElementById(errorId);
    error.textContent = valido ? "" : mensaje;
    campo.setAttribute("aria-invalid", valido ? "false" : "true");
    return valido;
}

form.addEventListener("submit", e => {
    e.preventDefault();
    exito.textContent = "";
    const nombre = form.nombre, correo = form.correo, corte = form.corte, fecha = form.fecha, hora = form.hora, barbero = form.barbero;
    const okNombre = validar(nombre, "errorNombre", "Escribe tu nombre (mínimo 3 letras).", nombre.value.trim().length >= 3);
    const okCorreo = validar(correo, "errorCorreo", "Escribe un correo electrónico válido (ejemplo: tucorreo@ejemplo.com).", /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo.value.trim()));
    const okCorte = validar(corte, "errorCorte", "Selecciona el corte.", corte.value !== "");
    const okFecha = validar(fecha, "errorFecha", "Elige una fecha desde hoy en adelante.", fecha.value !== "" && fecha.value >= hoyTexto);
    const okHora = validar(hora, "errorHora", "Elige una hora entre las 8:00 a. m. y las 7:00 p. m.", hora.value !== "" && hora.value >= "08:00" && hora.value <= "19:00");
    const okBarbero = validar(barbero, "errorBarbero", "Selecciona un barbero.", barbero.value !== "");
    if (okNombre && okCorreo && okCorte && okFecha && okHora && okBarbero) {
        exito.textContent = "Solicitud enviada. " + nombre.value.trim() + ", te llamaremos para confirmar tu cita y el abono del 50 %.";
        form.reset();
    } else {
        form.querySelector("[aria-invalid='true']").focus();
    }
});
