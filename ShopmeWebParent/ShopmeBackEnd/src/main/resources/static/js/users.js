$(document).ready(function() {

    $("#success-alert").delay(4000).slideUp(200, function() {
        $(this).alert('close');
    });
});