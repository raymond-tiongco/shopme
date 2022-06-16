/* Validation on submitting form */
function validateOnSubmit() {
	var isValid = true;
    var $fields = $("#userForm").find('input[name="roles"]:checked');
    var pswd = $(".pw-input").val();
    var fname = $.trim($("#fname").val());
    var lname = $.trim($("#lname").val());
    
    console.log("[In validateOnSubmit()] fname: " + fname);
    console.log("[In validateOnSubmit()] lname: " + lname);
    
    if(fname == "") {
		$("#fnameError").show();
		isValid = false;
	}
	if(lname == "") {
		$("#lnameError").show();
		isValid = false;
	}
	$("#fname").val(fname);
	$("#lname").val(lname);
	
    if ( pswd.length < 8 || !pswd.match(/[A-z]/) || !pswd.match(/[A-Z]/) || !pswd.match(/\d/)) {
	    $('#pwError').show();
	    isValid = false;
	}
	
    console.log("[In validateOnSubmit()] fields.length: " + $fields.length);
    if (!$fields.length) {
        $('#rolesError').show();
        isValid = false;
    }
    
    console.log("[In validateOnSubmit()] is valid? " + isValid);
    
    return isValid;
}

/* Check if passwords are valid */
$(".pw-input").keyup(function() {
	var pswd = $(this).val();
	
	if ( pswd.length >= 8 || pswd.match(/[A-z]/) || pswd.match(/[A-Z]/) || pswd.match(/\d/)) {
	    $('#pwError').hide();
	}
	else {
		$('#pwError').show();
	}
	
	//validate the length
	if ( pswd.length < 8) {
	    $('.pw-length').removeClass('valid').addClass('invalid');
	} else {
	    $('.pw-length').removeClass('invalid').addClass('valid');
	}
	
	//validate letter
	if ( pswd.match(/[A-z]/) ) {
	    $('.pw-letter').removeClass('invalid').addClass('valid');
	} else {
	    $('.pw-letter').removeClass('valid').addClass('invalid');
	}
	
	//validate capital letter
	if ( pswd.match(/[A-Z]/) ) {
	    $('.pw-capital').removeClass('invalid').addClass('valid');
	} else {
	    $('.pw-capital').removeClass('valid').addClass('invalid');
	}
	
	//validate number
	if ( pswd.match(/\d/) ) {
	    $('.pw-number').removeClass('invalid').addClass('valid');
	} else {
	    $('.pw-number').removeClass('valid').addClass('invalid');
	}

}).focus(function() {
    $('.pswd-info').show();
}).blur(function() {
    $('.pswd-info').hide();
});

/* Check if passwords match */
function checkPasswordMatchWithMessage() {
    var password = $("#newPassword").val();
    var confirmPassword = $("#newPasswordConfirm").val();
    if (password != confirmPassword) {
		$("#passwordMatchMsg").html("Passwords do not match!").css("color", "red");
	}
    else {
		$("#passwordMatchMsg").html("Passwords match.").css("color", "green");
	}
}
function checkValidPassword() {
    var password = $("#newPassword").val();
    var confirmPassword = $("#newPasswordConfirm").val();
    if ( password.length < 8 || !password.match(/[A-z]/) || !password.match(/[A-Z]/) || !password.match(/\d/)) {
	    $('.pwError').show();
	    return false;
	}
	else {
		$('.pwError').hide();
	}
    if (password != confirmPassword) {
		return false;
	}
    else {
		return true;
	}
}
$(function () {
	$("#newPasswordConfirm").keyup(checkPasswordMatchWithMessage);
	$("#changePasswordForm").submit(checkValidPassword);
});

/* Nav Dropdown for Principal User */
$("#profPic").click(function() {
	$("#loggedInUserDetails").toggle();
});

/* Dropdown toggle for export */
$("#dropdownExportMenuBtn").click(function() {
	$("#dropDownExportMenu").toggle();
});

/* Dropdown for items per page */
$("#itemsPerPageBtn").click(function() {
	$("#itemsPerPageDropdown").toggle();
});

/* Image Upload Preview */
$(function() {
    $('#fileImage').change(function () {
        const file = this.files[0];
        if (file) {
            let reader = new FileReader();
            reader.onload = function (event) {
                $('#thumbnail')
                  .attr("src", event.target.result);
            };
            reader.readAsDataURL(file);
        }
    });
});

/* Image/Photo Input hidden */
$('#photoUpload').on('click', function(e) {
	e.preventDefault();
	$('#fileImage').trigger('click');
});

/* Flash message */
$(document).ready(function() {
    $("#alertMessage").delay(3000).fadeOut(300);
});

/* Confirmation dialog box for delete */
$(".confirm-delete").confirm({
    title: 'Delete Confirmation',
    content: 'Are you sure you want to delete the user?',
    type: 'orange',
    icon: 'fa fa-warning',
    autoClose: 'cancel|8000',
    buttons: {
        deleteUser: {
            text: 'yes',
            btnClass: 'btn-blue',
            action: function () {
                location.href = this.$target.attr('href');
            }
        },
        cancel: {
			text: 'cancel',
            btnClass: 'btn-dark',
			action: function () {
	            $.alert('Deletion is canceled.');
	        }
		}
    }
});