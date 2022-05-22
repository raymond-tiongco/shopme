/* Image Upload Preview */
$(document).ready(() => {
    $("#fileImage").change(function () {
        const file = this.files[0];
        if (file) {
            let reader = new FileReader();
            reader.onload = function (event) {
                $("#thumbnail")
                  .attr("src", event.target.result);
            };
            reader.readAsDataURL(file);
        }
    });
});

/* Flash message */
$(document).ready(function(){
    $("#alertMessage").delay(3000).fadeOut(300);
});

/* Image/Photo Input hidden */
$('.photo-upload').on('click', function(e) {
	e.preventDefault();
	$('#fileImage').trigger('click');
});
